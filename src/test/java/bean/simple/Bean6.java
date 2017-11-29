package bean.simple;

import com.elin4it.convert.annotation.ConvertField;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: Bean6.java , v 0.1 2017/11/28 下午3:54 ZhouFeng Exp $
 */
public class Bean6 {

    private int a;

    @ConvertField(isCopy = false)
    private int b;

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }
}
