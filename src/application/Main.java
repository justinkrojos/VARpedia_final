package application;

import application.controllers.CreateController;
import application.controllers.WelcomeController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main extends Application {

    //private Stage _primaryStage;

    //private String _creationDir;

    private static Stage _primaryStage;



    @Override
    public void start(Stage primaryStage) throws IOException {
        //System.out.println(this.getClass().getResource("resources/Home.fxml"));

        // MediaPlayer player = new MediaPlayer(new Media(new File(System.getProperty("user.dir") + "/src/application/resources/bgmusic.wav").toURI().toString()));
        MediaPlayer player = new MediaPlayer(new Media(new File(System.getProperty("user.dir") + "/bgmusic.wav").toURI().toString()));
        _primaryStage = primaryStage;

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("resources/Welcome.fxml"));

        Parent layout = loader.load();

        WelcomeController welcomeController = loader.<WelcomeController>getController();
        welcomeController.transferMediaPlayer(player);
        player.play();

        Scene scene = new Scene(layout);
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Platform.exit();
                System.exit(0);
            }
        });
    }

    /**
     * This method changes the scene, of the main window and returns the fxml loader.
     * @param fxml
     * @throws IOException
     */
    public static FXMLLoader changeScene(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource(fxml));
        Parent layout = loader.load();
        Scene scene = new Scene(layout);
        _primaryStage.setScene(scene);
        _primaryStage.show();
        return loader;
    }


/*    public Stage getPrimaryStage() {
        return _primaryStage;
    }*/


    /**
     * Gets the creation directory.
     * Code written with the help of:
     * https://stackoverflow.com/questions/320542/how-to-get-the-path-of-a-running-jar-file
     * @return
     */
    public static String getCreationDir() {
        String creationsDir = null;
        try {
            creationsDir = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath();
            creationsDir = creationsDir.substring(0,creationsDir.lastIndexOf("/"));
/*            System.out.println(creationsDir);
            String systemDir = System.getProperty("user.dir");
            System.out.println(systemDir);*/
            creationsDir = creationsDir + "/creations";
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return creationsDir;
    }

    /**
     * Directory for all favourite creations.
     * @return
     */
    public static String getFavouriteDir() {
        String creationsDir = null;
        try {
            creationsDir = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath();
            creationsDir = creationsDir.substring(0,creationsDir.lastIndexOf("/"));
/*            System.out.println(creationsDir);
            String systemDir = System.getProperty("user.dir");
            System.out.println(systemDir);*/
            creationsDir = creationsDir + "/favourites";
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return creationsDir;
    }

    /**
     * Get the quiz directory
     * @return
     */
    public static String getQuizDir() {
        String creationsDir = null;
        try {
            creationsDir = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath();
            creationsDir = creationsDir.substring(0,creationsDir.lastIndexOf("/"));
/*            System.out.println(creationsDir);
            String systemDir = System.getProperty("user.dir");
            System.out.println(systemDir);*/
            creationsDir = creationsDir + "/quiz";
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return creationsDir;
    }

    /**
     * Directory for all creations.
     * @param dir
     * @throws InterruptedException
     * @throws IOException
     */
    private static void createDirectory(String dir) throws InterruptedException, IOException {
        //String creationsDir = dir;
        String cmd = "[ ! -d " + dir + " ]";
        ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);

        Process process = pb.start();
        //System.out.println(process.waitFor());
        if (process.waitFor() == 0) {
            cmd = "mkdir -p " + dir;
            ProcessBuilder mkdirpb = new ProcessBuilder("bash", "-c", cmd);
            Process mkdirP = mkdirpb.start();
        }
    }

    public static void main(String[] args) {
        try {
/*            String systemDir = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath();
            systemDir = systemDir.substring(0,systemDir.lastIndexOf("/"));
            //System.out.println(systemDir);

            //String systemDir = System.getProperty("user.dir");
            String creationsDir = systemDir + "/creations";*/
            //System.out.println(System.getProperty("user.dir"));

/*            String creationsDir = getCreationDir();
            String cmd = "[ ! -d " + creationsDir + " ]";
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);

            Process process = pb.start();
            //System.out.println(process.waitFor());
            if (process.waitFor() == 0) {
                cmd = "mkdir -p " + creationsDir;
                ProcessBuilder mkdirpb = new ProcessBuilder("bash", "-c", cmd);
                Process mkdirP = mkdirpb.start();
            }*/

            createDirectory(getFavouriteDir());
            createDirectory(getCreationDir());
            createDirectory(getQuizDir());


            launch(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
