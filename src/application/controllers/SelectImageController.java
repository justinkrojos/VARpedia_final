package application.controllers;

import application.DownloadImages;
import application.Main;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SelectImageController {

    private ExecutorService team = Executors.newSingleThreadExecutor();
    private static String _creationName;
    private static String _term;

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

    private ArrayList<ImageView> _imageViews =  new ArrayList<ImageView>(Arrays.asList());
    private ArrayList<CheckBox> _checkBoxs =  new ArrayList<CheckBox>(Arrays.asList());

    private List<Image> _images = new ArrayList<Image>();

    private final int _numRows = 2;
    private final int _numCols = 5;

    private List<Image> _image;

    private static File dir;

    public void setUp(String creationName, String term) {
        _creationName = creationName;
        _term = term;
    }

    public void initialize() {

        //dir = new File(Main.getCreationDir()+"/"+_creationName+"/"+"images/");
        System.out.println(_creationName);

        DownloadImages dl = new DownloadImages(_creationName,_term);
        team.submit(dl);
        dl.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                File[] files = dir.listFiles();
                for (File file: files) {
                    //_images.add();
                    System.out.println(file.getAbsolutePath());
                }

            }
        });


    }
}
