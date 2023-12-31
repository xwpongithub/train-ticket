package cn.xwplay.member.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.xwplay.common.response.PageResp;
import cn.xwplay.member.domain.TicketEntity;
import cn.xwplay.member.mapper.TicketMapper;
import cn.xwplay.member.req.MemberTicketReq;
import cn.xwplay.member.req.TicketQueryReq;
import cn.xwplay.member.resp.TicketQueryResp;
import cn.xwplay.member.service.TicketService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketServiceImpl implements TicketService {

    private final TicketMapper ticketMapper;


    /**
     * 会员购买车票后新增保存
     *
     * @param req
     */
    @Override
    public void save(MemberTicketReq req) {
        // log.info("seata全局事务ID save: {}", RootContext.getXID());
        var now = DateUtil.date();
        var ticket = BeanUtil.copyProperties(req, TicketEntity.class);
        ticket.setCreateTime(now);
        ticket.setUpdateTime(now);
        ticketMapper.insert(ticket);
        // 模拟被调用方出现异常
        // if (1 == 1) {
        //     throw new Exception("测试异常11");
        // }
    }

    @Override
    public PageResp<TicketQueryResp> queryList(TicketQueryReq req) {
        var q = Wrappers.<TicketEntity>lambdaQuery();
        q.orderByDesc(TicketEntity::getId)
                .eq(Objects.nonNull(req.getMemberId()),TicketEntity::getMemberId, req.getMemberId());
        log.info("查询页码：{}", req.getPage());
        log.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        var ticketList = ticketMapper.selectList(q);
        var pageInfo = new PageInfo<>(ticketList);
        log.info("总行数：{}", pageInfo.getTotal());
        log.info("总页数：{}", pageInfo.getPages());

        var list = BeanUtil.copyToList(ticketList, TicketQueryResp.class);

        PageResp<TicketQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    @Override
    public void delete(Long id) {
        ticketMapper.deleteById(id);
    }

}
