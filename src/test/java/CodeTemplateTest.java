import bean.simple.Bean1;
import com.elin4it.convert.util.CodeTemplate;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: CodeTemplateTest.java , v 0.1 2017/11/27 上午11:47 ZhouFeng Exp $
 */
public class CodeTemplateTest {

    @Test
    public void get_return_no_param() {

        Assert.assertEquals("return;", CodeTemplate.getReturn());

    }

    @Test
    public void get_return() {

        Assert.assertEquals("return abc;", CodeTemplate.getReturn("abc"));

    }

    @Test
    public void instance_constructor_no_param() {

        Assert.assertEquals("bean.simple.Bean1 abc = new bean.simple.Bean1();", CodeTemplate.instance("abc",
                Bean1.class));

    }

    @Test
    public void instance_constructor_one_param() {

        Assert.assertEquals("bean.simple.Bean1 abc = new bean.simple.Bean1(a);", CodeTemplate.instance("abc",
                Bean1.class, "a"));

    }

    @Test
    public void instance_constructor_two_param() {

        Assert.assertEquals("bean.simple.Bean1 abc = new bean.simple.Bean1(a,b);", CodeTemplate.instance("abc",
                Bean1.class, "a", "b"));

    }

    @Test
    public void cast() {
        Assert.assertEquals("bean.simple.Bean1 abc = (bean.simple.Bean1)$1;", CodeTemplate.cast("$1", "abc",
                Bean1.class));
    }


}
