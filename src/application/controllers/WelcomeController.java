package application.controllers;

import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class WelcomeController {

    public void initialize() {

    }

    @FXML
    private void handleBtnQuiz() throws IOException {
        Main.changeScene("resources/Quiz.fxml");
    }

    @FXML
    private void handleBtnPlay() throws IOException {
        Main.changeScene("resources/Home.fxml");
    }

    /**
     * Not working fully yet
     * @throws IOException
     */
    @FXML
    private void handleBtnCreate() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("resources/Create.fxml"));
        Parent layout = loader.load();
        //CreateController controller = (CreateController) loader.getController();

        Scene scene = new Scene(layout);
        Stage creationStage = new Stage();
        //controller.setUp(creationStage, this);
        creationStage.setScene(scene);
        creationStage.show();
    }
}
