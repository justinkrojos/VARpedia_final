package application.controllers;

import application.DownloadImages;
import application.Main;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SelectImageController {
    private static String _creationName;

    private static final File dir = new File(Main.getCreationDir()+"/"+_creationName+"/"+"images");

    private static final FilenameFilter _imageNameFiler = new FilenameFilter() {
        @Override
        public boolean accept(final File dir, final String name) {

            if (name.endsWith(".jpg")) {
                return (true);
            }
            return false;
        }

    };

    @FXML
    private GridPane _grid;

    private List _images = new ArrayList<Image>();

    private final int _numRows = 2;
    private final int _numCols = 5;

    public void setUp(String creationName) {
        _creationName = creationName;
    }

    public void initialize() {
        int i = 0;

        if (dir.isDirectory()) {
            for (final File f : dir.listFiles(_imageNameFiler)) {
                Image img = null;
                System.out.println(f.getAbsolutePath());
                img = new Image(f.getAbsolutePath());
                _images.add(img);

            }


            _grid.getChildren().add(new ImageView((Image) _images.get(0)));
        }

    }
}
