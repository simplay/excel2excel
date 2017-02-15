import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
                TestHelper.getMappingFilePath()
        };
        Properties.initialize(args);

        ExcelFile src = new ExcelFile(Properties.getFromExcelFilePath(), 0);
        ExcelFile dst = new ExcelFile(Properties.getToExcelFilePath(), 0);
        new Consolidator(Properties.getMappingFilePath(), src, dst);
        dst.save();

        ExcelFile excel = new ExcelFile(TestHelper.getWritableFilePath(), 0);
        assertEquals(src.getCellValue(0,0), excel.getCellValue(0, 0));
        assertEquals(src.getCellValue(0,1), excel.getCellValue(0, 1));
        assertEquals(src.getCellValue(0,2), excel.getCellValue(0, 2));
        assertEquals(src.getCellValue(1,0), excel.getCellValue(1, 0));
        assertEquals(src.getCellValue(1,1), excel.getCellValue(1, 1));
        assertEquals(src.getCellValue(1,2), excel.getCellValue(1, 2));
        assertEquals(src.getCellValue(2,0), excel.getCellValue(2, 0));
        assertEquals(src.getCellValue(2,1), excel.getCellValue(2, 1));
        assertEquals(src.getCellValue(2,2), excel.getCellValue(2, 2));
    }
}
