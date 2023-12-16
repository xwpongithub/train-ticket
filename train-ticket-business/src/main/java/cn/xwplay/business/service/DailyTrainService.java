package cn.xwplay.business.service;

import cn.xwplay.business.req.DailyTrainQueryReq;
import cn.xwplay.business.req.DailyTrainSaveReq;
import cn.xwplay.business.resp.DailyTrainQueryResp;
import cn.xwplay.common.response.PageResp;

import java.util.Date;

public interface DailyTrainService {

    void save(DailyTrainSaveReq req);

    PageResp<DailyTrainQueryResp> queryList(DailyTrainQueryReq req);

    void delete(Long id);

    void genDaily(Date date);

}
