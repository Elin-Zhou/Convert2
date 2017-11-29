package com.elin4it.convert.util;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: BeanUtil.java , v 0.1 2017/11/24 下午5:38 ZhouFeng Exp $
 */
public class BeanUtil {

    public static <T> T instantiate(Class<T> clazz) {

        if (clazz == null) {
            throw new IllegalArgumentException("class must not be null");
        }

        if (clazz.isInterface()) {
            throw new RuntimeException(clazz.getName() + " is an interface");
        }
        try {
            return clazz.newInstance();
        } catch (InstantiationException ex) {
            throw new RuntimeException(clazz.getName() + " is an abstract class?", ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(clazz.getName() + "  Is the constructor accessible?", ex);
        }
    }

}
