package bean.simple;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: Enum2.java , v 0.1 2018/2/10 下午4:58 ZhouFeng Exp $
 */
public enum Enum2 {

    A(1L), B(2L),;

    private long code;

    Enum2(long code) {
        this.code = code;
    }


    public long getCode() {
        return code;
    }

}
