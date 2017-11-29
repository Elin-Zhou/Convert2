package com.elin4it.convert.util;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: CodeTemplate.java , v 0.1 2017/11/27 上午11:39 ZhouFeng Exp $
 */
public class CodeTemplate {

    /**
     * 创建对象代码模板
     *
     * @param varName               创建的对象名
     * @param clazz                 创建的对象类型
     * @param constructorParameters 构造方法入参
     * @return
     */
    public static String instance(String varName, Class<?> clazz, String... constructorParameters) {

        if (constructorParameters == null || constructorParameters.length == 0) {
            return clazz.getName() + " " + varName + " = new " + clazz.getName() + "();";
        }

        StringBuilder stringBuilder = new StringBuilder(clazz.getName() + " " + varName + " = new " + clazz.getName()
                + "(");

        for (String parameter : constructorParameters) {
            stringBuilder.append(parameter).append(",");
        }

        stringBuilder.deleteCharAt(stringBuilder.length() - 1);

        stringBuilder.append(");");

        return stringBuilder.toString();

    }


    /**
     * 返回对象代码模板
     *
     * @param objectName 需要返回的对象名
     * @return
     */
    public static String getReturn(String objectName) {
        return "return " + objectName + ";";
    }

    /**
     * 直接返回代码模板
     *
     * @return
     */
    public static String getReturn() {
        return "return;";
    }


    /**
     * 类型转化代码模板
     *
     * @param oldVarName 转换前变量名
     * @param newVarName 转后后变量名
     * @param clazz      需要转换的目标类型
     * @return
     */
    public static String cast(String oldVarName, String newVarName, Class<?> clazz) {
        return clazz.getName() + " " + newVarName + " = (" + clazz.getName() + ")" + oldVarName + ";";
    }

    public static String cast(CodeInstanceDelegate oldVar, String newVarName, Class<?> clazz) {
        return clazz.getName() + " " + newVarName + " = (" + clazz.getName() + ")" + oldVar.getExpression() + ";";
    }

    public static String assign(CodeInstanceDelegate oldVar, String newVarName, Class<?> clazz) {
        return clazz.getName() + " " + newVarName + " = " + oldVar.getExpression() + ";";
    }

}
