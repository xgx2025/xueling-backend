package com.hope.xueling.common.exception;
/**
 * 系统异常类，用于表示系统级别的异常。
 * 用于处理非预期的技术错误，如外部接口调用失败、文件读取失败。
 * @author 谢光湘
 * @since 2026/1/20
 */
public class SystemException extends BaseException {
    /**
     * 构造系统异常
     * @param message 错误提示
     */
    public SystemException(String message) {
        super(message);
    }

    /**
     * 构造系统异常，提示语按 String.format 模板填充
     * @param message 提示模板
     * @param args 模板参数
     */
    public SystemException(String message, Object... args) {
        super(String.format(message, args));
    }
}
