package cn.xwplay.business.controller;

import cn.xwplay.business.service.StationService;
import cn.xwplay.common.response.CommonResp;
import cn.xwplay.business.resp.StationQueryResp;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("station")
public class StationController {

    private final StationService stationService;

    @GetMapping("query-all")
    public CommonResp<List<StationQueryResp>> queryList() {
        var list = stationService.queryAll();
        return new CommonResp<>(list);
    }

}
