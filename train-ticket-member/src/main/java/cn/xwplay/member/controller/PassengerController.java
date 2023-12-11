package cn.xwplay.member.controller;

import cn.xwplay.common.context.LoginMemberContext;
import cn.xwplay.common.response.CommonResp;
import cn.xwplay.common.response.PageResp;
import cn.xwplay.common.response.PassengerQueryResp;
import cn.xwplay.member.req.PassengerQueryReq;
import cn.xwplay.member.req.PassengerSaveReq;
import cn.xwplay.member.service.PassengerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

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

    @DeleteMapping("delete/{id}")
    public CommonResp<Void> delete(@PathVariable Long id) {
        passengerService.delete(id);
        return new CommonResp<>();
    }

    @GetMapping("query-list")
    public CommonResp<PageResp<PassengerQueryResp>> queryList(PassengerQueryReq req) {
        if (Objects.isNull(req.getMemberId())) {
            req.setMemberId(LoginMemberContext.getId());
        }
        var list = passengerService.queryList(req);
        return new CommonResp<>(list);
    }

}
