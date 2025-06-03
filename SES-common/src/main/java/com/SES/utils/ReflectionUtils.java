package com.SES.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 反射工具类，提供通用的反射操作方法
 */
public class ReflectionUtils {

    /**
     * 尝试调用 setter 方法，如果方法不存在则忽略
     *
     * @param entity 实体对象
     * @param methodName setter 方法名
     * @param value 要设置的值
     */
    public static void setIfMethodExists(Object entity, String methodName, Object value) {
        try {
            Class<?> clazz = entity.getClass();
            Method method = clazz.getDeclaredMethod(methodName, value.getClass());
            method.invoke(entity, value);
        } catch (NoSuchMethodException e) {
            // 方法不存在，不进行赋值，不做任何处理
        } catch (IllegalAccessException | InvocationTargetException e) {
            // 打印日志或者抛出异常，根据需要处理
            System.err.println("调用 setter 方法失败：" + methodName);
            e.printStackTrace();
        }
    }
}
