package com.chuan.wojcommon.common;

import lombok.Data;
import java.io.Serializable;

/**
 * @author chuan-wxy
 * @date 2024/08/15 09:44:28
 */
@Data
public class BaseResponse<T> implements Serializable {
    private Integer code;

    private String message;

    private T data;

    public BaseResponse(Integer code, String message,T data){
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public BaseResponse(Integer code, String message){this(code,message,null);}

    public BaseResponse(ResultStatus resultStatus){this(resultStatus.getCode(), resultStatus.getMessage(),null);}

}