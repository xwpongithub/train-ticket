package cn.xwplay.business.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
@TableName(value="daily_train_station",schema = "public")
@KeySequence(value="public.daily_train_station_id_seq",dbType = DbType.POSTGRE_SQL)
@Data
public class DailyTrainStationEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    private Date date;

    private String trainCode;

    private Integer index;

    private String name;

    private String namePinyin;

    private Date inTime;

    private Date outTime;

    private Date stopTime;

    private BigDecimal km;

    private Date createTime;

    private Date updateTime;

}
