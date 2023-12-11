package cn.xwplay.business.controller.admin;

import cn.xwplay.business.req.TrainSeatQueryReq;
import cn.xwplay.business.req.TrainSeatSaveReq;
import cn.xwplay.business.resp.TrainSeatQueryResp;
import cn.xwplay.business.service.TrainSeatService;
import cn.xwplay.common.response.CommonResp;
import cn.xwplay.common.response.PageResp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("admin/train-seat")
@RequiredArgsConstructor
public class TrainSeatAdminController {

    private final TrainSeatService trainSeatService;

    @PostMapping("save")
    public CommonResp<Object> save(@Valid @RequestBody TrainSeatSaveReq req) {
        trainSeatService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("query-list")
    public CommonResp<PageResp<TrainSeatQueryResp>> queryList(@Valid TrainSeatQueryReq req) {
        var list = trainSeatService.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        trainSeatService.delete(id);
        return new CommonResp<>();
    }

}
