import bean.simple.Bean1;
import bean.simple.Bean2;
import com.elin4it.convert.SimpleConvertorBuilder;
import com.elin4it.convert.Convertor;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: SimpleConvertTest.java , v 0.1 2017/11/23 下午5:35 ZhouFeng Exp $
 */
public class SimpleConvertTest {


    @Test
    public void basic_source_to_target() {

        Bean1 bean1 = new Bean1();
        bean1.setA(123);
        bean1.setB(222L);
        bean1.setC(1.2D);
        bean1.setD('A');
        bean1.setE(4.2F);
        bean1.setF(true);
        bean1.setG("hello");


        Convertor<Bean1, Bean2> convertor = SimpleConvertorBuilder.newBuilder(Bean1.class, Bean2.class).build();

        Bean2 bean2 = convertor.toTarget(bean1);

        Assert.assertEquals(bean1.getA(), bean2.getA());
        Assert.assertEquals(bean1.getB(), bean2.getB());
        Assert.assertEquals(bean1.getC(), bean2.getC(),1);
        Assert.assertEquals(bean1.getD(), bean2.getD());
        Assert.assertEquals(bean1.getE(), bean2.getE(),1);
        Assert.assertEquals(bean1.isF(), bean2.isF());
        Assert.assertEquals(bean1.getG(), bean2.getG());


    }

    @Test
    public void basic_target_to_source() {

        Bean2 bean2 = new Bean2();
        bean2.setA(123);
        bean2.setB(222L);
        bean2.setC(1.2D);
        bean2.setD('A');
        bean2.setE(4.2F);
        bean2.setF(true);
        bean2.setG("hello");

        Convertor<Bean1, Bean2> convertor = SimpleConvertorBuilder.newBuilder(Bean1.class, Bean2.class).build();

        Bean1 bean1 = convertor.toSource(bean2);

        Assert.assertEquals(bean1.getA(), bean2.getA());
        Assert.assertEquals(bean1.getB(), bean2.getB());
        Assert.assertEquals(bean1.getC(), bean2.getC(),1);
        Assert.assertEquals(bean1.getD(), bean2.getD());
        Assert.assertEquals(bean1.getE(), bean2.getE(),1);
        Assert.assertEquals(bean1.isF(), bean2.isF());
        Assert.assertEquals(bean1.getG(), bean2.getG());


    }


}


