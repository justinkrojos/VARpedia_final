package application.controllers;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class manages the quiz manager scene. Manages deleting a quiz video.
 */
public class QuizManController {

    private final File _folder = new File(Main.getQuizDir());
    private final File _delFolder = new File(Main.getQuizDir() + "/deleted");

    private ObservableList<String> _items;
    private ObservableList<String> _delItems;
    private String _selectedItem;

    private MediaPlayer _bgmusic;
    private Boolean _musicToggle;

    @FXML
    private ToggleButton btnMusic;

    @FXML
    private Button btnDel;
    @FXML
    private Button btnAdd;

    @FXML
    private ListView existingQuiz;
    @FXML
    private ListView deletedQuiz;


    public void initialize() {

        updateListTree();

        // Enable deletion button when existing quiz clicked
        existingQuiz.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                selectItem();

                if (existingQuiz.getSelectionModel().getSelectedItem() != null) {
                    btnDel.setDisable(false);
                    btnAdd.setDisable(true);

                }
            }
        });

        // Enable addition button when deleted quiz clicked
        deletedQuiz.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                if (deletedQuiz.getSelectionModel().getSelectedItem() != null) {
                    btnAdd.setDisable(false);
                    btnDel.setDisable(true);

                }
            }
        });
    }

    /**
     * Adds previously deleted quizzes back.
     * @throws IOException
     * @throws InterruptedException
     */
    @FXML
    private void handleBtnAdd() throws IOException, InterruptedException {

        String cmd = "mv '" + Main.getQuizDir() + "/deleted/" + deletedQuiz.getSelectionModel().getSelectedItem() + ".mp4' '" + Main.getQuizDir() + "'";

        ProcessBuilder movepb = new ProcessBuilder("bash", "-c", cmd);
        Process moveprocess = movepb.start();
        moveprocess.waitFor();

        updateListTree();

    }

    /**
     * Deleted selected mp4 in quiz folder.
     */
    @FXML
    private void handleBtnDelete() throws IOException, InterruptedException {

        String cmd = "mv '" + Main.getQuizDir() + "/" + _selectedItem + ".mp4' '" + Main.getQuizDir() + "/deleted'";

        ProcessBuilder movepb = new ProcessBuilder("bash", "-c", cmd);
        Process moveprocess = movepb.start();
        moveprocess.waitFor();

        updateListTree();

    }

    /**
     * Populate list view with array list of sorted quiz creations.
     */
    public void updateListTree() {

        btnAdd.setDisable(true);
        btnDel.setDisable(true);

        _items = FXCollections.observableArrayList(listFilesOfFolder(_folder));
        _delItems = FXCollections.observableArrayList(listFilesOfFolder(_delFolder));

        existingQuiz.setItems(_items);
        deletedQuiz.setItems(_delItems);

    }

    /**
     * Get the creations in the quiz folder into a sorted array list.
     *
     * Code created with help from:
     * https://stackoverflow.com/questions/1844688/how-to-read-all-files-in-a-folder-from-java
     * @param folder
     * @return
     */
    private ArrayList<String> listFilesOfFolder(final File folder) {

        ArrayList<String> list = new ArrayList<String>();

        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isDirectory()) {
                list.add(fileEntry.getName().replace(".mp4",""));//.replace(".mp4", ""));

            }
        }
        Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
        return list;

    }

    /**
     * Get the selected item from existing quizzes.
     */
    @FXML
    private void selectItem() {
        _selectedItem = (String) existingQuiz.getSelectionModel().getSelectedItem();

    }

    /**
     * Change back to quiz scene
     * @throws IOException
     */
    @FXML
    private void handleBtnBack() throws IOException {

        FXMLLoader loader = Main.changeScene("resources/Quiz.fxml");
        QuizController quizController = loader.<QuizController>getController();

        String s = "Music: ON";
            if (_musicToggle) {
                s = "Music: OFF";

            }
        quizController.transferMusic(_bgmusic, _musicToggle, s);

    }

    /**
     * Turn off music for this scene, but keep previous toggle.
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

        _musicToggle = toggle;

    }
}
