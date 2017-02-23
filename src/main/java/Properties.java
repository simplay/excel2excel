import java.nio.file.Paths;
import java.util.HashMap;

/**
 * Singleton carrying all project relevant properties
 * such as all file paths.
 *
 * Works with the user arguments, where the
 * 1st arguments is the relative file name / path to the FROM excel file (required)
 * 2nd arguments is the relative file name / path to the TO excel file (required)
 * 3rd arguments is the relative file name / path to the cell mapping file  (optional)
 * 4th arguments is the relative file name / path to the scale file (optional)
 * 5th arguments is the relative file name / path to the config file (optional)
 *
 * Is properly initialized by calling #initialize
 */
public class Properties extends FileReader {

    // Elements are whitespace separated
    public static String WHITESPACE_SEPARATOR = " ";

    private static Properties instance;
    private String[] userParameters;
    private HashMap<String, String> properties;

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

    public static boolean runNormalMode() {
        if (!getInstance().hasLoadedProperties()) return true;
        return getInstance().getDebugMode().equals("0");
    }

    public static void initializeLogger() {
        boolean muteLooger = !getInstance().isRunningLogger();
        Logger.getInstance(muteLooger);
        Logger.println("Logger is muted: " + muteLooger);
    }

    public static boolean hasBaseExcelPaths() {
        return getInstance().getUseBasePaths().equals("1");
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
        this.properties = new HashMap<String, String>();
        readFile(getConfigPath());
    }

    private String getConfigPath() {
        if (hasContentAt(4)) {
            return getPathAt(4);
        }
        return Paths.get("data", "config.txt").toString();
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

    /**
     * Report assigned paths
     */
    public static void reportPaths() {
        Logger.println("The following paths are used: ");
        Logger.println(" + FROM PATH: " + getFromExcelFilePath());
        Logger.println(" + TO PATH: " + getToExcelFilePath());
        Logger.println(" + MAPPING PATH: " + getMappingFilePath());
        Logger.println(" + SCALE PATH: " + getScaleValuesFilePath());
    }

    public String getDebugMode() {
        return properties.get("debug_mode");
    }

    public String getUseBasePaths() {
        return properties.get("use_base_paths");
    }

    public String getBaseFromLookupPath() {
        return properties.get("base_from_lookup_path");
    }

    public String getBaseToLookupPath() {
        return properties.get("base_to_lookup_path");
    }

    public boolean isRunningLogger() {
        if (!hasLoadedProperties()) return true;
        return properties.get("use_logger").equals("1");
    }

    public boolean hasLoadedProperties() {
        return !properties.isEmpty();
    }

    @Override
    protected void processLine(String line) {
        String[] items = line.split(WHITESPACE_SEPARATOR);

        // replace `:`
        String key = items[0].replace(":", "");

        // replace escaped text
        String value = items[1].replace("\"", "");
        properties.put(key, value);
    }
}
