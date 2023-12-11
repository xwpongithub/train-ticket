package cn.xwplay.business.service;

import cn.xwplay.business.req.TrainSeatQueryReq;
import cn.xwplay.business.req.TrainSeatSaveReq;
import cn.xwplay.business.resp.TrainSeatQueryResp;
import cn.xwplay.common.response.PageResp;

public interface TrainSeatService {

    void save(TrainSeatSaveReq req);

    PageResp<TrainSeatQueryResp> queryList(TrainSeatQueryReq req);

    void delete(Long id);

    void genTrainSeat(String trainCode);

}
