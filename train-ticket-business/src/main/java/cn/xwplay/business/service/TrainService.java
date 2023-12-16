package cn.xwplay.business.service;

import cn.xwplay.business.domain.TrainEntity;
import cn.xwplay.business.req.TrainQueryReq;
import cn.xwplay.business.req.TrainSaveReq;
import cn.xwplay.business.resp.TrainQueryResp;
import cn.xwplay.common.response.PageResp;

import java.util.List;

public interface TrainService {

    void save(TrainSaveReq req);

    PageResp<TrainQueryResp> queryList(TrainQueryReq req);

    void delete(Long id);

    List<TrainQueryResp> queryAll();

    List<TrainEntity> selectAll();

}
