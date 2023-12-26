package cn.xwplay.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.xwplay.business.domain.DailyTrainSeatEntity;
import cn.xwplay.business.mapper.DailyTrainSeatMapper;
import cn.xwplay.business.req.DailyTrainSeatQueryReq;
import cn.xwplay.business.req.DailyTrainSeatSaveReq;
import cn.xwplay.business.req.SeatSellReq;
import cn.xwplay.business.resp.DailyTrainSeatQueryResp;
import cn.xwplay.business.resp.SeatSellResp;
import cn.xwplay.business.service.DailyTrainSeatService;
import cn.xwplay.business.service.TrainSeatService;
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
public class DailyTrainSeatServiceImpl implements DailyTrainSeatService {

    private final DailyTrainSeatMapper dailyTrainSeatMapper;

    private final TrainSeatService trainSeatService;
    private final TrainStationService trainStationService;

    @Override
    public void save(DailyTrainSeatSaveReq req) {
       var now = DateUtil.date();
       var dailyTrainSeat = BeanUtil.copyProperties(req, DailyTrainSeatEntity.class);
        if (Objects.isNull(dailyTrainSeat.getId())) {
            dailyTrainSeat.setCreateTime(now);
            dailyTrainSeat.setUpdateTime(now);
            dailyTrainSeatMapper.insert(dailyTrainSeat);
        } else {
            dailyTrainSeat.setUpdateTime(now);
            dailyTrainSeatMapper.updateById(dailyTrainSeat);
        }
    }

    @Override
    public PageResp<DailyTrainSeatQueryResp> queryList(DailyTrainSeatQueryReq req) {
        var q = Wrappers.<DailyTrainSeatEntity>lambdaQuery();
        q.orderByDesc(DailyTrainSeatEntity::getDate)
                        .orderByAsc(DailyTrainSeatEntity::getTrainCode,
                                DailyTrainSeatEntity::getCarriageIndex,
                                DailyTrainSeatEntity::getCarriageSeatIndex)
                .eq(StrUtil.isNotBlank(req.getTrainCode()),DailyTrainSeatEntity::getTrainCode,req.getTrainCode());

        log.info("查询页码：{}", req.getPage());
        log.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        var dailyTrainSeatList = dailyTrainSeatMapper.selectList(q);

        var pageInfo = new PageInfo<>(dailyTrainSeatList);
        log.info("总行数：{}", pageInfo.getTotal());
        log.info("总页数：{}", pageInfo.getPages());

        var list = BeanUtil.copyToList(dailyTrainSeatList, DailyTrainSeatQueryResp.class);

        PageResp<DailyTrainSeatQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    @Override
    public void delete(Long id) {
        dailyTrainSeatMapper.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void genDaily(Date date, String trainCode) {
        log.info("生成日期【{}】车次【{}】的座位信息开始", DateUtil.formatDate(date), trainCode);

        var stationList = trainStationService.selectByTrainCode(trainCode);
        String sell = StrUtil.fillBefore("", '0', stationList.size() - 1);

        // 查出某车次的所有的座位信息
        var seatList = trainSeatService.selectByTrainCode(trainCode);
        if (CollUtil.isEmpty(seatList)) {
            log.info("该车次没有座位基础数据，生成该车次的座位信息结束");
            return;
        }

        for (var trainSeat : seatList) {
            var now = DateUtil.date();
            var dailyTrainSeat = BeanUtil.copyProperties(trainSeat, DailyTrainSeatEntity.class);
            dailyTrainSeat.setCreateTime(now);
            dailyTrainSeat.setUpdateTime(now);
            dailyTrainSeat.setDate(date);
            dailyTrainSeat.setSell(sell);
            dailyTrainSeatMapper.insert(dailyTrainSeat);
        }
        log.info("生成日期【{}】车次【{}】的座位信息结束", DateUtil.formatDate(date), trainCode);
    }

    @Override
    public int countSeat(Date date, String trainCode) {
        return countSeat(date, trainCode, null);
    }

    @Override
    public int countSeat(Date date, String trainCode, String seatType) {
        var q = Wrappers.<DailyTrainSeatEntity>lambdaQuery();
        q.eq(DailyTrainSeatEntity::getDate,date)
                .eq(DailyTrainSeatEntity::getTrainCode,trainCode)
                .eq(StrUtil.isNotBlank(seatType),DailyTrainSeatEntity::getSeatType,seatType);
        long l = dailyTrainSeatMapper.selectCount(q);
        if (l == 0L) {
            return -1;
        }
        return (int) l;
    }

    @Override
    public List<DailyTrainSeatEntity> selectByCarriage(Date date, String trainCode, Integer carriageIndex) {
        var q = Wrappers.<DailyTrainSeatEntity>lambdaQuery();
        q.orderByAsc(DailyTrainSeatEntity::getCarriageSeatIndex)
                .eq(DailyTrainSeatEntity::getDate,date)
                .eq(DailyTrainSeatEntity::getTrainCode,trainCode)
                .eq(DailyTrainSeatEntity::getCarriageIndex,carriageIndex);
        return dailyTrainSeatMapper.selectList(q);
    }

    /**
     * 查询某日某车次的所有座位
     */
    @Override
    public List<SeatSellResp> querySeatSell(SeatSellReq req) {
        var date = req.getDate();
        var trainCode = req.getTrainCode();
        log.info("查询日期【{}】车次【{}】的座位销售信息", DateUtil.formatDate(date), trainCode);
        var q = Wrappers.<DailyTrainSeatEntity>lambdaQuery();
        q.orderByAsc(DailyTrainSeatEntity::getCarriageIndex,DailyTrainSeatEntity::getCarriageSeatIndex)
                .eq(DailyTrainSeatEntity::getDate,date)
                .eq(DailyTrainSeatEntity::getTrainCode,trainCode);
        return BeanUtil.copyToList(dailyTrainSeatMapper.selectList(q), SeatSellResp.class);
    }

}
