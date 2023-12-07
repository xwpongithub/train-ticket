package cn.xwplay.member.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value="passenger",schema = "public")
@KeySequence(value="public.passenger_id_seq",dbType = DbType.POSTGRE_SQL)
public class PassengerEntity {

    @TableId(type = IdType.INPUT)
    private Long id;
    private Long memberId;
    private String name;
    private String idCard;
    private String type;
    private Date createTime;
    private Date updateTime;


}
