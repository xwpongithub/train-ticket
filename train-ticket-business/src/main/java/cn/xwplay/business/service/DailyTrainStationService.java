package cn.xwplay.business.service;

import cn.xwplay.business.req.DailyTrainStationQueryReq;
import cn.xwplay.business.req.DailyTrainStationSaveReq;
import cn.xwplay.business.resp.DailyTrainStationQueryResp;
import cn.xwplay.common.response.PageResp;

import java.util.Date;
import java.util.List;

public interface DailyTrainStationService {

    void save(DailyTrainStationSaveReq req);

    PageResp<DailyTrainStationQueryResp> queryList(DailyTrainStationQueryReq req);

    void delete(Long id);

    void genDaily(Date date, String trainCode);

    long countByTrainCode(Date date, String trainCode);

    List<DailyTrainStationQueryResp> queryByTrain(Date date, String trainCode);

}
