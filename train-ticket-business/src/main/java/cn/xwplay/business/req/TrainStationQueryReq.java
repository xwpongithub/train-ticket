package cn.xwplay.business.req;


import cn.xwplay.common.req.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TrainStationQueryReq extends PageReq {

    private String trainCode;

}
