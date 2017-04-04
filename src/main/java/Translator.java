import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.CellType;

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
    public static CellContent lookup(int lookupRow, String toBeTranslated) {
        Scale scale = getInstance().getScales().get(lookupRow);

        // normalize to be translated string
        String normalizedString = normalizedInputTranslation(toBeTranslated);
        return scale.getValueByLabel(normalizedString);
    }

    public static CellContent lookup(int lookupRow, CellContent fromValue) {
    	if(fromValue.type == CellType.STRING) {
    		fromValue.type = CellType.NUMERIC;
        	fromValue = Translator.lookup(lookupRow, fromValue.string);
    	} else if(fromValue.type == CellType.NUMERIC) {
    		//FIXME: currently a bit of a hack, eventually we should probably handle numeric types with their cell formatting
    		//DecimalFormat rounds the numeric to the number of #, so it will determine some source values equal that shouldn't have been
    		//it makes more sense to treat the numerics as they appear in the Excel file which is determined by the cell's formatting.
    		fromValue = Translator.lookup(lookupRow, new DecimalFormat("0.######").format(fromValue.numeric));
    	}
    	return fromValue;
    }

    /**
     * Normalizes a composite scale. Composite scales are separated by a `/` symbol.
     * The input can exhibit an arbitrary number of whitespace separation, i.e.
     * this method transforms
     *  (\s)(?)(\s)*((/)(\s)*(?)(\s))+
     * to
     * (?)((/)(?))+
     *
     * @param toBeTranslated lookup string
     * @return normalized lookup string
     */
    public static String normalizedInputTranslation(String toBeTranslated) {
        String normalizedString = toBeTranslated;
        normalizedString = normalizedString.replace(" ", "");

        // in case it we query for a composite symbolic value
        // E.g. "V / A" instead of just "V" or "A"
        // accept all inputs (V)(\s)*(/)(\s)*(A)
        if (normalizedString.contains("/")) {
            String[] tokens = normalizedString.split("/");
            int counter = 0;
            for (String token : tokens) {
                tokens[counter] = token.replace(" ", "");
                counter++;
            }

            String tmp = "";
            for (int k = 0; k < tokens.length - 1; k++) {
                tmp += tokens[k] + "/";
            }
            tmp += tokens[tokens.length - 1];
            normalizedString = tmp;
        }
        return normalizedString;
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
            CellContent value = new CellContent(row[k + itemCount]);
            scale.appendItem(label, value);
        }
        scales.add(scale);
    }
}
