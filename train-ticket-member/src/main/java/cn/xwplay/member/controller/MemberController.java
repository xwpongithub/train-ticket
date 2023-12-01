package cn.xwplay.member.controller;

import cn.xwplay.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
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
    public Long register(String mobile) {
        return memberService.register(mobile);
    }

}
