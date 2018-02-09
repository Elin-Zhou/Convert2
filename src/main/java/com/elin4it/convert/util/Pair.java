package com.elin4it.convert.util;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: Pair.java , v 0.1 2018/2/8 下午4:07 ZhouFeng Exp $
 */
public class Pair<T, U> {

    private T _1;

    private U _2;

    private Pair(T _1, U _2) {
        this._1 = _1;
        this._2 = _2;
    }

    public static <T, U> Pair of(T _1, U _2) {
        return new Pair<>(_1, _2);
    }

    public T _1() {
        return _1;
    }

    public U _2() {
        return _2;
    }

    @Override
    public int hashCode() {
        return Long.valueOf(_1.hashCode() + _2.hashCode()).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Pair)) {
            return false;
        }
        Pair other = (Pair) obj;
        if (_1 != null && other._1 != null && !_1.equals(other._1)) {
            return false;
        }
        if (_2 != null && other._2 != null && !_2.equals(other._2)) {
            return false;
        }
        return true;
    }
}
