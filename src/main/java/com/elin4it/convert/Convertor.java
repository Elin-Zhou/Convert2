package com.elin4it.convert;

/**
 * 类型转换接口
 * <p>
 * 该接口的实现类均需要为线程安全类
 *
 * @author ElinZhou
 * @version $Id: Convertor.java , v 0.1 2017/11/24 上午11:41 ElinZhou Exp $
 */
public interface Convertor<S, T> {

    /**
     * 将源对象转换为目标对象
     *
     * @param source 源对象
     * @return
     */
    T toTarget(S source);

    /**
     * 将目标对象转换为源对象
     *
     * @param target
     * @return
     */
    S toSource(T target);
}
