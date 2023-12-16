package cn.xwplay.business.service;

import cn.xwplay.business.domain.DailyTrainCarriageEntity;
import cn.xwplay.business.req.DailyTrainCarriageQueryReq;
import cn.xwplay.business.req.DailyTrainCarriageSaveReq;
import cn.xwplay.business.resp.DailyTrainCarriageQueryResp;
import cn.xwplay.common.response.PageResp;

import java.util.Date;
import java.util.List;

public interface DailyTrainCarriageService {

    void save(DailyTrainCarriageSaveReq req);
    PageResp<DailyTrainCarriageQueryResp> queryList(DailyTrainCarriageQueryReq req);
    void delete(Long id);
    void genDaily(Date date, String trainCode);
    List<DailyTrainCarriageEntity> selectBySeatType(Date date, String trainCode, String seatType);

}
