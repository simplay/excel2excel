import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.HashMap;
import static java.nio.charset.StandardCharsets.*;

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
    
    private boolean abortRequested;

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
        this.abortRequested = false;
        readFile(getConfigPath());
    }

    private String getConfigPath() {
        if (hasContentAt(5)) {
            return getPathAt(5);
        }
        return Paths.get("data", "config.txt").toString();
    }

    public static String getMappingFilePath() {
        if (getInstance().hasContentAt(3)) {
            return getInstance().getPathAt(3);
        }
        return Paths.get("data", "mappings.txt").toString();
    }

    public static String getScaleValuesFilePath() {
        if (getInstance().hasContentAt(4)) {
            return getInstance().getPathAt(4);
        }
        return Paths.get("data", "scale_values.txt").toString();
    }

    public static String getFromExcelFilePath() {
        return getInstance().getPathAt(0);
    }

    public static String getToExcelFilePath1() {
        return getInstance().getPathAt(1);
    }

    public static String getToExcelFilePath2() {
        return getInstance().getPathAt(2);
    }

    public String getPathAt(int idx) {
        return Paths.get(Properties.buildUTF8String(userParameters[idx])).toString();
    }

    public static String buildUTF8String(String input) {
        byte byteEncoding[] = input.getBytes(Charset.defaultCharset());
        return new String(byteEncoding, UTF_8);
    }

    public static String normalizedPath(String windowsPath) {
        String pathToken = "\\\\";
        String joinToken = "\\";
        if (windowsPath.contains("/")) {
            pathToken = "/";
            joinToken = "/";
        }
        String[] pathElements = windowsPath.split(pathToken);

        String normalizedPath = "";
        for (int k = 0; k < pathElements.length - 1; k++) {
            normalizedPath += buildUTF8String(pathElements[k]) + joinToken;
        }
        normalizedPath += buildUTF8String(pathElements[pathElements.length - 1]);
        return normalizedPath;
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
        Logger.println(" + TO PATH 1: " + getToExcelFilePath1());
        Logger.println(" + TO PATH 2: " + getToExcelFilePath2());
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
        return Properties.buildUTF8String(properties.get("base_from_lookup_path"));
    }

    public String getBaseToLookupPath() {
        return Properties.buildUTF8String(properties.get("base_to_lookup_path"));
    }

    public boolean isRunningLogger() {
        if (!hasLoadedProperties()) return true;
        return properties.get("use_logger").equals("1");
    }

    public boolean hasLoadedProperties() {
        return !properties.isEmpty();
    }

    public static boolean showErrorDialogue() {
    	String property = getInstance().properties.get("show_error_dialogue");
        return property != null && property.equals("1");
    }

    public static boolean abortRequested() {
        return getInstance().abortRequested;
    }

    public static void requestAbort() {
        getInstance().abortRequested = true;
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
