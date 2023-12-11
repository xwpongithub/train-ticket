package cn.xwplay.business.req;

import cn.xwplay.common.req.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TrainCarriageQueryReq extends PageReq {

    private String trainCode;

}
