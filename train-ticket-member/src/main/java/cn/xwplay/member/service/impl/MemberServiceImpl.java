package cn.xwplay.member.service.impl;

import cn.xwplay.common.exception.BusinessException;
import cn.xwplay.common.exception.BusinessExceptionEnum;
import cn.xwplay.common.response.MemberLoginResp;
import cn.xwplay.common.util.JwtUtil;
import cn.xwplay.member.domain.MemberEntity;
import cn.xwplay.member.mapper.MemberMapper;
import cn.xwplay.member.req.MemberLoginReq;
import cn.xwplay.member.req.MemberRegisterReq;
import cn.xwplay.member.req.MemberSendCodeReq;
import cn.xwplay.member.service.MemberService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;

    @Override
    public Long register(MemberRegisterReq req) {
        var mobile = req.getMobile();
        var mobileExistsQ = Wrappers.<MemberEntity>lambdaQuery();
        mobileExistsQ.eq(MemberEntity::getMobile,mobile);
        var mobileExists = memberMapper.exists(mobileExistsQ);
        if (mobileExists) {
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_EXIST);
        }
        var newMember = new MemberEntity();
        newMember.setMobile(mobile);
        memberMapper.insert(newMember);
        return newMember.getId();
    }

    @Override
    public void sendCode(MemberSendCodeReq req) {
        var mobile = req.getMobile();
        var mobileExistsQ = Wrappers.<MemberEntity>lambdaQuery();
        mobileExistsQ.eq(MemberEntity::getMobile,mobile);
        var mobileExists = memberMapper.exists(mobileExistsQ);
        if (!mobileExists) {
           // 手机号未注册则直接注册
            var newMember = new MemberEntity();
            newMember.setMobile(mobile);
            memberMapper.insert(newMember);
        }
        // 生成验证码
//        var code = RandomUtil.randomNum4();
        var code = "8888";
        log.info("生成短信验证码:{}",code);
        // 保存短信记录表 手机号，短信验证码，有效期，是否已使用，业务类型，发送时间，使用时间

        // 对接短信通道，发送短信
    }

    @Override
    public MemberLoginResp login(MemberLoginReq req) {
        var mobile = req.getMobile();
        var code = req.getCode();
        var mobileExistsQ = Wrappers.<MemberEntity>lambdaQuery();
        mobileExistsQ.eq(MemberEntity::getMobile,mobile);
        var mobileExists = memberMapper.selectOne(mobileExistsQ);
        if (Objects.isNull(mobileExists)) {
          throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_NOT_EXIST);
        }
        // 校验短信验证码
        if (!"8888".equals(code)) {
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_CODE_ERROR);
        }
        var memberLoginResp = new MemberLoginResp();
        BeanUtils.copyProperties(req,memberLoginResp);

        var token = JwtUtil.createToken(mobileExists.getId(),mobile);
        memberLoginResp.setToken(token);
        memberLoginResp.setId(mobileExists.getId());
        return memberLoginResp;
    }

}
