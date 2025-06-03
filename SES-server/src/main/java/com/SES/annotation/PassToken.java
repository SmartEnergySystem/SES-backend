package com.SES.annotation;

/**
 * 自定义注释：跳过jwt拦截
 */

import com.SES.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE}) // 可以加在类或方法上
@Retention(RetentionPolicy.RUNTIME)
public @interface PassToken {
}