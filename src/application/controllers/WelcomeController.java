package application.controllers;

import application.Main;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.MediaPlayer;

import java.io.IOException;

public class WelcomeController {

    private MediaPlayer _player;

    @FXML
    private ToggleButton btnMusic = new ToggleButton();

    @FXML
    private Button btnPlay;
    @FXML
    private Button btnCreate;
    @FXML
    private Button btnLearn;


    public void initialize() {

        // Initialise dynamic style changes to components

        btnPlay.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                btnPlay.setOpacity(0.7);
            }
        });

        btnPlay.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (!btnPlay.isDisabled()) {
                    btnPlay.setOpacity(1.0);
                }
            }
        });

        btnCreate.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                btnCreate.setOpacity(0.7);
            }
        });

        btnCreate.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (!btnCreate.isDisabled()) {
                    btnCreate.setOpacity(1.0);
                }
            }
        });

        btnLearn.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                btnLearn.setOpacity(0.7);
            }
        });

        btnLearn.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (!btnLearn.isDisabled()) {
                    btnLearn.setOpacity(1.0);
                }
            }
        });
    }

    /**
     * Go to quiz scene
     * @throws IOException
     */
    @FXML
    private void handleBtnQuiz() throws IOException {

        FXMLLoader loader = Main.changeScene("resources/Quiz.fxml");
        QuizController quizController = loader.<QuizController>getController();
        quizController.transferMusic(_player, btnMusic.isSelected(), btnMusic.getText());

    }

    /**
     * Go to watch screen
     * @throws IOException
     */
    @FXML
    private void handleBtnPlay() throws IOException {

        FXMLLoader loader = Main.changeScene("resources/Play.fxml");
        PlayController homeController = loader.<PlayController>getController();
        homeController.transferMusic(_player, btnMusic.isSelected(), btnMusic.getText());

    }

    /**
     * Switches to WikitSearch screen.
     * @throws IOException
     */
    @FXML
    private void handleBtnCreate() throws IOException {

        FXMLLoader loader = Main.changeScene("resources/WikitSearch.fxml");
        WikitSearchController wikitController = loader.<WikitSearchController>getController();
        wikitController.transferMusic(_player, btnMusic.isSelected(), btnMusic.getText());

    }

    /**
     * Toggle background music.
     */
    @FXML
    public void handleMusic() {

        if (btnMusic.isSelected()) {
            btnMusic.setText("Music: OFF");
            _player.pause();

        }
        else {
            btnMusic.setText("Music: ON");
            _player.play();

        }
    }

    /**
     * Transfer music to this scene.
     * @param bgmusic
     * @param toggle
     * @param text
     */
    public void transferMusic(MediaPlayer bgmusic, Boolean toggle, String text) {

        _player = bgmusic;
        btnMusic.setSelected(toggle);
        btnMusic.setText(text);

    }

    /**
     * Transfer MediaPlayer to scene.
     * @param player
     */
    public void transferMediaPlayer(MediaPlayer player) {

        this._player = player;

    }

}
