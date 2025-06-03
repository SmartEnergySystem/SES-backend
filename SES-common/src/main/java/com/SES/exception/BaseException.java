package com.SES.exception;

/**
 * 业务异常
 * 其余所有自定义异常都要继承这个异常
 */
public class BaseException extends RuntimeException {

    public BaseException() {
    }

    public BaseException(String msg) {
        super(msg);
    }

}
