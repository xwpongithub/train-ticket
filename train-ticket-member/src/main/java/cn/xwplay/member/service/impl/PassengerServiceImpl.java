package cn.xwplay.member.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.xwplay.common.context.LoginMemberContext;
import cn.xwplay.common.response.PageResp;
import cn.xwplay.common.response.PassengerQueryResp;
import cn.xwplay.member.domain.PassengerEntity;
import cn.xwplay.member.mapper.PassengerMapper;
import cn.xwplay.member.req.PassengerQueryReq;
import cn.xwplay.member.req.PassengerSaveReq;
import cn.xwplay.member.service.PassengerService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {

    private final PassengerMapper passengerMapper;

    @Override
    public void save(PassengerSaveReq req) {
        var now = DateUtil.date();
        var passenger = BeanUtil.copyProperties(req, PassengerEntity.class);
        passenger.setMemberId(LoginMemberContext.getId());
        var id = req.getId();
        if (Objects.isNull(id)) {
            passenger.setCreateTime(now);
            passenger.setUpdateTime(now);
            passengerMapper.insert(passenger);
        } else {
            passenger.setUpdateTime(now);
            passengerMapper.updateById(passenger);
        }
    }

    @Override
    public PageResp<PassengerQueryResp> queryList(PassengerQueryReq req) {
        var q = Wrappers.<PassengerEntity>lambdaQuery();
        var memberId = req.getMemberId();
        q.eq(Objects.nonNull(memberId),PassengerEntity::getMemberId,memberId)
                .orderByAsc(PassengerEntity::getId);
            PageHelper.startPage(req.getPage(),req.getSize());
            var passengerList = passengerMapper.selectList(q);
            var pageInfo = new PageInfo<>(passengerList);
            var resp = new PageResp<PassengerQueryResp>();
            var list = BeanUtil.copyToList(passengerList, PassengerQueryResp.class);
            resp.setTotal(pageInfo.getTotal());
            resp.setList(list);
            return resp;
    }

    @Override
    public void delete(Long id) {
        passengerMapper.deleteById(id);
    }
}
