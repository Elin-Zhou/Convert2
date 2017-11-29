package com.elin4it.convert;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: ConvertException.java , v 0.1 2017/11/28 下午4:48 ZhouFeng Exp $
 */
public class ConvertException extends RuntimeException {
    public ConvertException(Exception e) {
        super(e);
    }
}
