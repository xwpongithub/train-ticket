package cn.xwplay.business.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@TableName(value="daily_train_ticket",schema = "public")
@KeySequence(value="public.daily_train_ticket_id_seq",dbType = DbType.POSTGRE_SQL)
@Data
public class DailyTrainTicketEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    private Date date;

    private String trainCode;

    private String start;

    private String startPinyin;

    private Date startTime;

    private Integer startIndex;

    @TableField(value = "end_val")
    private String end;

    private String endPinyin;

    private Date endTime;

    private Integer endIndex;

    private Integer ydz;

    private BigDecimal ydzPrice;

    private Integer edz;

    private BigDecimal edzPrice;

    private Integer rw;

    private BigDecimal rwPrice;

    private Integer yw;

    private BigDecimal ywPrice;

    private Date createTime;

    private Date updateTime;


}
