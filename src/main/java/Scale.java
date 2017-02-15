import java.util.ArrayList;

/**
 * A Scale maps a series of abstract symbols to numeric values.
 * Eg. Male (m) and Female (f) can be mapped to integer values
 * (1 and 2) by defining a scale like the following:
 * Scale s = new Scale();
 * s.appendItem("m", "1")
 * s.appendItem("f", "2")
 *
 * Attempts to append an already existing scale value will be ignored.
 */
public class Scale {
    private ArrayList<String> labels;
    private ArrayList<Integer> values;

    public Scale() {
        labels = new ArrayList<String>();
        values = new ArrayList<Integer>();
    }

    /**
     * Append a new item to the scale. An item consists of a symbolic
     * label and an integer values.
     * @param label symbolic representation of scale values.
     * @param value numeric scale values.
     */
    public void appendItem(String label, int value) {
        if (!labels.contains(label)) {
            labels.add(label);
            values.add(value);
        }
    }

    /**
     * Obtain the numeric integer values of a given symbolic
     * scale values.
     *
     * @param queryLabel the symbol for which we want to lookup
     *  its numeric values in the scale.
     * @return the numeric scale values. -1 indicates that no such symbolic
     *  scale representation has been found.
     */
    public int getValueByLabel(String queryLabel) {
        int idx = 0;
        for (String label : labels) {
            if (label.equals(queryLabel)) {
                return values.get(idx);
            }
            idx++;
        }
        return -1;
    }
}
