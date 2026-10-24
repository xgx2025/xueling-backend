package com.hope.xueling.common.exception;

/**
 * 无权访问异常：由全局异常处理器转换为 403 响应
 * @author 谢光湘
 * @since 2026/10/24
 */
public class ForbiddenException extends BaseException {
    /**
     * 构造无权访问异常
     * @param message 错误提示
     */
    public ForbiddenException(String message) {
        super(message);
    }
}
