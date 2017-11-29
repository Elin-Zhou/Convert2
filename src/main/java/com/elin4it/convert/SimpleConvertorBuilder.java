package com.elin4it.convert;

import com.elin4it.convert.util.BeanUtil;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: SimpleConvertorBuilder.java , v 0.1 2017/11/24 上午10:18 ZhouFeng Exp $
 */
public class SimpleConvertorBuilder<S, T> {

    Class<S> sourceClass;
    Class<T> targetClass;


    Class<?> sourceRootClass = Object.class;

    Class<?> targetRootClass = Object.class;


    private SimpleConvertorBuilder(Class<S> sourceClass, Class<T> targetClass) {
        this.sourceClass = sourceClass;
        this.targetClass = targetClass;
    }

    public static <S, T> SimpleConvertorBuilder<S, T> newBuilder(Class<S> sourceClass, Class<T> targetClass) {
        return new SimpleConvertorBuilder(sourceClass, targetClass);
    }


    public SimpleConvertorBuilder sourceRootClass(Class<? super S> clazz) {
        if (clazz != null) {
            sourceRootClass = clazz;
        }
        return this;
    }

    public SimpleConvertorBuilder targetRootClass(Class<? super S> clazz) {
        if (clazz != null) {
            targetRootClass = clazz;
        }
        return this;
    }

    public Convertor build() {
        return new SimpleConvertor(this);
    }

    static class SimpleConvertor<S, T> implements Convertor<S, T> {

        private BeanInfo sourceBeanInfo;

        private BeanInfo targetBeanInfo;

        private Map<String, PropertyDescriptor> sourceDesciptorMap;

        private Map<String, PropertyDescriptor> targetDesciptorMap;

        private Class<S> sourceClass;

        private Class<T> targetClass;

        private Class<?> sourceRootClass = Object.class;

        private Class<?> targetRootClass = Object.class;

        private Set<String> sourceFieldNames;

        private Set<String> targetFieldNames;


        private SimpleConvertor(Class<S> sourceClass, Class<T> targetClass) {
            try {
                this.sourceClass = sourceClass;
                this.targetClass = targetClass;

                sourceBeanInfo = Introspector.getBeanInfo(sourceClass);
                targetBeanInfo = Introspector.getBeanInfo(targetClass);

                sourceFieldNames = getFieldsNames(sourceClass, sourceRootClass);
                targetFieldNames = getFieldsNames(targetClass, targetRootClass);

                sourceDesciptorMap = descriptorMapper(sourceBeanInfo);
                targetDesciptorMap = descriptorMapper(targetBeanInfo);

            } catch (IntrospectionException e) {
                e.printStackTrace();
            }
        }

        SimpleConvertor(SimpleConvertorBuilder builder) {
            this(builder.sourceClass, builder.targetClass);
            this.sourceRootClass = builder.sourceRootClass;
            this.targetRootClass = builder.targetRootClass;
        }

        private Map<String, PropertyDescriptor> descriptorMapper(BeanInfo beanInfo) {

            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

            Map<String, PropertyDescriptor> descriptorMap = new HashMap<String, PropertyDescriptor>((int)
                    (propertyDescriptors.length * 1.5));

            for (PropertyDescriptor descriptor : propertyDescriptors) {
                String name = descriptor.getName();

                descriptorMap.put(name, descriptor);
            }

            return descriptorMap;

        }

        private Set<String> getFieldsNames(Class<?> clazz, Class<?> rootClass) {

            Set<String> fields = new HashSet<String>();

            for (; ; ) {
                if (clazz.equals(rootClass) || clazz.equals(Object.class)) {
                    break;
                }

                Field[] declaredFields = clazz.getDeclaredFields();
                for (Field declaredField : declaredFields) {
                    fields.add(declaredField.getName());
                }

                clazz = clazz.getSuperclass();
            }
            return fields;

        }

        @Override
        public T toTarget(S source) {

            T object = BeanUtil.instantiate(targetClass);
            return (T) convert(source, object, sourceDesciptorMap, targetDesciptorMap, sourceFieldNames);
        }

        @Override
        public S toSource(T target) {

            S object = BeanUtil.instantiate(sourceClass);
            return (S) convert(target, object, targetDesciptorMap, sourceDesciptorMap, targetFieldNames);
        }

        private Object convert(Object from, Object to, Map<String, PropertyDescriptor> fromDesciptorMap, Map<String,
                PropertyDescriptor> toDesciptorMap, Set<String> fromFieldNames) {

            if (from == null || to == null) {
                return null;
            }

            try {


                for (String propertyName : fromFieldNames) {

                    PropertyDescriptor descriptor = fromDesciptorMap.get(propertyName);

                    if (descriptor == null) {
                        continue;
                    }

                    PropertyDescriptor receiverDescriptor = toDesciptorMap.get(propertyName);

                    if (receiverDescriptor == null) {
                        continue;
                    }

                    Method readMethod = descriptor.getReadMethod();
                    readMethod.setAccessible(true);

                    Object value = readMethod.invoke(from);

                    Method writeMethod = receiverDescriptor.getWriteMethod();
                    writeMethod.setAccessible(true);
                    writeMethod.invoke(to, value);


                }
            } catch (Exception e) {
                throw new RuntimeException("copy from " + sourceClass.getName() + " to " + targetClass.getName() +
                        " error", e);
            }


            return to;
        }

    }


}
