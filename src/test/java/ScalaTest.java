import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ScalaTest {

    @Test
    public void testCanFetchAppendedItem() {
        Scala s = new Scala();

        String label = "a_fancy_label";
        int numericLabelValue = 1337;
        s.appendItem(label, numericLabelValue);

        assertEquals(numericLabelValue, s.getValueByLabel(label));
    }

    @Test
    public void testCanFetchMultipleAppendedItems() {
        Scala s = new Scala();

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
        Scala s = new Scala();
        assertEquals(-1, s.getValueByLabel("male"));
    }

    @Test
    public void testCanOnlyFetchAppendedItems() {
        Scala s = new Scala();
        assertEquals(-1, s.getValueByLabel("male"));
        assertEquals(-1, s.getValueByLabel("female"));

        s.appendItem("female", 2);
        s.appendItem("male", 1);

        assertEquals(1, s.getValueByLabel("male"));
        assertEquals(2, s.getValueByLabel("female"));
    }

    @Test
    public void testAppendItemIgnoresAddingSameItemManyTimes() {
        Scala s = new Scala();

        s.appendItem("happy", 5);
        s.appendItem("happy", 2);
        s.appendItem("happy", 3);
        s.appendItem("happy", 13);

        assertEquals(5, s.getValueByLabel("happy"));
    }
}
