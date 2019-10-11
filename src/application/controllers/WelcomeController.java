package application.controllers;

import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class WelcomeController {

    public void initialize() {

        MediaPlayer player = new MediaPlayer(new Media(new File(System.getProperty("user.dir") + "/src/bgmusic.wav").toURI().toString()));
        player.play();

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
