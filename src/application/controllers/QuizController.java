package application.controllers;

import application.Main;
import application.Quiz;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;


import java.io.File;
import java.io.IOException;

/**
 * This class manages the quiz scene. Plays the quiz videos and checks if the user input is correct, matches the term and also keeps track of the .
 */
public class QuizController {

    MediaPlayer player;
    MediaPlayer bgmusic;

    private int _numCorrect = 0;
    private int _numQuestions = 0;

    private String _term;

    @FXML
    private Label _answerLabel;

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

    /**
     * Go back to main screen. Check if the player is still playing before going back.
     * @throws IOException
     */
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
        _correctText.setText("" +_numCorrect);
        _questionsText.setText("" + _numQuestions);
        _btnCheck.setDisable(true);

        _answerField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    handleBtnCheck();
                }
            }
        });
    }

    /**
     * Randomly pick a mp4 video from the quiz folder and play it in loop. 
     */
    @FXML
    private void handleBtnStart() {
        _correctText.setText("" +_numCorrect);
        _questionsText.setText("" + _numQuestions);
        _btnCheck.setDisable(false);
        System.out.println(_numCorrect +""+ _numQuestions);
        _player.getChildren().removeAll();
        _player.getChildren().clear();


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
            }
        });

        player.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                player.seek(Duration.ZERO);
                player.play();
            }
        });

        _player.getChildren().add(mediaView);
        _btnStart.setDisable(true);

    }

    /**
     * Check if the term enetered by the user is correct or incorrect.
     * And increment the score as well.
     */
    @FXML
    private void handleBtnCheck() {
        if (_answerField.getText().equals("")){
            return;
        }
        String term = _term.toLowerCase();
        String answer = _answerField.getText().toLowerCase();

        if (term.equals(answer)) {
            System.out.println("Correct");
            _numCorrect++;
            _numQuestions++;
            _answerLabel.setText("Correct!!! Keep Going!");
            _answerLabel.setTextFill(Color.GREEN);
            handleBtnStart();
            _correctText.setText("" +_numCorrect);
            _questionsText.setText("" + _numQuestions);

        } else {
            _answerLabel.setText("HINT: " +_term);
            _answerLabel.setTextFill(Color.RED);
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

    /**
     * Stop the current media player and change to manage quiz scene.
     * @throws IOException
     */
    @FXML
    private void handleManageQuiz() throws IOException {
        if (player != null) {
            player.stop();
            player = null;
        }
        Main.changeScene("resources/QuizMan.fxml");
    }
}
