package cn.xwplay.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.StrUtil;
import cn.xwplay.business.domain.DailyTrainEntity;
import cn.xwplay.business.domain.DailyTrainTicketEntity;
import cn.xwplay.business.enums.SeatTypeEnum;
import cn.xwplay.business.mapper.DailyTrainTicketMapper;
import cn.xwplay.business.req.DailyTrainTicketQueryReq;
import cn.xwplay.business.req.DailyTrainTicketSaveReq;
import cn.xwplay.business.resp.DailyTrainTicketQueryResp;
import cn.xwplay.business.service.DailyTrainSeatService;
import cn.xwplay.business.service.DailyTrainTicketService;
import cn.xwplay.business.service.TrainStationService;
import cn.xwplay.common.response.PageResp;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.xwplay.business.enums.TrainTypeEnum;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class DailyTrainTicketServiceImpl implements DailyTrainTicketService {

    private final DailyTrainTicketMapper dailyTrainTicketMapper;
    private final TrainStationService trainStationService;
    private final DailyTrainSeatService dailyTrainSeatService;

    @Override
    public void save(DailyTrainTicketSaveReq req) {
        var now = DateUtil.date();
        var dailyTrainTicket = BeanUtil.copyProperties(req, DailyTrainTicketEntity.class);
        if (Objects.isNull(dailyTrainTicket.getId())) {
            dailyTrainTicket.setCreateTime(now);
            dailyTrainTicket.setUpdateTime(now);
            dailyTrainTicketMapper.insert(dailyTrainTicket);
        } else {
            dailyTrainTicket.setUpdateTime(now);
            dailyTrainTicketMapper.updateById(dailyTrainTicket);
        }
    }

//    @Cacheable(value = "DailyTrainTicketService.queryList3")
//    public PageResp<DailyTrainTicketQueryResp> queryList3(DailyTrainTicketQueryReq req) {
//        log.info("测试缓存击穿");
//        return null;
//    }

//    @CachePut(value = "DailyTrainTicketService.queryList")
//    public PageResp<DailyTrainTicketQueryResp> queryList2(DailyTrainTicketQueryReq req) {
//        return queryList(req);
//    }

    // @Cacheable(value = "DailyTrainTicketService.queryList")
    @Override
    public PageResp<DailyTrainTicketQueryResp> queryList(DailyTrainTicketQueryReq req) {
        // 常见的缓存过期策略
        // TTL 超时时间
        // LRU 最近最少使用
        // LFU 最近最不经常使用
        // FIFO 先进先出
        // Random 随机淘汰策略
        // 去缓存里取数据，因数据库本身就没数据而造成缓存穿透
        // if (有数据) { null []
        //     return
        // } else {
        //     去数据库取数据
        // }
        var q = Wrappers.<DailyTrainTicketEntity>lambdaQuery();
        q.orderByDesc(DailyTrainTicketEntity::getDate)
                .orderByAsc(DailyTrainTicketEntity::getStartTime,DailyTrainTicketEntity::getTrainCode,DailyTrainTicketEntity::getStartIndex,DailyTrainTicketEntity::getEndIndex)
                .eq(Objects.nonNull(req.getDate()),DailyTrainTicketEntity::getDate,req.getDate())
                .eq(StrUtil.isNotBlank(req.getTrainCode()),DailyTrainTicketEntity::getDate,req.getTrainCode())
                .like(StrUtil.isNotBlank(req.getStart()),DailyTrainTicketEntity::getStart,req.getStart())
                .like(StrUtil.isNotBlank(req.getEnd()),DailyTrainTicketEntity::getEnd,req.getEnd())
        ;

        log.info("查询页码：{}", req.getPage());
        log.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        var dailyTrainTicketList = dailyTrainTicketMapper.selectList(q);

        var pageInfo = new PageInfo<>(dailyTrainTicketList);
        log.info("总行数：{}", pageInfo.getTotal());
        log.info("总页数：{}", pageInfo.getPages());

        var list = BeanUtil.copyToList(dailyTrainTicketList, DailyTrainTicketQueryResp.class);

        var pageResp = new PageResp<DailyTrainTicketQueryResp>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    @Override
    public void delete(Long id) {
        dailyTrainTicketMapper.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void genDaily(DailyTrainEntity dailyTrain, Date date, String trainCode) {
        log.info("生成日期【{}】车次【{}】的余票信息开始", DateUtil.formatDate(date), trainCode);

        // 查出某车次的所有的车站信息
        var stationList = trainStationService.selectByTrainCode(trainCode);
        if (CollUtil.isEmpty(stationList)) {
            log.info("该车次没有车站基础数据，生成该车次的余票信息结束");
            return;
        }

        var now = DateUtil.date();
        for (var i = 0; i < stationList.size(); i++) {
            // 得到出发站
            var trainStationStart = stationList.get(i);
            var sumKM = BigDecimal.ZERO;
            for (int j = (i + 1); j < stationList.size(); j++) {
                var trainStationEnd = stationList.get(j);
                sumKM = sumKM.add(trainStationEnd.getKm());
                var dailyTrainTicket = new DailyTrainTicketEntity();
                dailyTrainTicket.setDate(date);
                dailyTrainTicket.setTrainCode(trainCode);
                dailyTrainTicket.setStart(trainStationStart.getName());
                dailyTrainTicket.setStartPinyin(trainStationStart.getNamePinyin());
                dailyTrainTicket.setStartTime(trainStationStart.getOutTime());
                dailyTrainTicket.setStartIndex(trainStationStart.getIndex());
                dailyTrainTicket.setEnd(trainStationEnd.getName());
                dailyTrainTicket.setEndPinyin(trainStationEnd.getNamePinyin());
                dailyTrainTicket.setEndTime(trainStationEnd.getInTime());
                dailyTrainTicket.setEndIndex(trainStationEnd.getIndex());
                int ydz = dailyTrainSeatService.countSeat(date, trainCode, SeatTypeEnum.YDZ.getCode());
                int edz = dailyTrainSeatService.countSeat(date, trainCode, SeatTypeEnum.EDZ.getCode());
                int rw = dailyTrainSeatService.countSeat(date, trainCode, SeatTypeEnum.RW.getCode());
                int yw = dailyTrainSeatService.countSeat(date, trainCode, SeatTypeEnum.YW.getCode());
                // 票价 = 里程之和 * 座位单价 * 车次类型系数
                var trainType = dailyTrain.getType();
                // 计算票价系数：TrainTypeEnum.priceRate
                var priceRate = EnumUtil.getFieldBy(TrainTypeEnum::getPriceRate, TrainTypeEnum::getCode, trainType);
                var ydzPrice = sumKM.multiply(SeatTypeEnum.YDZ.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
                var edzPrice = sumKM.multiply(SeatTypeEnum.EDZ.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
                var rwPrice = sumKM.multiply(SeatTypeEnum.RW.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
                var ywPrice = sumKM.multiply(SeatTypeEnum.YW.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
                dailyTrainTicket.setYdz(ydz);
                dailyTrainTicket.setYdzPrice(ydzPrice);
                dailyTrainTicket.setEdz(edz);
                dailyTrainTicket.setEdzPrice(edzPrice);
                dailyTrainTicket.setRw(rw);
                dailyTrainTicket.setRwPrice(rwPrice);
                dailyTrainTicket.setYw(yw);
                dailyTrainTicket.setYwPrice(ywPrice);
                dailyTrainTicket.setCreateTime(now);
                dailyTrainTicket.setUpdateTime(now);
                dailyTrainTicketMapper.insert(dailyTrainTicket);
            }
        }
        log.info("生成日期【{}】车次【{}】的余票信息结束", DateUtil.formatDate(date), trainCode);
    }

    @Override
    public DailyTrainTicketEntity selectByUnique(Date date, String trainCode, String start, String end) {
        var q = Wrappers.<DailyTrainTicketEntity>lambdaQuery();
        q.eq(DailyTrainTicketEntity::getDate,date)
                .eq(DailyTrainTicketEntity::getTrainCode,trainCode)
                .eq(DailyTrainTicketEntity::getStart,start)
                .eq(DailyTrainTicketEntity::getEnd,end);
        return dailyTrainTicketMapper.selectOne(q);
    }

}
