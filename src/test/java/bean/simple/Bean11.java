package bean.simple;

import com.elin4it.convert.annotation.ConvertField;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: Bean11.java , v 0.1 2017/11/28 下午5:37 ZhouFeng Exp $
 */
public class Bean11 {

    private int aa;

    private long bb;

    private double cc;

    @ConvertField(alias = "d")
    private char dd;

    public int getAa() {
        return aa;
    }

    public void setAa(int aa) {
        this.aa = aa;
    }

    public long getBb() {
        return bb;
    }

    public void setBb(long bb) {
        this.bb = bb;
    }

    public double getCc() {
        return cc;
    }

    public void setCc(double cc) {
        this.cc = cc;
    }

    public char getDd() {
        return dd;
    }

    public void setDd(char dd) {
        this.dd = dd;
    }
}
