package gui;

import java.io.IOException;

import client.ChatClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

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




    public void getExitBtn(ActionEvent event) throws IOException {
        System.exit(0);
    }

    public void BorrowTimeRepClick(ActionEvent event) {
        statusRep.setSelected(false);
    }

    public void SubStatusRepClick(ActionEvent event) {
        borrowRep.setSelected(false);
    }

    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/gui/ReportsGUI.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
        primaryStage.setTitle("Reports GUI");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

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
