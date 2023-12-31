package cn.xwplay.business.service;

import cn.xwplay.business.domain.ConfirmOrderEntity;
import cn.xwplay.business.domain.DailyTrainSeatEntity;
import cn.xwplay.business.domain.DailyTrainTicketEntity;
import cn.xwplay.business.req.ConfirmOrderTicketReq;

import java.util.List;

public interface AfterConfirmOrderService {

    void afterDoConfirm(DailyTrainTicketEntity dailyTrainTicket, List<DailyTrainSeatEntity> finalSeatList, List<ConfirmOrderTicketReq> tickets, ConfirmOrderEntity confirmOrder);

}
