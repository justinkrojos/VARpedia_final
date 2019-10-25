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
import javafx.stage.Popup;
import javafx.stage.Stage;

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
    private static String _creationName;
    private static String _term;
    public int count = 0;

    @FXML
    private Button _btnDownload;

    @FXML
    private TextField creationName;

    @FXML private ImageView _loadingImage;

    @FXML Button _btnCreateCreation;

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
    private ToggleButton music;

    MediaPlayer bgmusic;

    @FXML
    private AnchorPane imagePane;

    @FXML
    private Button downloadImgBtn;

    private ArrayList<ImageView> _imageViews;
    private ArrayList<CheckBox> _checkBoxs;

    private List<Image> _images = new ArrayList<Image>();

    private String[][] savedText;

    private final int _numRows = 2;
    private final int _numCols = 5;

   // private List<Image> _image;

    private static File dir;

    FXMLLoader loader;

    /**
     * Download 10 images from flikr associated with the term.
     */
    @FXML
    private void handleBtnDownload() {
        if(!creationName.getText().matches("[a-zA-Z0-9_-]*") || creationName.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.getDialogPane().getStylesheets().add(("Alert.css"));
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Invalid Creation name");
            alert.setContentText("Please enter a valid non-empty creation name, allowed characters: a-z A-Z 0-9 _ -");
            alert.showAndWait();
            return;
        }

        String creationDir = Main.getCreationDir();
        String creationFile = creationDir + "/" + creationName.getText();
        //String cmd = "[ -e " + creationFile + " ]";
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
                alert.setHeaderText("Creation Name already in use, enter another name or overwrite the existing creation");
                alert.setContentText("WARNING: By selecting overwrite the old creation will be deleted!!!");
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
            _loadingImage.setVisible(true);
            downloadImgBtn.setDisable(true);
            downloadImgBtn.setText("Loading...");
            creationName.setDisable(true);
            createNewDir(creationFile);



        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        DownloadImages dl = new DownloadImages(creationName.getText(),_term);
        team.submit(dl);
        dl.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                _loadingImage.setVisible(false);
                imagePane.setVisible(true);
                downloadImgBtn.setText("Done");
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
        int counter = 0;
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

        if (!checkedBoxes()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.getDialogPane().getStylesheets().add(("Alert.css"));
            alert.setTitle("You selected no images D:");
            alert.setHeaderText(null);
            alert.setContentText("Please Select at least one image");
            alert.showAndWait();
            return;
        }

        _loadingImage.setVisible(true);
        _btnCreateCreation.setText("Creating creation...");
        _btnCreateCreation.setDisable(true);

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
                //System.out.println(cmd);
                imageNum++;
            }
            count++;
        }

        CreateAudioTask audioTask = new CreateAudioTask(creationName.getText(), savedText);
        team.submit(audioTask);

        AudioMergeTask audioMergeTask = new AudioMergeTask(creationName.getText(), savedText.length);
        team.submit(audioMergeTask);

        MakeSlideShow task = new MakeSlideShow(_term, creationName.getText());
        team.submit(task);
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {

                MergeTask task = new MergeTask(creationName.getText());
                team.submit(task);

                QuizTask quizTask = new QuizTask(_term, creationName.getText());
//                team.submit(quizTask);

                task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent workerStateEvent) {
                        team.submit(quizTask);
                        System.out.println("REACHED");

                        try {
                            loader = Main.changeScene("resources/Welcome.fxml");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }



                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.getDialogPane().getStylesheets().add(("Alert.css"));
                        alert.setTitle("Creation Complete!");
                        alert.setHeaderText(null);
                        alert.setContentText("Creation complete! You can now watch your new creation!");
                        alert.showAndWait();

                    }
                });

                quizTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent workerStateEvent) {
                        delCreationDir();
                    }
                });
                //_homeController.updateListTree();


            }
        });
    }

/*    private Popup createPopup(String message) {
        Popup popup = new Popup();
        popup.setAutoFix(true);
        popup.setAutoHide(true);
        popup.setHideOnEscape(true);
        Label label = new Label(message);
        label.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                popup.hide();
            }
        });
        popup.getContent().add(label);
        return popup;
    }*/

    /**
     * Deletes the creation folder of the current creation.
     */
    private void delCreationDir(){
        System.out.println("Deleteing creation folder");
        String cmd = "rm -r " + Main.getCreationDir()+ System.getProperty("file.separator") +  creationName.getText();
        ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
        try {
            Process p = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void initialize() {
        _loadingImage.setVisible(false);

        _imageViews = new ArrayList<ImageView>(Arrays.asList(_iv0,_iv1,_iv2,_iv3,_iv4,_iv5,_iv6,_iv7,_iv8,_iv9));
        _checkBoxs =  new ArrayList<CheckBox>(Arrays.asList(_cb0,_cb1,_cb2,_cb3,_cb4,_cb5,_cb6,_cb7,_cb8,_cb9));

        creationName.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    handleBtnDownload();
                }
            }
        });
    }

    public void transferInfo(String text, String[][] savedText) {
        _term = text;
        this.savedText = savedText;
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

        if (downloadImgBtn.isDisabled()) {
            delCreationDir();
        }

        FXMLLoader loader = Main.changeScene("resources/Welcome.fxml");
        WelcomeController welcomeController = loader.<WelcomeController>getController();
        welcomeController.transferMusic(bgmusic, music.isSelected(), music.getText());
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
}
