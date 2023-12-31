package cn.xwplay.business.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.xwplay.business.domain.ConfirmOrderEntity;
import cn.xwplay.business.domain.DailyTrainSeatEntity;
import cn.xwplay.business.domain.DailyTrainTicketEntity;
import cn.xwplay.business.enums.ConfirmOrderStatusEnum;
import cn.xwplay.business.feign.MemberFeign;
import cn.xwplay.business.mapper.ConfirmOrderMapper;
import cn.xwplay.business.mapper.DailyTrainSeatMapper;
import cn.xwplay.business.mapper.DailyTrainTicketMapper;
import cn.xwplay.business.req.ConfirmOrderTicketReq;
import cn.xwplay.business.req.MemberTicketReq;
import cn.xwplay.business.service.AfterConfirmOrderService;
import cn.xwplay.common.response.CommonResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AfterConfirmOrderServiceImpl implements AfterConfirmOrderService {

    private final DailyTrainSeatMapper dailyTrainSeatMapper;
    private final DailyTrainTicketMapper dailyTrainTicketMapper;
    private final ConfirmOrderMapper confirmOrderMapper;
    private final MemberFeign memberFeign;

    /**
     * 选中座位后事务处理：
     *  座位表修改售卖情况sell；
     *  余票详情表修改余票；
     *  为会员增加购票记录
     *  更新确认订单为成功
     */
     @Transactional(rollbackFor = Exception.class)
    // @GlobalTransactional
    public void afterDoConfirm(DailyTrainTicketEntity dailyTrainTicket, List<DailyTrainSeatEntity> finalSeatList, List<ConfirmOrderTicketReq> tickets, ConfirmOrderEntity confirmOrder) {
        var now = DateUtil.date();
         for (int j = 0; j < finalSeatList.size(); j++) {
            var dailyTrainSeat = finalSeatList.get(j);
            var seatForUpdate = new DailyTrainSeatEntity();
            seatForUpdate.setId(dailyTrainSeat.getId());
            seatForUpdate.setSell(dailyTrainSeat.getSell());
            seatForUpdate.setUpdateTime(now);
            dailyTrainSeatMapper.updateById(seatForUpdate);

            // 计算这个站卖出去后，影响了哪些站的余票库存
            // 参照2-3节 如何保证不超卖、不少卖，还要能承受极高的并发 10:30左右
            // 影响的库存：本次选座之前没卖过票的，和本次购买的区间有交集的区间
            // 假设10个站，本次买4~7站
            // 原售：001000001
            // 购买：000011100
            // 新售：001011101
            // 影响：XXX11111X
            // Integer startIndex = 4;
            // Integer endIndex = 7;
            // Integer minStartIndex = startIndex - 往前碰到的最后一个0;
            // Integer maxStartIndex = endIndex - 1;
            // Integer minEndIndex = startIndex + 1;
            // Integer maxEndIndex = endIndex + 往后碰到的最后一个0;
            var startIndex = dailyTrainTicket.getStartIndex();
            var endIndex = dailyTrainTicket.getEndIndex();
            var sell = seatForUpdate.getSell();
            var chars = sell.toCharArray();
            var maxStartIndex = endIndex - 1;
            var minEndIndex = startIndex + 1;
            var minStartIndex = 0;
            for (var i = startIndex - 1; i >= 0; i--) {
                var aChar = chars[i];
                if (aChar == '1') {
                    minStartIndex = i + 1;
                    break;
                }
            }
            log.info("影响出发站区间：{}-{}" , minStartIndex , maxStartIndex);

            var maxEndIndex = sell.length();
            for (var i = endIndex; i < maxEndIndex; i++) {
                var aChar = chars[i];
                if (aChar == '1') {
                    maxEndIndex = i;
                    break;
                }
            }
            log.info("影响到达站区间：{}-{}" ,minEndIndex , maxEndIndex);

            dailyTrainTicketMapper.updateCountBySell(
                    dailyTrainSeat.getDate(),
                    dailyTrainSeat.getTrainCode(),
                    dailyTrainSeat.getSeatType(),
                    minStartIndex,
                    maxStartIndex,
                    minEndIndex,
                    maxEndIndex);

            // 调用会员服务接口，为会员增加一张车票
            var memberTicketReq = new MemberTicketReq();
            memberTicketReq.setMemberId(confirmOrder.getMemberId());
            memberTicketReq.setPassengerId(tickets.get(j).getPassengerId());
            memberTicketReq.setPassengerName(tickets.get(j).getPassengerName());
            memberTicketReq.setTrainDate(dailyTrainTicket.getDate());
            memberTicketReq.setTrainCode(dailyTrainTicket.getTrainCode());
            memberTicketReq.setCarriageIndex(dailyTrainSeat.getCarriageIndex());
            memberTicketReq.setSeatRow(dailyTrainSeat.getRow());
            memberTicketReq.setSeatCol(dailyTrainSeat.getCol());
            memberTicketReq.setStartStation(dailyTrainTicket.getStart());
            memberTicketReq.setStartTime(dailyTrainTicket.getStartTime());
            memberTicketReq.setEndStation(dailyTrainTicket.getEnd());
            memberTicketReq.setEndTime(dailyTrainTicket.getEndTime());
            memberTicketReq.setSeatType(dailyTrainSeat.getSeatType());
            CommonResp<Object> commonResp = memberFeign.save(memberTicketReq);
            log.info("调用member接口，返回：{}", commonResp);

            // 更新订单状态为成功
            var confirmOrderForUpdate = new ConfirmOrderEntity();
            confirmOrderForUpdate.setId(confirmOrder.getId());
            confirmOrderForUpdate.setUpdateTime(now);
            confirmOrderForUpdate.setStatus(ConfirmOrderStatusEnum.SUCCESS.getCode());
            confirmOrderMapper.updateById(confirmOrderForUpdate);

            // 模拟调用方出现异常
            // Thread.sleep(10000);
            // if (1 == 1) {
            //     throw new Exception("测试异常");
            // }
        }
    }

}
