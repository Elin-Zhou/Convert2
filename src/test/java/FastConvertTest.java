import bean.simple.Bean1;
import bean.simple.Bean10;
import bean.simple.Bean11;
import bean.simple.Bean2;
import bean.simple.Bean3;
import bean.simple.Bean4;
import bean.simple.Bean6;
import bean.simple.Bean7;
import bean.simple.Bean8;
import bean.simple.Bean9;
import com.elin4it.convert.Convertor;
import com.elin4it.convert.FastConvertorBuilder;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: SimpleConvertTest.java , v 0.1 2017/11/23 下午5:35 ZhouFeng Exp $
 */
public class FastConvertTest {


    @Test
    public void source_to_target() {

        Bean1 bean1 = new Bean1();
        bean1.setA(123);
        bean1.setB(222L);
        bean1.setC(1.2D);
        bean1.setD('A');
        bean1.setE(4.2F);
        bean1.setF(true);
        bean1.setG("hello");
        bean1.setH((short) 2);
        bean1.setI((byte) 3);


        Convertor<Bean1, Bean2> convertor = FastConvertorBuilder.newBuilder(Bean1.class, Bean2.class).build();

        Bean2 bean2 = convertor.toTarget(bean1);

        Assert.assertEquals(bean1.getA(), bean2.getA());
        Assert.assertEquals(bean1.getB(), bean2.getB());
        Assert.assertEquals(bean1.getC(), bean2.getC(), 1);
        Assert.assertEquals(bean1.getD(), bean2.getD());
        Assert.assertEquals(bean1.getE(), bean2.getE(), 1);
        Assert.assertEquals(bean1.isF(), bean2.isF());
        Assert.assertEquals(bean1.getG(), bean2.getG());
        Assert.assertEquals(bean1.getH(), bean2.getH());
        Assert.assertEquals(bean1.getI(), bean2.getI());


    }

    @Test
    public void target_to_source() {

        Bean2 bean2 = new Bean2();
        bean2.setA(123);
        bean2.setB(222L);
        bean2.setC(1.2D);
        bean2.setD('A');
        bean2.setE(4.2F);
        bean2.setF(true);
        bean2.setG("hello");
        bean2.setH((short) 2);
        bean2.setI((byte) 3);

        Convertor<Bean1, Bean2> convertor = FastConvertorBuilder.newBuilder(Bean1.class, Bean2.class).build();

        Bean1 bean1 = convertor.toSource(bean2);

        Assert.assertEquals(bean1.getA(), bean2.getA());
        Assert.assertEquals(bean1.getB(), bean2.getB());
        Assert.assertEquals(bean1.getC(), bean2.getC(), 1);
        Assert.assertEquals(bean1.getD(), bean2.getD());
        Assert.assertEquals(bean1.getE(), bean2.getE(), 1);
        Assert.assertEquals(bean1.isF(), bean2.isF());
        Assert.assertEquals(bean1.getG(), bean2.getG());
        Assert.assertEquals(bean1.getH(), bean2.getH());
        Assert.assertEquals(bean1.getI(), bean2.getI());

    }


    @Test
    public void alias_source_to_target() {

        Bean1 bean1 = new Bean1();
        bean1.setA(123);

        Convertor<Bean1, Bean3> convertor = FastConvertorBuilder.newBuilder(Bean1.class, Bean3.class).build();

        Bean3 bean3 = convertor.toTarget(bean1);

        Assert.assertEquals(bean1.getA(), bean3.getAa());

    }

    @Test
    public void alias_target_to_source() {

        Bean3 bean3 = new Bean3();
        bean3.setAa(123);

        Convertor<Bean1, Bean3> convertor = FastConvertorBuilder.newBuilder(Bean1.class, Bean3.class).build();

        Bean1 bean1 = convertor.toSource(bean3);

        Assert.assertEquals(bean1.getA(), bean3.getAa());

    }


    @Test
    public void boxing_source_to_target() {

        Bean1 bean1 = new Bean1();
        bean1.setA(123);
        bean1.setB(222L);
        bean1.setC(1.2D);
        bean1.setD('A');
        bean1.setE(4.2F);
        bean1.setF(true);
        bean1.setG("hello");
        bean1.setH((short) 2);
        bean1.setI((byte) 3);

        Convertor<Bean1, Bean4> convertor = FastConvertorBuilder.newBuilder(Bean1.class, Bean4.class).build();

        Bean4 bean4 = convertor.toTarget(bean1);

        Assert.assertEquals(bean1.getA(), (int) bean4.getA());
        Assert.assertEquals(bean1.getB(), (long) bean4.getB());
        Assert.assertEquals(bean1.getC(), bean4.getC(), 1);
        Assert.assertEquals(bean1.getD(), (char) bean4.getD());
        Assert.assertEquals(bean1.getE(), bean4.getE(), 1);
        Assert.assertEquals(bean1.isF(), bean4.getF());
        Assert.assertEquals(bean1.getG(), bean4.getG());
        Assert.assertEquals(bean1.getH(), (short) bean4.getH());
        Assert.assertEquals(bean1.getI(), (byte) bean4.getI());

    }

    @Test
    public void boxing_target_to_source() {

        Bean4 bean4 = new Bean4();
        bean4.setA(123);
        bean4.setB(222L);
        bean4.setC(1.2D);
        bean4.setD('A');
        bean4.setE(4.2F);
        bean4.setF(true);
        bean4.setG("hello");
        bean4.setH((short) 2);
        bean4.setI((byte) 3);

        Convertor<Bean1, Bean4> convertor = FastConvertorBuilder.newBuilder(Bean1.class, Bean4.class).build();

        Bean1 bean1 = convertor.toSource(bean4);

        Assert.assertEquals(bean1.getA(), (int) bean4.getA());
        Assert.assertEquals(bean1.getB(), (long) bean4.getB());
        Assert.assertEquals(bean1.getC(), bean4.getC(), 1);
        Assert.assertEquals(bean1.getD(), (char) bean4.getD());
        Assert.assertEquals(bean1.getE(), bean4.getE(), 1);
        Assert.assertEquals(bean1.isF(), bean4.getF());
        Assert.assertEquals(bean1.getG(), bean4.getG());
        Assert.assertEquals(bean1.getH(), (short) bean4.getH());
        Assert.assertEquals(bean1.getI(), (byte) bean4.getI());

    }


    @Test
    public void no_copy_source_to_target() {

        Bean6 bean6 = new Bean6();
        bean6.setA(123);
        bean6.setB(456);

        Convertor<Bean6, Bean7> convertor = FastConvertorBuilder.newBuilder(Bean6.class, Bean7.class).build();

        Bean7 bean7 = convertor.toTarget(bean6);

        Assert.assertEquals(bean6.getA(), bean7.getA());
        Assert.assertNotEquals(bean6.getB(), bean7.getB());
    }

    @Test
    public void no_copy_target_to_source() {

        Bean7 bean7 = new Bean7();
        bean7.setA(123);
        bean7.setB(456);

        Convertor<Bean6, Bean7> convertor = FastConvertorBuilder.newBuilder(Bean6.class, Bean7.class).build();

        Bean6 bean6 = convertor.toSource(bean7);

        Assert.assertEquals(bean6.getA(), bean7.getA());
        Assert.assertNotEquals(bean6.getB(), bean7.getB());

    }

    @Test
    public void extend_source_to_target() {

        Bean1 bean1 = new Bean1();
        bean1.setA(123);
        bean1.setB(222L);
        bean1.setC(1.2D);
        bean1.setD('A');
        bean1.setE(4.2F);
        bean1.setF(true);
        bean1.setG("hello");
        bean1.setH((short) 2);
        bean1.setI((byte) 3);


        Convertor<Bean1, Bean9> convertor = FastConvertorBuilder.newBuilder(Bean1.class, Bean9.class).build();

        Bean9 bean9 = convertor.toTarget(bean1);

        Assert.assertEquals(bean1.getA(), bean9.getA());
        Assert.assertEquals(bean1.getB(), bean9.getB());
        Assert.assertEquals(bean1.getC(), bean9.getC(), 1);
        Assert.assertEquals(bean1.getD(), bean9.getD());
        Assert.assertEquals(bean1.getE(), bean9.getE(), 1);
        Assert.assertEquals(bean1.isF(), bean9.isF());
        Assert.assertEquals(bean1.getG(), bean9.getG());
        Assert.assertEquals(bean1.getH(), bean9.getH());
        Assert.assertEquals(bean1.getI(), bean9.getI());


    }

    @Test
    public void extend_target_to_source() {

        Bean9 bean9 = new Bean9();
        bean9.setA(123);
        bean9.setB(222L);
        bean9.setC(1.2D);
        bean9.setD('A');
        bean9.setE(4.2F);
        bean9.setF(true);
        bean9.setG("hello");
        bean9.setH((short) 2);
        bean9.setI((byte) 3);

        Convertor<Bean1, Bean9> convertor = FastConvertorBuilder.newBuilder(Bean1.class, Bean9.class).build();

        Bean1 bean1 = convertor.toSource(bean9);

        Assert.assertEquals(bean1.getA(), bean9.getA());
        Assert.assertEquals(bean1.getB(), bean9.getB());
        Assert.assertEquals(bean1.getC(), bean9.getC(), 1);
        Assert.assertEquals(bean1.getD(), bean9.getD());
        Assert.assertEquals(bean1.getE(), bean9.getE(), 1);
        Assert.assertEquals(bean1.isF(), bean9.isF());
        Assert.assertEquals(bean1.getG(), bean9.getG());
        Assert.assertEquals(bean1.getH(), bean9.getH());
        Assert.assertEquals(bean1.getI(), bean9.getI());

    }


    @Test
    public void extend_root_source_to_target() {

        Bean1 bean1 = new Bean1();
        bean1.setA(123);
        bean1.setB(222L);
        bean1.setC(1.2D);
        bean1.setD('A');
        bean1.setE(4.2F);
        bean1.setF(true);
        bean1.setG("hello");
        bean1.setH((short) 2);
        bean1.setI((byte) 3);


        Convertor<Bean1, Bean9> convertor = FastConvertorBuilder.newBuilder(Bean1.class, Bean9.class).targetRootClass
                (Bean8.class).build();

        Bean9 bean9 = convertor.toTarget(bean1);

        Assert.assertNotEquals(bean1.getA(), bean9.getA());
        Assert.assertNotEquals(bean1.getB(), bean9.getB());
        Assert.assertNotEquals(bean1.getC(), bean9.getC(), 1);
        Assert.assertNotEquals(bean1.getD(), bean9.getD());
        Assert.assertEquals(bean1.getE(), bean9.getE(), 1);
        Assert.assertEquals(bean1.isF(), bean9.isF());
        Assert.assertEquals(bean1.getG(), bean9.getG());
        Assert.assertEquals(bean1.getH(), bean9.getH());
        Assert.assertEquals(bean1.getI(), bean9.getI());


    }

    @Test
    public void extend_root_target_to_source() {

        Bean9 bean9 = new Bean9();
        bean9.setA(123);
        bean9.setB(222L);
        bean9.setC(1.2D);
        bean9.setD('A');
        bean9.setE(4.2F);
        bean9.setF(true);
        bean9.setG("hello");
        bean9.setH((short) 2);
        bean9.setI((byte) 3);

        Convertor<Bean1, Bean9> convertor = FastConvertorBuilder.newBuilder(Bean1.class, Bean9.class).targetRootClass
                (Bean8.class).build();

        Bean1 bean1 = convertor.toSource(bean9);

        Assert.assertNotEquals(bean1.getA(), bean9.getA());
        Assert.assertNotEquals(bean1.getB(), bean9.getB());
        Assert.assertNotEquals(bean1.getC(), bean9.getC(), 1);
        Assert.assertNotEquals(bean1.getD(), bean9.getD());
        Assert.assertEquals(bean1.getE(), bean9.getE(), 1);
        Assert.assertEquals(bean1.isF(), bean9.isF());
        Assert.assertEquals(bean1.getG(), bean9.getG());
        Assert.assertEquals(bean1.getH(), bean9.getH());
        Assert.assertEquals(bean1.getI(), bean9.getI());

    }


    @Test
    public void different_type_same_name_source_to_target() {

        Bean1 bean1 = new Bean1();
        bean1.setA(123);
        bean1.setB(456);

        Convertor<Bean1, Bean10> convertor = FastConvertorBuilder.newBuilder(Bean1.class, Bean10.class).build();

        Bean10 bean10 = convertor.toTarget(bean1);

        Assert.assertEquals(bean1.getA(), bean10.getA());
        Assert.assertNotEquals(bean1.getB(), bean10.getB());
    }

    @Test
    public void different_type_same_name_target_to_source() {

        Bean10 bean10 = new Bean10();
        bean10.setA(123);
        bean10.setB(456);

        Convertor<Bean1, Bean10> convertor = FastConvertorBuilder.newBuilder(Bean1.class, Bean10.class).build();

        Bean1 bean1 = convertor.toSource(bean10);

        Assert.assertEquals(bean1.getA(), bean10.getA());
        Assert.assertNotEquals(bean1.getB(), bean10.getB());

    }


    @Test
    public void explicit_alias_source_to_target() {

        Bean1 bean1 = new Bean1();
        bean1.setA(123);
        bean1.setB(222L);
        bean1.setC(1.2D);
        bean1.setD('A');

        Convertor<Bean1, Bean11> convertor = FastConvertorBuilder.newBuilder(Bean1.class, Bean11.class)
                .addTargetAlias("bb", "b").addTargetAlias("cc", "c")
                .addTargetAlias("dd", "ddd").build();

        Bean11 bean11 = convertor.toTarget(bean1);

        Assert.assertNotEquals(bean1.getA(), bean11.getAa());
        Assert.assertEquals(bean1.getB(), bean11.getBb());
        Assert.assertEquals(bean1.getC(), bean11.getCc(), 1);
        Assert.assertNotEquals(bean1.getD(), bean11.getDd());
    }


    @Test
    public void explicit_alias_source_to_source() {

        Bean11 bean11 = new Bean11();
        bean11.setAa(123);
        bean11.setBb(222L);
        bean11.setCc(1.2D);
        bean11.setDd('A');

        Convertor<Bean1, Bean11> convertor = FastConvertorBuilder.newBuilder(Bean1.class, Bean11.class)
                .addTargetAlias("bb", "b").addTargetAlias("cc", "c")
                .addTargetAlias("dd", "ddd").build();

        Bean1 bean1 = convertor.toSource(bean11);

        Assert.assertNotEquals(bean1.getA(), bean11.getAa());
        Assert.assertEquals(bean1.getB(), bean11.getBb());
        Assert.assertEquals(bean1.getC(), bean11.getCc(), 1);
        Assert.assertNotEquals(bean1.getD(), bean11.getDd());
    }
}


