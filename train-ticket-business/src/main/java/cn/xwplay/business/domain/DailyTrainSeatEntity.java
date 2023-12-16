package cn.xwplay.business.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@TableName(value="daily_train_seat",schema = "public")
@KeySequence(value="public.daily_train_seat_id_seq",dbType = DbType.POSTGRE_SQL)
@Data
public class DailyTrainSeatEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    private Date date;

    private String trainCode;

    private Integer carriageIndex;

    private String row;

    private String col;

    private String seatType;

    private Integer carriageSeatIndex;

    private String sell;

    private Date createTime;

    private Date updateTime;

}
