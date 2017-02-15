import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExcelFileTest {

    @Test
    public void testCanReadExistingFile() {
        ExcelFile excel = new ExcelFile(TestHelper.getReadOnlyFilePath(), 0);
        assertTrue(excel != null);
    }

    @Before
    public void initialize() {
        TestHelper.prepare();
    }

    @After
    public void cleanup() {
        TestHelper.cleanup();
    }

    @Test
    public void testCanReadExcelContent() {
        ExcelFile excel = new ExcelFile(TestHelper.getReadOnlyFilePath(), 0);
        assertEquals("1", excel.getCellValue(0, 0));
        assertEquals("2", excel.getCellValue(0, 1));
        assertEquals("3", excel.getCellValue(0, 2));
        assertEquals("4", excel.getCellValue(1, 0));
        assertEquals("5", excel.getCellValue(1, 1));
        assertEquals("6", excel.getCellValue(1, 2));
        assertEquals("7", excel.getCellValue(2, 0));
        assertEquals("8", excel.getCellValue(2, 1));
        assertEquals("9", excel.getCellValue(2, 2));
    }

    @Test
    public void testSimpleConstructurUsesFirstExcelSheet() {
        ExcelFile excel = new ExcelFile(TestHelper.getReadOnlyFilePath(), 0);
        ExcelFile excelZero = new ExcelFile(TestHelper.getReadOnlyFilePath());
        assertEquals(excelZero.getCellValue(0, 0), excel.getCellValue(0, 0));
        assertEquals(excelZero.getCellValue(0, 1), excel.getCellValue(0, 1));
        assertEquals(excelZero.getCellValue(0, 2), excel.getCellValue(0, 2));
        assertEquals(excelZero.getCellValue(1, 0), excel.getCellValue(1, 0));
        assertEquals(excelZero.getCellValue(1, 1), excel.getCellValue(1, 1));
        assertEquals(excelZero.getCellValue(1, 2), excel.getCellValue(1, 2));
        assertEquals(excelZero.getCellValue(2, 0), excel.getCellValue(2, 0));
        assertEquals(excelZero.getCellValue(2, 1), excel.getCellValue(2, 1));
        assertEquals(excelZero.getCellValue(2, 2), excel.getCellValue(2, 2));
    }

    @Test
    public void testAccessingInexistentCellYieldsNull() {
        ExcelFile excel = new ExcelFile(TestHelper.getReadOnlyFilePath(), 0);
        assertEquals(null, excel.getCellValue(1337, 1337));
        assertEquals(null, excel.getCellValue(0, 4));
        assertEquals(null, excel.getCellValue(4, 0));
    }

    @Test
    public void testCanWriteCell() {
        ExcelFile excel = new ExcelFile(TestHelper.getWritableFilePath(), 0);
        assertEquals(null, excel.getCellValue(0, 0));
        excel.writeCell("foobar1337", 0, 0);
        assertEquals("foobar1337", excel.getCellValue(0, 0));
    }

    @Test
    public void testWrieDoesNotOverwriteFile() {
        ExcelFile excel = new ExcelFile(TestHelper.getWritableFilePath(), 0);
        assertEquals(null, excel.getCellValue(0, 0));
        excel.writeCell("foobar1337", 0, 0);
        excel = new ExcelFile(TestHelper.getWritableFilePath(), 0);
        assertEquals(null, excel.getCellValue(0, 0));
    }

    @Test
    public void testSave() {
        ExcelFile excel = new ExcelFile(TestHelper.getWritableFilePath(), 0);
        excel.writeCell("foobar1337", 0, 0);
        assertEquals("foobar1337", excel.getCellValue(0, 0));
        excel.save();
        excel = new ExcelFile(TestHelper.getWritableFilePath(), 0);
        assertEquals("foobar1337", excel.getCellValue(0, 0));
    }

    @Test
    public void testFindEmptyCellColumnAtFixedRowWithoutAddition() {
        ExcelFile excel = new ExcelFile(TestHelper.getWritableFilePath(), 0);
        assertEquals(0, excel.findEmptyCellColumnAtFixedRow(0, 0));
        assertEquals(1, excel.findEmptyCellColumnAtFixedRow(0, 1));
        assertEquals(2, excel.findEmptyCellColumnAtFixedRow(0, 2));
        assertEquals(20, excel.findEmptyCellColumnAtFixedRow(0, 20));
        assertEquals(20, excel.findEmptyCellColumnAtFixedRow(20, 20));
    }

    @Test
    public void testFindEmptyCellColumnAtFixedRowWithAdditions() {
        ExcelFile excel = new ExcelFile(TestHelper.getWritableFilePath(), 0);
        assertEquals(0, excel.findEmptyCellColumnAtFixedRow(0, 0));
        assertEquals(1, excel.findEmptyCellColumnAtFixedRow(0, 1));
        assertEquals(2, excel.findEmptyCellColumnAtFixedRow(0, 2));
        assertEquals(3, excel.findEmptyCellColumnAtFixedRow(0, 3));

        excel.writeCell("foobar1337", 0, 0);

        assertEquals(1, excel.findEmptyCellColumnAtFixedRow(0, 0));
        assertEquals(1, excel.findEmptyCellColumnAtFixedRow(0, 1));
        assertEquals(2, excel.findEmptyCellColumnAtFixedRow(0, 2));
        assertEquals(3, excel.findEmptyCellColumnAtFixedRow(0, 3));

        excel.writeCell("foobar1337", 0, 1);

        assertEquals(2, excel.findEmptyCellColumnAtFixedRow(0, 0));
        assertEquals(2, excel.findEmptyCellColumnAtFixedRow(0, 1));
        assertEquals(2, excel.findEmptyCellColumnAtFixedRow(0, 2));
        assertEquals(3, excel.findEmptyCellColumnAtFixedRow(0, 3));
    }
}
