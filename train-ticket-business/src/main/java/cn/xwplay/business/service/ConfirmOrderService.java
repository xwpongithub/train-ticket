package cn.xwplay.business.service;

import cn.xwplay.business.req.ConfirmOrderDoReq;
import cn.xwplay.business.req.ConfirmOrderQueryReq;
import cn.xwplay.business.resp.ConfirmOrderQueryResp;
import cn.xwplay.common.response.PageResp;

public interface ConfirmOrderService {

    void save(ConfirmOrderDoReq req);

    PageResp<ConfirmOrderQueryResp> queryList(ConfirmOrderQueryReq req);

    void delete(Long id);

    void doConfirm(ConfirmOrderDoReq req);
}
