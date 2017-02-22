import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Paths;

public class Gui extends Frame{

    private final int width = 400;
    private final int height = 100;
    private final Panel panel= new Panel();
    private FileDialog fd;

    private String[] userInput;
    private String fromExcelPath = "";
    private String toExcelPath = "";

    /**
     * Build a new GUI to extract and copy the content of certain excel files.
     *
     * @param userInput optional provided user input
     */
    public Gui(String[] userInput) {
        this.userInput = userInput;

        setTitle("Excel2Excel");
        setSize(width, height);
        Button copyFromButton = new Button("From Excel File");
        Button copyToButton = new Button("To Excel File");
        Button copyButton = new Button("Copy");

        panel.add(copyFromButton);
        panel.add(copyToButton);
        panel.add(copyButton);

        fd = new FileDialog(this, "Choose From Excel File");
        copyFromButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fd.setVisible(true);
                fromExcelPath = Paths.get(fd.getDirectory(), fd.getFile()).toString();
            }
        });

        fd = new FileDialog(this, "Choose To Excel File");
        copyToButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fd.setVisible(true);
                toExcelPath = Paths.get(fd.getDirectory(), fd.getFile()).toString();
            }
        });

        copyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                // TODO set mapping and translation file as well
                String[] args = {
                        fromExcelPath,
                        toExcelPath
                };
                Properties.initialize(args);
                try {
                    Properties.reportPaths();
                    Logger.println("Reading excel files...");
                    Logger.println();

                    ExcelFile fromExcel = new ExcelFile(Properties.getFromExcelFilePath());
                    ExcelFile toExcel = new ExcelFile(Properties.getToExcelFilePath());
                    Logger.println(" => Excel files read.");
                    Logger.println("Copy content from FROM excel file to TO file...");
                    new Consolidator(Properties.getMappingFilePath(), fromExcel, toExcel);
                    Logger.println(" => Content copied.");
                    toExcel.save();
                    Logger.println(" => TO file saved.");
                    Logger.writeLog();

                } catch (Exception e) {
                    System.err.println(e.getMessage());
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
        }
    }
}
