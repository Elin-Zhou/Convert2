package com.elin4it.convert.util;

import java.lang.reflect.Method;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: InstanceDelegate.java , v 0.1 2017/11/27 下午2:08 ZhouFeng Exp $
 */
public class InstanceDelegate {

    private StringBuilder stringBuilder = new StringBuilder();

    public static InstanceDelegate of(String varName) {
        return new InstanceDelegate(varName);
    }

    public static InstanceDelegate newInstance(String className, InstanceDelegate... parameters) {

        StringBuilder sb = new StringBuilder("new ");
        sb.append(className);
        sb.append("(");
        for (InstanceDelegate parameter : parameters) {
            sb.append(parameter.getExpression()).append(",");
        }
        if (parameters.length != 0) {
            sb.delete(sb.length() - 1, sb.length());
        }

        sb.append(")");

        return new InstanceDelegate(sb.toString());

    }

    public InstanceDelegate(String varName) {
        stringBuilder.append(varName);
    }

    public InstanceDelegate invoke(Method method, InstanceDelegate... parameters) {
        invoke(method.getName(), parameters);

        return this;
    }

    public InstanceDelegate invoke(String methodName, InstanceDelegate... parameters) {
        stringBuilder.append(".").append(methodName).append("(");
        if (parameters != null && parameters.length != 0) {
            for (InstanceDelegate parameter : parameters) {
                stringBuilder.append(parameter.getExpression()).append(",");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        stringBuilder.append(")");

        return this;
    }

    public InstanceDelegate invoke(String methodName, String... parameters) {
        InstanceDelegate[] delegates = new InstanceDelegate[parameters.length];
        for (int index = 0; index < parameters.length; index++) {
            delegates[index] = InstanceDelegate.of(parameters[index]);
        }
        return invoke(methodName, delegates);
    }

    public InstanceDelegate invoke(String methodName) {
        stringBuilder.append(".").append(methodName).append("()");
        return this;
    }

    public InstanceDelegate cast(String descType) {
        stringBuilder = new StringBuilder("(").append(descType).append(")").append(stringBuilder);
        return this;
    }

    public InstanceDelegate cast(Class<?> descClass) {
        stringBuilder = new StringBuilder("(").append(descClass.getName()).append(")").append(stringBuilder);

        return this;
    }


    public StringBuilder getExpression() {
        return stringBuilder;
    }

    public String end() {
        return stringBuilder.append(";").toString();
    }

    public String assign(String newVarName, Class<?> clazz) {
        return assign(newVarName, clazz, true);
    }

    public String assign(String newVarName, Class<?> clazz, boolean isNew) {
        if (isNew) {
            return clazz.getName() + " " + newVarName + " = " + stringBuilder.append(";").toString();
        }
        return newVarName + " = " + stringBuilder.append(";").toString();
    }

    public static String defineVariable(String newVarName, String className) {
        return className + " " + newVarName + ";";
    }

    public String thenReturn() {
        return "return " + stringBuilder.append(";");
    }

    public static String getReturn() {
        return "return;";
    }
}
