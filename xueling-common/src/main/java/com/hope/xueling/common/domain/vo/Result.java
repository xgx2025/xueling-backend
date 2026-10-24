package com.hope.xueling.common.domain.vo;

import com.hope.xueling.common.constant.ResultCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 统一响应对象
 * @author 谢光湘
 * @since 2026/1/20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result <T>{
    /**
     * 业务状态码：0 成功，非 0 见 ResultCode
     */
    private Integer code;
    /**
     * 提示信息
     */
    private String msg;
    /**
     * 业务数据
     */
    private T data;

    /**
     * 成功响应
     * @param data 响应数据
     * @return 成功响应对象
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(0,null,data);
    }

    /**
     * 成功响应
     * @param data 响应数据
     * @param msg 自定义成功信息
     * @return 成功响应对象
     */
    public static <T> Result<T> success(T data, String msg) {
        return new Result<>(0, msg, data);
    }
    /**
     * 失败响应
     * @param resultCode 结果码枚举
     * @return 失败响应对象
     */
    public static <T> Result<T> fail(ResultCode resultCode) {
        Integer code = resultCode.getCode();
        String msg = resultCode.getMsg();
        return new Result<>(code, msg, null);
    }
    /**
     * 失败响应
     * @param resultCode 结果码枚举
     * @param msg 自定义错误信息
     * @return 失败响应对象
     */
    public static <T> Result<T> fail(ResultCode resultCode, String msg) {
        Integer code = resultCode.getCode();
        return new Result<>(code, msg, null);
    }
}
