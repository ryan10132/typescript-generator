
package cz.habarta.typescript.generator;

import cz.habarta.typescript.generator.parser.*;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;


public class Jackson1ParserTest {

    @Test
    public void test() {
        final Jackson1Parser jacksonParser = getJackson1Parser();
        final Class<?> bean = DummyBean.class;
        final Model model = jacksonParser.parseModel(bean);
        Assert.assertTrue(model.getBeans().size() > 0);
        final BeanModel beanModel = model.getBeans().get(0);
        System.out.println("beanModel: " + beanModel);
        Assert.assertEquals("DummyBean", beanModel.getBeanClass().getSimpleName());
        Assert.assertTrue(beanModel.getProperties().size() > 0);
        Assert.assertEquals("firstProperty", beanModel.getProperties().get(0).getName());
    }

    private static Jackson1Parser getJackson1Parser() {
        final Logger logger = Logger.getGlobal();
        final Settings settings = new Settings();
        return new Jackson1Parser(logger, settings, new ModelCompiler(logger, settings));
    }

}
