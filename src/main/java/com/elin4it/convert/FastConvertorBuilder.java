package com.elin4it.convert;

import com.alibaba.fastjson.JSON;
import com.elin4it.convert.annotation.ConvertField;
import com.elin4it.convert.enums.AutoCastType;
import com.elin4it.convert.enums.TypeConvertType;
import com.elin4it.convert.util.BeanUtil;
import com.elin4it.convert.util.CodeInstanceDelegate;
import com.elin4it.convert.util.CodeTemplate;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: FastConvertorBuilder.java , v 0.1 2017/11/24 下午5:30 ZhouFeng Exp $
 */
public class FastConvertorBuilder<S, T> {

    private static final String CONVERTOR_INTERFACE_NAME = Convertor.class.getName();

    private static final List<Class<?>> BASICS = ImmutableList.of(byte.class, boolean.class, short.class, char.class,
            int.class, long.class, float.class, double.class);

    private static final List<Class<?>> BOXED = ImmutableList.of(Byte.class, Boolean.class, Short.class, Character
            .class, Integer.class, Long.class, Float.class, Double.class);

    private BiMap<Class<?>, Class<?>> BOXING_MAPPER = HashBiMap.create();

    private BeanInfo sourceBeanInfo;

    private BeanInfo targetBeanInfo;

    private Map<String, PropertyDescriptor> sourceDesciptorMap;

    private Map<String, PropertyDescriptor> targetDesciptorMap;

    private Set<String> sourceFieldNames;

    private Set<String> targetFieldNames;

    private Map<String, String> sourceFieldAliasMapper = new HashMap<>();

    private Map<String, String> targetieldAliasMapper = new HashMap<>();


    /******* 下列成员变量的修改影响Convertor类定义 *******/

    private Class<S> sourceClass;

    private Class<T> targetClass;

    private Class<?> sourceRootClass = Object.class;

    private Class<?> targetRootClass = Object.class;

    private Map<String, String> sourceAliasMap = new HashMap<>();

    private Map<String, String> targetAliasMap = new HashMap<>();

    {

        //初始化自动拆装箱映射关系
        int size = BASICS.size();
        for (int i = 0; i < size; i++) {
            BOXING_MAPPER.put(BASICS.get(i), BOXED.get(i));
        }
    }


    private FastConvertorBuilder(Class<S> sourceClass, Class<T> targetClass) {
        this.sourceClass = sourceClass;
        this.targetClass = targetClass;
    }

    public static <S, T> FastConvertorBuilder<S, T> newBuilder(Class<S> sourceClass, Class<T> targetClass) {
        return new FastConvertorBuilder(sourceClass, targetClass);
    }

    public FastConvertorBuilder<S, T> sourceRootClass(Class<?> sourceRootClass) {
        this.sourceRootClass = sourceRootClass;
        return this;
    }

    public FastConvertorBuilder<S, T> targetRootClass(Class<?> targetRootClass) {
        this.targetRootClass = targetRootClass;
        return this;
    }

    public FastConvertorBuilder<S, T> addSourceAlias(String fieldName, String alias) {
        sourceAliasMap.put(fieldName, alias);
        return this;
    }

    public FastConvertorBuilder<S, T> addTargetAlias(String fieldName, String alias) {
        targetAliasMap.put(fieldName, alias);
        return this;
    }

    public Convertor<S, T> build() {

        try {

            String convertorClassName = generateName();

            Class<?> clazz;
            try {
                clazz = Class.forName(convertorClassName);
            } catch (ClassNotFoundException e) {

                init();

                ClassPool classPool = ClassPool.getDefault();

                CtClass convertorClass = classPool.makeClass(convertorClassName);

                convertorClass.addInterface(classPool.getCtClass(CONVERTOR_INTERFACE_NAME));

                CtClass objectCtClass = classPool.getCtClass(Object.class.getName());

                //因为泛型被类型擦除，所以toSource和toTarget的入参和返回值都为Object类型
                CtMethod toSource = new CtMethod(objectCtClass, "toSource", new CtClass[]{objectCtClass},
                        convertorClass);
                toSource.setBody(generateToSource());
                convertorClass.addMethod(toSource);

                CtMethod toTarget = new CtMethod(objectCtClass, "toTarget", new CtClass[]{objectCtClass},
                        convertorClass);
                toTarget.setBody(generateToTarget());
                convertorClass.addMethod(toTarget);

                convertorClass.writeFile("target/classes");

                clazz = convertorClass.toClass();
            }
            Convertor<S, T> instance = (Convertor<S, T>) BeanUtil.instantiate(clazz);

            return instance;

        } catch (Exception e) {
            throw new ConvertException(e);
        }

    }

    /**
     * 根据源类和目标类初始化参数
     *
     * @throws IntrospectionException
     */
    private void init() throws IntrospectionException {
        sourceBeanInfo = Introspector.getBeanInfo(sourceClass);
        targetBeanInfo = Introspector.getBeanInfo(targetClass);

        sourceFieldNames = getFieldsNames(sourceClass, sourceRootClass);
        targetFieldNames = getFieldsNames(targetClass, targetRootClass);

        sourceDesciptorMap = descriptorMapper(sourceBeanInfo, sourceClass, sourceFieldAliasMapper, sourceAliasMap);
        targetDesciptorMap = descriptorMapper(targetBeanInfo, targetClass, targetieldAliasMapper, targetAliasMap);
    }


    /**
     * 生成转化类的类名
     *
     * @return
     */
    private String generateName() {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(sourceClass.getSimpleName()).append("$$");

        stringBuilder.append(targetClass.getSimpleName()).append("$$");

        stringBuilder.append("Convertor").append("$$");


        StringBuilder code = new StringBuilder();
        code.append(sourceClass.hashCode()).append("$$").append(targetClass.hashCode());
        code.append(sourceRootClass.getName()).append(targetRootClass.getName());
        code.append(JSON.toJSONString(sourceAliasMap)).append(JSON.toJSONString(targetAliasMap));

        stringBuilder.append(DigestUtils.md5Hex(code.toString()));

        return stringBuilder.toString();


    }

    private String generateToSource() {

        return generateConvert(targetClass, sourceClass, targetDesciptorMap, sourceDesciptorMap, targetFieldNames,
                sourceFieldNames, targetieldAliasMapper);
    }

    private String generateToTarget() {

        return generateConvert(sourceClass, targetClass, sourceDesciptorMap, targetDesciptorMap, sourceFieldNames,
                targetFieldNames, sourceFieldAliasMapper);
    }

    private String generateConvert(Class<?> fromClass, Class<?> toClass, Map<String, PropertyDescriptor>
            fromDesciptorMap, Map<String, PropertyDescriptor> toDesciptorMap, Set<String> fromFieldNames, Set<String>
                                           toFieldNames, Map<String, String> fieldAliasMapper) {
        StringBuilder sb = new StringBuilder("{");

        sb.append(CodeTemplate.cast("$1", "from", fromClass));
        sb.append(CodeTemplate.instance("to", toClass));

        for (String propertyName : fromFieldNames) {

            PropertyDescriptor descriptor = fromDesciptorMap.get(propertyName);

            if (descriptor == null) {
                continue;
            }
            PropertyDescriptor receiverDescriptor = toDesciptorMap.get(propertyName);

            if (receiverDescriptor == null) {

                String alias = fieldAliasMapper.get(propertyName);
                if (StringUtils.isBlank(alias)) {
                    continue;
                }
                receiverDescriptor = toDesciptorMap.get(alias);
                if (receiverDescriptor == null) {
                    continue;
                }
            }

            // 当前字段名或者经过别名映射的字段名，必须在目标类中可拷贝的字段列表中
            // 可拷贝字段列表 = 所有字段 - 不可拷贝字段
            // 不可拷贝字段列表包括根拷贝类（rootClass）及其祖先类中的字段
            if (!toFieldNames.contains(propertyName) && !toFieldNames.contains(receiverDescriptor.getName())) {
                continue;
            }

            Method readMethod = descriptor.getReadMethod();

            Method writeMethod = receiverDescriptor.getWriteMethod();

            String tempVarName;

            TypeConvertType typeConvertType = convertType(readMethod, writeMethod);
            switch (typeConvertType) {
                case AUTO_BOXING_UNBOXING:
                    tempVarName = autoBoxingAndAutoUnboxing(sb, propertyName, readMethod, writeMethod);
                    break;
                case NEED_NOT_CONVERT:
                    tempVarName = "temp$" + propertyName;
                    sb.append(CodeTemplate.assign(new CodeInstanceDelegate("from").invoke(readMethod), tempVarName,
                            readMethod.getReturnType()));
                    break;
                default:
                    continue;
            }


            sb.append(new CodeInstanceDelegate("to").invoke(writeMethod, new CodeInstanceDelegate(tempVarName)).end());
        }


        sb.append(CodeTemplate.getReturn("to"));

        sb.append("}");
        return sb.toString();
    }

    /**
     * 自动装箱拆箱
     *
     * @param sb
     * @param propertyName
     * @param readMethod
     * @param writeMethod
     * @return
     */
    private String autoBoxingAndAutoUnboxing(StringBuilder sb, String propertyName, Method readMethod, Method
            writeMethod) {
        String tempVarName = "temp$" + propertyName;

        AutoCastType autoCastType = useAutoCast(readMethod, writeMethod);
        Class<?> toType = writeMethod.getParameterTypes()[0];

        if (autoCastType == AutoCastType.BOXING) {
            sb.append(CodeTemplate.assign(new CodeInstanceDelegate(toType.getSimpleName())
                            .invoke("valueOf", new CodeInstanceDelegate("from").invoke(readMethod)),
                    tempVarName, toType));
        } else {
            sb.append(CodeTemplate.assign(new CodeInstanceDelegate("from").invoke(readMethod).invoke(toType
                    .getSimpleName() + "Value"), tempVarName, toType));
        }
        return tempVarName;
    }


    /**
     * 简历变量和descriptor的映射
     *
     * @param beanInfo
     * @return
     */
    private Map<String, PropertyDescriptor> descriptorMapper(BeanInfo beanInfo, Class<?> clazz, Map<String,
            String> fieldAliasMapper, Map<String, String> aliasMap) {

        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

        Map<String, PropertyDescriptor> descriptorMap = new HashMap<String, PropertyDescriptor>((int)
                (propertyDescriptors.length * 1.5));

        for (PropertyDescriptor descriptor : propertyDescriptors) {
            String name = descriptor.getName();


            boolean useAlia = false;
            // 判断是否使用了显式别名(会覆盖注解别名)
            if (aliasMap.containsKey(name)) {
                String alias = aliasMap.get(name);
                descriptorMap.put(alias, descriptor);
                fieldAliasMapper.put(name, alias);

                useAlia = true;
            }


            // 判断是否使用了注解
            Field field = FieldUtils.getField(clazz, name, true);
            if (field != null) {
                ConvertField annotation = field.getAnnotation(ConvertField.class);
                if (annotation != null) {

                    if (!annotation.isCopy()) {
                        continue;
                    }

                    if (!useAlia) {
                        // 使用了显式别名，则注解别名不生效
                        String alias = annotation.alias();
                        if (StringUtils.isNotBlank(alias)) {
                            descriptorMap.put(alias, descriptor);
                            fieldAliasMapper.put(name, alias);
                        }
                    }

                }
            }

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

    private AutoCastType useAutoCast(Method readMethod, Method writeMethod) {
        Class<?> fromClass = readMethod.getReturnType();
        Class<?> toClass = writeMethod.getParameterTypes()[0];

        if (BOXING_MAPPER.containsKey(fromClass)) {
            if (BOXING_MAPPER.get(fromClass).equals(toClass)) {
                return AutoCastType.BOXING;
            }
        } else if (BOXING_MAPPER.containsValue(fromClass)) {
            if (BOXING_MAPPER.inverse().get(fromClass).equals(toClass)) {
                return AutoCastType.UNBOXING;
            }
        }

        return AutoCastType.NONE;
    }


    private TypeConvertType convertType(Method readMethod, Method writeMethod) {

        Class<?> fromType = readMethod.getReturnType();
        Class<?> toType = writeMethod.getParameterTypes()[0];

        if (fromType.equals(toType)) {
            return TypeConvertType.NEED_NOT_CONVERT;
        } else if (Optional.ofNullable(BOXING_MAPPER.get(fromType)).map(type -> type.equals(toType)).orElse(false)
                || Optional.ofNullable(BOXING_MAPPER.get(toType)).map(type -> type.equals(fromType)).orElse(false)) {
            return TypeConvertType.AUTO_BOXING_UNBOXING;
        }


        return TypeConvertType.NOT_SUPPORT_CONVERT;
    }

}
