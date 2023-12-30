package cn.xwplay.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.xwplay.business.domain.StationEntity;
import cn.xwplay.business.mapper.StationMapper;
import cn.xwplay.business.req.StationQueryReq;
import cn.xwplay.business.req.StationSaveReq;
import cn.xwplay.business.service.StationService;
import cn.xwplay.common.exception.BusinessException;
import cn.xwplay.common.exception.BusinessExceptionEnum;
import cn.xwplay.common.response.PageResp;
import cn.xwplay.business.resp.StationQueryResp;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 车站
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class StationServiceImpl implements StationService {

    private final StationMapper stationMapper;

    @Override
    public void save(StationSaveReq req) {
        var now = DateUtil.date();
        var station = BeanUtil.copyProperties(req, StationEntity.class);
        if (Objects.isNull(station.getId())) {
            // 保存之前，先校验唯一键是否存在
            var stationDB = selectByUnique(req.getName());
            if (Objects.nonNull(stationDB)) {
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_STATION_NAME_UNIQUE_ERROR);
            }
            station.setCreateTime(now);
            station.setUpdateTime(now);
            stationMapper.insert(station);
        } else {
            station.setUpdateTime(now);
            stationMapper.updateById(station);
        }
    }

    @Override
    public PageResp<StationQueryResp> queryList(StationQueryReq req) {
        var q = Wrappers.<StationEntity>lambdaQuery();
        q.orderByDesc(StationEntity::getId);
        log.info("查询页码：{}", req.getPage());
        log.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        var stationList = stationMapper.selectList(q);

        var pageInfo = new PageInfo<>(stationList);
        log.info("总行数：{}", pageInfo.getTotal());
        log.info("总页数：{}", pageInfo.getPages());

        var list = BeanUtil.copyToList(stationList, StationQueryResp.class);
        var pageResp = new PageResp<StationQueryResp>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    @Override
    public void delete(Long id) {
        stationMapper.deleteById(id);
    }

    @Override
    public List<StationQueryResp> queryAll() {
        var q = Wrappers.<StationEntity>lambdaQuery();
        q.orderByAsc(StationEntity::getNamePy);
        var stationList = stationMapper.selectList(q);
        return BeanUtil.copyToList(stationList, StationQueryResp.class);
    }

    private StationEntity selectByUnique(String name) {
        var q = Wrappers.<StationEntity>lambdaQuery();
        q.eq(StationEntity::getName,name);
        return stationMapper.selectOne(q);
    }

}
