package application.controllers;

import application.Main;
import application.tasks.WikitSearchTask;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.MediaPlayer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.io.IOException;

/**
 * This class manages the wikit search screen.
 */
public class WikitSearchController {

    private ExecutorService team = Executors.newSingleThreadExecutor();

    MediaPlayer bgmusic;

    @FXML
    private ToggleButton music;

    @FXML
    private Button btnSearch;

    @FXML
    private TextField _termField;

    @FXML
    private Label errorLabel;

    @FXML
    private ImageView loadingGif;

    @FXML
    private AnchorPane searchPane;

    public void initialize() {
        _termField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    try {
                        handleBtnSearch();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                }
        });
    }

    /**
     * This method searches the wiki with the inputted term. Runs the bash command wikit with the term inputted by the user. And checks for a valid input.
     * @throws IOException
     */
    @FXML
    private void handleBtnSearch() throws IOException {

        WikitSearchTask task = new WikitSearchTask(_termField.getText());
        team.submit(task);

        searchPane.setVisible(false);
        loadingGif.setVisible(true);
        btnSearch.setDisable(true);
        errorLabel.setVisible(false);
        _termField.setDisable(true);
        btnSearch.setText("Searching...");

        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                if (_termField.getText().isEmpty() | task.getExit() != 0 | task.getOutput().equals( _termField.getText()+" not found :^(")) {
                   searchPane.setVisible(true);
                   loadingGif.setVisible(false);
                    errorLabel.setVisible(true);
                    btnSearch.setDisable(false);
                    btnSearch.setText("Search");
                    _termField.setDisable(false);
                    return;
                }
                else {
                    try {
                        FXMLLoader loader = Main.changeScene("resources/CreateCreation.fxml");
                        CreateCreationController createCreationController = loader.<CreateCreationController>getController();
                        createCreationController.transferTerm(_termField.getText(), task.getOutput());
                        createCreationController.transferMusic(bgmusic, music.isSelected(), music.getText());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            }
        });
    }

    /**
     * Go back to the main menu screen.
     * @throws IOException
     */
    @FXML
    private void handleBtnBack() throws IOException {
        FXMLLoader loader = Main.changeScene("resources/Welcome.fxml");
        WelcomeController welcomeController = loader.<WelcomeController>getController();
        welcomeController.transferMusic(bgmusic, music.isSelected(), music.getText());
    }

    /**
     * Toggle background music.
     */
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

    /**
     * Transfer music to this scene.
     * @param bgmusic
     * @param toggle
     * @param text
     */
    public void transferMusic(MediaPlayer bgmusic, Boolean toggle, String text) {
        this.bgmusic = bgmusic;
        music.setSelected(toggle);
        music.setText(text);
    }
}
