package cn.xwplay.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.xwplay.business.domain.DailyTrainStationEntity;
import cn.xwplay.business.mapper.DailyTrainStationMapper;
import cn.xwplay.business.req.DailyTrainStationQueryReq;
import cn.xwplay.business.req.DailyTrainStationSaveReq;
import cn.xwplay.business.resp.DailyTrainStationQueryResp;
import cn.xwplay.business.service.DailyTrainStationService;
import cn.xwplay.business.service.TrainStationService;
import cn.xwplay.common.response.PageResp;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class DailyTrainStationServiceImpl implements DailyTrainStationService {

    private final DailyTrainStationMapper dailyTrainStationMapper;
    private final TrainStationService trainStationService;

    @Override
    public void save(DailyTrainStationSaveReq req) {
        var now = DateUtil.date();
        var dailyTrainStation = BeanUtil.copyProperties(req, DailyTrainStationEntity.class);
        if (Objects.isNull(dailyTrainStation.getId())) {
            dailyTrainStation.setCreateTime(now);
            dailyTrainStation.setUpdateTime(now);
            dailyTrainStationMapper.insert(dailyTrainStation);
        } else {
            dailyTrainStation.setUpdateTime(now);
            dailyTrainStationMapper.updateById(dailyTrainStation);
        }
    }

    @Override
    public PageResp<DailyTrainStationQueryResp> queryList(DailyTrainStationQueryReq req) {
        var q = Wrappers.<DailyTrainStationEntity>lambdaQuery();
        q.orderByDesc(DailyTrainStationEntity::getDate)
                .orderByAsc(DailyTrainStationEntity::getTrainCode,DailyTrainStationEntity::getIndex)
                .eq(Objects.nonNull(req.getDate()),DailyTrainStationEntity::getDate,req.getDate())
                .eq(StrUtil.isNotBlank(req.getTrainCode()),DailyTrainStationEntity::getTrainCode,req.getTrainCode());
        log.info("查询页码：{}", req.getPage());
        log.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        var dailyTrainStationList = dailyTrainStationMapper.selectList(q);

        var pageInfo = new PageInfo<>(dailyTrainStationList);
        log.info("总行数：{}", pageInfo.getTotal());
        log.info("总页数：{}", pageInfo.getPages());

        var list = BeanUtil.copyToList(dailyTrainStationList, DailyTrainStationQueryResp.class);

        PageResp<DailyTrainStationQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }
    @Override
    public void delete(Long id) {
        dailyTrainStationMapper.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void genDaily(Date date, String trainCode) {
        log.info("生成日期【{}】车次【{}】的车站信息开始", DateUtil.formatDate(date), trainCode);

        // 查出某车次的所有的车站信息
        var stationList = trainStationService.selectByTrainCode(trainCode);
        if (CollUtil.isEmpty(stationList)) {
            log.info("该车次没有车站基础数据，生成该车次的车站信息结束");
            return;
        }

        for (var trainStation : stationList) {
            var now = DateUtil.date();
            var dailyTrainStation = BeanUtil.copyProperties(trainStation, DailyTrainStationEntity.class);
            dailyTrainStation.setCreateTime(now);
            dailyTrainStation.setUpdateTime(now);
            dailyTrainStation.setDate(date);
            dailyTrainStationMapper.insert(dailyTrainStation);
        }
        log.info("生成日期【{}】车次【{}】的车站信息结束", DateUtil.formatDate(date), trainCode);
    }

    /**
     * 按车次查询全部车站
     */
    @Override
    public long countByTrainCode(Date date, String trainCode) {
        var q = Wrappers.<DailyTrainStationEntity>lambdaQuery();
        q.eq(DailyTrainStationEntity::getDate,date)
                        .eq(DailyTrainStationEntity::getTrainCode,trainCode);
        return dailyTrainStationMapper.selectCount(q);
    }

    /**
     * 按车次日期查询车站列表，用于界面显示一列车经过的车站
     */
    @Override
    public List<DailyTrainStationQueryResp> queryByTrain(Date date, String trainCode) {
        var q = Wrappers.<DailyTrainStationEntity>lambdaQuery();
        q.orderByAsc(DailyTrainStationEntity::getIndex)
                .eq(DailyTrainStationEntity::getDate,date)
                .eq(DailyTrainStationEntity::getTrainCode,trainCode);
        var list = dailyTrainStationMapper.selectList(q);
        return BeanUtil.copyToList(list, DailyTrainStationQueryResp.class);
    }

}
