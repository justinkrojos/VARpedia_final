package application.controllers;

import application.Main;
import application.Quiz;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.util.Duration;


import java.io.File;
import java.io.IOException;

public class QuizController {

    MediaPlayer player;
    MediaPlayer bgmusic;

    private int _numCorrect = 0;
    private int _numQuestions = 0;

    private String _term;

    @FXML
    private Text _correctText;

    @FXML
    private Text _questionsText;


    @FXML
    private Button _btnStart;

    @FXML
    private TextField _answerField;

    @FXML
    private ToggleButton music;

    private Quiz _quiz = new Quiz();

    @FXML
    private Pane _player;

    @FXML
    private Button _btnCheck;

    @FXML
    private void handleBtnBack() throws IOException {
        if (player != null) {
            player.stop();
            player = null;
        }

        _btnStart.setDisable(false);
        FXMLLoader loader = Main.changeScene("resources/Welcome.fxml");
        WelcomeController welcomeController = loader.<WelcomeController>getController();
        welcomeController.transferMusic(bgmusic, music.isSelected(), music.getText());
    }


    public void initialize() {
 /*       _correctText.textProperty().bind(new SimpleIntegerProperty(_numCorrect).asString());
        _questionsText.textProperty().bind(new SimpleIntegerProperty(_numQuestions).asString());*/
        _correctText.setText("" +_numCorrect);
        _questionsText.setText("" + _numQuestions);
        _btnCheck.setDisable(true);
    }

    @FXML
    private void handleBtnStart() {
        _correctText.setText("" +_numCorrect);
        _questionsText.setText("" + _numQuestions);
        _btnCheck.setDisable(false);
        System.out.println(_numCorrect +""+ _numQuestions);
        _player.getChildren().removeAll();
        _player.getChildren().clear();
        //_numQuestions++;

        String selectedItem = _quiz.play();
        _term = selectedItem;
        File fileUrl = new File(Main.getQuizDir()+"/"+selectedItem+".mp4");
        Media video = new Media(fileUrl.toURI().toString());
        player = new MediaPlayer(video);
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
        if (_answerField.getText() == null){
            return;
        }
        String term = _term.toLowerCase();
        String answer = _answerField.getText().toLowerCase();

        if (term.equals(answer)) {
            System.out.println("Correct");
            _numCorrect++;
            _numQuestions++;
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Correct");
            alert.setContentText("Correct!!!");
            alert.setHeaderText(null);
            alert.showAndWait();
            handleBtnStart();
            _correctText.setText("" +_numCorrect);
            _questionsText.setText("" + _numQuestions);

        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Wrong");
            alert.setContentText("WRONG!!!!" + System.getProperty("line.separator")+"Correct Answer is : " + _term);
            alert.setHeaderText(null);
            alert.showAndWait();
            _numQuestions++;
            _correctText.setText("" +_numCorrect);
            _questionsText.setText("" + _numQuestions);
        }

        _answerField.clear();
    }

    @FXML
    private void handleMusic() {

        if (music.isSelected()) {
            music.setText("Music: OFF");
            bgmusic.pause();
        }
        else {
            music.setText("Music: ON");
            bgmusic.play();
        }
    }

    public void transferMusic(MediaPlayer bgmusic, Boolean toggle, String text) {
        this.bgmusic = bgmusic;
        music.setSelected(toggle);
        music.setText(text);
    }

    @FXML
    private void handleManageQuiz() throws IOException {
        Main.changeScene("resources/QuizMan.fxml");
    }
}
