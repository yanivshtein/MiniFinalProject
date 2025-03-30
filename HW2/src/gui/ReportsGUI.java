package gui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Controller class for the Reports GUI.
 * Allows librarians to generate and view various reports, including borrow reports and status reports.
 */
public class ReportsGUI {

    @FXML
    private VBox statusReportContainer;
    @FXML
    private PieChart statusChart;
    @FXML
    private Text activeText, frozenText, totalText;
    @FXML
    private RadioButton borrowRep = null;
    @FXML
    private RadioButton statusRep = null;
    @FXML
    private ComboBox<String> months = null;
    @FXML
    private ComboBox<String> years = null;
    @FXML
    private TextArea Displayarea = null;
    @FXML
    private Button Viewbtt = null;
    @FXML
    private Button ExitBtt = null;
    @FXML
    private Button ReturnBtn = null;
    @FXML
    private Pane mainPane;  // Pane to hold graphical elements

    /**
     * Initializes the controller by populating the month and year selection boxes.
     */
    public void initialize() {
        String[] monthsArray = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        };

        String[] yearsArray = {
            "2020", "2021", "2022", "2023", "2024", "2025"
        };

        months.getItems().addAll(monthsArray);
        years.getItems().addAll(yearsArray);

    }

    /**
     * Handles the action for the "View Report" button.
     * Generates the selected report based on the user's choices.
     *
     * @param event the action event triggered by clicking the "View Report" button.
     */
    public void ViewBttClick(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        String selectedMonth = getMonthNumber(months.getValue());
        String selectedYear = years.getValue();

        if (selectedMonth == null || selectedYear == null) {
            alert.setTitle("Selection Error");
            alert.setContentText("You must select a month and a year!");
            alert.showAndWait();
            return;
        }

        try {
            Stage stage = new Stage();

            if (borrowRep.isSelected()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/BorrowReportView.fxml"));
                Parent root = loader.load();
                BorrowReportViewController controller = loader.getController();
                controller.loadBorrowReport(selectedMonth, selectedYear);

                stage.setTitle("Borrow Report");
                stage.setScene(new Scene(root));
                stage.show();

            } else if (statusRep.isSelected()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/StatusReportView.fxml"));
                Parent root = loader.load();
                StatusReportViewController controller = loader.getController();
                controller.loadStatusReport(selectedMonth, selectedYear);

                stage.setTitle("Status Report");
                stage.setScene(new Scene(root));
                stage.show();
            }
            else {
            	 alert.setTitle("Selection Error");
                 alert.setContentText("You must select a report type!");
                 alert.showAndWait();
                 return;
            }

        } catch (IOException e) {
            e.printStackTrace();
            alert.setTitle("Error");
            alert.setContentText("Failed to load report window.");
            alert.showAndWait();
        }
    }

    /**
     * Exits the application when the "Exit" button is clicked.
     *
     * @param event the action event triggered by clicking the "Exit" button.
     */
    public void getExitBtn(ActionEvent event) throws IOException {
        System.exit(0);
    }

    /**
     * Handles the action when the "Borrow Report" radio button is selected.
     * Ensures that the "Status Report" radio button is deselected.
     *
     * @param event the action event triggered by selecting the "Borrow Report" radio button.
     */
    public void BorrowTimeRepClick(ActionEvent event) {
        statusRep.setSelected(false);
    }

    /**
     * Handles the action when the "Status Report" radio button is selected.
     * Ensures that the "Borrow Report" radio button is deselected.
     *
     * @param event the action event triggered by selecting the "Status Report" radio button.
     */
    public void SubStatusRepClick(ActionEvent event) {
        borrowRep.setSelected(false);
    }

    /**
     * Starts the Reports GUI by loading the FXML layout and displaying it in a new window.
     *
     * @param primaryStage the primary stage for this application.
     * @throws Exception if an error occurs while loading the FXML file or initializing the scene.
     */
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/gui/ReportsGUI.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
        primaryStage.setTitle("Reports GUI");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Converts a month name to its corresponding number (e.g., "January" -> "01").
     *
     * @param monthName the name of the month.
     * @return the month number as a string, or null if the month name is invalid.
     */
    public String getMonthNumber(String monthName) {
        String[] monthsArray = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        };

        for (int i = 0; i < monthsArray.length; i++) {
            if (monthsArray[i].equalsIgnoreCase(monthName)) {
                return String.format("%02d", i + 1);
            }
        }
        return null;
    }

    /**
     * Handles the action for the "Return" button.
     * Navigates back to the Librarian Home Page.
     *
     * @param event the action event triggered by clicking the "Return" button.
     * @throws IOException if an error occurs while loading the FXML file.
     */
    public void ReturnButton(ActionEvent event) throws IOException {
        // Get the current stage
        Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();

        // Load the new FXML file only once
        Parent root = FXMLLoader.load(getClass().getResource("/gui/LibrarianGUIHomePageController.fxml"));
        Scene scene = new Scene(root);

        // Apply stylesheet
        scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());

        // Set the scene on the current stage and show it
        currentStage.setTitle("Librarian Home Page");
        currentStage.setScene(scene);
        currentStage.show();
    }

}
