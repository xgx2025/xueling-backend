package com.hope.xueling.common.exception;

/**
 * 业务异常类，用于表示业务级别的异常。
 * 用于处理也就是“预期内”的逻辑阻断，比如“余额不足”、“用户已存在”。
 * @author 谢光湘
 * @since 2026/1/20
 */
public class BusinessException extends BaseException {
    /**
     * 构造业务异常
     * @param message 错误提示
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * 构造业务异常，提示语按 String.format 模板填充
     * @param message 提示模板
     * @param args 模板参数
     */
    public BusinessException(String message, Object... args) {
        super(String.format(message, args));
    }
}
