
package cz.habarta.typescript.generator;

import java.io.File;
import java.util.Arrays;
import org.junit.Test;


public class PersonTest {
    
    @Test
    public void test() {
        TypeScriptGenerator.generateTypeScript(Arrays.asList(Person.class), new Settings(), new File("target/person.d.ts"));
    }

}
