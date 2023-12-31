package cn.xwplay.member.service;

import cn.xwplay.common.response.PageResp;
import cn.xwplay.member.req.MemberTicketReq;
import cn.xwplay.member.req.TicketQueryReq;
import cn.xwplay.member.resp.TicketQueryResp;

public interface TicketService {

    void save(MemberTicketReq req);

    PageResp<TicketQueryResp> queryList(TicketQueryReq req);

    void delete(Long id);

}
