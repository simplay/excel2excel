import java.util.ArrayList;

/**
 * A Translator offers to translate symbolic values to numeric values
 * according to a certain scale.
 */
public class Translator extends FileReader{

    // singleton instance
    private static Translator instance;

    // Collection of read scale instances
    private final ArrayList<Scale> scales = new ArrayList<Scale>() ;

    /**
     * Get all scales
     *
     * @return scale collection
     */
    public ArrayList<Scale> getScales() {
        return scales;
    }

    /**
     * Fetch the Translator singleton
     *
     * @return singleton
     */
    public static Translator getInstance() {
        if (instance == null) {
            instance = new Translator();
        }
        return instance;
    }

    private Translator() {
        readFile(Properties.getScaleValuesFilePath());
    }

    /**
     * Lookup the the numeric values from target scale, given
     * a certain symbolic representation.
     * 
     * @param lookupRow
     * @param toBeTranslated
     * @return
     */
    public static String lookup(int lookupRow, String toBeTranslated) {
        Scale scale = getInstance().getScales().get(lookupRow);
        return Integer.toString(scale.getValueByLabel(toBeTranslated));
    }

    /**
     * Extracts the content of a scale file
     * The items are supposed to be whitespace separated.
     * @info: Every scale instance is formed by a symbolic-
     *  and a numerical value. Thus, a valid scale file line
     *  consists of 2*n elements (i.e. an even number).
     *
     * @param line a read file line.
     */
    @Override
    protected void processLine(String line) {
        String[] row = line.split(Properties.WHITESPACE_SEPARATOR);
        int itemCount = row.length / 2;
        Scale scale = new Scale();
        for (int k = 0; k < itemCount; k++) {
            String label = row[k];
            int value = Integer.parseInt(row[k + itemCount]);
            scale.appendItem(label, value);
        }
        scales.add(scale);
    }
}
