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
    private ArrayList<String> favourites = new ArrayList<String>();

    @FXML
    private ListView<Label> _creationList;

    @FXML
    private Button btnPlay;

    @FXML
    private Button btnDel;

    @FXML
    private Pane _player;

    @FXML
    private Button btnStop;

    @FXML
    private ChoiceBox<String> musicCreationBox;

    @FXML
    private ToggleButton btnFavourite;

    @FXML
    private ToggleButton music;

    private MediaPlayer bgmusic;

    private MediaPlayer creationMusic;

    private MediaPlayer player;

    @FXML
    private Slider timeSlider;

    private Boolean musicToggled;

    private MediaView mediaView;

    private Media video;

    public void initialize() throws IOException, InterruptedException {
        updateListTree();

        _creationList.setOnMouseClicked(new EventHandler<MouseEvent>() {
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
                        video = new Media(fileUrl.toURI().toString());
                        player = new MediaPlayer(video);
                        mediaView = new MediaView(player);
                        mediaView.fitWidthProperty().bind(_player.widthProperty());
                        mediaView.fitHeightProperty().bind(_player.heightProperty());
                        mediaView.setPreserveRatio(false);
                        _player.getChildren().clear();
                        _player.getChildren().add(mediaView);
                        btnPlay.setDisable(false);
                        btnDel.setDisable(false);
                        btnFavourite.setDisable(false);
                    }
                    if (favourites.contains(_selectedItem)) {
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

        btnStop.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                btnStop.setOpacity(0.7);
            }
        });

        btnStop.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                btnStop.setOpacity(1.0);
            }
        });
    }

    /**
     * Stops playing a video creation.
     */
    public void handleBtnStop() {
        btnStop.setDisable(true);
        player.stop();
        _player.getChildren().removeAll();
        _player.getChildren().clear();
        _player.getChildren().add(mediaView);
        player = new MediaPlayer(video);

        btnPlay.setStyle("-fx-background-color: rgba(0, 255, 0, 0.5); -fx-border-width: 5; -fx-border-color: green; -fx-border-radius: 20 0 0 20; -fx-background-radius: 20 0 0 20;");
        btnPlay.setText("Play  ▶");
        btnFavourite.setDisable(false);
        btnDel.setDisable(false);
        timeSlider.setValue(0);
        musicCreationBox.setDisable(false);

        if (creationMusic != null) {
            creationMusic.stop();
            creationMusic = null;
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
            btnPlay.setStyle("-fx-background-color: rgba(0, 255, 0, 0.5); -fx-border-width: 5; -fx-border-color: green; -fx-border-radius: 20 0 0 20; -fx-background-radius: 20 0 0 20;");
            btnPlay.setText("Continue ▶");
            btnDel.setDisable(false);
            btnFavourite.setDisable(false);
            player.pause();

            if (creationMusic != null) {
                creationMusic.pause();
            }
        }

        else {

            timeSlider.setDisable(false);
            btnPlay.setStyle("-fx-background-color: rgba(255, 165, 0, 0.8); -fx-border-width: 5; -fx-border-color: orange; -fx-border-radius: 20 0 0 20; -fx-background-radius: 20 0 0 20;");
            btnPlay.setText("Pause");

            btnFavourite.setDisable(true);
            btnDel.setDisable(true);
            musicCreationBox.setDisable(true);

            if (player.getCurrentTime() != Duration.ZERO) {

                if (creationMusic != null) {
                    creationMusic.play();
                }

                player.play();
                player.currentTimeProperty().addListener(new InvalidationListener() {
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
                            player.seek(player.getMedia().getDuration().multiply(timeSlider.getValue() / 100));
                        }
                    }
                });
            }
            else {

                if (musicCreationBox.getSelectionModel().getSelectedItem().equals("Techno Music")) {
                    creationMusic = new MediaPlayer(new Media(new File(System.getProperty("user.dir") + "/Techno.mp3").toURI().toString()));
                    creationMusic.play();
                }
                else if (musicCreationBox.getSelectionModel().getSelectedItem().equals("Chill Music")) {
                    creationMusic = new MediaPlayer(new Media(new File(System.getProperty("user.dir") + "/Chill.mp3").toURI().toString()));
                    creationMusic.play();
                }
                else {
                    creationMusic = null;
                }

                _player.getChildren().removeAll();
                _player.getChildren().clear();
                player.setAutoPlay(true);

                player.setOnReady(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
                player.setOnEndOfMedia(new Runnable() {
                    @Override
                    public void run() {
                        timeSlider.setDisable(true);
                        timeSlider.setValue(0);
                        _player.getChildren().removeAll();
                        _player.getChildren().clear();
                        _player.getChildren().add(mediaView);
                        player = new MediaPlayer(video);
                        btnDel.setDisable(false);
                        btnFavourite.setDisable(false);
                        btnPlay.setStyle("-fx-background-color: rgba(0, 255, 0, 0.5); -fx-border-width: 5; -fx-border-color: green; -fx-border-radius: 20 0 0 20; -fx-background-radius: 20 0 0 20;");
                        btnPlay.setText("Play  ▶");
                        btnStop.setDisable(true);
                        if (creationMusic != null) {
                            creationMusic.stop();
                            creationMusic = null;
                        }
                        musicCreationBox.setDisable(false);
                    }
                });


                _player.getChildren().add(mediaView);


                player.currentTimeProperty().addListener(new InvalidationListener() {
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
                        if (player == null) {
                            return;
                        }
                        else if (timeSlider.isPressed()) {
                            player.seek(player.getMedia().getDuration().multiply(timeSlider.getValue() / 100));
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
                timeSlider.setValue(player.getCurrentTime().toMillis()/player.getTotalDuration().toMillis() * 100);
            }
        });
    }


    /**
     * Delete a creation. And prompts the user to confirm their actions.
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
            btnPlay.setDisable(true);
            btnDel.setDisable(true);
            btnFavourite.setDisable(true);

            player = null;
            _player.getChildren().clear();

            if (favourites.contains(_selectedItem)) {
                favourites.remove(_selectedItem);
            }

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
     * Switches to WikitSearch screen.
     * @throws IOException
     */
    @FXML
    private void handleBtnCreate() throws IOException {
        if (player != null) {
            player.stop();
        }
        FXMLLoader loader = Main.changeScene("resources/WikitSearch.fxml");
        WikitSearchController wikitController = loader.<WikitSearchController>getController();
        String s = "Music: OFF";
        if (!musicToggled) {
            s = "Music: ON";
            bgmusic.play();
        }
        wikitController.transferMusic(player, musicToggled, s);
    }

    /**
     * Toggles the favourites/like button.
     * @throws IOException
     * @throws InterruptedException
     */

    @FXML
    public void handleBtnFavourite() throws IOException, InterruptedException {
        if (btnFavourite.isSelected()) {
            _creationList.getSelectionModel().getSelectedItem().setStyle("-fx-background-color: rgba(255, 255, 0, 0.5);");
            btnFavourite.setText("Unlike ★");
            favourites.add(_selectedItem);

        } else {
            _creationList.getSelectionModel().getSelectedItem().setStyle("-fx-background-color: rgba(255, 255, 0, 0.0);");
            btnFavourite.setText("Like ★");
            favourites.remove(_selectedItem);
        }

        updateFavourites();

    }

    public void updateFavourites() throws IOException {

        String s = "";
        for (int i = 0; i < favourites.size(); i++) {
            s += favourites.get(i) + ".mp4 ";
        }

        String cmd = "echo '" + s + "' > " + Main.getFavouriteDir() + "/favourites.txt";

        ProcessBuilder updateFavouritespb = new ProcessBuilder("bash", "-c", cmd);
        Process favouritesProcess = updateFavouritespb.start();
    }

    /**
     * Get the creations and favourites in the folder.
     */
    public void updateListTree() throws FileNotFoundException {
        _creationList.getItems().clear();

        String allFavourites = "";
        try {
            Scanner scanner = new Scanner(new File(Main.getFavouriteDir() + "/favourites.txt")).useDelimiter("\\A");
            if (scanner.hasNextLine()) {
                allFavourites = scanner.nextLine();

                favourites.addAll(Arrays.asList(allFavourites.split(".mp4 ")));
                Collections.sort(favourites);
            }

        }
        catch (FileNotFoundException e) {

        }



        _items = FXCollections.observableArrayList(listFilesOfFolder(_folder));
        for (int i = 0; i < _items.size(); i++) {
            Label label = new Label(_items.get(i));
            label.prefWidthProperty().bind(_creationList.widthProperty().subtract(27));
            label.setWrapText(true);

            for (int j = 0; j < favourites.size(); j++) {
                if (_items.get(i).equals(favourites.get(j))) {
                    label.setStyle("-fx-background-color: rgba(255, 255, 0, 0.5);");
                }
            }
            _creationList.getItems().add(label);
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
            _selectedItem = _creationList.getSelectionModel().getSelectedItem().getText();

    }

    /**
     * Go back to welcome screen
     * @throws IOException
     */
    @FXML
    private void handleBtnBack() throws IOException {
        if (player != null) {
            player.pause();
        }
        FXMLLoader loader = Main.changeScene("resources/Welcome.fxml");
        WelcomeController welcomeController = loader.<WelcomeController>getController();
        String s = "Music: OFF";
        if (!musicToggled) {
            s = "Music: ON";
            bgmusic.play();
        }
        welcomeController.transferMusic(bgmusic, musicToggled, s);
    }

    /**
     * Turn off music for this window and keep previous user selection for music.
     * @param bgmusic
     * @param toggle
     * @param text
     */
    public void transferMusic(MediaPlayer bgmusic, Boolean toggle, String text) {
        this.bgmusic = bgmusic;
        music.setSelected(true);
        music.setText("Music: OFF");
        music.setDisable(true);
        bgmusic.pause();
        musicToggled = toggle;
    }

}
