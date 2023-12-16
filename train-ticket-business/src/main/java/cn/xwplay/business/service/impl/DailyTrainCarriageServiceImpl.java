package cn.xwplay.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.xwplay.business.domain.DailyTrainCarriageEntity;
import cn.xwplay.business.enums.SeatColEnum;
import cn.xwplay.business.mapper.DailyTrainCarriageMapper;
import cn.xwplay.business.req.DailyTrainCarriageSaveReq;
import cn.xwplay.business.service.DailyTrainCarriageService;
import cn.xwplay.business.service.TrainCarriageService;
import cn.xwplay.common.response.PageResp;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.xwplay.business.req.DailyTrainCarriageQueryReq;
import cn.xwplay.business.resp.DailyTrainCarriageQueryResp;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class DailyTrainCarriageServiceImpl implements DailyTrainCarriageService {

    private final DailyTrainCarriageMapper dailyTrainCarriageMapper;

    private final TrainCarriageService trainCarriageService;

    @Override
    public void save(DailyTrainCarriageSaveReq req) {
        var now = DateUtil.date();
        // 自动计算出列数和总座位数
        var seatColEnums = SeatColEnum.getColsByType(req.getSeatType());
        req.setColCount(seatColEnums.size());
        req.setSeatCount(req.getColCount() * req.getRowCount());

        var dailyTrainCarriage = BeanUtil.copyProperties(req, DailyTrainCarriageEntity.class);
        if (Objects.isNull(dailyTrainCarriage.getId())) {
            dailyTrainCarriage.setCreateTime(now);
            dailyTrainCarriage.setUpdateTime(now);
            dailyTrainCarriageMapper.insert(dailyTrainCarriage);
        } else {
            dailyTrainCarriage.setUpdateTime(now);
            dailyTrainCarriageMapper.updateById(dailyTrainCarriage);
        }
    }

    @Override
    public PageResp<DailyTrainCarriageQueryResp> queryList(DailyTrainCarriageQueryReq req) {
        var q = Wrappers.<DailyTrainCarriageEntity>lambdaQuery();
        q.orderByDesc(DailyTrainCarriageEntity::getDate)
                        .orderByAsc(DailyTrainCarriageEntity::getTrainCode,DailyTrainCarriageEntity::getIndex)
                .eq(Objects.nonNull(req.getDate()),DailyTrainCarriageEntity::getDate,req.getDate())
                .eq(StrUtil.isNotBlank(req.getTrainCode()),DailyTrainCarriageEntity::getTrainCode,req.getTrainCode());
        log.info("查询页码：{}", req.getPage());
        log.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        var dailyTrainCarriageList = dailyTrainCarriageMapper.selectList(q);

        var pageInfo = new PageInfo<>(dailyTrainCarriageList);
        log.info("总行数：{}", pageInfo.getTotal());
        log.info("总页数：{}", pageInfo.getPages());

        var list = BeanUtil.copyToList(dailyTrainCarriageList, DailyTrainCarriageQueryResp.class);

        var pageResp = new PageResp<DailyTrainCarriageQueryResp>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    @Override
    public void delete(Long id) {
        dailyTrainCarriageMapper.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void genDaily(Date date, String trainCode) {
        log.info("生成日期【{}】车次【{}】的车厢信息开始", DateUtil.formatDate(date), trainCode);

        // 删除某日某车次的车厢信息
        var delQ = Wrappers.<DailyTrainCarriageEntity>lambdaQuery();
        delQ.eq(DailyTrainCarriageEntity::getDate,date)
                .eq(DailyTrainCarriageEntity::getTrainCode,trainCode);
        dailyTrainCarriageMapper.delete(delQ);

        // 查出某车次的所有的车厢信息
        var carriageList = trainCarriageService.selectByTrainCode(trainCode);
        if (CollUtil.isEmpty(carriageList)) {
            log.info("该车次没有车厢基础数据，生成该车次的车厢信息结束");
            return;
        }

        for (var trainCarriage : carriageList) {
            var now = DateUtil.date();
            var dailyTrainCarriage = BeanUtil.copyProperties(trainCarriage, DailyTrainCarriageEntity.class);
            dailyTrainCarriage.setCreateTime(now);
            dailyTrainCarriage.setUpdateTime(now);
            dailyTrainCarriage.setDate(date);
            dailyTrainCarriageMapper.insert(dailyTrainCarriage);
        }
        log.info("生成日期【{}】车次【{}】的车厢信息结束", DateUtil.formatDate(date), trainCode);
    }

    @Override
    public List<DailyTrainCarriageEntity> selectBySeatType(Date date, String trainCode, String seatType) {
        var q = Wrappers.<DailyTrainCarriageEntity>lambdaQuery();
        q.eq(DailyTrainCarriageEntity::getDate,date)
                .eq(DailyTrainCarriageEntity::getTrainCode,trainCode)
                .eq(DailyTrainCarriageEntity::getSeatType,seatType);
        return dailyTrainCarriageMapper.selectList(q);
    }

}
