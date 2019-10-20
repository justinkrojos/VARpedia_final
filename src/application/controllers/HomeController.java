package application.controllers;

import application.Main;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

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

    @FXML
    private ToggleButton music;

    @FXML
    private Button homeHelpBtn;

    private MediaPlayer bgmusic;

    private MediaPlayer player;

    @FXML
    private Slider timeSlider;

    public void initialize() {

        updateListTree();

        _creationList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                selectItem();
                    try {
                        sortFavourites();
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }


                if (_selectedItem != null) {
                    if (player != null) {
                        btnPlay.setDisable(true);
                        btnDel.setDisable(true);
                        btnFavourite.setDisable(true);
                    }
                    else {
                        btnPlay.setDisable(false);
                        btnDel.setDisable(false);
                        btnFavourite.setDisable(false);
                    }
                    if (_favouriteList.getItems().contains(_selectedItem)) {
                        btnFavourite.setText("U N F A V O U R I T E");
                        btnFavourite.setSelected(true);
                        btnFavourite.setOpacity(0.7);
                    }
                    else {
                        btnFavourite.setText("F A V O U R I T E");
                        btnFavourite.setSelected(false);
                    }
                }
            }
        });

        _favouriteList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                selectItem();

                    try {
                        sortFavourites();
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }


                if (_selectedItem != null) {
                    if (player != null) {
                        btnPlay.setDisable(true);
                        btnDel.setDisable(true);
                        btnFavourite.setDisable(true);
                    }
                    else {
                        btnPlay.setDisable(false);
                        btnDel.setDisable(false);
                        btnFavourite.setDisable(false);
                    }
                    if (_favouriteList.getItems().contains(_selectedItem)) {
                        btnFavourite.setText("U N F A V O U R I T E");
                        btnFavourite.setSelected(true);
                        btnFavourite.setOpacity(0.7);
                    }
                    else {
                        btnFavourite.setText("F A V O U R I T E");
                        btnFavourite.setSelected(false);
                    }
                }
            }
        });

        btnPlay.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                btnPlay.setOpacity(0.7);
            }
        });

        btnPlay.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                btnPlay.setOpacity(1.0);
            }
        });

        btnDel.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                btnDel.setOpacity(0.7);
            }
        });

        btnDel.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                btnDel.setOpacity(1.0);
            }
        });

        btnFavourite.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                btnFavourite.setOpacity(0.7);
            }
        });

        btnFavourite.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                btnFavourite.setOpacity(1.0);
            }
        });

    }

    public void sortFavourites() throws IOException, InterruptedException {

        String listOfFavourites = " ";

        for (int i = 0; i < _favouriteList.getItems().size(); i++) {
            listOfFavourites += _favouriteList.getItems().get(i) + ".mp4 ";
        }

        String cmd = "echo -e '" + listOfFavourites + "' > " + Main.getFavouriteDir() + "/favourites.txt";

        ProcessBuilder getFavouritespb = new ProcessBuilder("bash", "-c", cmd);
        Process getFavouritesprocess = getFavouritespb.start();
        getFavouritesprocess.waitFor();

        if (_favouriteList.getItems().size() > 2) {

            String allFavourites = new Scanner(new File(Main.getFavouriteDir() + "/favourites.txt")).useDelimiter("\\A").next();
            allFavourites.trim();

            String[] favouriteArray = allFavourites.split(".mp4 ");

            Arrays.sort(favouriteArray);

            _favouriteList.getItems().clear();
            for (int i = 1; i < favouriteArray.length; i++) {
                _favouriteList.getItems().add(favouriteArray[i]);
            }
        }
    }

    @FXML
    private void handleBtnBack() throws IOException {
        if (player != null) {
            player.stop();
        }
        FXMLLoader loader = Main.changeScene("resources/Welcome.fxml");
        WelcomeController welcomeController = loader.<WelcomeController>getController();
        welcomeController.transferMusic(bgmusic, music.isSelected(), music.getText());
    }

    /**
     * Play a video creation.
     */
    @FXML
    private void handleBtnPlay() {

        if (btnPlay.getText().equals("S T O P   P L A Y I N G")) {
            btnPlay.setStyle("-fx-background-color: rgba(0, 255, 0, 0.5); -fx-border-width: 5; -fx-border-color: green; -fx-border-radius: 20 20 0 0; -fx-background-radius: 20 20 0 0;");
            btnPlay.setText("P L A Y   C R E A T I O N");
            btnDel.setDisable(false);
            btnFavourite.setDisable(false);
            player.stop();
            player = null;
            _player.getChildren().clear();
        }

        else {




            btnPlay.setStyle("-fx-background-color: rgba(255, 165, 0, 0.8); -fx-border-width: 5; -fx-border-color: orange; -fx-border-radius: 20 20 0 0; -fx-background-radius: 20 20 0 0;");
            btnPlay.setText("S T O P   P L A Y I N G");

            btnFavourite.setDisable(true);
            btnDel.setDisable(true);

            _player.getChildren().removeAll();
            _player.getChildren().clear();
            File fileUrl = new File(Main.getCreationDir() + "/" + _selectedItem + ".mp4");
            Media video = new Media(fileUrl.toURI().toString());
            player = new MediaPlayer(video);
            player.setAutoPlay(true);
            MediaView mediaView = new MediaView(player);
            mediaView.fitWidthProperty().bind(_player.widthProperty());
            mediaView.fitHeightProperty().bind(_player.heightProperty());
            mediaView.setPreserveRatio(false);



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
                    _player.getChildren().clear();
                    player = null;
                    btnPlay.setDisable(false);
                    btnDel.setDisable(false);
                    btnFavourite.setDisable(false);
                    btnPlay.setStyle("-fx-background-color: rgba(0, 255, 0, 0.5); -fx-border-width: 5; -fx-border-color: green; -fx-border-radius: 20 20 0 0; -fx-background-radius: 20 20 0 0;");
                    btnPlay.setText("P L A Y   C R E A T I O N");
                }
            });


            _player.getChildren().add(mediaView);


            player.currentTimeProperty().addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable observable) {
                    if (btnPlay.getText().equals("S T O P   P L A Y I N G")){
                        updateValues();
                    }

                }
            });

            timeSlider.valueProperty().addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable observable) {
                    if (timeSlider.isPressed()) {
                        player.seek(player.getMedia().getDuration().multiply(timeSlider.getValue()/100));
                    }
                }
            });




        }
    }

    protected void updateValues() {
        Platform.runLater(new Runnable() {
            public void run() {
                timeSlider.setValue(player.getCurrentTime().toMillis()/player.getTotalDuration().toMillis() * 100);
            }
        });
    }


    /**
     * Delete a creation.
     */
    @FXML
    private void handleBtnDel() throws IOException, InterruptedException {
        //System.out.println("Deleting");

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Deleting...");
        alert.setHeaderText("Are you sure you want to delete " + _selectedItem + "?");
        alert.setContentText("Are you sure? Press okay to delete, cancel to keep.");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK) {
            _favouriteList.getItems().remove(_selectedItem);
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
            sortFavourites();
        }
    }

    /**
     * Opens up the create menu.
     * @throws IOException
     */
    @FXML
    private void handleBtnCreate() throws IOException {
        if (player != null) {
            player.stop();
        }
        FXMLLoader loader = Main.changeScene("resources/WikitSearch.fxml");
        WikitSearchController wikitController = loader.<WikitSearchController>getController();
        wikitController.transferMusic(player, music.isSelected(), music.getText());
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
        if (btnFavourite.isSelected()) {
            _favouriteList.getItems().add(_selectedItem);
            btnFavourite.setText("U N F A V O U R I T E");


        } else {
            _favouriteList.getItems().remove(_selectedItem);
            btnFavourite.setText("F A V O U R I T E");
        }

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
        //System.out.println(_selectedItem);
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
    private void handleHomeHelp() {
    }
}
