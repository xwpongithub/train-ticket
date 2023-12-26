package cn.xwplay.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.xwplay.business.domain.ConfirmOrderEntity;
import cn.xwplay.business.domain.DailyTrainTicketEntity;
import cn.xwplay.business.enums.ConfirmOrderStatusEnum;
import cn.xwplay.business.enums.SeatColEnum;
import cn.xwplay.business.enums.SeatTypeEnum;
import cn.xwplay.business.mapper.ConfirmOrderMapper;
import cn.xwplay.business.req.ConfirmOrderDoReq;
import cn.xwplay.business.req.ConfirmOrderQueryReq;
import cn.xwplay.business.req.ConfirmOrderTicketReq;
import cn.xwplay.business.resp.ConfirmOrderQueryResp;
import cn.xwplay.business.service.ConfirmOrderService;
import cn.xwplay.business.service.DailyTrainTicketService;
import cn.xwplay.common.context.LoginMemberContext;
import cn.xwplay.common.exception.BusinessException;
import cn.xwplay.common.exception.BusinessExceptionEnum;
import cn.xwplay.common.response.PageResp;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConfirmOrderServiceImpl implements ConfirmOrderService {

    private final ConfirmOrderMapper confirmOrderMapper;
    private final DailyTrainTicketService dailyTrainTicketService;

    @Override
    public void save(ConfirmOrderDoReq req) {
        var now = DateUtil.date();
        var confirmOrder = BeanUtil.copyProperties(req, ConfirmOrderEntity.class);
        if (Objects.isNull(confirmOrder.getId())) {
            confirmOrder.setCreateTime(now);
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.insert(confirmOrder);
        } else {
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.updateById(confirmOrder);
        }
    }

    @Override
    public PageResp<ConfirmOrderQueryResp> queryList(ConfirmOrderQueryReq req) {
        var q = Wrappers.<ConfirmOrderEntity>lambdaQuery();
        q.orderByDesc(ConfirmOrderEntity::getId);
        log.info("查询页码：{}", req.getPage());
        log.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        var confirmOrderList = confirmOrderMapper.selectList(q);
        var pageInfo = new PageInfo<>(confirmOrderList);
        log.info("总行数：{}", pageInfo.getTotal());
        log.info("总页数：{}", pageInfo.getPages());
        var list = BeanUtil.copyToList(confirmOrderList, ConfirmOrderQueryResp.class);
        PageResp<ConfirmOrderQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    @Override
    public void delete(Long id) {
        confirmOrderMapper.deleteById(id);
    }

    @Override
    public void doConfirm(ConfirmOrderDoReq req) {
        var date = req.getDate();
        var start = req.getStart();
        var end = req.getEnd();
        var trainCode = req.getTrainCode();
        var tickets = req.getTickets();
      // 省略业务数据校验 如 车次是否存在，余票是否存在，车次是否在有效期内，tickets条数大于0，同乘客同车次不能重复选座
        // 保存确认订单表，状态初始
        var orderInfo = new ConfirmOrderEntity();
        var now = DateUtil.date();
        orderInfo.setMemberId(LoginMemberContext.getId());
        orderInfo.setCreateTime(now);
        orderInfo.setUpdateTime(now);
        orderInfo.setDate(date);
        orderInfo.setTrainCode(trainCode);
        orderInfo.setStart(start);
        orderInfo.setEnd(end);
        orderInfo.setDailyTrainTicketId(req.getDailyTrainTicketId());
        orderInfo.setStatus(ConfirmOrderStatusEnum.INIT.getCode());
        orderInfo.setTickets(JSON.toJSONString(tickets));
        confirmOrderMapper.insert(orderInfo);
        // 查出余票记录，需要得到真实的库存
        var dailyTrainTicket = dailyTrainTicketService.selectByUnique(date,trainCode,start,end);
        log.info("查出真实余票记录:{}",dailyTrainTicket);

        // 扣减余票数量，并判断余票是否足够
        reduceTickets(req, dailyTrainTicket);

        // 选座
        //// 一个车厢一个车厢的获取座位数据
        //// 挑选符合条件的作为，如果这个车厢不满足，则进入下一个车厢（多个选座应该在同一个车厢）
        var ticketReq0 = tickets.get(0);
        if (StrUtil.isBlank(ticketReq0.getSeat())){
            log.info("本次购票没有选座");
        } else {
            log.info("本次购票有选座");
            // 计算偏移值
            var colEnumList = SeatColEnum.getColsByType(ticketReq0.getSeatTypeCode());
            log.info("本次选座的作为类型包含的列:{}",colEnumList);
            // 组成和前端两排选座一样的列表，用于做参照的作为列表，如：referSeatList = {A1,C1,D1,F1}
            var referSeatList = CollUtil.<String>newArrayList();
            for (int i = 1; i <= 2; i++) {
                for (var seatColEnum : colEnumList) {
                  referSeatList.add(seatColEnum.getCode()+i);
                }
            }
            log.info("用于做参照的两排座位:{}",referSeatList);
            // 绝对偏移值，即：在参照作为列表中的位置
            var absoluteOffsetList = CollUtil.<Integer>newArrayList();
            for (var ticket : tickets) {
                var seat = ticket.getSeat();
                var index = referSeatList.indexOf(seat);
                absoluteOffsetList.add(index);
            }
            log.info("计算得到所有座位的绝对偏移值：{}",absoluteOffsetList);
            var offsetList = CollUtil.<Integer>newArrayList();
            for (var index : absoluteOffsetList) {
               var offset = index - absoluteOffsetList.get(0);
               offsetList.add(offset);
            }
            log.info("计算得到所有座位的相对第一个座位的偏移值：{}",offsetList);
        }


        // 对选中的作为做事务处理
        //// 修改作为表售卖情况(Sell字段)
        //// 修改余票详情表的余票数量
        //// 为会员增加购票记录
        //// 更新确认订单表为成功
    }

    private void reduceTickets(ConfirmOrderDoReq req, DailyTrainTicketEntity dailyTrainTicket) {
        var tickets = req.getTickets();
        for (var ticketReq : tickets) {
            var seatTypeCode = ticketReq.getSeatTypeCode();
            var seatType = SeatTypeEnum.getEnumByCode(seatTypeCode);
            switch (seatType){
                case YDZ -> {
                    var countLeft = dailyTrainTicket.getYdz() - 1;
                    if (countLeft<0){
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYdz(countLeft);
                }
                case EDZ -> {
                    var countLeft = dailyTrainTicket.getEdz() - 1;
                    if (countLeft<0){
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setEdz(countLeft);
                }
                case RW -> {
                    var countLeft = dailyTrainTicket.getRw() - 1;
                    if (countLeft<0){
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setRw(countLeft);
                }
                case YW -> {
                    var countLeft = dailyTrainTicket.getYw() - 1;
                    if (countLeft<0){
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYw(countLeft);
                }
            }
        }
    }

}
