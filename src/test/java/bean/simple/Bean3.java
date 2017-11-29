package bean.simple;

import com.elin4it.convert.annotation.ConvertField;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: Bean3.java , v 0.1 2017/11/28 上午10:30 ZhouFeng Exp $
 */
public class Bean3 {

    @ConvertField(alias = "a")
    private int aa;


    public int getAa() {
        return aa;
    }

    public void setAa(int aa) {
        this.aa = aa;
    }
}
