package application.controllers;

import application.Main;
import com.sun.javafx.charts.Legend;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class manages the quiz manager scene. Manages deleting a quiz video.
 */
public class QuizManController {

    private final File _folder = new File(Main.getQuizDir());

    @FXML
    private ListView _listView;
    private ObservableList<String> _items;
    private String _selectedItem;

    public void initialize() {
        updateListTree();
    }

    /**
     * Deleted selected mp4 in quiz folder.
     */
    @FXML
    private void handleBtnDelete() {
        if (_selectedItem == null) {
            return;
        }
        File file = new File(Main.getQuizDir()+System.getProperty("file.separator") + _selectedItem + ".mp4");
        //System.out.println(file.getAbsolutePath());
        file.delete();

        updateListTree();
    }

    public void updateListTree() {
        _items = FXCollections.observableArrayList(listFilesOfFolder(_folder));
        _listView.setItems(_items);
    }

    /**
     * Get the creations in the folder into an arrylist sorted.
     *
     * Code copied from:
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

    @FXML
    private void handleBtnBack() throws IOException {
        Main.changeScene("resources/Quiz.fxml");
    }

}
