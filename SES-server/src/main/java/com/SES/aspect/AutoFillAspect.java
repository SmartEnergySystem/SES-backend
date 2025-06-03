package com.SES.aspect;

import com.SES.annotation.AutoFill;
import com.SES.constant.AutoFillConstant;
import com.SES.context.BaseContext;
import com.SES.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static com.SES.utils.ReflectionUtils.setIfMethodExists;

/**
 * 自定义切面类
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /**
     * 切入点
     */
    @Pointcut("execution(* com.SES.mapper.*.*(..)) && @annotation(com.SES.annotation.AutoFill)")
    public void autoFillPointCut(){}

    /**
     * 前置通知
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) throws NoSuchMethodException {
        log.info("开始进行自动填充");

        // 获取操作类型
        MethodSignature signature =  (MethodSignature)joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();

        // 获取被拦截方法的参数（实体对象）
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }


        Object entity = args[0];
        // 准备数据
        LocalDateTime nowTime = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();


        // 根据操作类型执行不同的填充逻辑

        if (operationType == OperationType.INSERT) {
            setIfMethodExists(entity, AutoFillConstant.SET_CREATE_TIME, nowTime);
            setIfMethodExists(entity, AutoFillConstant.SET_UPDATE_TIME, nowTime);

            if (currentId != null) {
                setIfMethodExists(entity, AutoFillConstant.SET_CREATE_USER, currentId);
                setIfMethodExists(entity, AutoFillConstant.SET_UPDATE_USER, currentId);
            } else {
                log.warn("当前用户ID为空，跳过 create_user 和 update_user 字段填充");
            }
        } else if (operationType == OperationType.UPDATE) {
            setIfMethodExists(entity, AutoFillConstant.SET_UPDATE_TIME, nowTime);

            if (currentId != null) {
                setIfMethodExists(entity, AutoFillConstant.SET_UPDATE_USER, currentId);
            } else {
                log.warn("当前用户ID为空，跳过 update_user 字段填充");
            }
        }
    }


}



