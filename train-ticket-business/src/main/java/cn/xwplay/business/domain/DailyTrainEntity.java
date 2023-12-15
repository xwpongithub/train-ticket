package cn.xwplay.business.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@TableName(value="daily_train",schema = "public")
@KeySequence(value="public.daily_train_id_seq",dbType = DbType.POSTGRE_SQL)
@Data
public class DailyTrainEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    private Date date;

    private String code;

    private String type;

    private String start;

    private String startPinyin;

    private Date startTime;

    @TableField(value = "end_val")
    private String end;

    private String endPinyin;

    private Date endTime;

    private Date createTime;

    private Date updateTime;


}
