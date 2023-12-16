package cn.xwplay.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.xwplay.business.domain.TrainEntity;
import cn.xwplay.business.mapper.TrainMapper;
import cn.xwplay.business.req.TrainQueryReq;
import cn.xwplay.business.req.TrainSaveReq;
import cn.xwplay.business.service.TrainService;
import cn.xwplay.common.exception.BusinessException;
import cn.xwplay.common.exception.BusinessExceptionEnum;
import cn.xwplay.common.response.PageResp;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import cn.xwplay.business.resp.TrainQueryResp;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrainServiceImpl implements TrainService {

    private final TrainMapper trainMapper;

    @Override
    public void save(TrainSaveReq req) {
        var now = DateUtil.date();
        var train = BeanUtil.copyProperties(req, TrainEntity.class);
        if (Objects.isNull(train.getId())) {
            // 保存之前，先校验唯一键是否存在
            var trainDB = selectByUnique(req.getCode());
            if (Objects.nonNull(trainDB)) {
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_CODE_UNIQUE_ERROR);
            }
            train.setCreateTime(now);
            train.setUpdateTime(now);
            trainMapper.insert(train);
        } else {
            train.setUpdateTime(now);
            trainMapper.updateById(train);
        }
    }

    @Override
    public PageResp<TrainQueryResp> queryList(TrainQueryReq req) {
        var q = Wrappers.<TrainEntity>lambdaQuery();
        q.orderByAsc(TrainEntity::getCode);

        log.info("查询页码：{}", req.getPage());
        log.info("每页条数：{}", req.getSize());

        PageHelper.startPage(req.getPage(), req.getSize());
        var trainList = trainMapper.selectList(q);

        var pageInfo = new PageInfo<>(trainList);
        log.info("总行数：{}", pageInfo.getTotal());
        log.info("总页数：{}", pageInfo.getPages());

        var list = BeanUtil.copyToList(trainList, TrainQueryResp.class);
        var pageResp = new PageResp<TrainQueryResp>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    @Override
    public void delete(Long id) {
        trainMapper.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<TrainQueryResp> queryAll() {
        var trainList = selectAll();
        // LOG.info("再查一次");
        // trainList = selectAll();
        return BeanUtil.copyToList(trainList, TrainQueryResp.class);
    }

    @Override
    public List<TrainEntity> selectAll() {
        var q = Wrappers.<TrainEntity>lambdaQuery();
       q.orderByAsc(TrainEntity::getCode);
        return trainMapper.selectList(q);
    }

    private TrainEntity selectByUnique(String code) {
        var q = Wrappers.<TrainEntity>lambdaQuery();
        q.eq(TrainEntity::getCode,code);
        return trainMapper.selectOne(q);
    }

}
