package com.elin4it.convert.util;

import com.elin4it.convert.Convertor;
import com.elin4it.convert.FastConvertorBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ElinZhou zhoufeng@duiba.com.cn
 * @version $Id: ConvertorUtils.java , v 0.1 2018-12-20 16:48 ElinZhou Exp $
 */
public class ConvertorUtils {

//    private static final ConcurrentHashMap<Class<?>,ConcurrentHashMap<Class<?>, Convertor<?,?>>>

    @SuppressWarnings("unchecked")
    public static <T, U> U convert(T t, Class<U> uClass) {
        Convertor<T, U> convertor = FastConvertorBuilder.newBuilder((Class<T>) t.getClass(), uClass).build();
        return convertor.toTarget(t);
    }


    public static <T, U> List<U> convert(List<T> ts, Class<U> uClass) {
        if (ts == null) {
            return Collections.emptyList();
        }
        List<U> us = new ArrayList<>(ts.size());
        for (T t : ts) {
            U u = convert(t, uClass);
            us.add(u);
        }
        return us;
    }
}
