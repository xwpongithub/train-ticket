package cn.xwplay.member.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.xwplay.common.context.LoginMemberContext;
import cn.xwplay.member.domain.PassengerEntity;
import cn.xwplay.member.mapper.PassengerMapper;
import cn.xwplay.member.req.PassengerSaveReq;
import cn.xwplay.member.service.PassengerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {

    private final PassengerMapper passengerMapper;

    @Override
    public void save(PassengerSaveReq req) {
        var now = DateUtil.date();
        var passenger = BeanUtil.copyProperties(req, PassengerEntity.class);
        passenger.setCreateTime(now);
        passenger.setUpdateTime(now);
        passenger.setMemberId(LoginMemberContext.getId());
        passengerMapper.insert(passenger);
    }

}
