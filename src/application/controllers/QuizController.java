package application.controllers;

import application.Main;
import application.Quiz;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
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

    private MediaPlayer _player;
    private MediaPlayer _bgmusic;

    private Boolean _musictoggle;

    private int _numCorrect = 0;
    private int _numQuestions = 0;

    private String _term;

    private Quiz _quiz = new Quiz();

    @FXML
    private Label answerLabel;

    @FXML
    private Text correctText;
    @FXML
    private Text questionsText;

    @FXML
    private Button btnStart;
    @FXML
    private Button btnSkip;
    @FXML
    private Button btnCheck;

    @FXML
    private ToggleButton btnMusic;

    @FXML
    private TextField answerField;

    @FXML
    private Pane videoPane;


    public void initialize() {

        correctText.setText("" +_numCorrect);
        questionsText.setText("" + _numQuestions);

        btnCheck.setDisable(true);

        answerField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    handleBtnCheck();
                }
            }
        });

        //Initialise dynamic style changes

        btnSkip.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                btnSkip.setOpacity(0.7);
            }
        });

        btnSkip.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (!btnSkip.isDisabled()) {
                    btnSkip.setOpacity(1.0);
                }
            }
        });

        btnStart.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                btnStart.setOpacity(0.7);
            }
        });

        btnStart.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (!btnStart.isDisabled()) {
                    btnStart.setOpacity(1.0);
                }
            }
        });
    }

    /**
     * Randomly pick a mp4 video from the quiz folder and play it in loop. 
     */
    @FXML
    private void handleBtnStart() {

        if (_player != null) {
            _player.stop();

        }
        String selectedItem = _quiz.play();

        if (selectedItem == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.getDialogPane().getStylesheets().add(("Alert.css"));
            alert.setTitle("No Quizzes Available");
            alert.setContentText("Make sure you have created some creations! You can see your quizzes in the quiz manager!");
            alert.showAndWait();
            return;

        }
        btnSkip.setDisable(false);
        btnCheck.setDisable(false);
        answerField.setDisable(false);
        btnStart.setDisable(true);

        correctText.setText("" +_numCorrect);
        questionsText.setText("" + _numQuestions);

        _term = selectedItem;

        File fileUrl = new File(Main.getQuizDir()+"/"+selectedItem+".mp4");
        Media video = new Media(fileUrl.toURI().toString());

        _player = new MediaPlayer(video);
        _player.setAutoPlay(true);

        _player.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                _player.seek(Duration.ZERO);
                _player.play();

            }
        });

        MediaView mediaView = new MediaView(_player);
        mediaView.fitWidthProperty().bind(videoPane.widthProperty());
        mediaView.fitHeightProperty().bind(videoPane.heightProperty());
        mediaView.setPreserveRatio(false);

        videoPane.getChildren().clear();
        videoPane.getChildren().add(mediaView);

    }

    /**
     * Check if the term entered by the user is correct or incorrect.
     * And increment the score as well.
     */
    @FXML
    private void handleBtnCheck() {

        if (answerField.getText().equals("")){
            return;

        }
        String term = _term.toLowerCase();
        String answer = answerField.getText().toLowerCase();

        if (term.equals(answer)) {
            _numCorrect++;
            _numQuestions++;

            answerLabel.setText("Correct!!! Keep Going!");
            answerLabel.setTextFill(Color.GREEN);

            correctText.setText("" +_numCorrect);
            questionsText.setText("" + _numQuestions);

            handleBtnStart();

        }
        else {
            answerLabel.setText("HINT: " +_term);
            answerLabel.setTextFill(Color.RED);

            _numQuestions++;

            correctText.setText("" +_numCorrect);
            questionsText.setText("" + _numQuestions);

        }
        answerField.clear();

    }

    /**
     * Go back to main screen. Check if the player is still playing before going back.
     * @throws IOException
     */
    @FXML
    private void handleBtnBack() throws IOException {

        if (_player != null) {
            _player.stop();
            _player = null;

        }
        btnStart.setDisable(false);

        FXMLLoader loader = Main.changeScene("resources/Welcome.fxml");
        WelcomeController welcomeController = loader.<WelcomeController>getController();

        String s = "Music: ON";
        if (_musictoggle) {
            s = "Music: OFF";

        }
        welcomeController.transferMusic(_bgmusic, _musictoggle, s);

    }

    /**
     * Stop the current media player and change to manage quiz scene.
     * @throws IOException
     */
    @FXML
    private void handleManageQuiz() throws IOException {

        if (_player != null) {
            _player.stop();
            _player = null;

        }

        FXMLLoader loader = Main.changeScene("resources/QuizMan.fxml");
        QuizManController quizManController = loader.<QuizManController>getController();

        String s = "Music: ON";
        if (_musictoggle) {
            s = "Music: OFF";
        }
        quizManController.transferMusic(_bgmusic, _musictoggle, s);

    }

    /**
     * Turn off music for this window and keep previous user selection for music.
     * @param bgmusic
     * @param toggle
     * @param text
     */
    public void transferMusic(MediaPlayer bgmusic, Boolean toggle, String text) {

        this._bgmusic = bgmusic;
        this._bgmusic.pause();
        
        btnMusic.setSelected(true);
        btnMusic.setText("Music: OFF");
        btnMusic.setDisable(true);

        _musictoggle = toggle;

    }
}
