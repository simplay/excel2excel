import sun.rmi.runtime.Log;

import java.nio.file.Paths;

/**
 * Singleton carrying all project relevant properties
 * such as all file paths.
 *
 * Works with the user arguments, where the
 * 1st arguments is the relative file name / path to the FROM excel file (required)
 * 2nd arguments is the relative file name / path to the TO excel file (required)
 * 3rd arguments is the relative file name / path to the cell mapping file  (optional)
 * 4th arguments is the relative file name / path to the scale file (optional)
 *
 * Is properly initialized by calling #initialize
 */
public class Properties {
    private static Properties instance;
    private String[] userParameters;

    public static Properties getInstance(String[] userParameters) {
        if (instance == null) {
            instance = new Properties(userParameters);
        }
        return instance;
    }

    /**
     * Clear the internal state
     */
    public static void clear() {
        instance = null;
    }

    /**
     * Get the singleton
     *
     * @return properties singleton
     */
    public static Properties getInstance() {
        return getInstance(null);
    }

    public static void initialize(String[] userParameters) {
        getInstance(userParameters);
    }

    public Properties(String[] userParameters) {
        this.userParameters = userParameters;
    }

    public static String getMappingFilePath() {
        if (getInstance().hasContentAt(2)) {
            return getInstance().getPathAt(2);
        }
        return Paths.get("data", "mappings.txt").toString();
    }

    public static String getScaleValuesFilePath() {
        if (getInstance().hasContentAt(3)) {
            return getInstance().getPathAt(3);
        }
        return Paths.get("data", "scale_values.txt").toString();
    }

    public static String getFromExcelFilePath() {
        return getInstance().getPathAt(0);
    }

    public static String getToExcelFilePath() {
        return getInstance().getPathAt(1);
    }

    public String getPathAt(int idx) {
        return Paths.get(userParameters[idx]).toString();
    }

    public boolean hasContentAt(int idx) {
        if (userParameters == null ) return false;
        return idx <= userParameters.length - 1;
    }

    public static void reportPaths() {
        Logger.println("Using FROM PATH: " + getFromExcelFilePath());
        Logger.println("Using TO PATH: " + getToExcelFilePath());
        Logger.println("Using MAPPING PATH: " + getMappingFilePath());
        Logger.println("Using SCALE PATH: " + getScaleValuesFilePath());
    }
}
