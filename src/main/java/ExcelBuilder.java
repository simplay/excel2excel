import java.nio.charset.Charset;

public class ExcelBuilder {

    /**
     * Builds the appropriate excel file
     *
     * @param filePath path to target excel file
     * @return abstract excel file representation, defining the basic interface.
     */
    public static Excel build(String filePath) {
        if (filePath.matches("(.)+(\\.xls)$")) {
            Logger.println("Building a xls excel file for `" + filePath + "`.");
            return new XlsExcelFile(filePath);
        } else {
            Logger.println("Building a xlsx excel file for `" + filePath + "`.");
            return new XlsxExcelFile(filePath);
        }
    }
}
