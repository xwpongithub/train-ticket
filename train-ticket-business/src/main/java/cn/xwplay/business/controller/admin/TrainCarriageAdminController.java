package cn.xwplay.business.controller.admin;

import cn.xwplay.business.req.TrainCarriageQueryReq;
import cn.xwplay.business.req.TrainCarriageSaveReq;
import cn.xwplay.business.resp.TrainCarriageQueryResp;
import cn.xwplay.common.response.CommonResp;
import cn.xwplay.common.response.PageResp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import cn.xwplay.business.service.TrainCarriageService;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin/train-carriage")
public class TrainCarriageAdminController {

    private final TrainCarriageService trainCarriageService;

    @PostMapping("save")
    public CommonResp<Object> save(@Valid @RequestBody TrainCarriageSaveReq req) {
        trainCarriageService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("query-list")
    public CommonResp<PageResp<TrainCarriageQueryResp>> queryList(@Valid TrainCarriageQueryReq req) {
        PageResp<TrainCarriageQueryResp> list = trainCarriageService.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        trainCarriageService.delete(id);
        return new CommonResp<>();
    }

}
