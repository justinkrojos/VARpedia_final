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
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class HomeController {

    private final File _folder = new File(Main.getCreationDir());
    private ObservableList<String> _items;
    private String _selectedItem = null;
    private ArrayList<String> _favourites = new ArrayList<String>();

    private MediaPlayer _bgmusic;
    private MediaPlayer _creationMusic;
    private MediaPlayer _player;
    private MediaView _mediaView;
    private Media _video;
    private Boolean _musicToggle;

    @FXML
    private ListView<Label> creationList;

    @FXML
    private Button btnDel;
    @FXML
    private Button btnPlay;
    @FXML
    private Button btnStop;

    @FXML
    private ToggleButton btnFavourite;
    @FXML
    private ToggleButton music;

    @FXML
    private Pane videoPane;

    @FXML
    private ChoiceBox<String> musicCreationBox;

    @FXML
    private Slider timeSlider;


    public void initialize() throws IOException, InterruptedException {
        updateListTree();

        // Upon selecting a creation, display video buttons and image preview, and toggle favourite
        creationList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                selectItem();

                if (_selectedItem != null) {

                    if (!timeSlider.isDisabled()) {
                        btnPlay.setDisable(true);
                        btnDel.setDisable(true);
                        btnFavourite.setDisable(true);

                    }
                    else {
                        File fileUrl = new File(Main.getCreationDir() + "/" + _selectedItem + ".mp4");
                        _video = new Media(fileUrl.toURI().toString());
                        _player = new MediaPlayer(_video);

                        _mediaView = new MediaView(_player);
                        _mediaView.fitWidthProperty().bind(videoPane.widthProperty());
                        _mediaView.fitHeightProperty().bind(videoPane.heightProperty());
                        _mediaView.setPreserveRatio(false);

                        videoPane.getChildren().clear();
                        videoPane.getChildren().add(_mediaView);

                        btnPlay.setDisable(false);
                        btnDel.setDisable(false);
                        btnFavourite.setDisable(false);

                    }

                    if (_favourites.contains(_selectedItem)) {
                        btnFavourite.setText("Unlike ★");
                        btnFavourite.setSelected(true);
                        btnFavourite.setOpacity(0.7);

                    }
                    else {
                        btnFavourite.setText("Like ★");
                        btnFavourite.setSelected(false);

                    }
                }
            }
        });

        // Initialise dynamic style changes for components
        btnPlay.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                btnPlay.setOpacity(0.7);
            }
        });

        btnPlay.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (!btnPlay.isDisabled()) {
                    btnPlay.setOpacity(1.0);
                }
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
                if (!btnDel.isDisabled()) {
                    btnDel.setOpacity(1.0);
                }
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
                if (!btnFavourite.isDisabled()) {
                    btnFavourite.setOpacity(1.0);
                }
            }
        });

        btnStop.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                btnStop.setOpacity(0.7);
            }
        });

        btnStop.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (!btnStop.isDisabled()) {
                    btnStop.setOpacity(1.0);
                }
            }
        });
    }

    /**
     * Stops playing a video creation.
     */
    public void handleBtnStop() {

        btnStop.setDisable(true);

        _player.stop();
        _player = new MediaPlayer(_video);

        videoPane.getChildren().clear();
        videoPane.getChildren().add(_mediaView);

        btnPlay.setStyle("-fx-background-color: rgba(0, 255, 0, 0.5); -fx-border-width: 5; -fx-border-color: green; -fx-border-radius: 20 0 0 20; -fx-background-radius: 20 0 0 20;");
        btnPlay.setText("Play  ▶");
        btnFavourite.setDisable(false);
        btnDel.setDisable(false);

        timeSlider.setValue(0);
        musicCreationBox.setDisable(false);

        if (_creationMusic != null) {
            _creationMusic.stop();
            _creationMusic = null;

        }
    }

    /**
     * Play/Pause a video creation.
     */
    @FXML
    private void handleBtnPlay() {

        btnStop.setDisable(false);

        if (btnPlay.getText().equals("Pause")) {
            timeSlider.setDisable(true);
            _player.pause();

            btnPlay.setStyle("-fx-background-color: rgba(0, 255, 0, 0.5); -fx-border-width: 5; -fx-border-color: green; -fx-border-radius: 20 0 0 20; -fx-background-radius: 20 0 0 20;");
            btnPlay.setText("Continue ▶");
            btnDel.setDisable(false);
            btnFavourite.setDisable(false);

            if (_creationMusic != null) {
                _creationMusic.pause();

            }
        }
        else {
            timeSlider.setDisable(false);

            btnPlay.setStyle("-fx-background-color: rgba(255, 165, 0, 0.8); -fx-border-width: 5; -fx-border-color: orange; -fx-border-radius: 20 0 0 20; -fx-background-radius: 20 0 0 20;");
            btnPlay.setText("Pause");
            btnDel.setDisable(true);
            btnFavourite.setDisable(true);

            musicCreationBox.setDisable(true);

            if (_player.getCurrentTime() != Duration.ZERO) {

                if (_creationMusic != null) {
                    _creationMusic.play();

                }

                _player.play();

                // Configure time slider to sync with video after pausing
                _player.currentTimeProperty().addListener(new InvalidationListener() {
                    @Override
                    public void invalidated(Observable observable) {
                        if (btnPlay.getText().equals("Pause")) {
                            updateValues();
                        }

                    }
                });
                timeSlider.valueProperty().addListener(new InvalidationListener() {
                    @Override
                    public void invalidated(Observable observable) {
                        if (timeSlider.isPressed()) {
                            _player.seek(_player.getMedia().getDuration().multiply(timeSlider.getValue() / 100));
                        }
                    }
                });
            }

            else {

                if (musicCreationBox.getSelectionModel().getSelectedItem().equals("Techno Music")) {
                    _creationMusic = new MediaPlayer(new Media(new File(System.getProperty("user.dir") + "/Techno.mp3").toURI().toString()));
                    _creationMusic.play();
                }
                else if (musicCreationBox.getSelectionModel().getSelectedItem().equals("Chill Music")) {
                    _creationMusic = new MediaPlayer(new Media(new File(System.getProperty("user.dir") + "/Chill.mp3").toURI().toString()));
                    _creationMusic.play();
                }
                else {
                    _creationMusic = null;
                }

                videoPane.getChildren().clear();
                videoPane.getChildren().add(_mediaView);
                _player.setAutoPlay(true);

                // On end of video, disable buttons and reset time slider/pane to default
                _player.setOnEndOfMedia(new Runnable() {
                    @Override
                    public void run() {
                        timeSlider.setDisable(true);
                        timeSlider.setValue(0);

                        videoPane.getChildren().clear();
                        videoPane.getChildren().add(_mediaView);

                        _player = new MediaPlayer(_video);

                        btnDel.setDisable(false);
                        btnFavourite.setDisable(false);
                        btnPlay.setStyle("-fx-background-color: rgba(0, 255, 0, 0.5); -fx-border-width: 5; -fx-border-color: green; -fx-border-radius: 20 0 0 20; -fx-background-radius: 20 0 0 20;");
                        btnPlay.setText("Play  ▶");
                        btnStop.setDisable(true);

                        musicCreationBox.setDisable(false);

                        if (_creationMusic != null) {
                            _creationMusic.stop();
                            _creationMusic = null;

                        }
                    }
                });




                // Configure time slider to sync with video
                _player.currentTimeProperty().addListener(new InvalidationListener() {
                    @Override
                    public void invalidated(Observable observable) {
                        if (btnPlay.getText().equals("Pause")) {
                            updateValues();
                        }

                    }
                });
                timeSlider.valueProperty().addListener(new InvalidationListener() {
                    @Override
                    public void invalidated(Observable observable) {
                        if (_player == null) {
                            return;

                        }
                        else if (timeSlider.isPressed()) {
                            _player.seek(_player.getMedia().getDuration().multiply(timeSlider.getValue() / 100));

                        }
                    }
                });

            }
        }
    }

    /**
     * Update values of time slider to current video.
     */
    protected void updateValues() {
        Platform.runLater(new Runnable() {
            public void run() {
                timeSlider.setValue(_player.getCurrentTime().toMillis()/ _player.getTotalDuration().toMillis() * 100);

            }
        });
    }

    /**
     * Delete a creation. And prompts the user to confirm their actions.
     * @throws IOException
     */
    @FXML
    private void handleBtnDel() throws IOException {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getDialogPane().getStylesheets().add(("Alert.css"));
        alert.setTitle("Confirm Deletion");
        alert.setContentText("Are you sure you want to delete " + _selectedItem + "?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK) {
            btnPlay.setDisable(true);
            btnDel.setDisable(true);
            btnFavourite.setDisable(true);

            _player = null;
            videoPane.getChildren().clear();

            if (_favourites.contains(_selectedItem)) {
                _favourites.remove(_selectedItem);

            }
            String delCmd = "rm -r "+ Main.getCreationDir() + "/" + _selectedItem + " " + Main.getCreationDir() + "/"+_selectedItem + ".mp4 " + Main.getQuizDir() + "/" + _selectedItem + ".mp4";

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
     * Switches to WikitSearch screen.
     * @throws IOException
     */
    @FXML
    private void handleBtnCreate() throws IOException {

        if (_player != null) {
            _player.stop();

        }
        FXMLLoader loader = Main.changeScene("resources/WikitSearch.fxml");
        WikitSearchController wikitController = loader.<WikitSearchController>getController();

        String s = "Music: OFF";
        if (!_musicToggle) {
            s = "Music: ON";
            _bgmusic.play();
        }

        wikitController.transferMusic(_player, _musicToggle, s);
    }

    /**
     * Toggles the favourites/like button.
     * @throws IOException
     */
    @FXML
    public void handleBtnFavourite() throws IOException {

        if (btnFavourite.isSelected()) {
            creationList.getSelectionModel().getSelectedItem().setStyle("-fx-background-color: rgba(255, 255, 0, 0.5);");
            btnFavourite.setText("Unlike ★");
            _favourites.add(_selectedItem);


        } else {
            creationList.getSelectionModel().getSelectedItem().setStyle("-fx-background-color: rgba(255, 255, 0, 0.0);");
            btnFavourite.setText("Like ★");
            _favourites.remove(_selectedItem);

        }
        updateFavourites();

    }

    /**
     * Updates text file from current array.
     * @throws IOException
     */
    public void updateFavourites() throws IOException {

        String s = "";
        for (int i = 0; i < _favourites.size(); i++) {
            s += _favourites.get(i) + ".mp4 ";
        }

        String cmd = "echo '" + s + "' > " + Main.getFavouriteDir() + "/favourites.txt";

        ProcessBuilder updateFavouritespb = new ProcessBuilder("bash", "-c", cmd);
        Process favouritesProcess = updateFavouritespb.start();
    }

    /**
     * Get the creations and favourites in the folder.
     * @throws FileNotFoundException
     */
    public void updateListTree() throws FileNotFoundException {
        creationList.getItems().clear();

        String allFavourites = "";
        try {
            Scanner scanner = new Scanner(new File(Main.getFavouriteDir() + "/favourites.txt")).useDelimiter("\\A");
            if (scanner.hasNextLine()) {
                allFavourites = scanner.nextLine();

                _favourites.addAll(Arrays.asList(allFavourites.split(".mp4 ")));
                Collections.sort(_favourites);
            }

        }
        catch (FileNotFoundException e) {
            // Do nothing
        }

        _items = FXCollections.observableArrayList(listFilesOfFolder(_folder));

        for (int i = 0; i < _items.size(); i++) {
            Label label = new Label(_items.get(i));
            label.prefWidthProperty().bind(creationList.widthProperty().subtract(27));
            label.setWrapText(true);

            for (int j = 0; j < _favourites.size(); j++) {
                if (_items.get(i).equals(_favourites.get(j))) {
                    label.setStyle("-fx-background-color: rgba(255, 255, 0, 0.5);");
                }

            }
            creationList.getItems().add(label);

        }
    }

    /**
     * Get the creations in the folder into an arraylist sorted.
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
     * Get the selected item in list view
     */
    @FXML
    private void selectItem() {
            _selectedItem = creationList.getSelectionModel().getSelectedItem().getText();

    }

    /**
     * Go back to welcome screen
     * @throws IOException
     */
    @FXML
    private void handleBtnBack() throws IOException {

        if (_player != null) {
            _player.pause();

        }
        FXMLLoader loader = Main.changeScene("resources/Welcome.fxml");
        WelcomeController welcomeController = loader.<WelcomeController>getController();

        String s = "Music: OFF";
        if (!_musicToggle) {
            s = "Music: ON";
            _bgmusic.play();

        }
        welcomeController.transferMusic(_bgmusic, _musicToggle, s);

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

        music.setSelected(true);
        music.setText("Music: OFF");
        music.setDisable(true);

        _musicToggle = toggle;

    }

}
