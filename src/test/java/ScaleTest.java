import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ScaleTest {

    @Test
    public void testCanFetchAppendedItem() {
        Scale s = new Scale();

        String label = "a_fancy_label";
        int numericLabelValue = 1337;
        s.appendItem(label, new CellContent(numericLabelValue));

        assertEquals(new CellContent(numericLabelValue), s.getValueByLabel(label));
    }

    @Test
    public void testCanFetchMultipleAppendedItems() {
        Scale s = new Scale();

        String[] labels = {"foo", "bar", "baz"};
        int[] values = {11, 22, 33};

        for (int k = 0 ; k < values.length; k++) {
            s.appendItem(labels[k], new CellContent(values[k]));
        }

        for (int k = 0 ; k < values.length; k++) {
            assertEquals(new CellContent(values[k]), s.getValueByLabel(labels[k]));
        }
    }

    @Test
    public void testCannotFetchInexistentItem() {
        Scale s = new Scale();
        assertEquals(null, s.getValueByLabel("male"));
    }

    @Test
    public void testCanOnlyFetchAppendedItems() {
        Scale s = new Scale();
        assertEquals(null, s.getValueByLabel("male"));
        assertEquals(null, s.getValueByLabel("female"));

        s.appendItem("female", new CellContent(2));
        s.appendItem("male", new CellContent(1));

        assertEquals(new CellContent(1), s.getValueByLabel("male"));
        assertEquals(new CellContent(2), s.getValueByLabel("female"));
    }

    @Test
    public void testAppendItemIgnoresAddingSameItemManyTimes() {
        Scale s = new Scale();

        s.appendItem("happy", new CellContent(5));
        s.appendItem("happy", new CellContent(2));
        s.appendItem("happy", new CellContent(3));
        s.appendItem("happy", new CellContent(13));

        assertEquals(new CellContent(5), s.getValueByLabel("happy"));
    }
}
