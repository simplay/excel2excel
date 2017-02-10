import java.util.ArrayList;

/**
 * A Translator allow to translate symbolic values to numeric values
 * according to a certain scala.
 */
public class Translator extends FileReader{
    private static Translator instance;

    private final ArrayList<Scala> scalas = new ArrayList<Scala>() ;

    public ArrayList<Scala> getScalas() {
        return scalas;
    }

    public static Translator getInstance() {
        if (instance == null) {
            instance = new Translator();
        }
        return instance;
    }

    public Translator() {
        readFile(Properties.getScalaValuesFilePath());
    }

    /**
     * Lookup the the numeric values from target scala, given
     * a certain symbolic representation.
     * 
     * @param lookupRow
     * @param toBeTranslated
     * @return
     */
    public static String lookup(int lookupRow, String toBeTranslated) {
        Scala scala = getInstance().getScalas().get(lookupRow);
        return Integer.toString(scala.getValueByLabel(toBeTranslated));
    }

    @Override
    protected void processLine(String line) {
        String[] row = line.split(" ");
        int itemCount = row.length / 2;
        Scala scala = new Scala();
        for (int k = 0; k < itemCount; k++) {
            String label = row[k];
            int value = Integer.parseInt(row[k + itemCount]);
            scala.appendItem(label, value);
        }
        scalas.add(scala);
    }
}
