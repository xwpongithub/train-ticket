package cn.xwplay.business.service;

import cn.xwplay.business.req.StationQueryReq;
import cn.xwplay.business.req.StationSaveReq;
import cn.xwplay.common.response.PageResp;
import cn.xwplay.business.resp.StationQueryResp;

import java.util.List;

public interface StationService {

    void save(StationSaveReq req);

    void delete(Long id);

    PageResp<StationQueryResp> queryList(StationQueryReq req);

    List<StationQueryResp> queryAll();

}
