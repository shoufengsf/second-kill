package com.shoufeng.server.common.constant;

/**
 * 秒杀状态
 *
 * @author shoufeng
 */
public enum KillStatusEnum {

    /**
     * 无效
     */
    INVALID(-1, "无效"),
    /**
     * 成功（未付款）
     */
    SUCCESS(0, "成功（未付款）"),
    /**
     * 已付款
     */
    PAYED(1, "已付款"),
    /**
     * 取消
     */
    CANCEL(2, "取消");

    private Integer code;

    private String message;

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    KillStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
