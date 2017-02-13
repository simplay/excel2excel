import java.util.ArrayList;

/**
 * A Scala maps a series of abstract symbols to numeric values.
 * Eg. Male (m) and Female (f) can be mapped to integer values
 * (1 and 2) by defining a scala like the following:
 * Scala s = new Scala();
 * s.appendItem("m", "1")
 * s.appendItem("f", "2")
 *
 * Attempts to append an already existing scala value will be ignored.
 */
public class Scala {
    private ArrayList<String> labels;
    private ArrayList<Integer> values;

    public Scala() {
        labels = new ArrayList<String>();
        values = new ArrayList<Integer>();
    }

    /**
     * Append a new item to the scala. An item consists of a symbolic
     * label and an integer values.
     * @param label symbolic representation of scala values.
     * @param value numeric scala values.
     */
    public void appendItem(String label, int value) {
        if (!labels.contains(label)) {
            labels.add(label);
            values.add(value);
        }
    }

    /**
     * Obtain the numeric integer values of a given symbolic
     * scala values.
     *
     * @param queryLabel the symbol for which we want to lookup
     *  its numeric values in the scala.
     * @return the numeric scala values. -1 indicates that no such symbolic
     *  scala representation has been found.
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
