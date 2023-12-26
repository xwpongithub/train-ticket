package cn.xwplay.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.xwplay.business.domain.*;
import cn.xwplay.business.mapper.*;
import cn.xwplay.business.req.DailyTrainQueryReq;
import cn.xwplay.business.req.DailyTrainSaveReq;
import cn.xwplay.business.resp.DailyTrainQueryResp;
import cn.xwplay.business.service.*;
import cn.xwplay.common.response.PageResp;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Objects;

@RequiredArgsConstructor
@Service
@Slf4j
public class DailyTrainServiceImpl implements DailyTrainService {

    private final DailyTrainMapper dailyTrainMapper;
    private final TrainService trainService;
    private final DailyTrainStationService dailyTrainStationService;
    private final DailyTrainCarriageService dailyTrainCarriageService;
    private final DailyTrainSeatService dailyTrainSeatService;
    private final DailyTrainTicketService dailyTrainTicketService;
    private final DailyTrainStationMapper dailyTrainStationMapper;
    private final DailyTrainCarriageMapper dailyTrainCarriageMapper;
    private final DailyTrainSeatMapper dailyTrainSeatMapper;
    private final DailyTrainTicketMapper dailyTrainTicketMapper;

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

    /**
     * 生成某日所有车次信息，包括车次、车站、车厢、座位
     * @param date
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void genDaily(Date date) {
        var trainList = trainService.selectAll();
        if (CollUtil.isEmpty(trainList)) {
            log.info("没有车次基础数据，任务结束");
            return;
        }

        trainList.forEach(train-> {
            // 删除该车次已有的数据
            var delQ = new LambdaQueryWrapper<DailyTrainEntity>();
            delQ
//                    .eq(DailyTrainEntity::getDate, date)
                    .eq(DailyTrainEntity::getCode, train.getCode());
            dailyTrainMapper.delete(delQ);
            // 删除某日某车次的车站信息
            var delStationQ = Wrappers.<DailyTrainStationEntity>lambdaQuery();
            delStationQ
//                    .eq(DailyTrainStationEntity::getDate,date)
                    .eq(DailyTrainStationEntity::getTrainCode,train.getCode());
            dailyTrainStationMapper.delete(delStationQ);
            // 删除某日某车次的车厢信息
            var delCarriageQ = Wrappers.<DailyTrainCarriageEntity>lambdaQuery();
            delCarriageQ
//                    .eq(DailyTrainCarriageEntity::getDate,date)
                    .eq(DailyTrainCarriageEntity::getTrainCode,train.getCode());
            dailyTrainCarriageMapper.delete(delCarriageQ);
            //  删除某日某车次的座位信息
            var delSeatQ = Wrappers.<DailyTrainSeatEntity>lambdaQuery();
            delSeatQ
//                    .eq(DailyTrainSeatEntity::getDate,date)
                    .eq(DailyTrainSeatEntity::getTrainCode,train.getCode());
            dailyTrainSeatMapper.delete(delSeatQ);
            // 删除某日某车次的余票信息
            var delTicketQ = Wrappers.<DailyTrainTicketEntity>lambdaQuery();
            delTicketQ
//                    .eq(DailyTrainTicketEntity::getDate,date)
                    .eq(DailyTrainTicketEntity::getTrainCode,train.getCode());
            dailyTrainTicketMapper.delete(delTicketQ);
        });

        var dateList = DateUtil.rangeToList(DateUtil.date(),date, DateField.DAY_OF_MONTH,1);
        dateList.forEach(everyday ->
            trainList.forEach(train-> genDailyTrain(everyday,train))
        );
    }

    private void genDailyTrain(Date date, TrainEntity train) {
        log.info("生成日期【{}】车次【{}】的信息开始", DateUtil.formatDate(date), train.getCode());
        // 生成该车次的数据
        var now = DateUtil.date();
        var dailyTrain = BeanUtil.copyProperties(train, DailyTrainEntity.class);
        dailyTrain.setCreateTime(now);
        dailyTrain.setUpdateTime(now);
        dailyTrain.setDate(date);
        dailyTrainMapper.insert(dailyTrain);

        // 生成该车次的车站数据
        dailyTrainStationService.genDaily(date, train.getCode());

        // 生成该车次的车厢数据
        dailyTrainCarriageService.genDaily(date, train.getCode());

        // 生成该车次的座位数据
        dailyTrainSeatService.genDaily(date, train.getCode());

        //   生成该车次的余票数据
        dailyTrainTicketService.genDaily(dailyTrain, date, train.getCode());

        // 生成令牌余量数据
//        skTokenService.genDaily(date, train.getCode());

        log.info("生成日期【{}】车次【{}】的信息结束", DateUtil.formatDate(date), train.getCode());
    }

}
