import bean.simple.Bean1;
import bean.simple.Bean2;
import com.elin4it.convert.util.InstanceDelegate;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: InstanceDelegateTest.java , v 0.1 2017/11/27 下午2:18 ZhouFeng Exp $
 */
public class InstanceDelegateTest {


    @Test
    public void test_with_not_params() throws Exception {

        Method method = Bean1.class.getDeclaredMethod("getA");

        InstanceDelegate instanceDelegate = new InstanceDelegate("bean1");

        Assert.assertEquals("bean1.getA();", instanceDelegate.invoke(method).end());

    }

    @Test
    public void test_with_one_params() throws Exception {

        Method method = Bean1.class.getDeclaredMethod("getA");

        InstanceDelegate instanceDelegate = new InstanceDelegate("bean1");

        Assert.assertEquals("bean1.getA(bean2);", instanceDelegate.invoke(method, new InstanceDelegate("bean2")).end());

    }

    @Test
    public void test_with_two_params() throws Exception {

        Method method = Bean1.class.getDeclaredMethod("getA");

        InstanceDelegate instanceDelegate = new InstanceDelegate("bean1");

        Assert.assertEquals("bean1.getA(bean2,bean3);", instanceDelegate.invoke(method, new InstanceDelegate("bean2")
                , new
                        InstanceDelegate("bean3")).end());

    }

    @Test
    public void test_with_one_params_nested() throws Exception {

        Method method = Bean1.class.getDeclaredMethod("setA", int.class);

        Method method1 = Bean2.class.getDeclaredMethod("getA");

        InstanceDelegate instanceDelegate = new InstanceDelegate("bean1");

        Assert.assertEquals("bean1.setA(bean2.getA());", instanceDelegate.invoke(method, new InstanceDelegate
                ("bean2").invoke
                (method1)).end());

    }


    @Test
    public void test_method_name_with_not_params() throws Exception {

        InstanceDelegate instanceDelegate = new InstanceDelegate("bean1");

        Assert.assertEquals("bean1.getA();", instanceDelegate.invoke("getA").end());

    }

    @Test
    public void test_method_name_with_one_params() throws Exception {


        InstanceDelegate instanceDelegate = new InstanceDelegate("bean1");

        Assert.assertEquals("bean1.getA(bean2);", instanceDelegate.invoke("getA", new InstanceDelegate("bean2")).end());

    }

    @Test
    public void test_method_name_with_two_params() throws Exception {


        InstanceDelegate instanceDelegate = new InstanceDelegate("bean1");

        Assert.assertEquals("bean1.getA(bean2,bean3);", instanceDelegate.invoke("getA", new InstanceDelegate("bean2")
                , new
                        InstanceDelegate("bean3")).end());

    }

    @Test
    public void test_method_name_with_one_params_nested() throws Exception {

        InstanceDelegate instanceDelegate = new InstanceDelegate("bean1");

        Assert.assertEquals("bean1.setA(bean2.getA());", instanceDelegate.invoke("setA", new InstanceDelegate
                ("bean2").invoke
                ("getA")).end());

    }

    @Test
    public void test_newInstance_no_params() throws Exception {
        InstanceDelegate instanceDelegate = InstanceDelegate.newInstance("java.lang.ArrayList");

        Assert.assertEquals("new java.lang.ArrayList();", instanceDelegate.end());

    }


    @Test
    public void test_newInstance_with_params() throws Exception {
        InstanceDelegate instanceDelegate = InstanceDelegate.newInstance("java.lang.ArrayList", InstanceDelegate.of
                ("a"), InstanceDelegate.of("b"));

        Assert.assertEquals("new java.lang.ArrayList(a,b);", instanceDelegate.end());

    }

    @Test
    public void assign() {
        InstanceDelegate instanceDelegate = InstanceDelegate.newInstance("java.lang.ArrayList", InstanceDelegate.of
                ("a"), InstanceDelegate.of("b"));

        Assert.assertEquals("java.util.List list = new java.lang.ArrayList(a,b);", instanceDelegate.assign("list",
                List.class));


    }

    @Test
    public void cast() {
        InstanceDelegate delegate = InstanceDelegate.of("a").cast(int.class);

        Assert.assertEquals("return (int)a;", delegate.thenReturn());
    }

}
