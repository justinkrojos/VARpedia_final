package application.controllers;

import application.*;
import application.tasks.*;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This controller manages the select image scene. Downloads the images related to the search term and creates the creation.
 */
public class SelectImageController {

    private ExecutorService team = Executors.newSingleThreadExecutor();
    private static String _term;

    public int count = 0;

    private MediaPlayer _bgmusic;

    private ArrayList<ImageView> _imageViews;
    private ArrayList<CheckBox> _checkBoxs;

    private List<Image> _images = new ArrayList<Image>();

    private String[][] _savedText;

    private static File dir;

    @FXML
    private TextField creationName;

    @FXML private ImageView _iv0;
    @FXML private ImageView _iv1;
    @FXML private ImageView _iv2;
    @FXML private ImageView _iv3;
    @FXML private ImageView _iv4;
    @FXML private ImageView _iv5;
    @FXML private ImageView _iv6;
    @FXML private ImageView _iv7;
    @FXML private ImageView _iv8;
    @FXML private ImageView _iv9;

    @FXML private CheckBox _cb0;
    @FXML private CheckBox _cb1;
    @FXML private CheckBox _cb2;
    @FXML private CheckBox _cb3;
    @FXML private CheckBox _cb4;
    @FXML private CheckBox _cb5;
    @FXML private CheckBox _cb6;
    @FXML private CheckBox _cb7;
    @FXML private CheckBox _cb8;
    @FXML private CheckBox _cb9;

    @FXML
    private ToggleButton btnMusic;

    @FXML
    private AnchorPane imagePane;
    @FXML
    private AnchorPane createPane;
    @FXML
    private AnchorPane loadingImage;

    @FXML
    private Button btnDownload;
    @FXML
    private Button btnCreateCreation;
    @FXML
    private Button btnBack;

    @FXML
    private Label selectToolTip;


    public void initialize() {

        _imageViews = new ArrayList<ImageView>(Arrays.asList(_iv0,_iv1,_iv2,_iv3,_iv4,_iv5,_iv6,_iv7,_iv8,_iv9));
        _checkBoxs =  new ArrayList<CheckBox>(Arrays.asList(_cb0,_cb1,_cb2,_cb3,_cb4,_cb5,_cb6,_cb7,_cb8,_cb9));


        // On selection for each checkbox, change colour of background
        for (CheckBox c: _checkBoxs) {

            c.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {

                    if (c.isSelected()) {
                        c.setStyle("-fx-background-color: black;");

                    }
                    else {
                        c.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7)");

                    }

                    if (checkedBoxes()) {
                        createPane.setVisible(true);
                        loadingImage.setVisible(false);

                    }
                    else {
                        createPane.setVisible(false);

                    }
                }
            });
        }

        creationName.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    handleBtnDownload();
                }
            }
        });

        btnCreateCreation.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                btnCreateCreation.setOpacity(0.7);
            }
        });

        btnCreateCreation.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                btnCreateCreation.setOpacity(1.0);
            }
        });
    }

    /**
     * Download 10 images from flikr associated with the term.
     */
    @FXML
    private void handleBtnDownload() {

        if (!creationName.getText().matches("[a-zA-Z0-9_-]*") || creationName.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.getDialogPane().getStylesheets().add(("Alert.css"));
            alert.setTitle("Invalid Creation name");
            alert.setContentText("Please enter a valid non-empty creation name, allowed characters: a-z A-Z 0-9 _ -");
            alert.showAndWait();
            return;

        }

        String creationDir = Main.getCreationDir();
        String creationFile = creationDir + "/" + creationName.getText();
        String cmd = "[ -e " + creationFile+".mp4" + " ] || [ -e " + creationFile + " ]";
        ProcessBuilder checkName = new ProcessBuilder("bash", "-c", cmd);
        Process checkNamep = null;
        try {
            checkNamep = checkName.start();
            int exitStatus = checkNamep.waitFor();

            if (exitStatus == 0) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.getDialogPane().getStylesheets().add(("Alert.css"));
                alert.setTitle("Creation Name Error");
                alert.setHeaderText("Creation name is taken. Enter another name or overwrite the existing creation.");
                alert.setContentText("WARNING: By selecting overwrite the old creation will be deleted!");

                ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                ButtonType btnOverwrite = new ButtonType("Overwrite");

                alert.getButtonTypes().setAll(btnCancel,btnOverwrite);

                Optional<ButtonType> result = alert.showAndWait();
                if(result.get() == btnOverwrite) {
                    String cmdOverwrite = "rm -r " + creationFile + " " + creationFile + ".mp4";
                    ProcessBuilder overwritePB = new ProcessBuilder("bash", "-c", cmdOverwrite);
                    Process overwriteP = overwritePB.start();
                    overwriteP.waitFor();

                }
                else {
                    return;

                }
            }

            btnBack.setDisable(true);
            btnDownload.setDisable(true);
            btnDownload.setText("Loading...");
            creationName.setDisable(true);

            createNewDir(creationFile);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();

        }
        DownloadImages dl = new DownloadImages(creationName.getText(),_term);
        team.submit(dl);

        // When task finishes, load images to imageviews
        dl.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                btnBack.setDisable(false);
                imagePane.setVisible(true);
                btnDownload.setText("Done");

                dir = new File(Main.getCreationDir()+"/"+creationName.getText()+"/"+"images/");
                File[] files = dir.listFiles();

                for (File file: files) {
                    _images.add(new Image(file.toURI().toString(), 200, 150, false, false));

                }
                int count = 0;

                for (ImageView i: _imageViews) {
                    i.setImage(_images.get(count));
                    count++;

                }
            }
        });
    }

    /**
     * This method checks if there is atleast one checkbox selected.
     * @return
     */
    private boolean checkedBoxes() {

        for (CheckBox c: _checkBoxs) {

            if (c.isSelected()) {
                return true;
            }

        }
        return false;

    }

    /**
     * Download the selected images, in the check boxes.
     * @throws IOException
     * @throws InterruptedException
     */
    @FXML
    private void handleBtnSubmit() throws IOException, InterruptedException {

        loadingImage.setVisible(true);
        imagePane.setDisable(true);
        selectToolTip.setOpacity(1.0);

        btnBack.setDisable(true);
        btnCreateCreation.setText("Creating creation...");
        btnCreateCreation.setDisable(true);

        int imageNum = 0;

        for (CheckBox c: _checkBoxs) {

            String path = Main.getCreationDir()+"/"+creationName.getText()+"/"+"images/";
            String newPath = Main.getCreationDir()+"/"+creationName.getText()+"/";

            if (c.isSelected()) {
                path += "image" + count + ".jpg";
                newPath += "image" + imageNum + ".jpg";

                String cmd = "cp " + path + " " + newPath;
                ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
                Process p = pb.start();
                p.waitFor();

                imageNum++;

            }
            count++;

        }

        CreateAudioTask audioTask = new CreateAudioTask(creationName.getText(), _savedText);
        team.submit(audioTask);

        MakeSlideShow task = new MakeSlideShow(_term, creationName.getText());
        team.submit(task);

        // After slideshow is created, merge audio and videos and create quiz
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {

                MergeTask task = new MergeTask(creationName.getText());
                team.submit(task);

                QuizTask quizTask = new QuizTask(_term, creationName.getText());

                // After quiz is created, load back to home
                task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent workerStateEvent) {
                        team.submit(quizTask);

                        try {
                            FXMLLoader loader = Main.changeScene("resources/Welcome.fxml");
                            WelcomeController welcomeController = loader.<WelcomeController>getController();
                            welcomeController.transferMusic(_bgmusic, btnMusic.isSelected(), btnMusic.getText());

                        } catch (IOException e) {
                            e.printStackTrace();

                        }

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.getDialogPane().getStylesheets().add(("Alert.css"));
                        alert.setTitle("Creation Complete!");
                        alert.setHeaderText("Creation complete! You can now watch your new creation!");
                        alert.showAndWait();

                    }
                });
            }
        });
    }

    /**
     * Deletes the creation folder of the current creation.
     */
    private void delCreationDir(){

        String cmd = "rm -r " + Main.getCreationDir()+ System.getProperty("file.separator") +  creationName.getText();
        ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);

        try {
            Process p = pb.start();

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    /**
     * Transfer search term and saved texts/voices
     * @param text
     * @param savedText
     */
    public void transferInfo(String text, String[][] savedText) {

        _term = text;
        this._savedText = savedText;

    }

    /**
     * Create new directory to store images.
     * @param creationFile
     */
    private void createNewDir(String creationFile) {

        ProcessBuilder overwritePB = new ProcessBuilder("bash", "-c", "mkdir -p " + creationFile);

        try {
            Process overwriteP = overwritePB.start();
            overwriteP.waitFor();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();

        }
    }

    /**
     * Go back to welcome screen
     * @throws IOException
     */
    @FXML
    private void handleBtnBack() throws IOException {
        team.shutdownNow();

        if (btnDownload.isDisabled()) {
            delCreationDir();
        }

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
