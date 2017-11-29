import bean.simple.Bean1;
import bean.simple.Bean2;
import com.elin4it.convert.util.CodeInstanceDelegate;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: CodeInstanceDelegateTest.java , v 0.1 2017/11/27 下午2:18 ZhouFeng Exp $
 */
public class CodeInstanceDelegateTest {


    @Test
    public void test_with_not_params() throws Exception {

        Method method = Bean1.class.getDeclaredMethod("getA");

        CodeInstanceDelegate codeInstanceDelegate = new CodeInstanceDelegate("bean1");

        Assert.assertEquals("bean1.getA();", codeInstanceDelegate.invoke(method).end());

    }

    @Test
    public void test_with_one_params() throws Exception {

        Method method = Bean1.class.getDeclaredMethod("getA");

        CodeInstanceDelegate codeInstanceDelegate = new CodeInstanceDelegate("bean1");

        Assert.assertEquals("bean1.getA(bean2);", codeInstanceDelegate.invoke(method, new CodeInstanceDelegate("bean2")).end());

    }

    @Test
    public void test_with_two_params() throws Exception {

        Method method = Bean1.class.getDeclaredMethod("getA");

        CodeInstanceDelegate codeInstanceDelegate = new CodeInstanceDelegate("bean1");

        Assert.assertEquals("bean1.getA(bean2,bean3);", codeInstanceDelegate.invoke(method, new CodeInstanceDelegate("bean2"), new
                CodeInstanceDelegate("bean3")).end());

    }

    @Test
    public void test_with_one_params_nested() throws Exception {

        Method method = Bean1.class.getDeclaredMethod("setA", int.class);

        Method method1 = Bean2.class.getDeclaredMethod("getA");

        CodeInstanceDelegate codeInstanceDelegate = new CodeInstanceDelegate("bean1");

        Assert.assertEquals("bean1.setA(bean2.getA());", codeInstanceDelegate.invoke(method, new CodeInstanceDelegate("bean2").invoke
                (method1)).end());

    }


    @Test
    public void test_method_name_with_not_params() throws Exception {

        CodeInstanceDelegate codeInstanceDelegate = new CodeInstanceDelegate("bean1");

        Assert.assertEquals("bean1.getA();", codeInstanceDelegate.invoke("getA").end());

    }

    @Test
    public void test_method_name_with_one_params() throws Exception {


        CodeInstanceDelegate codeInstanceDelegate = new CodeInstanceDelegate("bean1");

        Assert.assertEquals("bean1.getA(bean2);", codeInstanceDelegate.invoke("getA", new CodeInstanceDelegate("bean2")).end());

    }

    @Test
    public void test_method_name_with_two_params() throws Exception {


        CodeInstanceDelegate codeInstanceDelegate = new CodeInstanceDelegate("bean1");

        Assert.assertEquals("bean1.getA(bean2,bean3);", codeInstanceDelegate.invoke("getA", new CodeInstanceDelegate("bean2"), new
                CodeInstanceDelegate("bean3")).end());

    }

    @Test
    public void test_method_name_with_one_params_nested() throws Exception {

        CodeInstanceDelegate codeInstanceDelegate = new CodeInstanceDelegate("bean1");

        Assert.assertEquals("bean1.setA(bean2.getA());", codeInstanceDelegate.invoke("setA", new CodeInstanceDelegate("bean2").invoke
                ("getA")).end());

    }


}
