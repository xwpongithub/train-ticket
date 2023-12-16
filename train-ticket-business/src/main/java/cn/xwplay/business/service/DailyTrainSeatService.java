package cn.xwplay.business.service;

import cn.xwplay.business.domain.DailyTrainSeatEntity;
import cn.xwplay.business.req.DailyTrainSeatQueryReq;
import cn.xwplay.business.req.DailyTrainSeatSaveReq;
import cn.xwplay.business.req.SeatSellReq;
import cn.xwplay.business.resp.DailyTrainSeatQueryResp;
import cn.xwplay.business.resp.SeatSellResp;
import cn.xwplay.common.response.PageResp;

import java.util.Date;
import java.util.List;

public interface DailyTrainSeatService {

    void save(DailyTrainSeatSaveReq req);

    PageResp<DailyTrainSeatQueryResp> queryList(DailyTrainSeatQueryReq req);

    void delete(Long id);

    void genDaily(Date date, String trainCode);
    int countSeat(Date date, String trainCode);

    int countSeat(Date date, String trainCode, String seatType);

    List<DailyTrainSeatEntity> selectByCarriage(Date date, String trainCode, Integer carriageIndex);

    List<SeatSellResp> querySeatSell(SeatSellReq req);
}
