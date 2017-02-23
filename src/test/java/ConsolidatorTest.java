import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ConsolidatorTest {

    @After
    public void cleanup() {
        TestHelper.cleanup();
        Properties.clear();
    }

    @Before
    public void initialize() {
        TestHelper.prepare();
    }

    @Test
    public void testSimpleCopyCellsToOtherExcel() {
        String[] args = {
                TestHelper.getReadOnlyFilePath(),
                TestHelper.getWritableFilePath(),
                TestHelper.getMappingFilePath(),
                "",
                "src/test/data/config.txt"
        };
        Properties.clear();
        Properties.initialize(args);

        XlsxExcelFile src = new XlsxExcelFile(Properties.getFromExcelFilePath(), 0);
        XlsxExcelFile dst = new XlsxExcelFile(Properties.getToExcelFilePath(), 0);
        new Consolidator(Properties.getMappingFilePath(), src, dst);
        dst.save();

        XlsxExcelFile excel = new XlsxExcelFile(TestHelper.getWritableFilePath(), 0);
        assertEquals(src.getCellValue(0,0), excel.getCellValue(0, 0));
        assertEquals(src.getCellValue(0,1), excel.getCellValue(0, 1));
        assertEquals(src.getCellValue(0,2), excel.getCellValue(0, 2));
        assertEquals(src.getCellValue(1,0), excel.getCellValue(1, 0));
        assertEquals(src.getCellValue(1,1), excel.getCellValue(1, 1));
        assertEquals(src.getCellValue(1,2), excel.getCellValue(1, 2));
        assertEquals(src.getCellValue(2,0), excel.getCellValue(2, 0));
        assertEquals(src.getCellValue(2,1), excel.getCellValue(2, 1));
        assertEquals(src.getCellValue(2,2), excel.getCellValue(2, 2));

        assertNotEquals(src.getCellValue(0,0), excel.getCellValue(0, 1));
    }

    @Test
    public void testCopyConstantValuesToOtherExcel() {
        String[] args = {
                TestHelper.getReadOnlyFilePath(),
                TestHelper.getWritableFilePath(),
                TestHelper.getMappingFilePath(),
                "",
                "src/test/data/config.txt"
        };
        Properties.clear();
        Properties.initialize(args);

        XlsxExcelFile src = new XlsxExcelFile(Properties.getFromExcelFilePath(), 0);
        XlsxExcelFile dst = new XlsxExcelFile(Properties.getToExcelFilePath(), 0);
        new Consolidator("src/test/data/mappingsWithConstant.txt", src, dst);
        dst.save();

        XlsxExcelFile excel = new XlsxExcelFile(TestHelper.getWritableFilePath(), 0);
        assertEquals("foobar1", excel.getCellValue(0, 0));
        assertEquals("foobar2", excel.getCellValue(0, 1));
        assertEquals("foobar3", excel.getCellValue(0, 2));
        assertEquals("foobar4", excel.getCellValue(1, 0));
        assertEquals("foobar5", excel.getCellValue(1, 1));
        assertEquals("foobar6", excel.getCellValue(1, 2));
        assertEquals("foobar7", excel.getCellValue(2, 0));
        assertEquals("foobar8", excel.getCellValue(2, 1));
        assertEquals("foobar9", excel.getCellValue(2, 2));

        assertNotEquals("foobar1", excel.getCellValue(0, 1));
    }

    // TODO test growing columns
    // TODO test with mixed cases

    @Test
    public void testCopyWithScalaTranslation() {
        String[] args = {
                "src/test/data/scale_values.xlsx",
                TestHelper.getWritableFilePath(),
                "src/test/data/mappingsWithScale.txt",
                "src/test/data/scale_values.txt",
                "src/test/data/config.txt"
        };
        Properties.clear();
        Properties.initialize(args);
        XlsxExcelFile src = new XlsxExcelFile(Properties.getFromExcelFilePath());
        XlsxExcelFile dst = new XlsxExcelFile(Properties.getToExcelFilePath());
        new Consolidator(Properties.getMappingFilePath(), src, dst);
        dst.save();

        XlsxExcelFile excel = new XlsxExcelFile(TestHelper.getWritableFilePath(), 0);
        assertEquals("1", excel.getCellValue(0, 0));
        assertEquals("2", excel.getCellValue(0, 1));
        assertEquals("3", excel.getCellValue(0, 2));
        assertEquals("10", excel.getCellValue(1, 0));
        assertEquals("11", excel.getCellValue(1, 1));
        assertEquals("12", excel.getCellValue(1, 2));
        assertEquals("23", excel.getCellValue(2, 0));
        assertEquals("34", excel.getCellValue(2, 1));
        assertEquals("45", excel.getCellValue(2, 2));
    }
}
