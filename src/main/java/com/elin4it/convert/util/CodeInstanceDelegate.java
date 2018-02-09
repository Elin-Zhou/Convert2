package com.elin4it.convert.util;

import java.lang.reflect.Method;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: CodeInstanceDelegate.java , v 0.1 2017/11/27 下午2:08 ZhouFeng Exp $
 */
public class CodeInstanceDelegate {

    private StringBuilder stringBuilder = new StringBuilder();

    public static CodeInstanceDelegate of(String varName) {
        return new CodeInstanceDelegate(varName);
    }
    public CodeInstanceDelegate(String varName) {
        stringBuilder.append(varName);
    }

    public CodeInstanceDelegate invoke(Method method, CodeInstanceDelegate... parameters) {
        invoke(method.getName(), parameters);

        return this;
    }

    public CodeInstanceDelegate invoke(String methodName, CodeInstanceDelegate... parameters) {
        stringBuilder.append(".").append(methodName).append("(");
        if (parameters != null && parameters.length != 0) {
            for (CodeInstanceDelegate parameter : parameters) {
                stringBuilder.append(parameter.getExpression()).append(",");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        stringBuilder.append(")");

        return this;
    }

    public CodeInstanceDelegate invoke(String methodName, String... parameters) {
        CodeInstanceDelegate[] delegates = new CodeInstanceDelegate[parameters.length];
        for (int index = 0; index < parameters.length; index++) {
            delegates[index] = CodeInstanceDelegate.of(parameters[index]);
        }
        return invoke(methodName, delegates);
    }

    public CodeInstanceDelegate invoke(String methodName) {
        stringBuilder.append(".").append(methodName).append("()");
        return this;
    }
    public StringBuilder getExpression() {
        return stringBuilder;
    }

    public String end() {
        return stringBuilder.append(";").toString();
    }
}
