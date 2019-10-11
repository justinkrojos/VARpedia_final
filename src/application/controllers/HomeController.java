package application.controllers;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeController {

    private ExecutorService team = Executors.newSingleThreadExecutor();

    private final File _folder = new File(Main.getCreationDir());
    private ObservableList<String> _items;
    private String _selectedItem = null;

    @FXML
    private Text _title;

    @FXML
    private Text _creationTitle;

    @FXML
    private ListView _creationList;

    @FXML
    private ListView _favouriteList;

    @FXML
    private Button btnPlay;

    @FXML
    private Button btnDel;

    @FXML
    private Button btnCreate;

    @FXML
    private Pane _player;

    @FXML
    private Button btnRefresh;

    @FXML
    private Button btnPause;

    @FXML
    private Button btnMute;

    @FXML
    private Button btnForward;

    @FXML
    private Button btnBackward;

    //private MediaView mediaView;

    @FXML
    private TabPane creationCategories;

    @FXML
    private ToggleButton btnFavourite;

    public void initialize() {
        updateListTree();
        btnBackward.setVisible(false);
        btnForward.setVisible(false);
        btnMute.setVisible(false);
        btnPause.setVisible(false);

        try {
            sortFavourites();
        } catch (FileNotFoundException e) {
            // do nothing
        }
    }

    public void sortFavourites() throws FileNotFoundException {

        String allFavourites = new Scanner(new File(Main.getFavouriteDir() + "/favourites.txt")).useDelimiter("\\A").next();

        String[] favouriteArray = allFavourites.split(".mp4 ");

        Arrays.sort(favouriteArray);

        _favouriteList.getItems().clear();
        for (int i = 1; i < favouriteArray.length; i++) {
            _favouriteList.getItems().add(favouriteArray[i]);
        }
    }

    @FXML
    private void handleBtnBack() throws IOException {
        Main.changeScene("resources/Welcome.fxml");
    }

    /**
     * Play a video creation.
     */
    @FXML
    private void handleBtnPlay() {
        _player.getChildren().removeAll();
        _player.getChildren().clear();
        // btnPlay.setDisable(true);
        //System.out.println("Playing");//TODO Play/pause stop not done yet
        if (_selectedItem == null) {
            alertNullSelection();
            return;
        }
        //System.out.println(Main.getCreationDir()+"/"+_selectedItem+"/"+_selectedItem+".mp4");
        //File fileUrl = new File(Main.getCreationDir()+"/"+_selectedItem+"/"+_selectedItem+".mp4");
        File fileUrl = new File(Main.getCreationDir()+"/"+_selectedItem+".mp4");
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
                _player.getChildren().removeAll();
                // btnPlay.setDisable(false);
                //mediaView = null;
            }
        });



        _player.getChildren().add(mediaView);
    }

    /**
     * Delete a creation.
     */
    @FXML
    private void handleBtnDel() {
        //System.out.println("Deleting");
        if (_selectedItem == null) {
            alertNullSelection();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Deleting...");
        alert.setHeaderText("Are you sure you want to delete " + _selectedItem + "?");
        alert.setContentText("Are you sure? Press okay to delete, cancel to keep.");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK) {
            //String delCmd = "rm -r "+ Main.getCreationDir() + "/" + _selectedItem;
            String delCmd = "rm -r "+ Main.getCreationDir() + "/" + _selectedItem + " " + Main.getCreationDir() + "/"+_selectedItem + ".mp4";
            ProcessBuilder delBuilder = new ProcessBuilder("bash","-c",delCmd);

            Process delProcess = null;
            try {
                delProcess = delBuilder.start();
                delProcess.waitFor();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            updateListTree();
        }
    }

    /**
     * Opens up the create menu.
     * @throws IOException
     */
    @FXML
    private void handleBtnCreate() throws IOException {
        //System.out.println("Creating");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("resources/Create.fxml"));
        Parent layout = loader.load();
        CreateController controller = (CreateController) loader.getController();

        Scene scene = new Scene(layout);
        Stage creationStage = new Stage();
        controller.setUp(creationStage, this);
        creationStage.setScene(scene);
        creationStage.show();
    }

    /**
     * Refresh the creation list.
     */
    @FXML
    public void handleBtnRefresh() {
        //System.out.println("Refresh");
        updateListTree();
    }

    @FXML
    public void handleBtnFavourite() throws IOException, InterruptedException {
        if (_selectedItem == null) {
            alertNullSelection();
        }
        else if (btnFavourite.isSelected()) {
            _favouriteList.getItems().add(_selectedItem);


        } else {
            _favouriteList.getItems().remove(_selectedItem);
        }

        String listOfFavourites = new String();

        for (int i = 0; i < _favouriteList.getItems().size(); i++) {
            listOfFavourites += _favouriteList.getItems().get(i) + ".mp4 ";
        }

        String cmd = "echo -e '" + listOfFavourites + "' > " + Main.getFavouriteDir() + "/favourites.txt";

        ProcessBuilder getFavouritespb = new ProcessBuilder("bash", "-c", cmd);
        Process getFavouritesprocess = getFavouritespb.start();
        getFavouritesprocess.waitFor();

        try {
            sortFavourites();
        }
        catch (FileNotFoundException e) {
            // do nothing
        }
    }

    /**
     * Get the creations in the folder.
     */
    public void updateListTree() {
        _items = FXCollections.observableArrayList(listFilesOfFolder(_folder));
        _creationList.setItems(_items);
    }

    /**
     * Get the creations in the folder into an arrylist sorted.
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

    @FXML
    private void selectItem() {
        if (creationCategories.getSelectionModel().getSelectedIndex() == 0) {
            _selectedItem = (String) _creationList.getSelectionModel().getSelectedItem();

        }
        else {
            _selectedItem = (String) _favouriteList.getSelectionModel().getSelectedItem();
        }

        if (_favouriteList.getItems().contains(_selectedItem)) {
            btnFavourite.setSelected(true);
        }
        else {
            btnFavourite.setSelected(false);
        }

        //System.out.println(_selectedItem);
    }

    /**
     * A alert for when no collection is selected.
     */
    private void alertNullSelection() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Please Select A Creation");
        alert.setHeaderText("No item has been selected.");
        alert.setContentText("Pease select a creation");
        alert.showAndWait();
    }

}
