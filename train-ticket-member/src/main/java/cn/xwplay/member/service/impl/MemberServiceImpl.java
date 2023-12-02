package cn.xwplay.member.service.impl;

import cn.xwplay.member.domain.MemberEntity;
import cn.xwplay.member.mapper.MemberMapper;
import cn.xwplay.member.req.MemberRegisterReq;
import cn.xwplay.member.service.MemberService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;

    public Long register(MemberRegisterReq req) {
        var mobile = req.getMobile();
        var mobileExistsQ = Wrappers.<MemberEntity>lambdaQuery();
        mobileExistsQ.eq(MemberEntity::getMobile,mobile);
        var mobileExists = memberMapper.exists(mobileExistsQ);
        if (mobileExists) {
            throw new RuntimeException("该手机号已被其他人使用");
        }
        var newMember = new MemberEntity();
        newMember.setMobile(mobile);
        memberMapper.insert(newMember);
        return newMember.getId();
    }

}
