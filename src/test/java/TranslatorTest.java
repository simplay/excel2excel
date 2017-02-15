import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TranslatorTest {

    @After
    public void cleanup() {
        Properties.clear();
    }

    @Before
    public void initialize() {
        String[] args = {
                "src/test/data/scala_values.xlsx",
                TestHelper.getWritableFilePath(),
                "src/test/data/mappingsWithScala.txt",
                "src/test/data/scala_values.txt"
        };
        Properties.initialize(args);
    }

    @Test
    public void testLookup() {
        assertEquals("1", Translator.lookup(0, "R"));
        assertEquals("2", Translator.lookup(0, "G"));
        assertEquals("3", Translator.lookup(0, "B"));
        assertEquals("10", Translator.lookup(1, "X"));
        assertEquals("11", Translator.lookup(1, "Y"));
        assertEquals("12", Translator.lookup(1, "Z"));
        assertEquals("23", Translator.lookup(2, "U"));
        assertEquals("34", Translator.lookup(2, "V"));
        assertEquals("45", Translator.lookup(2, "W"));

        assertNotEquals("1", Translator.lookup(0, "G"));
        assertNotEquals("1", Translator.lookup(1, "R"));
    }

    @Test
    public void tesGetScalas() {
        ArrayList<Scala> scalas = Translator.getInstance().getScalas();
        String[] gtScalaSymbols = {"R", "G", "B", "X", "Y", "Z", "U", "V", "W"};
        int[] gtScalaValues = {1, 2, 3, 10, 11, 12, 23, 34, 45};

        int idx = 0;
        for (Scala scala : scalas) {
            int value1 = scala.getValueByLabel(gtScalaSymbols[idx]);
            int value2 = scala.getValueByLabel(gtScalaSymbols[idx + 1]);
            int value3 = scala.getValueByLabel(gtScalaSymbols[idx + 2]);
            assertEquals(gtScalaValues[idx], value1);
            assertEquals(gtScalaValues[idx + 1], value2);
            assertEquals(gtScalaValues[idx + 2], value3);
            idx += 3;
        }
    }

}
