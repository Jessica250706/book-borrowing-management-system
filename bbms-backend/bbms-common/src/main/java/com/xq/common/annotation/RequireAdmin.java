package com.xq.common.annotation;

import java.lang.annotation.*;

/**
 * 需要管理员权限注解
 * 标注在方法上，表示需要管理员权限才能访问
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireAdmin {

}