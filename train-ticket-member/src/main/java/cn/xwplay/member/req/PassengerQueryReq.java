package cn.xwplay.member.req;

import cn.xwplay.common.req.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PassengerQueryReq extends PageReq {

    private Long memberId;

}
