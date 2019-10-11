package application.controllers;

import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class WelcomeController {

    @FXML
    ToggleButton music = new ToggleButton();

    MediaPlayer player = new MediaPlayer(new Media(new File(System.getProperty("user.dir") + "/src/bgmusic.wav").toURI().toString()));

    FXMLLoader loader;

    public void initialize() {

        player.setCycleCount(MediaPlayer.INDEFINITE);
            player.play();


    }

    @FXML
    public void handleMusic() {

        if (music.isSelected()) {
            music.setText("Music: OFF");
            player.pause();
        }
        else {
            music.setText("Music: ON");
            player.play();
        }
    }

    @FXML
    private void handleBtnQuiz() throws IOException {
        loader = Main.changeScene("resources/Quiz.fxml");
        QuizController quizController = loader.<QuizController>getController();
        quizController.transferMusic(player);
    }

    @FXML
    private void handleBtnPlay() throws IOException {
        loader = Main.changeScene("resources/Home.fxml");
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
