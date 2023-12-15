package cn.xwplay.business.controller.admin;

import cn.xwplay.business.req.DailyTrainStationQueryReq;
import cn.xwplay.business.req.DailyTrainStationSaveReq;
import cn.xwplay.business.resp.DailyTrainStationQueryResp;
import cn.xwplay.business.service.DailyTrainStationService;
import cn.xwplay.common.response.CommonResp;
import cn.xwplay.common.response.PageResp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/daily-train-station")
@RequiredArgsConstructor
public class DailyTrainStationAdminController {

    private final DailyTrainStationService dailyTrainStationService;

    @PostMapping("save")
    public CommonResp<Object> save(@Valid @RequestBody DailyTrainStationSaveReq req) {
        dailyTrainStationService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("query-list")
    public CommonResp<PageResp<DailyTrainStationQueryResp>> queryList(@Valid DailyTrainStationQueryReq req) {
        PageResp<DailyTrainStationQueryResp> list = dailyTrainStationService.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        dailyTrainStationService.delete(id);
        return new CommonResp<>();
    }

}
