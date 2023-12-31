package cn.xwplay.member.controller.feign;

import cn.xwplay.common.response.CommonResp;
import cn.xwplay.member.req.MemberTicketReq;
import cn.xwplay.member.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

;

@RestController
@RequestMapping("/feign/ticket")
@RequiredArgsConstructor
public class FeignTicketController {

    private final TicketService ticketService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody MemberTicketReq req) {
        ticketService.save(req);
        return new CommonResp<>();
    }

}
