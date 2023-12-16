package cn.xwplay.business.controller.admin;

import cn.xwplay.business.req.DailyTrainSeatQueryReq;
import cn.xwplay.business.req.DailyTrainSeatSaveReq;
import cn.xwplay.business.resp.DailyTrainSeatQueryResp;
import cn.xwplay.business.service.DailyTrainSeatService;
import cn.xwplay.common.response.CommonResp;
import cn.xwplay.common.response.PageResp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/daily-train-seat")
@Slf4j
@RequiredArgsConstructor
public class DailyTrainSeatAdminController {

    private final DailyTrainSeatService dailyTrainSeatService;

    @PostMapping("save")
    public CommonResp<Object> save(@Valid @RequestBody DailyTrainSeatSaveReq req) {
        dailyTrainSeatService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("query-list")
    public CommonResp<PageResp<DailyTrainSeatQueryResp>> queryList(@Valid DailyTrainSeatQueryReq req) {
        var list = dailyTrainSeatService.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        dailyTrainSeatService.delete(id);
        return new CommonResp<>();
    }

}
