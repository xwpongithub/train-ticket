package cn.xwplay.business.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@TableName(value="train",schema = "public")
@KeySequence(value="public.train_id_seq",dbType = DbType.POSTGRE_SQL)
@Data
public class TrainEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    private String code;

    private String type;

    private String start;

    private String startPinyin;

    private Date startTime;

    private String end;

    private String endPinyin;

    private Date endTime;

    private Date createTime;

    private Date updateTime;

}
