package cn.xwplay.member.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@TableName(value="member",schema = "public")
@KeySequence(value="public.member_id_seq",dbType = DbType.POSTGRE_SQL)
@Data
public class MemberEntity {

  @TableId(type = IdType.INPUT)
  private Long id;
  private String mobile;

}
