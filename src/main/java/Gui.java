import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * A very basic graphical user interface that allows to easily specify
 * the FROM and TO excel file paths and start the actual cell copy process.
 */
public class Gui extends Frame {

    private Frame self;
    private final int width = 400;
    private final int height = 100;
    private final Panel panel = new Panel();
    private boolean useGuiInput;
    private String[] userInput;
    private String fromExcelPath = "";
    private String toExcelPath1 = "";
    private String toExcelPath2 = "";
    private FileDialog fileDialogCopyFrom;
    private FileDialog fileDialogButton1;
    private FileDialog fileDialogButton2;

    /**
     * Build a new GUI to extract and copy the content of certain excel files.
     *
     * @param userInput optional provided user input
     */
    public Gui(final String[] userInput) {
        this.self = this;
        this.userInput = userInput;
        useGuiInput = false;

        setTitle("Excel2Excel");
        setSize(width, height);
        Button copyFromButton = new Button("From Excel File");
        Button copyToButton1 = new Button("To Excel File 1");
        Button copyToButton2 = new Button("To Excel File 2");
        Button copyButton = new Button("Copy");

        panel.add(copyFromButton);
        panel.add(copyToButton1);
        panel.add(copyToButton2);
        panel.add(copyButton);

        copyFromButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileDialogCopyFrom = new FileDialog(self, "Choose To Excel File");
                if (Properties.hasBaseExcelPaths()) {
                    fileDialogCopyFrom.setDirectory(Properties.normalizedPath(Properties.getInstance().getBaseFromLookupPath()));
                }
                fileDialogCopyFrom.setVisible(true);
                try {
                    fromExcelPath = Paths.get(fileDialogCopyFrom.getDirectory(), fileDialogCopyFrom.getFile()).toString();
                    fromExcelPath = Properties.normalizedPath(fromExcelPath);
                } catch (Exception exception) {}
                useGuiInput = true;
            }
        });

        copyToButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileDialogButton1 = new FileDialog(self, "Choose To Excel File");
                if (Properties.hasBaseExcelPaths()) {
                    fileDialogButton1.setDirectory(Properties.normalizedPath(Properties.getInstance().getBaseToLookupPath()));
                }
                fileDialogButton1.setVisible(true);
                try {
                    toExcelPath1 = Paths.get(fileDialogButton1.getDirectory(), fileDialogButton1.getFile()).toString();
                    toExcelPath1 = Properties.normalizedPath(toExcelPath1);
                } catch (Exception exception) {}
                useGuiInput = true;
            }
        });

        copyToButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileDialogButton2 = new FileDialog(self, "Choose To Excel File");
                if (Properties.hasBaseExcelPaths()) {
                    fileDialogButton2.setDirectory(Properties.normalizedPath(Properties.getInstance().getBaseToLookupPath()));
                }
                fileDialogButton2 .setVisible(true);
                try {
                    toExcelPath2 = Paths.get(fileDialogButton2.getDirectory(), fileDialogButton2.getFile()).toString();
                    toExcelPath2 = Properties.normalizedPath(toExcelPath2);
                } catch (Exception exception) {}
                useGuiInput = true;
            }
        });

        copyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String[] args = {
                        fromExcelPath,
                        toExcelPath1,
                        toExcelPath2,
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

                    ArrayList<Excel> toExcels = new ArrayList<Excel>();
                    toExcels.add(ExcelBuilder.build(Properties.getToExcelFilePath1()));
                    toExcels.add(ExcelBuilder.build(Properties.getToExcelFilePath2()));
                    Logger.println(" => Excel files read.");

                    for (int k = 0; k < 2; k++) {
                        Logger.println("Copying content from FROM excel file to TO file " + k + "...");
                        new Consolidator(Properties.getMappingFilePath(), fromExcel, toExcels, k);
                        Logger.println(" => Content copied.");
                        Logger.println("Saving TO excel file...");
                        toExcels.get(k).save();
                        Logger.println(" => TO file saved.");
                        Logger.println("Excel2Excel successfully finished.");
                    }
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