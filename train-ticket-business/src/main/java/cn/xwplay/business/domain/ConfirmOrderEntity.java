package cn.xwplay.business.domain;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName(value="confirm_order",schema = "public")
@KeySequence(value="public.confirm_order_id_seq",dbType = DbType.POSTGRE_SQL)
@Data
public class ConfirmOrderEntity {
    private Long id;

    private Long memberId;

    private Date date;

    private String trainCode;

    private String start;

    @TableField(value = "end_val")
    private String end;

    private Long dailyTrainTicketId;

    private String status;

    private Date createTime;

    private Date updateTime;

    private String tickets;

}
