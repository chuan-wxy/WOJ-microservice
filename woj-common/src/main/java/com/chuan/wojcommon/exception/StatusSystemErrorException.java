package com.chuan.wojcommon.exception;

import java.io.Serial;

/**
 * 系统内部错误异常
 * 适用于：服务端自身故障导致请求处理失败的场景（非业务逻辑问题）
 * 例如：数据库连接失败、Redis调用异常、第三方接口调用超时/失败、代码运行时异常（NPE/数组越界等）
 * 默认业务错误码：500（对应HTTP 500 Internal Server Error），可自定义细分码（如5001-数据库异常、5002-第三方接口异常）
 * 注意：该异常应隐藏底层具体错误信息（避免泄露系统细节），对外仅返回通用提示，底层详情可记录日志
 *
 * @author chuan-wxy
 */
public class StatusSystemErrorException extends Exception {

    /**
     * 序列化版本号（与同系列异常类版本号区分，避免序列化冲突）
     */
    @Serial
    private static final long serialVersionUID = 4L;

    /**
     * 业务错误码（默认500，支持自定义细分系统错误码）
     */
    private Integer code;

    /**
     * 无参构造器
     * 默认错误码：500，默认错误信息："系统内部错误，请稍后重试"
     */
    public StatusSystemErrorException() {
        super("系统内部错误，请稍后重试");
        this.code = 500;
    }

    /**
     * 仅指定错误信息的构造器
     * 默认错误码：500（适用于需要自定义用户侧提示的场景）
     *
     * @param message 自定义错误信息（建议仅用通用提示，如："数据服务暂不可用，请稍后重试"）
     */
    public StatusSystemErrorException(String message) {
        super(message);
        this.code = 500;
    }

    /**
     * 指定错误码和错误信息的构造器
     *
     * @param code    自定义业务错误码（如：5001=数据库连接失败，5002=第三方接口调用异常）
     * @param message 自定义错误信息（用户侧通用提示）
     */
    public StatusSystemErrorException(Integer code, String message) {
        super(message);
        // 非空兜底：避免传入null导致空指针，默认赋值500
        this.code = code != null ? code : 500;
    }

    /**
     * 指定错误信息和异常根因的构造器
     * 默认错误码：500，适用于需要记录底层异常根因（日志排查）的场景
     *
     * @param message 自定义错误信息（用户侧通用提示）
     * @param cause   异常根因（如：SQLException、RedisException、第三方接口调用异常等，仅用于日志排查）
     */
    public StatusSystemErrorException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
    }

    /**
     * 仅指定异常原因的构造器
     * 默认错误码：500，默认错误信息："系统内部错误，请稍后重试"
     *
     * @param cause 异常根因（底层具体错误，用于日志排查）
     */
    public StatusSystemErrorException(Throwable cause) {
        super("系统内部错误，请稍后重试", cause);
        this.code = 500;
    }

    /**
     * 全参数构造器（包含抑制异常、栈轨迹可写性）
     * 适用于需要精细控制异常行为的特殊场景（如：生产环境关闭栈轨迹以提升性能）
     *
     * @param message            自定义错误信息（用户侧通用提示）
     * @param cause              异常根因（底层具体错误）
     * @param enableSuppression  是否允许抑制异常
     * @param writableStackTrace 是否生成可写的栈轨迹
     */
    public StatusSystemErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = 500;
    }

    /**
     * 扩展构造器：指定错误码、错误信息和异常根因
     * 高频场景：需要自定义系统错误细分码，且需记录底层根因（如：5001-数据库异常，根因是连接超时）
     *
     * @param code    自定义业务错误码
     * @param message 自定义错误信息（用户侧通用提示）
     * @param cause   异常根因（底层具体错误，用于日志排查）
     */
    public StatusSystemErrorException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code != null ? code : 500;
    }

    // ========== Getter/Setter（符合JavaBean规范） ==========
    public Integer getCode() {
        return code;
    }

    /**
     * 可选Setter：仅在特殊场景下允许外部修改错误码（优先通过构造器初始化）
     *
     * @param code 业务错误码（非null，否则默认赋值500）
     */
    public void setCode(Integer code) {
        this.code = code != null ? code : 500;
    }
}