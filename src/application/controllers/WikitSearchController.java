package application.controllers;

import application.Main;
import application.WikitSearchTask;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.io.IOException;

public class WikitSearchController {

    private ExecutorService team = Executors.newSingleThreadExecutor();

    @FXML
    private Button btnSearch;

    @FXML
    private TextField _termField;

    @FXML
    private void handleBtnSearch() throws IOException {

        WikitSearchTask task = new WikitSearchTask(_termField.getText());
        team.submit(task);


        btnSearch.setDisable(true);
        _termField.setDisable(true);
        btnSearch.setText("Searching...");
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                //TODO What happens when wikit search fails?? invalid wikie searches not handled.
                if (_termField.getText().isEmpty() | task.getExit() != 0 | task.getOutput().equals( _termField.getText()+" not found :^(")) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Wikit Search");
                    alert.setHeaderText("Please enter a valid search term");
                    alert.setContentText("Enter a valid search term and try again.");
                    alert.showAndWait();
                    btnSearch.setDisable(false);
                    btnSearch.setText("Search");
                    return;
                }
                else {
                    try {
                        FXMLLoader loader = Main.changeScene("resources/CreateCreation.fxml");
                        CreateCreationController createCreationController = loader.<CreateCreationController>getController();
                        createCreationController.transferTerm(_termField.getText(), task.getOutput());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            }
        });


    }

}
