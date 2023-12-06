package cn.xwplay.member.service;

import cn.xwplay.common.response.MemberLoginResp;
import cn.xwplay.member.req.MemberLoginReq;
import cn.xwplay.member.req.MemberRegisterReq;
import cn.xwplay.member.req.MemberSendCodeReq;

/**
 * @author X02413
 * @date 2023/12/01 22:27
 * <p>
 * 更新日      更新者        更新内容
 * update   2023/12/01    X02413        TODO
 **/
public interface MemberService {

    Long register(MemberRegisterReq req);

    void sendCode(MemberSendCodeReq req);

    MemberLoginResp login(MemberLoginReq req);
}
