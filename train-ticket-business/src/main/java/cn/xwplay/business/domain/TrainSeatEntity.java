package cn.xwplay.business.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@TableName(value="train_seat",schema = "public")
@KeySequence(value="public.train_seat_id_seq",dbType = DbType.POSTGRE_SQL)
@Data
public class TrainSeatEntity {

    @TableId(type = IdType.INPUT)
    private Long id;

    private String trainCode;

    private Integer carriageIndex;

    private String row;

    private String col;

    private String seatType;

    private Integer carriageSeatIndex;

    private Date createTime;

    private Date updateTime;


}
