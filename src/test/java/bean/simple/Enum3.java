package bean.simple;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: Enum3.java , v 0.1 2018/2/11 下午2:51 ZhouFeng Exp $
 */
public enum Enum3 {

    A((byte) 1), B((byte) 2);

    private byte code;

    Enum3(byte code) {
        this.code = code;
    }


    public byte getCode() {
        return code;
    }
}
