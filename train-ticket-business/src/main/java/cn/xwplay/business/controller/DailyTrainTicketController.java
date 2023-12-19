package cn.xwplay.business.controller;

import cn.xwplay.business.req.DailyTrainTicketQueryReq;
import cn.xwplay.business.resp.DailyTrainTicketQueryResp;
import cn.xwplay.business.service.DailyTrainTicketService;
import cn.xwplay.common.response.CommonResp;
import cn.xwplay.common.response.PageResp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/daily-train-ticket")
@RequiredArgsConstructor
public class DailyTrainTicketController {

    private final DailyTrainTicketService dailyTrainTicketService;

    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainTicketQueryResp>> queryList(@Valid DailyTrainTicketQueryReq req) {
        var list = dailyTrainTicketService.queryList(req);
        return new CommonResp<>(list);
    }

}
