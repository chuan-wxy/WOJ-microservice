package com.chuan.wojcommon.utils;

import com.chuan.wojcommon.common.BaseResponse;
import com.chuan.wojcommon.common.ResultStatus;


/**
 * 返回工具类
 *
 * @author chuan-wxy
 * @date 2024/08/14 03:59:45
 */
public class ResultUtils {
    /**
     *
     * @return
     * @param <T>
     */
    public static<T> BaseResponse<T> success(){
        return new BaseResponse<>(0, "success", null);
    }

    /**
     * @param data
     * @return
     */
    public static<T> BaseResponse<T> success(T data){
        return new BaseResponse<>(0, "success", data);
    }

    /**
     * @param message
     * @param data
     * @return
     */
    public static<T> BaseResponse<T> success(String message, T data){
        return new BaseResponse<>(0, message, data);
    }

    /**
     * @param resultStatus
     * @return
     */
    public static<T> BaseResponse<T> error(ResultStatus resultStatus){
        return new BaseResponse<>(resultStatus.getCode(), resultStatus.getMessage(), null);
    }

    /**
     * @param code
     * @param message
     * @return
     */
    public static<T> BaseResponse<T> error(int code, String message){
        return new BaseResponse<>(code, message, null);
    }

    /**
     * @param message
     * @return
     */
    public static<T> BaseResponse<T> error(String message){
        return new BaseResponse<>(ResultStatus.FAIL.getCode(), message, null);
    }
}
