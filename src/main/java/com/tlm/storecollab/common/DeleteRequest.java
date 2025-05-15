package com.tlm.storecollab.common;

import lombok.Data;

/**
 * 通用删除数据请求
 * @author tlm
 */
@Data
public class DeleteRequest {
    /**
     * 要删除的数据的id
     */
    private Long id;
}
