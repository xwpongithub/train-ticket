package cn.xwplay.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.xwplay.business.domain.DailyTrainEntity;
import cn.xwplay.business.mapper.DailyTrainMapper;
import cn.xwplay.business.req.DailyTrainQueryReq;
import cn.xwplay.business.req.DailyTrainSaveReq;
import cn.xwplay.business.resp.DailyTrainQueryResp;
import cn.xwplay.business.service.DailyTrainService;
import cn.xwplay.common.response.PageResp;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@RequiredArgsConstructor
@Service
@Slf4j
public class DailyTrainServiceImpl implements DailyTrainService {

    private final DailyTrainMapper dailyTrainMapper;

    @Override
    public void save(DailyTrainSaveReq req) {
        var now = DateUtil.date();
        var dailyTrain = BeanUtil.copyProperties(req, DailyTrainEntity.class);
        if (Objects.isNull(dailyTrain.getId())) {
            dailyTrain.setCreateTime(now);
            dailyTrain.setUpdateTime(now);
            dailyTrainMapper.insert(dailyTrain);
        } else {
            dailyTrain.setUpdateTime(now);
            dailyTrainMapper.updateById(dailyTrain);
        }
    }

    @Override
    public PageResp<DailyTrainQueryResp> queryList(DailyTrainQueryReq req) {
        var q = new LambdaQueryWrapper<DailyTrainEntity>();
        q
                .select(
                        DailyTrainEntity::getId,
                        DailyTrainEntity::getCode,
                        DailyTrainEntity::getType,
                        DailyTrainEntity::getStart,
                        DailyTrainEntity::getStartPinyin,
                        DailyTrainEntity::getStartTime,
                        DailyTrainEntity::getEnd,
                        DailyTrainEntity::getEndPinyin,
                        DailyTrainEntity::getEndTime,
                        DailyTrainEntity::getCreateTime,
                        DailyTrainEntity::getUpdateTime
                )
                .eq(Objects.nonNull(req.getDate()),DailyTrainEntity::getDate,req.getDate())
                .eq(Objects.nonNull(req.getCode()),DailyTrainEntity::getCode,req.getCode())
                .orderByDesc(DailyTrainEntity::getDate)
                        .orderByAsc(DailyTrainEntity::getCode);

        log.info("查询页码：{}", req.getPage());
        log.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        var dailyTrainList = dailyTrainMapper.selectList(q);

        var pageInfo = new PageInfo<>(dailyTrainList);
        log.info("总行数：{}", pageInfo.getTotal());
        log.info("总页数：{}", pageInfo.getPages());

        var list = BeanUtil.copyToList(dailyTrainList, DailyTrainQueryResp.class);

        var pageResp = new PageResp<DailyTrainQueryResp>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    @Override
    public void delete(Long id) {
        dailyTrainMapper.deleteById(id);
    }

}
