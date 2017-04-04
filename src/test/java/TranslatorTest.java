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
                "src/test/data/scale_values.xlsx",
                TestHelper.getWritableFilePath(),
                "src/test/data/mappingsWithScale.txt",
                "src/test/data/scale_values.txt"
        };
        Properties.initialize(args);
    }

    @Test
    public void testLookup() {
        assertEquals(new CellContent(1), Translator.lookup(0, "R"));
        assertEquals(new CellContent(2), Translator.lookup(0, "G"));
        assertEquals(new CellContent(3), Translator.lookup(0, "B"));
        assertEquals(new CellContent(10), Translator.lookup(1, "X"));
        assertEquals(new CellContent(11), Translator.lookup(1, "Y"));
        assertEquals(new CellContent(12), Translator.lookup(1, "Z"));
        assertEquals(new CellContent(23), Translator.lookup(2, "U"));
        assertEquals(new CellContent(34), Translator.lookup(2, "V"));
        assertEquals(new CellContent(45), Translator.lookup(2, "W"));

        assertNotEquals(1, Translator.lookup(0, "G"));
        assertNotEquals(1, Translator.lookup(1, "R"));
    }

    @Test
    public void testGetScales() {
        ArrayList<Scale> scales = Translator.getInstance().getScales();
        String[] gtScalaSymbols = {"R", "G", "B", "X", "Y", "Z", "U", "V", "W"};
        int[] gtScalaValues = {1, 2, 3, 10, 11, 12, 23, 34, 45};

        int idx = 0;
        for (Scale scale : scales) {
            CellContent value1 = scale.getValueByLabel(gtScalaSymbols[idx]);
            CellContent value2 = scale.getValueByLabel(gtScalaSymbols[idx + 1]);
            CellContent value3 = scale.getValueByLabel(gtScalaSymbols[idx + 2]);
            assertEquals(new CellContent(gtScalaValues[idx]), value1);
            assertEquals(new CellContent(gtScalaValues[idx + 1]), value2);
            assertEquals(new CellContent(gtScalaValues[idx + 2]), value3);
            idx += 3;
        }
    }

    @Test
    public void testTokenNormalization2Labels() {
        assertEquals("A/B", Translator.normalizedInputTranslation("A/B"));
        assertEquals("A/B", Translator.normalizedInputTranslation("A / B"));
        assertEquals("A/B", Translator.normalizedInputTranslation("A/ B"));
        assertEquals("A/B", Translator.normalizedInputTranslation("A /B"));
        assertEquals("A/B", Translator.normalizedInputTranslation(" A / B "));
        assertEquals("A/B", Translator.normalizedInputTranslation(" A/ B "));
        assertEquals("A/B", Translator.normalizedInputTranslation(" A /B "));
        assertEquals("A/B", Translator.normalizedInputTranslation(" A/B "));
        assertEquals("A/B", Translator.normalizedInputTranslation(" A   /  B  "));
    }

    @Test
    public void testTokenNormalization3Labels() {
        assertEquals("A/B/C", Translator.normalizedInputTranslation("A/B/C"));
        assertEquals("A/B/C", Translator.normalizedInputTranslation("A / B / C"));
        assertEquals("A/B/C", Translator.normalizedInputTranslation("A/B  /C  "));
    }

}
