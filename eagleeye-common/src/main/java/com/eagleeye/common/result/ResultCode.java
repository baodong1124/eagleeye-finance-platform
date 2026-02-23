package com.eagleeye.common.result;

/**
 * 响应码枚举
 */
public enum ResultCode {

    SUCCESS(200, "操作成功"),

    BAD_REQUEST(400, "请求参数错误"),

    UNAUTHORIZED(401, "未授权"),

    FORBIDDEN(403, "无权限访问"),

    NOT_FOUND(404, "资源不存在"),

    INTERNAL_ERROR(500, "服务器内部错误"),

    // 业务错误码（1000-1999）
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_DISABLED(1002, "用户已被禁用"),

    // 账户相关错误码（2000-2999）
    ACCOUNT_NOT_FOUND(2001, "账户不存在"),
    INSUFFICIENT_BALANCE(2002, "账户余额不足"),
    INSUFFICIENT_BUDGET(2003, "预算余额不足"),

    // 报销相关错误码（3000-3999）
    EXPENSE_NOT_FOUND(3001, "报销单不存在"),
    EXPENSE_ALREADY_APPROVED(3002, "报销单已审批"),

    // 支付相关错误码（4000-4999）
    PAYMENT_FAILED(4001, "支付失败"),
    RECONCILIATION_FAILED(4002, "对账失败");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
