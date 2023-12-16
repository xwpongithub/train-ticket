package cn.xwplay.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.xwplay.business.domain.TrainSeatEntity;
import cn.xwplay.business.enums.SeatColEnum;
import cn.xwplay.business.mapper.TrainSeatMapper;
import cn.xwplay.business.req.TrainSeatQueryReq;
import cn.xwplay.business.req.TrainSeatSaveReq;
import cn.xwplay.business.resp.TrainSeatQueryResp;
import cn.xwplay.business.service.TrainCarriageService;
import cn.xwplay.business.service.TrainSeatService;
import cn.xwplay.common.response.PageResp;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrainSeatServiceImpl implements TrainSeatService {

    private final TrainSeatMapper trainSeatMapper;
    private final TrainCarriageService trainCarriageService;

    @Override
    public void save(TrainSeatSaveReq req) {
        var now = DateUtil.date();
        var trainSeat = BeanUtil.copyProperties(req, TrainSeatEntity.class);
        if (Objects.isNull(trainSeat.getId())) {
            trainSeat.setCreateTime(now);
            trainSeat.setUpdateTime(now);
            trainSeatMapper.insert(trainSeat);
        } else {
            trainSeat.setUpdateTime(now);
            trainSeatMapper.updateById(trainSeat);
        }
    }

    @Override
    public PageResp<TrainSeatQueryResp> queryList(TrainSeatQueryReq req) {
        var q = Wrappers.<TrainSeatEntity>lambdaQuery();
        q.eq(StrUtil.isNotBlank(req.getTrainCode()),TrainSeatEntity::getTrainCode,req.getTrainCode())
                .orderByAsc(
                        TrainSeatEntity::getTrainCode,
                        TrainSeatEntity::getCarriageIndex,
                        TrainSeatEntity::getCarriageSeatIndex
                );

        log.info("查询页码：{}", req.getPage());
        log.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        var trainSeatList = trainSeatMapper.selectList(q);

        var pageInfo = new PageInfo<>(trainSeatList);
        log.info("总行数：{}", pageInfo.getTotal());
        log.info("总页数：{}", pageInfo.getPages());
        var list = BeanUtil.copyToList(trainSeatList, TrainSeatQueryResp.class);

        var pageResp = new PageResp<TrainSeatQueryResp>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    @Override
    public void delete(Long id) {
        trainSeatMapper.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void genTrainSeat(String trainCode) {
        var now = DateUtil.date();
        // 清空当前车次下的所有的座位记录
        var q = Wrappers.<TrainSeatEntity>lambdaQuery();
        q.eq(TrainSeatEntity::getTrainCode,trainCode);
        trainSeatMapper.delete(q);

        // 查找当前车次下的所有的车厢
        var carriageList = trainCarriageService.selectByTrainCode(trainCode);
        log.info("当前车次下的车厢数：{}", carriageList.size());

        // 循环生成每个车厢的座位
        for (var trainCarriage : carriageList) {
            // 拿到车厢数据：行数、座位类型(得到列数)
            var rowCount = trainCarriage.getRowCount();
            var seatType = trainCarriage.getSeatType();
            int seatIndex = 1;
            // 根据车厢的座位类型，筛选出所有的列，比如车箱类型是一等座，则筛选出columnList={ACDF}
            var colEnumList = SeatColEnum.getColsByType(seatType);
            log.info("根据车厢的座位类型，筛选出所有的列：{}", colEnumList);
            // 循环行数
            for (var row = 1; row <= rowCount; row++) {
                // 循环列数
                for (var seatColEnum : colEnumList) {
                    // 构造座位数据并保存数据库
                    var trainSeat = new TrainSeatEntity();
                    trainSeat.setTrainCode(trainCode);
                    trainSeat.setCarriageIndex(trainCarriage.getIndex());
                    trainSeat.setRow(StrUtil.fillBefore(String.valueOf(row), '0', 2));
                    trainSeat.setCol(seatColEnum.getCode());
                    trainSeat.setSeatType(seatType);
                    trainSeat.setCarriageSeatIndex(seatIndex++);
                    trainSeat.setCreateTime(now);
                    trainSeat.setUpdateTime(now);
                    trainSeatMapper.insert(trainSeat);
                }
            }
        }
    }

    @Override
    public List<TrainSeatEntity> selectByTrainCode(String trainCode) {
        var q = Wrappers.<TrainSeatEntity>lambdaQuery();
        q.orderByAsc(TrainSeatEntity::getId)
                .eq(TrainSeatEntity::getTrainCode,trainCode);
        return trainSeatMapper.selectList(q);
    }

}
