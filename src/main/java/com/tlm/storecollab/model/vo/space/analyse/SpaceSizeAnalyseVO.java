package com.tlm.storecollab.model.vo.space.analyse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpaceSizeAnalyseVO implements Serializable {

    /**
     * 图片大小范围.  example:  < 100KB, 100 KB <=  size < 1MB, 1MB <= size < 10MB, 10MB <= size < 100MB, 100MB <= size < 1GB, 1GB <= size < 10GB, 10GB <= size < 100GB, 100GB <= size < 1TB, 1TB <= size < 10TB, 10TB <= size < 100TB,
     */
    private String sizeRange;

    /**
     * 指定范围下图片数量
     */
    private Long amount;
}
