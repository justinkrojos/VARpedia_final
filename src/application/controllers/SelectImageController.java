package application.controllers;

import application.DownloadImages;
import application.Main;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SelectImageController {

    private ExecutorService team = Executors.newSingleThreadExecutor();
    private static String _creationName;
    private static String _term;

    @FXML
    private Button _btnDownload;

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

    private ArrayList<ImageView> _imageViews;
    private ArrayList<CheckBox> _checkBoxs;

    private List<Image> _images = new ArrayList<Image>();

    private final int _numRows = 2;
    private final int _numCols = 5;

   // private List<Image> _image;

    private static File dir;

    private CreateController _createController;
    private SelectImageController _selectImageController;
    private Stage _imageStage;

    public void setUp(String creationName, String term, CreateController createController, Stage imageStage) {
        _imageStage = imageStage;
        _createController = createController;
        _creationName = creationName;
        _term = term;
        dir = new File(Main.getCreationDir()+"/"+_creationName+"/"+"images/");
    }

    @FXML
    private void handleBtnDownload() {
        DownloadImages dl = new DownloadImages(_creationName,_term);
        team.submit(dl);
        dl.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                _btnDownload.setDisable(true);

                File[] files = dir.listFiles();
                for (File file: files) {
                    //_images.add();
                    System.out.println(file.getAbsolutePath());
                    _images.add(new Image(file.toURI().toString()));
                }
                int count = 0;
                for (ImageView i: _imageViews) {
                    System.out.println(_images.get(count));
                    i.setImage(_images.get(count));

                    count++;
                }

            }
        });
    }

    @FXML
    private void handleBtnSubmit() throws IOException, InterruptedException {

        int count = 0;
        int imageNum = 0;
        for (CheckBox c: _checkBoxs) {
            String path = Main.getCreationDir()+"/"+_creationName+"/"+"images/";
            String newPath = Main.getCreationDir()+"/"+_creationName+"/";
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
        _imageStage.close();

    }


    public void initialize() {
        _imageViews = new ArrayList<ImageView>(Arrays.asList(_iv0,_iv1,_iv2,_iv3,_iv4,_iv5,_iv6,_iv7,_iv8,_iv9));
        _checkBoxs =  new ArrayList<CheckBox>(Arrays.asList(_cb0,_cb1,_cb2,_cb3,_cb4,_cb5,_cb6,_cb7,_cb8,_cb9));

    }
}
