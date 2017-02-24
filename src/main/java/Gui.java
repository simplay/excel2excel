import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Paths;

/**
 * A very basic graphical user interface that allows to easily specify
 * the FROM and TO excel file paths and start the actual cell copy process.
 */
public class Gui extends Frame {

    private final int width = 400;
    private final int height = 100;
    private final Panel panel = new Panel();
    private FileDialog fileDialog;
    private boolean useGuiInput;

    private String[] userInput;
    private String fromExcelPath = "";
    private String toExcelPath = "";

    /**
     * Build a new GUI to extract and copy the content of certain excel files.
     *
     * @param userInput optional provided user input
     */
    public Gui(final String[] userInput) {
        this.userInput = userInput;
        useGuiInput = false;

        setTitle("Excel2Excel");
        setSize(width, height);
        Button copyFromButton = new Button("From Excel File");
        Button copyToButton = new Button("To Excel File");
        Button copyButton = new Button("Copy");

        panel.add(copyFromButton);
        panel.add(copyToButton);
        panel.add(copyButton);

        fileDialog = new FileDialog(this, "Choose From Excel File");
        copyFromButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileDialog.setVisible(true);
                if (Properties.hasBaseExcelPaths()) {
                    fileDialog.setDirectory(Properties.normalizedPath(Properties.getInstance().getBaseFromLookupPath()));
                }

                try {
                    fromExcelPath = Paths.get(fileDialog.getDirectory(), fileDialog.getFile()).toString();
                    fromExcelPath = Properties.normalizedPath(fromExcelPath);
                } catch (Exception exception) {}
                useGuiInput = true;
            }
        });

        fileDialog = new FileDialog(this, "Choose To Excel File");
        copyToButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileDialog.setVisible(true);
                if (Properties.hasBaseExcelPaths()) {
                    fileDialog.setDirectory(Properties.normalizedPath(Properties.getInstance().getBaseToLookupPath()));
                }

                try {
                    toExcelPath = Paths.get(fileDialog.getDirectory(), fileDialog.getFile()).toString();
                    toExcelPath = Properties.normalizedPath(toExcelPath);
                } catch (Exception exception) {}
                useGuiInput = true;
            }
        });

        copyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String[] args = {
                        fromExcelPath,
                        toExcelPath,
                        Properties.getMappingFilePath(),
                        Properties.getScaleValuesFilePath(),
                };
                Properties.clear();

                if (userInput.length > 0 && !useGuiInput) {
                    Logger.println("Program was invoked with user specified runtime arguments.");
                    Logger.println(" => Using these arguments to setup paths.");
                    args = userInput;
                }

                Properties.initialize(args);
                // TODO export this logic to initialize
                Properties.initializeLogger();

                try {
                    Properties.reportPaths();
                    Logger.println("Reading excel files...");

                    Excel fromExcel = ExcelBuilder.build(Properties.getFromExcelFilePath());
                    Excel toExcel = ExcelBuilder.build(Properties.getToExcelFilePath());

                    Logger.println(" => Excel files read.");
                    Logger.println("Copying content from FROM excel file to TO file...");
                    new Consolidator(Properties.getMappingFilePath(), fromExcel, toExcel);
                    Logger.println(" => Content copied.");
                    Logger.println("Saving TO excel file...");
                    toExcel.save();
                    Logger.println(" => TO file saved.");
                    Logger.println("Excel2Excel successfully finished.");
                } catch (Exception e) {
                    Logger.printError(e.getMessage());
                }
            }
        });

        add(panel);
        addWindowListener(new GuiEventListener());
        pack();
        setVisible(true);
        setResizable(false);
    }

    class GuiEventListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            e.getWindow().dispose();
            System.exit(0);
            Logger.writeLog();
        }
    }
}