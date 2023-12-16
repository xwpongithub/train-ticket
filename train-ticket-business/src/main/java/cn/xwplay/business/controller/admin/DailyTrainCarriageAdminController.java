package cn.xwplay.business.controller.admin;

import cn.xwplay.business.req.DailyTrainCarriageQueryReq;
import cn.xwplay.business.req.DailyTrainCarriageSaveReq;
import cn.xwplay.business.resp.DailyTrainCarriageQueryResp;
import cn.xwplay.business.service.DailyTrainCarriageService;
import cn.xwplay.common.response.CommonResp;
import cn.xwplay.common.response.PageResp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("admin/daily-train-carriage")
public class DailyTrainCarriageAdminController {

    private final DailyTrainCarriageService dailyTrainCarriageService;

    @PostMapping("save")
    public CommonResp<Object> save(@Valid @RequestBody DailyTrainCarriageSaveReq req) {
        dailyTrainCarriageService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("query-list")
    public CommonResp<PageResp<DailyTrainCarriageQueryResp>> queryList(@Valid DailyTrainCarriageQueryReq req) {
        var list = dailyTrainCarriageService.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        dailyTrainCarriageService.delete(id);
        return new CommonResp<>();
    }

}
