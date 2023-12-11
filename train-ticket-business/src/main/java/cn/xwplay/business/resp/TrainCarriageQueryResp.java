package cn.xwplay.business.resp;

import lombok.Data;

import java.util.Date;

@Data
public class TrainCarriageQueryResp {

    /**
     * id
     */
    private Long id;

    /**
     * 车次编号
     */
    private String trainCode;

    /**
     * 厢号
     */
    private Integer index;

    /**
     * 座位类型|枚举[SeatTypeEnum]
     */
    private String seatType;

    /**
     * 座位数
     */
    private Integer seatCount;

    /**
     * 排数
     */
    private Integer rowCount;

    /**
     * 列数
     */
    private Integer colCount;

    /**
     * 新增时间
     */

    private Date createTime;

    /**
     * 修改时间
     */

    private Date updateTime;


}
