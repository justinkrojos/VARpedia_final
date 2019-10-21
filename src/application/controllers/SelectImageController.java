package application.controllers;

import application.*;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SelectImageController {

    private ExecutorService team = Executors.newSingleThreadExecutor();
    private static String _creationName;
    private static String _term;
    public int count = 0;

    @FXML
    private Button _btnDownload;

    @FXML
    private TextField creationName;

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

    @FXML
    private void handleBtnDownload() {
        if(!creationName.getText().matches("[a-zA-Z0-9_-]*") || creationName.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
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

    @FXML
    private void handleBtnSubmit() throws IOException, InterruptedException {

        if (!checkedBoxes()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("You selected no images D:");
            alert.setHeaderText(null);
            alert.setContentText("Please Select at least one image");
            alert.showAndWait();
            return;
        }

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
                team.submit(quizTask);

                task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent workerStateEvent) {



                        System.out.println("REACHED");
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Creation Complete!");
                        alert.setHeaderText(null);
                        alert.setContentText("Creation complete! You can now watch your new creation!");
                        alert.showAndWait();


                        try {
                            loader = Main.changeScene("resources/Welcome.fxml");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
                //_homeController.updateListTree();


            }
        });
/*        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Creation is being created.");
        alert.setHeaderText(null);
        alert.setContentText("Creation is being created, you will get a popup when its done.");
        //alert.show();*/


    }


    public void initialize() {
        _imageViews = new ArrayList<ImageView>(Arrays.asList(_iv0,_iv1,_iv2,_iv3,_iv4,_iv5,_iv6,_iv7,_iv8,_iv9));
        _checkBoxs =  new ArrayList<CheckBox>(Arrays.asList(_cb0,_cb1,_cb2,_cb3,_cb4,_cb5,_cb6,_cb7,_cb8,_cb9));

    }

    public void transferInfo(String text, String[][] savedText) {
        _term = text;
        this.savedText = savedText;
    }

    private void createNewDir(String creationFile) {
        ProcessBuilder overwritePB = new ProcessBuilder("bash", "-c", "mkdir -p " + creationFile);
        try {
            Process overwriteP = overwritePB.start();
            overwriteP.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBtnBack() throws IOException {
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
