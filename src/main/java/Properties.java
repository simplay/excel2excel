import java.nio.file.Paths;

/**
 * Singleton carrying all project relevant properties
 * such as all file paths.
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
        return Paths.get("data/mappings.txt").toString();
    }

    public static String getScalaValuesFilePath() {
        return Paths.get("data/scala_values.txt").toString();
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
}
