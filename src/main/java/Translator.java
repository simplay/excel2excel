import java.util.ArrayList;

/**
 * A Translator offers to translate symbolic values to numeric values
 * according to a certain scale.
 */
public class Translator extends FileReader{
    private static Translator instance;

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

    public Translator() {
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

    @Override
    protected void processLine(String line) {
        String[] row = line.split(" ");
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
