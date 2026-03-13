package com.chuan.wojcommon.exception;

import java.io.Serial;

/**
 * 访问被拒绝异常（权限不足）
 * 适用于：用户已完成身份认证（登录），但无具体操作的权限场景
 * 区分：与StatusForbiddenException的核心差异是——该异常是"权限体系内的权限不足"，后者是"资源层面的禁止访问"
 *
 * @author chuan-wxy
 */
public class StatusAccessDeniedException extends Exception {

    /**
     * 序列化版本号（与同系列异常类版本号区分，避免序列化冲突）
     */
    @Serial
    private static final long serialVersionUID = 3L;

    /**
     * 业务错误码（默认403，支持自定义细分权限错误码）
     */
    private Integer code;

    /**
     * 无参构造器
     * 默认错误码：403，默认错误信息："您无此操作的权限"
     */
    public StatusAccessDeniedException() {
        super("您无此操作的权限");
        this.code = 403;
    }

    /**
     * 仅指定错误信息的构造器
     * 默认错误码：403
     *
     * @param message 自定义错误信息（如："仅管理员可执行批量删除操作"）
     */
    public StatusAccessDeniedException(String message) {
        super(message);
        this.code = 403;
    }

    /**
     * 指定错误码和错误信息的构造器
     *
     * @param code    自定义业务错误码（如：4030=操作权限不足，4033=数据权限不足）
     * @param message 自定义错误信息
     */
    public StatusAccessDeniedException(Integer code, String message) {
        super(message);
        // 非空兜底：避免传入null导致空指针，默认赋值403
        this.code = code != null ? code : 403;
    }

    /**
     * 指定错误信息和异常原因的构造器
     * 默认错误码：403，适用于需要记录异常根因的场景
     *
     * @param message 自定义错误信息
     * @param cause   异常根因（如：权限校验服务调用失败、用户角色查询异常）
     */
    public StatusAccessDeniedException(String message, Throwable cause) {
        super(message, cause);
        this.code = 403;
    }

    /**
     * 仅指定异常原因的构造器
     * 默认错误码：403，默认错误信息："您无此操作的权限"
     *
     * @param cause 异常根因
     */
    public StatusAccessDeniedException(Throwable cause) {
        super("您无此操作的权限", cause);
        this.code = 403;
    }

    /**
     * 全参数构造器（包含抑制异常、栈轨迹可写性）
     * 适用于需要精细控制异常行为的特殊场景
     *
     * @param message            自定义错误信息
     * @param cause              异常根因
     * @param enableSuppression  是否允许抑制异常
     * @param writableStackTrace 是否生成可写的栈轨迹
     */
    public StatusAccessDeniedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = 403;
    }

    /**
     * 扩展构造器：指定错误码、错误信息和异常根因
     * 高频场景：需要自定义权限细分码，且需记录异常根因（如：4033-数据权限不足，根因是部门权限查询失败）
     *
     * @param code    自定义业务错误码
     * @param message 自定义错误信息
     * @param cause   异常根因
     */
    public StatusAccessDeniedException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code != null ? code : 403;
    }

    // ========== Getter/Setter（符合JavaBean规范） ==========
    public Integer getCode() {
        return code;
    }

    /**
     * 可选Setter：仅在特殊场景下允许外部修改错误码（优先通过构造器初始化）
     *
     * @param code 业务错误码（非null，否则默认赋值403）
     */
    public void setCode(Integer code) {
        this.code = code != null ? code : 403;
    }
}