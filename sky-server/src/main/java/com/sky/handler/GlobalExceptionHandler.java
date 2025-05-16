package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 处理SQL异常
     * @param ex 异常对象
     * @return 结果对象
     */
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        // 获取异常信息
        String message = ex.getMessage();

        // 检查异常信息是否包含"Duplicate entry"
        if (message.contains("Duplicate entry")) {
            // 分割异常信息字符串以获取用户名
            String[] split = message.split(" ");
            String username = split[2];

            // 构建错误消息
            String msg = username + MessageConstant.ALREADY_EXISTS;

            // 返回带有错误消息的结果
            return Result.error(msg);
        } else {
            // 如果不是重复条目异常，则返回未知错误结果
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }
    }
}
