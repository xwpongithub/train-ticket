package cn.xwplay.business.service;

import cn.xwplay.business.domain.TrainCarriageEntity;
import cn.xwplay.business.req.TrainCarriageQueryReq;
import cn.xwplay.business.req.TrainCarriageSaveReq;
import cn.xwplay.business.resp.TrainCarriageQueryResp;
import cn.xwplay.common.response.PageResp;

import java.util.List;

public interface TrainCarriageService {

    void save(TrainCarriageSaveReq req);
    PageResp<TrainCarriageQueryResp> queryList(TrainCarriageQueryReq req);

    void delete(Long id);

    List<TrainCarriageEntity> selectByTrainCode(String trainCode);

}
