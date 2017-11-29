import bean.simple.Bean1;
import bean.simple.Bean2;
import com.elin4it.convert.FastConvertorBuilder;
import com.elin4it.convert.Convertor;
import com.elin4it.convert.SimpleConvertorBuilder;
import net.sf.cglib.beans.BeanCopier;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: PerformanceTest.java , v 0.1 2017/11/27 下午3:22 ZhouFeng Exp $
 */
public class PerformanceTest {


    @Test
    public void performanceTest() {
        Bean1 bean1 = new Bean1();


        Convertor<Bean1, Bean2> simpleConvertor = SimpleConvertorBuilder.newBuilder(Bean1.class, Bean2.class).build();
        Convertor<Bean1, Bean2> asmConvertor = FastConvertorBuilder.newBuilder(Bean1.class, Bean2.class).build();

        BeanCopier beanCopier = BeanCopier.create(Bean1.class, Bean2.class, false);

        int times = 1000000;


        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < times; i++) {
            list.add(i << 2 + 1);
        }

        long start = System.currentTimeMillis();


        for (int i = 0; i < times; i++) {
            bean1.setA(list.get(i));
            simpleConvertor.toTarget(bean1);
        }

        long end = System.currentTimeMillis();


        System.out.println("simpleConvertor spend time: " + (end - start) + " ms");


        start = System.currentTimeMillis();


        for (int i = 0; i < times; i++) {
            bean1.setA(list.get(i));
            Bean2 bean2 = new Bean2();
            bean2.setA(bean1.getA());
            bean2.setB(bean1.getB());
            Assert.assertEquals(bean1.getA(), bean2.getA());
        }

        end = System.currentTimeMillis();


        System.out.println("native spend time: " + (end - start) + " ms");


        start = System.currentTimeMillis();


        for (int i = 0; i < times; i++) {
            bean1.setA(list.get(i));

            Bean2 bean2 = new Bean2();
            beanCopier.copy(bean1, bean2, null);
            Assert.assertEquals(bean1.getA(), bean2.getA());
        }

        end = System.currentTimeMillis();


        System.out.println("beancopier spend time: " + (end - start) + " ms");


        start = System.currentTimeMillis();


        for (int i = 0; i < times; i++) {
            bean1.setA(list.get(i));
            Bean2 bean2 = asmConvertor.toTarget(bean1);
            Assert.assertEquals(bean1.getA(), bean2.getA());
        }

        end = System.currentTimeMillis();


        System.out.println("asmConvertor spend time: " + (end - start) + " ms");

    }

    @Test
    public void instanceTest() {

        int times = 100;

        long start = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {

            Convertor<Bean1, Bean2> asmConvertor = FastConvertorBuilder.newBuilder(Bean1.class, Bean2.class).build();
        }

        long end = System.currentTimeMillis();
        System.out.println("asmConvertor spend time: " + (end - start) + " ms");


        start = System.currentTimeMillis();

        for (int i = 0; i < times; i++) {

            BeanCopier beanCopier = BeanCopier.create(Bean1.class, Bean2.class, false);
        }

        end = System.currentTimeMillis();

        System.out.println("beancopire spend time: " + (end - start) + " ms");
    }


}
