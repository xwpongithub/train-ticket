package cn.xwplay.business.controller.admin;

import cn.xwplay.business.req.TrainQueryReq;
import cn.xwplay.business.req.TrainSaveReq;
import cn.xwplay.business.resp.TrainQueryResp;
import cn.xwplay.business.service.TrainService;
import cn.xwplay.common.response.CommonResp;
import cn.xwplay.common.response.PageResp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import cn.xwplay.business.service.TrainSeatService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin/train")
public class TrainAdminController {

    private final TrainService trainService;
    private final TrainSeatService trainSeatService;

    @PostMapping("save")
    public CommonResp<Object> save(@Valid @RequestBody TrainSaveReq req) {
        trainService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("query-list")
    public CommonResp<PageResp<TrainQueryResp>> queryList(@Valid TrainQueryReq req) {
        PageResp<TrainQueryResp> list = trainService.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        trainService.delete(id);
        return new CommonResp<>();
    }

    @GetMapping("query-all")
    public CommonResp<List<TrainQueryResp>> queryList() {
        List<TrainQueryResp> list = trainService.queryAll();
        return new CommonResp<>(list);
    }

    @GetMapping("gen-seat/{trainCode}")
    public CommonResp<Object> genSeat(@PathVariable String trainCode) {
        trainSeatService.genTrainSeat(trainCode);
        return new CommonResp<>();
    }

}
