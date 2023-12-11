package cn.xwplay.business.controller;

import cn.xwplay.business.resp.TrainQueryResp;
import cn.xwplay.business.service.TrainService;
import cn.xwplay.common.response.CommonResp;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/train")
@RequiredArgsConstructor
public class TrainController {

    private final TrainService trainService;

    @GetMapping("query-all")
    public CommonResp<List<TrainQueryResp>> queryList() {
        var list = trainService.queryAll();
        return new CommonResp<>(list);
    }

}
