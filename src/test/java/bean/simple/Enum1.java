package bean.simple;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: Enum1.java , v 0.1 2018/2/8 下午3:48 ZhouFeng Exp $
 */
public enum  Enum1 {

    A(1),B(2),

    ;

    private int code;

    Enum1(int code) {
        this.code = code;
    }


    public int getCode() {
        return code;
    }
}
