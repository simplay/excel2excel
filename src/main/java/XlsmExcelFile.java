import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;

public class XlsmExcelFile extends XlsxExcelFile {

    public XlsmExcelFile(String filePath, int sheetNr) {
        super(filePath, sheetNr);
    }

    public XlsmExcelFile(String filePath) {
        super(filePath, 0);
    }

    @Override
    protected XSSFWorkbook loadWorkbook() {
        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(
                    OPCPackage.open(filePath)
            );

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        return workbook;
    }
}
