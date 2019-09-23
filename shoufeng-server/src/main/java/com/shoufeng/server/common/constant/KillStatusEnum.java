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
    INVALID(-1),
    /**
     * 成功（未付款）
     */
    SUCCESS(0),
    /**
     * 已付款
     */
    PAYED(1),
    /**
     * 取消
     */
    CANCEL(2);

    private Integer value;

    KillStatusEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
