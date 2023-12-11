package cn.xwplay.business.service;

import cn.xwplay.business.domain.TrainStationEntity;
import cn.xwplay.business.req.TrainStationQueryReq;
import cn.xwplay.business.req.TrainStationSaveReq;
import cn.xwplay.business.resp.TrainStationQueryResp;
import cn.xwplay.common.response.PageResp;

import java.util.List;

public interface TrainStationService {

    void save(TrainStationSaveReq req);
    void delete(Long id);
    List<TrainStationEntity> selectByTrainCode(String trainCode);

    PageResp<TrainStationQueryResp> queryList(TrainStationQueryReq req);

}
