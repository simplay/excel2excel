import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CellMappingTest {

    @Test
    public void testCreateSimpleFromToMapping() {
        CellMapping m = new CellMapping(1, 2, 3, 4);

        assertEquals(1, m.getFromRowIndex());
        assertEquals(2, m.getFromColumnIndex());
        assertEquals(3, m.getToRowIndex());
        assertEquals(4, m.getToColumnIndex());

        assertFalse(m.hasTranslation());
        assertEquals(-2, m.getTranslationRow());

        assertFalse(m.hasDefaultValue());
        assertEquals("", m.getDefaultValue());

        assertFalse(m.usesOffset());
    }

    @Test
    public void testCreateWithDefault() {
        CellMapping m = new CellMapping(1, 2, true, "foobar");

        assertEquals(-1, m.getFromRowIndex());
        assertEquals(-1, m.getFromColumnIndex());
        assertEquals(1, m.getToRowIndex());
        assertEquals(2, m.getToColumnIndex());

        assertFalse(m.hasTranslation());
        assertEquals(-2, m.getTranslationRow());

        assertTrue(m.hasDefaultValue());
        assertEquals("foobar", m.getDefaultValue());

        assertTrue(m.usesOffset());
    }

    @Test
    public void testCreateWithCascadingColumn() {
        CellMapping m = new CellMapping(1, 2, 3, 4, false);

        assertEquals(1, m.getFromRowIndex());
        assertEquals(2, m.getFromColumnIndex());
        assertEquals(3, m.getToRowIndex());
        assertEquals(4, m.getToColumnIndex());

        assertFalse(m.hasTranslation());
        assertEquals(-2, m.getTranslationRow());

        assertFalse(m.hasDefaultValue());
        assertEquals("", m.getDefaultValue());

        assertFalse(m.usesOffset());
    }

    @Test
    public void testCreateWithTranslationRow() {
        CellMapping m = new CellMapping(1, 2, 3, 4, true, 5);

        assertEquals(1, m.getFromRowIndex());
        assertEquals(2, m.getFromColumnIndex());
        assertEquals(3, m.getToRowIndex());
        assertEquals(4, m.getToColumnIndex());

        assertTrue(m.hasTranslation());
        assertEquals(5, m.getTranslationRow());

        assertFalse(m.hasDefaultValue());
        assertEquals("", m.getDefaultValue());

        assertTrue(m.usesOffset());
    }
}