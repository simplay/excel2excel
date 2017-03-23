import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

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
                TestHelper.getWritableFilePath(),
                TestHelper.getMappingFilePath(),
                "",
                "src/test/data/config.txt"
        };
        Properties.clear();
        Properties.initialize(args);

        XlsxExcelFile src = new XlsxExcelFile(Properties.getFromExcelFilePath(), 0);
        XlsxExcelFile dst = new XlsxExcelFile(Properties.getToExcelFilePath1(), 0);


        ArrayList<Excel> excels = new ArrayList<>();
        excels.add(dst);

        new Consolidator(Properties.getMappingFilePath(), src, excels);
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
                TestHelper.getWritableFilePath(),
                TestHelper.getMappingFilePath(),
                "",
                "src/test/data/config.txt"
        };
        Properties.clear();
        Properties.initialize(args);

        XlsxExcelFile src = new XlsxExcelFile(Properties.getFromExcelFilePath(), 0);
        XlsxExcelFile dst = new XlsxExcelFile(Properties.getToExcelFilePath1(), 0);

        ArrayList<Excel> excels = new ArrayList<>();
        excels.add(dst);

        new Consolidator("src/test/data/mappingsWithConstant.txt", src, excels);
        dst.save();

        XlsxExcelFile excel = new XlsxExcelFile(TestHelper.getWritableFilePath(), 0);
        assertEquals("foobar1", excel.getCellValue(0, 0).toString());
        assertEquals("foobar2", excel.getCellValue(0, 1).toString());
        assertEquals("foobar3", excel.getCellValue(0, 2).toString());
        assertEquals("foobar4", excel.getCellValue(1, 0).toString());
        assertEquals("foobar5", excel.getCellValue(1, 1).toString());
        assertEquals("foobar6", excel.getCellValue(1, 2).toString());
        assertEquals("foobar7", excel.getCellValue(2, 0).toString());
        assertEquals("foobar8", excel.getCellValue(2, 1).toString());
        assertEquals("foobar9", excel.getCellValue(2, 2).toString());

        assertNotEquals("foobar1", excel.getCellValue(0, 1).toString());
    }

    // TODO test growing columns
    // TODO test with mixed cases

    @Test
    public void testCopyWithScalaTranslation() {
        String[] args = {
                "src/test/data/scale_values.xlsx",
                TestHelper.getWritableFilePath(),
                TestHelper.getWritableFilePath(),
                "src/test/data/mappingsWithScale.txt",
                "src/test/data/scale_values.txt",
                "src/test/data/config.txt"
        };
        Properties.clear();
        Properties.initialize(args);
        XlsxExcelFile src = new XlsxExcelFile(Properties.getFromExcelFilePath());
        XlsxExcelFile dst = new XlsxExcelFile(Properties.getToExcelFilePath1());

        ArrayList<Excel> excels = new ArrayList<>();
        excels.add(dst);

        new Consolidator(Properties.getMappingFilePath(), src, excels);
        dst.save();

        XlsxExcelFile excel = new XlsxExcelFile(TestHelper.getWritableFilePath(), 0);
        assertEquals("1.0", excel.getCellValue(0, 0).toString());
        assertEquals("2.0", excel.getCellValue(0, 1).toString());
        assertEquals("3.0", excel.getCellValue(0, 2).toString());
        assertEquals("10.0", excel.getCellValue(1, 0).toString());
        assertEquals("11.0", excel.getCellValue(1, 1).toString());
        assertEquals("12.0", excel.getCellValue(1, 2).toString());
        assertEquals("23.0", excel.getCellValue(2, 0).toString());
        assertEquals("34.0", excel.getCellValue(2, 1).toString());
        assertEquals("45.0", excel.getCellValue(2, 2).toString());
    }
}
