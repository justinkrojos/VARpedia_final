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

    private MediaPlayer _bgmusic;

    @FXML
    private ToggleButton btnMusic;

    @FXML
    private Button btnSearch;
    @FXML
    private Button btnBack;

    @FXML
    private TextField termField;

    @FXML
    private Label errorLabel;

    @FXML
    private ImageView loadingGif;

    public void initialize() {

        termField.setOnKeyReleased(new EventHandler<KeyEvent>() {
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

        WikitSearchTask task = new WikitSearchTask(termField.getText());
        team.submit(task);

        btnBack.setDisable(true);
        loadingGif.setVisible(true);
        btnSearch.setDisable(true);
        errorLabel.setVisible(false);
        termField.setDisable(true);
        btnSearch.setText("Searching...");

        // When wikit search finishes, go to next scene
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {

                if (termField.getText().isEmpty() | task.getExit() != 0 | task.getOutput().equals( termField.getText()+" not found :^(")) {
                    errorLabel.setVisible(true);
                    loadingGif.setVisible(false);
                    termField.setDisable(false);
                    btnBack.setDisable(false);
                    btnSearch.setDisable(false);
                    btnSearch.setText("Search");
                    return;

                }
                else {

                    try {
                        FXMLLoader loader = Main.changeScene("resources/CreateCreation.fxml");
                        CreateCreationController createCreationController = loader.<CreateCreationController>getController();
                        createCreationController.transferTerm(termField.getText(), task.getOutput());
                        createCreationController.transferMusic(_bgmusic, btnMusic.isSelected(), btnMusic.getText());

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
        welcomeController.transferMusic(_bgmusic, btnMusic.isSelected(), btnMusic.getText());

    }

    /**
     * Toggle background music.
     */
    @FXML
    private void handleMusic() {

        if (btnMusic.isSelected()) {
            btnMusic.setText("Music: OFF");
            _bgmusic.pause();

        }
        else {
            btnMusic.setText("Music: ON");
            _bgmusic.play();

        }
    }

    /**
     * Transfer music to this scene.
     * @param bgmusic
     * @param toggle
     * @param text
     */
    public void transferMusic(MediaPlayer bgmusic, Boolean toggle, String text) {

        this._bgmusic = bgmusic;
        btnMusic.setSelected(toggle);
        btnMusic.setText(text);

    }
}
