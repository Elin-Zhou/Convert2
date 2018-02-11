package com.elin4it.convert;

import com.alibaba.fastjson.JSON;
import com.elin4it.convert.annotation.ConvertField;
import com.elin4it.convert.enums.AutoCastType;
import com.elin4it.convert.enums.TypeConvertType;
import com.elin4it.convert.util.BeanUtil;
import com.elin4it.convert.util.InstanceDelegate;
import com.elin4it.convert.util.Pair;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
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

    private static final List<Class<? extends Comparable>> BOXED = ImmutableList.of(Byte.class, Boolean.class,
            Short.class, Character.class, Integer.class, Long.class, Float.class, Double.class);

    private static final BiMap<Class<?>, Class<? extends Comparable>> BOXING_MAPPER = HashBiMap.create();

    private static final Map<Class<? extends Comparable>, String> TYPE_SUFFIX = ImmutableMap.of(Long.class, "L", Double
            .class, "D", Float.class, "F");

    private static final Set<Class<?>> NEED_CAST_LITERAL_TYPE = ImmutableSet.of(byte.class, Byte.class, Short.class,
            short.class);

    private Map<String, PropertyDescriptor> sourceDesciptorMap;

    private Map<String, PropertyDescriptor> targetDesciptorMap;

    private Set<String> sourceFieldNames;

    private Set<String> targetFieldNames;

    private Map<String, String> sourceFieldAliasMapper = new HashMap<>();

    private Map<String, String> targetieldAliasMapper = new HashMap<>();

    private Map<Pair<Class<?>, Class<?>>, Map> enumPairMap;

    private int tempValueNameCount = 0;


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
        return new FastConvertorBuilder<>(sourceClass, targetClass);
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

                generateEnumConvert(classPool, convertorClass);


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


//                convertorClass.writeFile("target/classes");

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
        BeanInfo sourceBeanInfo = Introspector.getBeanInfo(sourceClass);
        BeanInfo targetBeanInfo = Introspector.getBeanInfo(targetClass);

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

        sb.append(InstanceDelegate.of("$1").cast(fromClass).assign("from", fromClass));
        sb.append(InstanceDelegate.newInstance(toClass.getName()).assign("to", toClass));

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

            Class<?> readType = readMethod.getReturnType();
            Class<?> writeType = writeMethod.getParameterTypes()[0];

            TypeConvertType typeConvertType = convertType(readType, writeType);
            switch (typeConvertType) {
                case AUTO_BOXING_UNBOXING:
                    tempVarName = autoBoxingAndAutoUnboxing(sb, readMethod, writeMethod);
                    break;
                case NEED_NOT_CONVERT:
                    tempVarName = getTempName();

                    sb.append(InstanceDelegate.of("from").invoke(readMethod).cast(readType).assign(tempVarName,
                            readType));
                    break;
                case ENUM_CONVERT:
                    tempVarName = enumConvert(sb, readMethod, readType, writeType);
                    break;
                default:
                    continue;
            }


            sb.append(new InstanceDelegate("to").invoke(writeMethod.getName(), tempVarName).end());
        }

        sb.append(InstanceDelegate.of("to").thenReturn());
        sb.append("}");
        return sb.toString();
    }

    private String enumConvert(StringBuilder sb, Method readMethod, Class<?> readType, Class<?> writeType) {
        String tempVarName;
        tempVarName = getTempName();

        String mapName = enumMapName(readType, writeType);

        String readVarName = getTempName();
        if (readType == getBoxedClass(readType)) {
            //如果包装类型和原类型相同，有两种可能
            //1. 该类型本身为包装类型，则不需要转换
            //2. 该类型为枚举类型，不存在包装类型，即getBoxedClass方法返回本身
            sb.append(InstanceDelegate.of("from").invoke(readMethod).assign(readVarName, readType));
        } else {
            sb.append(autoBoxing(new InstanceDelegate("from").invoke(readMethod), readType).assign(readVarName,
                    getBoxedClass(writeType)));
        }

        String mapValueVarName = getTempName();
        sb.append(InstanceDelegate.of(mapName).invoke("get", readVarName).cast(getBoxedClass(writeType))
                .assign(mapValueVarName, getBoxedClass(writeType)));
        sb.append(InstanceDelegate.of(autoBoxingAndAutoUnboxing(mapValueVarName, getBoxedClass(writeType), writeType))
                .assign(tempVarName, writeType));
        return tempVarName;
    }

    /**
     * 自动装箱拆箱
     *
     * @param sb
     * @param readMethod
     * @param writeMethod
     * @return
     */
    private String autoBoxingAndAutoUnboxing(StringBuilder sb, Method readMethod, Method writeMethod) {
        String tempVarName = getTempName();

        AutoCastType autoCastType = useAutoCast(readMethod, writeMethod);
        Class<?> toType = writeMethod.getParameterTypes()[0];

        if (autoCastType == AutoCastType.BOXING) {
            sb.append(InstanceDelegate.of(toType.getSimpleName()).invoke("valueOf", InstanceDelegate.of("from")
                    .invoke(readMethod)).assign(tempVarName, toType));
        } else {
            sb.append(InstanceDelegate.of("from").invoke(readMethod).invoke(toType.getSimpleName() + "Value").assign
                    (tempVarName, toType));
        }
        return tempVarName;
    }

    private String autoBoxingAndAutoUnboxing(String varName, Class<?> fromClass, Class<?> toClass) {

        AutoCastType autoCastType = useAutoCast(fromClass, toClass);

        if (autoCastType == AutoCastType.BOXING) {
            return autoBoxing(varName, fromClass);
        } else if (autoCastType == AutoCastType.UNBOXING) {
            return autoUnbox(varName, fromClass);
        }
        return varName;
    }

    /**
     * 建立变量和descriptor的映射
     *
     * @param beanInfo
     * @return
     */
    private Map<String, PropertyDescriptor> descriptorMapper(BeanInfo beanInfo, Class<?> clazz, Map<String,
            String> fieldAliasMapper, Map<String, String> aliasMap) {

        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

        Map<String, PropertyDescriptor> descriptorMap = new HashMap<>((int) (propertyDescriptors.length * 1.5));

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

        return useAutoCast(fromClass, toClass);
    }

    private AutoCastType useAutoCast(Class<?> fromClass, Class<?> toClass) {
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

    private TypeConvertType convertType(Class<?> fromType, Class<?> toType) {

        if (fromType.equals(toType)) {
            return TypeConvertType.NEED_NOT_CONVERT;
        } else if (Optional.ofNullable(BOXING_MAPPER.get(fromType)).map(type -> type.equals(toType)).orElse(false)
                || Optional.ofNullable(BOXING_MAPPER.get(toType)).map(type -> type.equals(fromType)).orElse(false)) {
            return TypeConvertType.AUTO_BOXING_UNBOXING;
        } else if (fromType.isEnum() != toType.isEnum()) {
            return TypeConvertType.ENUM_CONVERT;
        }


        return TypeConvertType.NOT_SUPPORT_CONVERT;
    }


    private boolean hasEnumConvert() {

        boolean hasEnumConvert = false;

        Set<Pair> enumPairCache = new HashSet<>();
        enumPairMap = new HashMap<>();

        for (Map.Entry<String, PropertyDescriptor> entry : sourceDesciptorMap.entrySet()) {
            String fieldName = entry.getKey();
            PropertyDescriptor sourceProperty = entry.getValue();
            PropertyDescriptor targetProperty = targetDesciptorMap.get(fieldName);
            if (targetProperty == null) {
                continue;
            }

            //如果一对字段均不为枚举或均为枚举时，则不需要枚举转换
            if (sourceProperty.getPropertyType().isEnum() == targetProperty.getPropertyType().isEnum()) {
                continue;
            }
            hasEnumConvert = true;

            Class<?> enumClass;
            Class<?> baseClass;

            if (sourceProperty.getPropertyType().isEnum()) {
                enumClass = sourceProperty.getPropertyType();
                baseClass = targetProperty.getPropertyType();
            } else {
                enumClass = targetProperty.getPropertyType();
                baseClass = sourceProperty.getPropertyType();
            }

            Field[] enumFileds = enumClass.getDeclaredFields();

            Field baseField = null;
            int sameTimes = 0;
            for (Field enumFiled : enumFileds) {
                if (getBoxedClass(enumFiled.getType()).equals(getBoxedClass(baseClass))) {
                    baseField = enumFiled;
                    sameTimes++;
                }
            }

            if (baseField == null || sameTimes > 1) {
                //表示在该枚举中找不到对应类型的字段,或同类型字段有多个，无法判断根据哪一个进行转换，则无法转换
                continue;
            }
            Pair pair = Pair.of(enumClass, getBoxedClass(baseClass));
            if (enumPairCache.contains(pair)) {
                //如果该映射关系已存在，则跳过
                continue;
            }
            enumPairCache.add(pair);

            List enums;
            try {
                Method values = enumClass.getMethod("values");
                Object object = values.invoke(null);
                enums = Arrays.asList((Object[]) object);
            } catch (Exception e) {
                //枚举一定含有values方法
                e.printStackTrace();
                continue;
            }

            Map enum2Base = new HashMap(enums.size());

            baseField.setAccessible(true);
            for (Object anEnum : enums) {
                try {
                    Object baseValue = baseField.get(anEnum);
                    enum2Base.put(anEnum, baseValue);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }

            enumPairMap.put(pair, enum2Base);

        }

        return hasEnumConvert;

    }


    private void generateEnumConvert(ClassPool classPool, CtClass convertorClass) throws NotFoundException,
            CannotCompileException {

        CtConstructor ctConstructor = convertorClass.makeClassInitializer();


        StringBuilder sb = new StringBuilder("{");

        if (hasEnumConvert()) {

            CtClass mapClass = classPool.getCtClass(Map.class.getName());
            CtClass hashMapClass = classPool.getCtClass(HashMap.class.getName());

            for (Map.Entry<Pair<Class<?>, Class<?>>, Map> pairMapEntry : enumPairMap.entrySet()) {
                Pair<Class<?>, Class<?>> pair = pairMapEntry.getKey();

                String enum2BaseName = enumMapName(pair._1(), pair._2());
                String base2EnumName = enumMapName(pair._2(), pair._1());

                CtField enum2BaseField = new CtField(mapClass, enum2BaseName, convertorClass);
                enum2BaseField.setModifiers(Modifier.STATIC | Modifier.PRIVATE | Modifier.FINAL);
                CtField base2EnumField = new CtField(mapClass, base2EnumName, convertorClass);
                base2EnumField.setModifiers(Modifier.STATIC | Modifier.PRIVATE | Modifier.FINAL);

                convertorClass.addField(enum2BaseField, CtField.Initializer.byNew(hashMapClass));
                convertorClass.addField(base2EnumField, CtField.Initializer.byNew(hashMapClass));


                Map enumMap = pairMapEntry.getValue();


                for (Object o : enumMap.entrySet()) {
                    Map.Entry enumEntry = (Map.Entry) o;

                    Object enumObj = enumEntry.getKey();
                    Class<?> enumClass = enumObj.getClass();
                    String enumClassName = enumClass.getName();
                    InstanceDelegate enumDelegate;
                    try {
                        Method name = enumClass.getMethod("name");
                        String enumName = (String) name.invoke(enumObj);
                        enumDelegate = new InstanceDelegate(enumClassName + "." + enumName);

                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }

                    InstanceDelegate baseDelegate = new InstanceDelegate(autoBoxing(enumEntry.getValue(),
                            enumEntry.getValue().getClass()));
                    sb.append(new InstanceDelegate(enum2BaseName).invoke("put", enumDelegate, baseDelegate).end());
                    sb.append(new InstanceDelegate(base2EnumName).invoke("put", baseDelegate, enumDelegate).end());

                }
            }
        }

        sb.append("}");
        ctConstructor.setBody(sb.toString());
    }

    private String enumMapName(Class<?> fromType, Class<?> toType) {
        return ("_" + getBoxedClass(fromType).getName() + "$$" + getBoxedClass(toType).getName() + "Map").replaceAll
                ("\\.", "");
    }

    private InstanceDelegate autoBoxing(InstanceDelegate value, Class<?> type) {
        Class<?> boxedClass;
        if (BOXING_MAPPER.containsKey(type)) {
            boxedClass = BOXING_MAPPER.get(type);
        } else if (BOXING_MAPPER.containsValue(type)) {
            boxedClass = type;
        } else {
            return value;
        }
        if (NEED_CAST_LITERAL_TYPE.contains(type)) {
            value = value.cast(type);
        }
        return InstanceDelegate.of(boxedClass.getName()).invoke("valueOf", value);
    }

    private String autoBoxing(Object value, Class<?> type) {
        Class<?> boxedClass;
        if (BOXING_MAPPER.containsKey(type)) {
            boxedClass = BOXING_MAPPER.get(type);
        } else if (BOXING_MAPPER.containsValue(type)) {
            boxedClass = type;
        } else {
            return value.toString();
        }
        String suffix = TYPE_SUFFIX.get(boxedClass);
        suffix = suffix == null ? "" : suffix;

        String prefix = "";
        if (NEED_CAST_LITERAL_TYPE.contains(type)) {
            prefix = "(" + BOXING_MAPPER.inverse().get(boxedClass).getName() + ")";
        }

        return InstanceDelegate.of(boxedClass.getName()).invoke("valueOf", InstanceDelegate.of(prefix + value + suffix))
                .getExpression().toString();
    }

    private String autoUnbox(Object value, Class<?> type) {
        String unBoxClass;
        String valueString = value.toString();
        if (BOXING_MAPPER.containsKey(type)) {
            unBoxClass = type.getName();
        } else if (BOXING_MAPPER.containsValue(type)) {
            unBoxClass = BOXING_MAPPER.inverse().get(type).getName();
        } else {
            return valueString;
        }
        return InstanceDelegate.of(valueString).invoke(unBoxClass + "Value").getExpression().toString();
    }

    private Class<?> getBoxedClass(Class<?> baseClass) {
        if (BASICS.contains(baseClass)) {
            return BOXING_MAPPER.get(baseClass);
        }
        return baseClass;
    }

    private String getTempName() {
        return "_temp$" + (tempValueNameCount++);
    }

}
