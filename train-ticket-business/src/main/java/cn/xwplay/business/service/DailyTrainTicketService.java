package cn.xwplay.business.service;

import cn.xwplay.business.domain.DailyTrainEntity;
import cn.xwplay.business.domain.DailyTrainTicketEntity;
import cn.xwplay.business.req.DailyTrainTicketQueryReq;
import cn.xwplay.business.req.DailyTrainTicketSaveReq;
import cn.xwplay.business.resp.DailyTrainTicketQueryResp;
import cn.xwplay.common.response.PageResp;

import java.util.Date;

public interface DailyTrainTicketService {

    void save(DailyTrainTicketSaveReq req);

    void delete(Long id);

    PageResp<DailyTrainTicketQueryResp> queryList(DailyTrainTicketQueryReq req);

    DailyTrainTicketEntity selectByUnique(Date date, String trainCode, String start, String end);

    void genDaily(DailyTrainEntity dailyTrain, Date date, String trainCode);

}
