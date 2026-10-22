package com.hope.xueling.common.constant;

import lombok.Getter;

/**
 * 统一的结果码枚举类
 * @author 谢光湘
 * @since 2026/1/20
 */
@Getter
public enum ResultCode {
    TEST(111,"测试枚举"),
    BUSINESS_ERROR(500,"业务异常"), VALIDATION_ERROR(501,"验证异常"),
    SYSTEM_ERROR(600,"系统异常"), DUPLICATE_KEY_ERROR(601,"数据库唯一冲突异常"), UNAUTHORIZED(401,"未登录"), FORBIDDEN(403,"无权访问"), EMAIL_SEND_TOO_FREQUENTLY(602,"请不要频繁发送验证码");

    private final Integer code;
    private final String msg;

    ResultCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
