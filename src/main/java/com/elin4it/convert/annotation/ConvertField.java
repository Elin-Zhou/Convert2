package com.elin4it.convert.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * duiba.com.cn Inc.
 * Copyright (c) 2014-2017 All Rights Reserved.
 */

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConvertField {

    String alias() default "";

    boolean isCopy() default true;

}
