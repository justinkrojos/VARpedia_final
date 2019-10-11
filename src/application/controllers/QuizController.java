package application.controllers;

import application.Main;
import application.Quiz;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;


import java.io.File;
import java.io.IOException;

public class QuizController {

    private String _term;

    @FXML
    private Button _btnStart;

    @FXML
    private TextField _answerField;

    private Quiz _quiz = new Quiz();

    @FXML
    private Pane _player;

    @FXML
    private void handleBtnBack() throws IOException {
        Main.changeScene("resources/Welcome.fxml");
    }



    @FXML
    private void handleBtnStart() {
        _player.getChildren().removeAll();
        _player.getChildren().clear();

        String selectedItem = _quiz.play();
        _term = selectedItem;
        File fileUrl = new File(Main.getQuizDir()+"/"+selectedItem+".mp4");
        Media video = new Media(fileUrl.toURI().toString());
        MediaPlayer player = new MediaPlayer(video);
        player.setAutoPlay(true);
        MediaView mediaView = new MediaView(player);
        mediaView.setFitWidth(800);
        mediaView.setFitHeight(600);
        player.setOnReady(new Runnable() {
            @Override
            public void run() {
                //Some observable map thing goes here.

            }
        });
        player.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                player.seek(Duration.ZERO);
                player.play();
                // btnPlay.setDisable(false);
                //mediaView = null;
            }
        });



        _player.getChildren().add(mediaView);
        _btnStart.setDisable(true);

    }

    @FXML
    private void handleBtnCheck() {
        String term = _term.toLowerCase();
        String answer = _answerField.getText().toLowerCase();

        if (term.equals(answer)) {
            System.out.println("Correct");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Correct");
            alert.setContentText("Correct!!!");
            alert.setHeaderText(null);
            alert.showAndWait();
            handleBtnStart();

        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Wrong");
            alert.setContentText("WRONG!!!!");
            alert.setHeaderText(null);
            alert.showAndWait();
        }

    }
}
