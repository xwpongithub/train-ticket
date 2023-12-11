package cn.xwplay.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.xwplay.business.domain.TrainCarriageEntity;
import cn.xwplay.business.enums.SeatColEnum;
import cn.xwplay.business.mapper.TrainCarriageMapper;
import cn.xwplay.business.req.TrainCarriageQueryReq;
import cn.xwplay.business.req.TrainCarriageSaveReq;
import cn.xwplay.business.resp.TrainCarriageQueryResp;
import cn.xwplay.business.service.TrainCarriageService;
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
@Service
@Slf4j
public class TrainCarriageServiceImpl implements TrainCarriageService {

    private final TrainCarriageMapper trainCarriageMapper;

    @Override
    public void save(TrainCarriageSaveReq req) {
        var now = DateUtil.date();
        // 自动计算出列数和总座位数
        var seatColEnums = SeatColEnum.getColsByType(req.getSeatType());
        req.setColCount(seatColEnums.size());
        req.setSeatCount(req.getColCount() * req.getRowCount());

        var trainCarriage = BeanUtil.copyProperties(req, TrainCarriageEntity.class);
        if (Objects.isNull(trainCarriage.getId())) {
            // 保存之前，先校验唯一键是否存在
            var trainCarriageDB = selectByUnique(req.getTrainCode(), req.getIndex());
            if (Objects.nonNull(trainCarriageDB)) {
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_CARRIAGE_INDEX_UNIQUE_ERROR);
            }
            trainCarriage.setCreateTime(now);
            trainCarriage.setUpdateTime(now);
            trainCarriageMapper.insert(trainCarriage);
        } else {
            trainCarriage.setUpdateTime(now);
            trainCarriageMapper.updateById(trainCarriage);
        }
    }

    @Override
    public PageResp<TrainCarriageQueryResp> queryList(TrainCarriageQueryReq req) {
        var q = Wrappers.<TrainCarriageEntity>lambdaQuery();
        q
                .eq(StrUtil.isNotBlank(req.getTrainCode()),TrainCarriageEntity::getTrainCode,req.getTrainCode())
                .orderByAsc(TrainCarriageEntity::getTrainCode,TrainCarriageEntity::getIndex);

        log.info("查询页码：{}", req.getPage());
        log.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        var trainCarriageList = trainCarriageMapper.selectList(q);
        var pageInfo = new PageInfo<>(trainCarriageList);
        log.info("总行数：{}", pageInfo.getTotal());
        log.info("总页数：{}", pageInfo.getPages());

        var list = BeanUtil.copyToList(trainCarriageList, TrainCarriageQueryResp.class);
        var pageResp = new PageResp<TrainCarriageQueryResp>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;
    }

    @Override
    public void delete(Long id) {
        trainCarriageMapper.deleteById(id);
    }

    @Override
    public List<TrainCarriageEntity> selectByTrainCode(String trainCode) {
       var q = Wrappers.<TrainCarriageEntity>lambdaQuery();
       q.orderByAsc(TrainCarriageEntity::getIndex)
               .eq(TrainCarriageEntity::getTrainCode, trainCode);
        return trainCarriageMapper.selectList(q);
    }

    private TrainCarriageEntity selectByUnique(String trainCode, Integer index) {
        var q = Wrappers.<TrainCarriageEntity>lambdaQuery()
                .eq(TrainCarriageEntity::getTrainCode,trainCode)
                .eq(TrainCarriageEntity::getIndex,index);
        return trainCarriageMapper.selectOne(q);
    }

}
