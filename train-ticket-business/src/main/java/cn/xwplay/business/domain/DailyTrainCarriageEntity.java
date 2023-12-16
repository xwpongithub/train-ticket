package cn.xwplay.business.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@TableName(value="daily_train_carriage",schema = "public")
@KeySequence(value="public.daily_train_carriage_id_seq",dbType = DbType.POSTGRE_SQL)
@Data
public class DailyTrainCarriageEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    private Date date;

    private String trainCode;

    private Integer index;

    private String seatType;

    private Integer seatCount;

    private Integer rowCount;

    private Integer colCount;

    private Date createTime;

    private Date updateTime;


}
