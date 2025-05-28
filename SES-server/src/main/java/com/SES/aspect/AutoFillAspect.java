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




        // 通过反射赋值
        if (operationType == OperationType.INSERT) {
            try{
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                setCreateTime.invoke(entity,nowTime);
                setCreateUser.invoke(entity,currentId);
                setUpdateTime.invoke(entity,nowTime);
                setUpdateUser.invoke(entity,currentId);
            }catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e){
                e.printStackTrace();
            }
        }else{
            try{
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                setUpdateTime.invoke(entity,nowTime);
                setUpdateUser.invoke(entity,currentId);
            }catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e){
                e.printStackTrace();
            }
        }


    }








}



