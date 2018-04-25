package bean.simple;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: Enum4.java , v 0.1 2018/2/11 下午3:42 ZhouFeng Exp $
 */
public enum Enum4 {

    A('a'),B('b'),

    ;


    private char code;

    Enum4(char code) {
        this.code = code;
    }

    public char getCode() {
        return code;
    }
}
