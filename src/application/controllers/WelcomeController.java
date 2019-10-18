package application.controllers;

import application.Main;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class WelcomeController {

    @FXML
    ToggleButton music = new ToggleButton();

    MediaPlayer player;

    FXMLLoader loader;

    @FXML
    Button playBtn;

    @FXML
    Button createBtn;

    @FXML
    Button learnBtn;

    public void initialize() {

        playBtn.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                playBtn.setOpacity(0.7);
            }
        });

        playBtn.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                playBtn.setOpacity(1.0);
            }
        });

        createBtn.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                createBtn.setOpacity(0.7);
            }
        });

        createBtn.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                createBtn.setOpacity(1.0);
            }
        });

        learnBtn.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                learnBtn.setOpacity(0.7);
            }
        });

        learnBtn.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                learnBtn.setOpacity(1.0);
            }
        });
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
        quizController.transferMusic(player, music.isSelected(), music.getText());
    }

    @FXML
    private void handleBtnPlay() throws IOException {
        loader = Main.changeScene("resources/Home.fxml");
        HomeController homeController = loader.<HomeController>getController();
        homeController.transferMusic(player, music.isSelected(), music.getText());
    }

    /**
     * Not working fully yet
     * @throws IOException
     */
    @FXML
    private void handleBtnCreate() throws IOException {
        /*
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("resources/Create.fxml"));

        Parent layout = loader.load();
        CreateController controller = (CreateController) loader.getController();

        Scene scene = new Scene(layout);
        Stage creationStage = new Stage();
        controller.setUp(creationStage);
        creationStage.setScene(scene);
        creationStage.show();
        */
        loader = Main.changeScene("resources/CreateCreation.fxml");
    }

    public void transferMusic(MediaPlayer bgmusic, Boolean toggle, String text) {
        player = bgmusic;
        music.setSelected(toggle);
        music.setText(text);
    }

    public void transferMediaPlayer(MediaPlayer bgmusic) {
        player = bgmusic;
    }
}
