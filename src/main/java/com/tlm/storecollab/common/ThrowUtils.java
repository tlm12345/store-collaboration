package com.tlm.storecollab.common;

import com.tlm.storecollab.exception.BusinessException;

/**
 * 抛异常工具类
 */
public class ThrowUtils {

    public static void throwIf(boolean condition, ErrorCode errorCode, String message) {
        if(condition){
            throw new BusinessException(errorCode, message);
        }
    }

    public static void throwIf(boolean condition, ErrorCode errorCode) {
        throwIf(condition, errorCode, "");
    }
}
