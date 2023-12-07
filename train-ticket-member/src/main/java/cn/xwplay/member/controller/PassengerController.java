package cn.xwplay.member.controller;

import cn.xwplay.common.response.CommonResp;
import cn.xwplay.member.req.PassengerSaveReq;
import cn.xwplay.member.service.PassengerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author X02413
 * @date 2023/12/01 22:56
 * <p>
 * 更新日      更新者        更新内容
 * update   2023/12/01    X02413        TODO
 **/
@RestController
@RequiredArgsConstructor
@RequestMapping("passenger")
public class PassengerController {

    private final PassengerService passengerService;

    @PostMapping("save")
    public CommonResp<Void> save(@Valid @RequestBody PassengerSaveReq req) {
        passengerService.save(req);
        return new CommonResp<>();
    }

}
