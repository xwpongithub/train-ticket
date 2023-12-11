package cn.xwplay.business.controller.admin;

import cn.xwplay.business.service.StationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import cn.xwplay.common.response.PageResp;
import cn.xwplay.common.response.CommonResp;
import cn.xwplay.business.resp.StationQueryResp;
import cn.xwplay.business.req.StationSaveReq;
import cn.xwplay.business.req.StationQueryReq;

import java.util.List;
@RestController
@RequestMapping("admin/station")
@RequiredArgsConstructor
public class StationAdminController {

    private final StationService stationService;

    @PostMapping("save")
    public CommonResp<Object> save(@Valid @RequestBody StationSaveReq req) {
        stationService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("query-list")
    public CommonResp<PageResp<StationQueryResp>> queryList(@Valid StationQueryReq req) {
        var list = stationService.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        stationService.delete(id);
        return new CommonResp<>();
    }

    @GetMapping("query-all")
    public CommonResp<List<StationQueryResp>> queryList() {
        var list = stationService.queryAll();
        return new CommonResp<>(list);
    }

}
