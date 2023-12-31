package cn.xwplay.member.req;

import cn.xwplay.common.req.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TicketQueryReq extends PageReq {

    private Long memberId;

}
