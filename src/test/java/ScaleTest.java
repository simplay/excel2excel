import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ScaleTest {

    @Test
    public void testCanFetchAppendedItem() {
        Scale s = new Scale();

        String label = "a_fancy_label";
        int numericLabelValue = 1337;
        s.appendItem(label, numericLabelValue);

        assertEquals(numericLabelValue, s.getValueByLabel(label));
    }

    @Test
    public void testCanFetchMultipleAppendedItems() {
        Scale s = new Scale();

        String[] labels = {"foo", "bar", "baz"};
        int[] values = {11, 22, 33};

        for (int k = 0 ; k < values.length; k++) {
            s.appendItem(labels[k], values[k]);
        }

        for (int k = 0 ; k < values.length; k++) {
            assertEquals(values[k], s.getValueByLabel(labels[k]));
        }
    }

    @Test
    public void testCannotFetchInexistentItem() {
        Scale s = new Scale();
        assertEquals(-1, s.getValueByLabel("male"));
    }

    @Test
    public void testCanOnlyFetchAppendedItems() {
        Scale s = new Scale();
        assertEquals(-1, s.getValueByLabel("male"));
        assertEquals(-1, s.getValueByLabel("female"));

        s.appendItem("female", 2);
        s.appendItem("male", 1);

        assertEquals(1, s.getValueByLabel("male"));
        assertEquals(2, s.getValueByLabel("female"));
    }

    @Test
    public void testAppendItemIgnoresAddingSameItemManyTimes() {
        Scale s = new Scale();

        s.appendItem("happy", 5);
        s.appendItem("happy", 2);
        s.appendItem("happy", 3);
        s.appendItem("happy", 13);

        assertEquals(5, s.getValueByLabel("happy"));
    }
}
