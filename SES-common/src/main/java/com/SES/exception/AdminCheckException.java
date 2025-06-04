package com.SES.exception;

/**
 * 检查管理员权限失败
 */
public class AdminCheckException extends BaseException{
    public AdminCheckException(String msg){
        super(msg);
    }
}
