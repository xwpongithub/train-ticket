package cn.xwplay.business.controller.admin;

import cn.xwplay.business.req.DailyTrainTicketQueryReq;
import cn.xwplay.business.req.DailyTrainTicketSaveReq;
import cn.xwplay.business.resp.DailyTrainTicketQueryResp;
import cn.xwplay.business.service.DailyTrainTicketService;
import cn.xwplay.common.response.CommonResp;
import cn.xwplay.common.response.PageResp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/daily-train-ticket")
public class DailyTrainTicketAdminController {

    private final DailyTrainTicketService dailyTrainTicketService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody DailyTrainTicketSaveReq req) {
        dailyTrainTicketService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainTicketQueryResp>> queryList(@Valid DailyTrainTicketQueryReq req) {
        PageResp<DailyTrainTicketQueryResp> list = dailyTrainTicketService.queryList(req);
        return new CommonResp<>(list);
    }

//    @GetMapping("/query-list2")
//    public CommonResp<PageResp<DailyTrainTicketQueryResp>> queryList2(@Valid DailyTrainTicketQueryReq req) {
//        PageResp<DailyTrainTicketQueryResp> list = dailyTrainTicketService.queryList2(req);
//        return new CommonResp<>(list);
//    }
//
//    @GetMapping("/query-list3")
//    public CommonResp<PageResp<DailyTrainTicketQueryResp>> queryList3(@Valid DailyTrainTicketQueryReq req) {
//        PageResp<DailyTrainTicketQueryResp> list = dailyTrainTicketService.queryList3(req);
//        return new CommonResp<>(list);
//    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        dailyTrainTicketService.delete(id);
        return new CommonResp<>();
    }

}
