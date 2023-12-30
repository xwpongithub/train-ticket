package cn.xwplay.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.xwplay.business.domain.ConfirmOrderEntity;
import cn.xwplay.business.domain.DailyTrainSeatEntity;
import cn.xwplay.business.domain.DailyTrainTicketEntity;
import cn.xwplay.business.enums.ConfirmOrderStatusEnum;
import cn.xwplay.business.enums.SeatColEnum;
import cn.xwplay.business.enums.SeatTypeEnum;
import cn.xwplay.business.mapper.ConfirmOrderMapper;
import cn.xwplay.business.req.ConfirmOrderDoReq;
import cn.xwplay.business.req.ConfirmOrderQueryReq;
import cn.xwplay.business.resp.ConfirmOrderQueryResp;
import cn.xwplay.business.service.ConfirmOrderService;
import cn.xwplay.business.service.DailyTrainCarriageService;
import cn.xwplay.business.service.DailyTrainSeatService;
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

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConfirmOrderServiceImpl implements ConfirmOrderService {

    private final ConfirmOrderMapper confirmOrderMapper;
    private final DailyTrainTicketService dailyTrainTicketService;
    private final DailyTrainCarriageService dailyTrainCarriageService;
    private final DailyTrainSeatService dailyTrainSeatService;

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

        // 最终的选座结果
        var finalSeatList = CollUtil.<DailyTrainSeatEntity>newArrayList();
        // 选座
        //// 一个车厢一个车厢的获取座位数据
        //// 挑选符合条件的作为，如果这个车厢不满足，则进入下一个车厢（多个选座应该在同一个车厢）
        var ticketReq0 = tickets.get(0);
        if (StrUtil.isBlank(ticketReq0.getSeat())){
            log.info("本次购票没有选座");
            for (var ticket : tickets) {
                chooseSeat(
                        finalSeatList,
                        date,trainCode,
                        ticket.getSeat().split("")[0],
                        null,
                        null,
                        dailyTrainTicket.getStartIndex(),
                        dailyTrainTicket.getEndIndex()
                );
            }
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
            chooseSeat(
                    finalSeatList,
                    date,trainCode,ticketReq0.getSeatTypeCode(),
                    ticketReq0.getSeat().split("")[0],offsetList,
                    dailyTrainTicket.getStartIndex(),
                    dailyTrainTicket.getEndIndex());
        }
        log.info("最终选座:{}",finalSeatList);


        // 对选中的作为做事务处理
        //// 修改作为表售卖情况(Sell字段)
        //// 修改余票详情表的余票数量
        //// 为会员增加购票记录
        //// 更新确认订单表为成功
    }

    // 一个车厢一个车厢的获取座位
    // 如果有选座，则一次性挑完，如果没有选座，则一个一个挑
    // 例如: sell = 10001,本次购买区间站1-4，则区间已售000
    // 全部是0表示这个区间可买，只要有1，就表示区间内已卖过票
    private void chooseSeat(
            List<DailyTrainSeatEntity> finalSeatList,
            Date date, String trainCode, String seatType, String selectedCol, List<Integer> offsetList,Integer startIndex,Integer endIndex) {
       var getSeatList = CollUtil.<DailyTrainSeatEntity>newArrayList();
        var carriageList = dailyTrainCarriageService.selectBySeatType(date,trainCode,seatType);
       log.info("共查出{}个符合条件的车厢",carriageList);
        for (var dailyTrainCarriageEntity : carriageList) {
            getSeatList.clear();
            var carriageIndex = dailyTrainCarriageEntity.getIndex();
            log.info("开始从车厢{}选座",carriageIndex);
            var seatList = dailyTrainSeatService.selectByCarriage(date,trainCode,carriageIndex);
            log.info("车厢{}的座位数：{}",carriageIndex,seatList.size());
            for (var i = 0;i<seatList.size();i++) {
                var dailyTrainSeatEntity = seatList.get(i);
                var seatIndex = dailyTrainSeatEntity.getCarriageSeatIndex();
                // 判断column，有值的话要对比列号
                var col = dailyTrainSeatEntity.getCol();
                var alreadyChooseFlag = false;
                for (var trainSeatEntity : finalSeatList) {
                    if (trainSeatEntity.getId().equals(dailyTrainSeatEntity.getId())){
                       alreadyChooseFlag = true;
                        break;
                    }
                }
                if (alreadyChooseFlag) {
                    log.info("座位{}已被选中，不能重复选中，继续判断下一个座位",seatIndex);
                    continue;
                }
                if (StrUtil.isBlank(selectedCol)) {
                    log.info("无选座");
                } else {
                   if (!StrUtil.equals(col,selectedCol)){
                       log.info("座位{}列值不对，继续判断下一个座位,当前列值:{},目标列值:{}",seatIndex,col,selectedCol);
                       continue;
                   }
                }
                var isChoose = calSell(dailyTrainSeatEntity,startIndex,endIndex);
                if (!isChoose){
                    continue;
                } else {
                    getSeatList.add(dailyTrainSeatEntity);
                    log.info("选中座位");
                }

                // 根据offset选剩下的座位
                var isGetAllOffsetSeat = true;
                if (CollUtil.isNotEmpty(offsetList)){
                  log.info("有偏移值:{},校验偏移的座位是否可选",offsetList);
                    for (int j = 1; j < offsetList.size(); j++) {
                        // 从索引1开始，索引0就是当前已选中的票
                        var offset = offsetList.get(j);
                        // 座位在库里的索引是从1开始
                        var nextIndex = i + offset;
                        // 有选座时一定是在同一个车厢
                        if (nextIndex >= seatList.size()){
                            log.info("座位{}不可选，偏移后的索引超出了这个车厢的座位数",nextIndex);
                            isGetAllOffsetSeat = false;
                            break;
                        }

                        var nextDailyTrainSeat = seatList.get(nextIndex);
                        var isChooseNext = calSell(nextDailyTrainSeat,startIndex,endIndex);
                        if (isChooseNext){
                            log.info("选中座位{}",nextDailyTrainSeat.getCarriageSeatIndex());
                            getSeatList.add(nextDailyTrainSeat);
                        } else {
                            log.info("座位{}不可选",nextDailyTrainSeat.getCarriageSeatIndex());
                            isGetAllOffsetSeat = false;
                            break;
                        }
                    }
                }

                if (!isGetAllOffsetSeat){
                    getSeatList.clear();
                    continue;
                }

                // 保存选好的座位到数据库
                finalSeatList.addAll(getSeatList);
                return;

            }

        }
    }

    /**
     * 选中: true 未选中: false
     * 计算某座位在某区间是否可售卖
     */
    private boolean calSell(DailyTrainSeatEntity dailyTrainSeatEntity,int startIndex,int endIndex) {
        // 10001
        var sell = dailyTrainSeatEntity.getSell();
      // 000
        var sellPart = sell.substring(startIndex,endIndex);
      if (Integer.parseInt(sellPart) > 0) {
          log.info("座位{}造本次车站区间{}-{}已被售出，不可被选中",dailyTrainSeatEntity.getCarriageSeatIndex(),
                  startIndex,endIndex);
          return false;
      } else {
          log.info("座位{}造本次车站区间{}-{}未被售出，可被选中",dailyTrainSeatEntity.getCarriageSeatIndex(),
                  startIndex,endIndex);
          // 111
          var curSell = sellPart.replace('0','1');
          // 0111
          curSell = StrUtil.fillBefore(curSell,'0', endIndex);
          // 01110
          curSell = StrUtil.fillAfter(curSell,'0',sell.length());
          // 当前区间的售票信息curSell与库里的已售信息进行按位或运算，即可得到该座位卖出此票后的售票情况
          var newSellInt = NumberUtil.binaryToInt(curSell) | NumberUtil.binaryToInt(sell);
          var newSell = NumberUtil.getBinaryStr(newSellInt);
          newSell = StrUtil.fillBefore(newSell,'0',sell.length());
          log.info("座位{}被选中,原售票信息:{},车站区间:{}-{},即:{},最终售票信息:{}",
                  dailyTrainSeatEntity.getCarriageSeatIndex(),sell,startIndex,
                  endIndex,curSell,newSell);
          dailyTrainSeatEntity.setSell(newSell);
          return true;
      }
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
