package cn.xwplay.business.controller.admin;

import cn.xwplay.business.req.TrainStationQueryReq;
import cn.xwplay.business.req.TrainStationSaveReq;
import cn.xwplay.business.resp.TrainStationQueryResp;
import cn.xwplay.business.service.TrainStationService;
import cn.xwplay.common.response.CommonResp;
import cn.xwplay.common.response.PageResp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/train-station")
@RequiredArgsConstructor
public class TrainStationAdminController {

    private final TrainStationService trainStationService;

    @PostMapping("save")
    public CommonResp<Object> save(@Valid @RequestBody TrainStationSaveReq req) {
        trainStationService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("query-list")
    public CommonResp<PageResp<TrainStationQueryResp>> queryList(@Valid TrainStationQueryReq req) {
        PageResp<TrainStationQueryResp> list = trainStationService.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        trainStationService.delete(id);
        return new CommonResp<>();
    }

}
