package com.hope.xueling.common.exception;

import com.hope.xueling.common.domain.vo.Result;
import com.hope.xueling.common.constant.ResultCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 全局异常处理类，用于处理应用程序中抛出的异常
 * @author 谢光湘
 * @since 2026/1/20
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 处理无权访问异常，返回 403 与对应提示
     * @param e 无权访问异常
     * @return 统一失败响应
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Result<String>> handleForbiddenException(ForbiddenException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Result.fail(ResultCode.FORBIDDEN, e.getMessage()));
    }

    /**
     * 处理数据库唯一冲突异常
     * @param e 重复键异常对象
     * @return 错误信息
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Result<String> handleDuplicateKeyException(DuplicateKeyException e){
        log.error("数据库唯一冲突异常",e);
        String errorMessage = e.getMessage();
        String friendlyMsg = "数据已存在";
        //正则匹配：从异常信息中提取 值、字段名
        Pattern pattern = Pattern.compile("Duplicate entry '(.*?)' for key '(.*?)'");
        Matcher matcher = pattern.matcher(errorMessage);
        if (matcher.find()){
            String duplicateValue = matcher.group(1);   //冲突的值
            String keyName = matcher.group(2);  //冲突的字段名

            if(keyName.contains("username")){
                log.info("用户名冲突");
                friendlyMsg = String.format("用户名 %s 已被使用", duplicateValue);
            }else if (keyName.contains("email")){
                log.info("邮箱冲突");
                friendlyMsg = String.format("邮箱 %s 已注册", duplicateValue);
            }else if (keyName.contains("phone")){
                log.info("手机号冲突");
                friendlyMsg = String.format("手机号 %s 已绑定账号", duplicateValue);

            }
        }
        return Result.fail(ResultCode.DUPLICATE_KEY_ERROR, friendlyMsg);
    }



    /**
     * 处理验证异常
     * @param e 验证异常对象
     * @return 错误信息
     */
    @ExceptionHandler(ValidationException.class)
    public Result<String> handleValidationException(ValidationException e) {
        log.error("验证异常: {}", e.getMessage());
        return Result.fail(ResultCode.VALIDATION_ERROR, e.getMessage());
    }

    /**
     * 处理业务异常
     * @param e 业务异常对象
     * @return 错误信息
     */
    @ExceptionHandler(BusinessException.class)
    public Result<String> handleBusinessException(BusinessException e) {
        log.error("业务异常: {}", e.getMessage());
        return Result.fail(ResultCode.BUSINESS_ERROR, e.getMessage());
    }

    /**
     * 处理系统异常
     * @param e 系统异常对象
     * @return 错误信息
     */
    @ExceptionHandler(SystemException.class)
    public Result<String> handleSystemException(SystemException e) {
        log.error("系统异常: {}", e.getMessage());
        return Result.fail(ResultCode.SYSTEM_ERROR, e.getMessage());
    }


    /**
     * 处理所有未捕获的异常
     * @param e 抛出的异常
     * @return 错误信息
     */
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        // 在这里可以添加日志记录等操作
        log.error("未处理的异常: {}", e.getMessage(), e);
        return Result.fail(ResultCode.SYSTEM_ERROR, e.getMessage());
    }
}
