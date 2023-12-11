package cn.xwplay.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.xwplay.business.domain.TrainStationEntity;
import cn.xwplay.business.mapper.TrainStationMapper;
import cn.xwplay.business.req.TrainStationQueryReq;
import cn.xwplay.business.req.TrainStationSaveReq;
import cn.xwplay.business.resp.TrainStationQueryResp;
import cn.xwplay.business.service.TrainStationService;
import cn.xwplay.common.exception.BusinessException;
import cn.xwplay.common.exception.BusinessExceptionEnum;
import cn.xwplay.common.response.PageResp;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
@Service
public class TrainStationServiceImpl implements TrainStationService {

    private final TrainStationMapper trainStationMapper;

    @Override
    public void save(TrainStationSaveReq req) {
        var now = DateUtil.date();
        var trainStation = BeanUtil.copyProperties(req, TrainStationEntity.class);
        if (Objects.isNull(trainStation.getId())) {
            // 保存之前，先校验唯一键是否存在
            var trainStationDB = selectByUnique(req.getTrainCode(), req.getIndex());
            if (Objects.nonNull(trainStationDB)) {
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_STATION_INDEX_UNIQUE_ERROR);
            }
            // 保存之前，先校验唯一键是否存在
            trainStationDB = selectByUnique(req.getTrainCode(), req.getName());
            if (Objects.nonNull(trainStationDB)) {
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_STATION_NAME_UNIQUE_ERROR);
            }

            trainStation.setCreateTime(now);
            trainStation.setUpdateTime(now);
            trainStationMapper.insert(trainStation);
        } else {
            trainStation.setUpdateTime(now);
            trainStationMapper.updateById(trainStation);
        }
    }


    @Override
    public PageResp<TrainStationQueryResp> queryList(TrainStationQueryReq req) {
        var q = Wrappers.<TrainStationEntity>lambdaQuery();
        q.orderByAsc(TrainStationEntity::getTrainCode)
                .eq(StrUtil.isNotBlank(req.getTrainCode()),TrainStationEntity::getTrainCode,req.getTrainCode());
        log.info("查询页码：{}", req.getPage());
        log.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        var trainStationList = trainStationMapper.selectList(q);
        var pageInfo = new PageInfo<>(trainStationList);
        log.info("总行数：{}", pageInfo.getTotal());
        log.info("总页数：{}", pageInfo.getPages());

        var list = BeanUtil.copyToList(trainStationList, TrainStationQueryResp.class);
        var pageResp = new PageResp<TrainStationQueryResp>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    @Override
    public void delete(Long id) {
        trainStationMapper.deleteById(id);
    }

    @Override
    public List<TrainStationEntity> selectByTrainCode(String trainCode) {
        var q = Wrappers.<TrainStationEntity>lambdaQuery();
        q
                .eq(TrainStationEntity::getTrainCode,trainCode)
                .orderByAsc(TrainStationEntity::getIndex);
        return trainStationMapper.selectList(q);
    }

    private TrainStationEntity selectByUnique(String trainCode, Integer index) {
        var q = Wrappers.<TrainStationEntity>lambdaQuery();
        q.eq(TrainStationEntity::getTrainCode,trainCode)
                .eq(TrainStationEntity::getIndex,index);
        return trainStationMapper.selectOne(q);
    }


    private TrainStationEntity selectByUnique(String trainCode, String name) {
        var q = Wrappers.<TrainStationEntity>lambdaQuery();
        q.eq(TrainStationEntity::getTrainCode,trainCode)
                .eq(TrainStationEntity::getName,name);
        return trainStationMapper.selectOne(q);
    }
}
