package com.tlm.storecollab.model.vo.space.analyse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpaceUserUploadBehaviorAnalyseVO {

    /**
     * 时间
     * 如果尺度为day,  则为 yyyy-MM-dd
     * 如果尺度为week, 则为 x, x为某一年的第几周
     *  如果尺度为month,则为 yyyy-MM
     */
    private String timeRange;

    /**
     * 上传图片数量
     */
    private Long amount;
}
