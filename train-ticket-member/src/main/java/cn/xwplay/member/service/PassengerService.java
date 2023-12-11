package cn.xwplay.member.service;

import cn.xwplay.common.response.PageResp;
import cn.xwplay.common.response.PassengerQueryResp;
import cn.xwplay.member.req.PassengerQueryReq;
import cn.xwplay.member.req.PassengerSaveReq;

public interface PassengerService {

    void save(PassengerSaveReq req);

    PageResp<PassengerQueryResp> queryList(PassengerQueryReq req);

    void delete(Long id);
}
