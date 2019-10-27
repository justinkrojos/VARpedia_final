package application.controllers;

import application.Main;
import com.sun.javafx.charts.Legend;
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

    private MediaPlayer bgmusic;

    private Boolean musicToggled;

    @FXML
    private ToggleButton music;

    @FXML
    private Button btnDel;

    @FXML
    private Button btnAdd;

    @FXML
    private ListView _listView;

    @FXML
    private ListView deletedList;

    private ObservableList<String> _items;
    private ObservableList<String> _delItems;
    private String _selectedItem;

    public void initialize() {
        updateListTree();
        _listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                selectItem();
                if (_listView.getSelectionModel().getSelectedItem() != null) {
                    btnDel.setDisable(false);
                }
            }
        });

        deletedList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (deletedList.getSelectionModel().getSelectedItem() != null) {
                    btnAdd.setDisable(false);
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

        String cmd = "mv '" + Main.getQuizDir() + "/deleted/" + deletedList.getSelectionModel().getSelectedItem() + ".mp4' '" + Main.getQuizDir() + "'";

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

        //File file = new File(Main.getQuizDir()+System.getProperty("file.separator") + _selectedItem + ".mp4");

        String cmd = "mv '" + Main.getQuizDir() + "/" + _selectedItem + ".mp4' '" + Main.getQuizDir() + "/deleted'";

        ProcessBuilder movepb = new ProcessBuilder("bash", "-c", cmd);
        Process moveprocess = movepb.start();
        moveprocess.waitFor();

        updateListTree();

        //_listView.getItems().remove(_listView.getSelectionModel().getSelectedIndex());
        //deletedList.getItems().add(_selectedItem);
        //System.out.println(file.getAbsolutePath());
        //file.delete();
    }

    /**
     * Populate list view with array list of sorted quiz creations.
     */
    public void updateListTree() {
        btnAdd.setDisable(true);
        btnDel.setDisable(true);
        _items = FXCollections.observableArrayList(listFilesOfFolder(_folder));
        _delItems = FXCollections.observableArrayList(listFilesOfFolder(_delFolder));
        _listView.setItems(_items);
        deletedList.setItems(_delItems);
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
     * Get the selected item from list view.
     */
    @FXML
    private void selectItem() {
        _selectedItem = (String) _listView.getSelectionModel().getSelectedItem();
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
            if (musicToggled) {
                s = "Music: OFF";
            }

        quizController.transferMusic(bgmusic, musicToggled, s);
    }

    public void transferMusic(MediaPlayer bgmusic, Boolean toggle, String text) {
        this.bgmusic = bgmusic;
        music.setSelected(true);
        music.setText("Music: OFF");
        music.setDisable(true);
        bgmusic.pause();
        musicToggled = toggle;
    }

}
