package cn.xwplay.member.controller;

import cn.xwplay.common.response.CommonResp;
import cn.xwplay.common.response.MemberLoginResp;
import cn.xwplay.member.req.MemberLoginReq;
import cn.xwplay.member.req.MemberRegisterReq;
import cn.xwplay.member.req.MemberSendCodeReq;
import cn.xwplay.member.service.MemberService;
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
@RequestMapping("member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("register")
    public CommonResp<Long> register(@RequestBody MemberRegisterReq req) {
        var resp = new CommonResp<Long>();
        resp.setContent(memberService.register(req));
        resp.setMessage("注册成功");
        return resp;
    }

    @PostMapping("send-code")
    public CommonResp<Void> sendCode(@Valid @RequestBody MemberSendCodeReq req) {
        memberService.sendCode(req);
        return new CommonResp<>();
    }

    @PostMapping("login")
    public CommonResp<MemberLoginResp> login(@Valid @RequestBody MemberLoginReq req) {
       var resp = memberService.login(req);
       return new CommonResp<>(resp);
    }

}
