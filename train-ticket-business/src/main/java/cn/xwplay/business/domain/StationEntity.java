package cn.xwplay.business.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@TableName(value="station",schema = "public")
@KeySequence(value="public.station_id_seq",dbType = DbType.POSTGRE_SQL)
@Data
public class StationEntity {

  @TableId(type = IdType.INPUT)
  private Long id;

  private String name;

  private String namePinyin;

  private String namePy;

  private Date createTime;

  private Date updateTime;

}
