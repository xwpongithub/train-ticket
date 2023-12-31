package cn.xwplay.member.controller.admin;

import cn.xwplay.common.response.CommonResp;
import cn.xwplay.common.response.PageResp;
import cn.xwplay.member.req.TicketQueryReq;
import cn.xwplay.member.resp.TicketQueryResp;
import cn.xwplay.member.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/ticket")
@RequiredArgsConstructor
public class TicketAdminController {

    private final TicketService ticketService;

    @GetMapping("/query-list")
    public CommonResp<PageResp<TicketQueryResp>> queryList(@Valid TicketQueryReq req) {
        var list = ticketService.queryList(req);
        return new CommonResp<>(list);
    }

}
