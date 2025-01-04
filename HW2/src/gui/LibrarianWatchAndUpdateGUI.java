package gui;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import client.ChatClient;
import client.ClientUI;
import client.ReaderCardLibrariaViewUI;
import common.Subscriber1;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;

public class LibrarianWatchAndUpdateGUI {
    @FXML
    private TextField subID = null;
    @FXML
    private TextField name = null;
    @FXML
    private TextField phone_number = null;
    @FXML
    private TextField email = null;
    @FXML
    private TextField history = null;
    @FXML
    private TextArea Bview = null;
    @FXML
    private Button ViewClick = null;
    @FXML
    private Button ManBorrowClick = null;
    @FXML
    private TextField BookName = null;
    @FXML
    private TextField OldRetDate = null;
    @FXML
    private TextField NewRetDate = null;
    @FXML
    private DialogPane ChangesSavedPop = null;
    @FXML
    private Button SaveChangesbtt = null;
    @FXML
    private TextField SubStatus = null;
    
    private  String TempStatus;
    
    private String Librarian_Name = "dafi";
    
    private ArrayList<String> borrowHistory;

    public void ViewDetBtt(ActionEvent event) throws IOException {
        ReaderCardLibrariaViewUI.chat.accept("select", subID.getText(), "", "");
        Subscriber1 sub = ChatClient.s1;
        subID.setText(String.valueOf(sub.getSubscriber_id()));
        name.setText(sub.getSubscriber_name());
        phone_number.setText(sub.getSubscriber_phone_number());
        email.setText(sub.getSubscriber_email());
        SubStatus.setText(sub.getSub_status());
        
        
        ReaderCardLibrariaViewUI.chat.accept("watch borrow history", subID.getText(), "", "");
        borrowHistory = ChatClient.borrowHistory;

        if (borrowHistory == null || borrowHistory.isEmpty()) {
            Bview.setText("No borrow history found.");
            return;
        }

        for (int i = 0; i < borrowHistory.size(); i++) {
            Bview.appendText(borrowHistory.get(i));
            Bview.appendText("\n\n");
        }
        TempStatus = sub.getSub_status();
        if (TempStatus.equals("frozen")) {
        	ChangesSavedPop.setContentText("The account is frozen. It is not possible to extend the borrow.");
        	return;
        	
        }
    }

    public void ManReturnBtt(ActionEvent event) throws IOException {
    	if (TempStatus.equals("frozen")) {
    		return;
    	}
        for (int i = 0; i < borrowHistory.size(); i++) {
            if (borrowHistory.get(i).contains(BookName.getText())) {
                int dateIndex = borrowHistory.get(i).indexOf("Date: ");
                if (dateIndex != -1) {
                    int startIndex = dateIndex + 6;
                    String dateTimePart = borrowHistory.get(i).substring(startIndex, startIndex + 19);
                    OldRetDate.setText(dateTimePart);
                    break;
                }
            }
        }
    }

    public void SaveChangBtt(ActionEvent event) throws IOException {
    	if (TempStatus.equals("frozen")) {
    		return;
    	}
        if (!isLessThanTwoWeeks(OldRetDate.getText(), NewRetDate.getText())) {
            ChangesSavedPop.setContentText("Invalid New Date! - must be less than 2 weeks!");
            return;
        }

        ReaderCardLibrariaViewUI.chat.book_accept("set new return date", subID.getText(), BookName.getText(), OldRetDate.getText(), NewRetDate.getText() , Librarian_Name );

        if (ChatClient.bool) {
            ChangesSavedPop.setContentText("Updated successfully");
        } else {
            ChangesSavedPop.setContentText("The update failed");
        }
    }

    private boolean isLessThanTwoWeeks(String oldDateStr, String newDateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        try {
            Date oldDate = dateFormat.parse(oldDateStr);
            Date newDate = dateFormat.parse(newDateStr);

            long diffInMillis = Math.abs(newDate.getTime() - oldDate.getTime());
            long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);

            return diffInDays < 14;
        } catch (ParseException e) {
            ChangesSavedPop.setContentText("Error parsing dates: " + e.getMessage());
            return false;
        }
    }

    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/gui/LibrarianWatchAndUpdateGUI.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/LibrarianWatchAndUpdateGUI.css").toExternalForm());
        primaryStage.setTitle("Librarian watch and update GUI");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
