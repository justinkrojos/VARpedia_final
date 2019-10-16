package application.controllers;

import application.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateController {

    private ExecutorService team = Executors.newSingleThreadExecutor();

    @FXML
    private Button _btnSelectImages;

    @FXML
    private AnchorPane _ap;

    @FXML
    private TextField _numImageField;

    @FXML
    private Button btnImage;

    @FXML
    private Button btnCheckCreationName;

    @FXML
    private TextArea _textArea;

    @FXML
    private TextField _termField;

    @FXML
    private Button btnSearch;

    @FXML
    private TextField _creationNameField;

    @FXML
    private Button btnPlay;

    @FXML
    private Button btnSave;


    @FXML
    private Button btnCreate;


    @FXML
    private TextField _audioName;

    @FXML
    private ListView<HBox> _audioList;

    @FXML
    private ChoiceBox<String> voicesChoiceBox;

    @FXML
    private ChoiceBox<String> voicesChoiceBox1;

    @FXML
    private Button btnPreviewAudio;

    @FXML
    private Button btnStopAudio;

    private Button btnDeleteAudio; // dynamically added

    private Stage _currentStage;
    private HomeController _homeController;
    private WelcomeController _welcomeController;

    @FXML
    private CheckBox step1;

    @FXML
    private CheckBox step2;

    @FXML
    private CheckBox step3;

    @FXML
    private CheckBox step4;




    /**
     * Check if creation name is taken, and if so let the user pick if they want to overwrite
     */
    @FXML
    private Button btnSaveAudioFile;

    ArrayList<String> existingAudio = new ArrayList<String>();

    /**
     * Set up the fields.
     * @param stage
     * @param
     */
    public void setUp(Stage stage){
        _currentStage = stage;
        //_welcomeController = welcomeController;
        //_homeController = homeController;
    }

    public void initialize(){
        btnCreate.setVisible(false);
        btnCreate.setDisable(true);

        // _currentStage = (Stage) _ap.getScene().getWindow();
    }


    @FXML
    private void handleBtnSelectImages() throws IOException, InterruptedException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("resources/SelectImage.fxml"));
        Parent layout = null;
        try {
            layout = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SelectImageController controller = loader.getController();
        controller.setUp(_creationNameField.getText(),_termField.getText(), this);
        Scene scene = new Scene(layout);
        Stage imageStage = new Stage();
        imageStage.setScene(scene);
        imageStage.show();

/*        DownloadImages dl = new DownloadImages(_creationNameField.getText(), _termField.getText());
        team.submit(dl);
        dl.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(Main.class.getResource("resources/SelectImage.fxml"));
                Parent layout = null;
                try {
                    layout = loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                SelectImageController controller = loader.getController();
                controller.setUp(_creationNameField.getText(),_termField.getText());
                Scene scene = new Scene(layout);
                Stage imageStage = new Stage();
                imageStage.setScene(scene);
                imageStage.show();
            }
        });*/

    }

    /**
     * Checks the creation name and handles overwriting creation name.
     */
    @FXML
    public void handleCreationName() {
        if(!_creationNameField.getText().matches("[a-zA-Z0-9_-]*") || _creationNameField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Invalid Creation name");
            alert.setContentText("Please enter a valid non-empty creation name, allowed characters: a-z A-Z 0-9 _ -");
            alert.showAndWait();
            return;
        }
        String creationDir = Main.getCreationDir();
        String creationFile = creationDir + "/" +_creationNameField.getText();
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
                    createNewDir(creationFile);
                    _creationNameField.setDisable(true);
                    btnCheckCreationName.setText("Success!");
                    btnCheckCreationName.setDisable(true);
                    step1.setSelected(true);
                }
                return;
            }
            createNewDir(creationFile);
            _creationNameField.setDisable(true);
            btnCheckCreationName.setText("Success!");
            btnCheckCreationName.setDisable(true);
            step1.setSelected(true);



        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create new direction with name of creation.
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
     * Handle the wikit search term.
     */
    @FXML
    public void handleSearch() {


            WikitSearchTask task = new WikitSearchTask(_termField.getText());
            team.submit(task);
            btnSearch.setDisable(true);
            task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent workerStateEvent) {
                    //TODO What happens when wikit search fails?? invalid wikie searches not handled.
                    if (_termField.getText().isEmpty() | task.getExit() != 0 | task.getOutput().equals( _termField.getText()+" not found :^(")) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Wikit Search");
                        alert.setHeaderText("Please enter a valid search term");
                        alert.setContentText("Enter a valid search term and try again.");
                        alert.showAndWait();
                        btnSearch.setDisable(false);
                        return;
                    }
                    else {
                        _textArea.setText(task.getOutput());
                        btnSearch.setDisable(true);
                        btnSearch.setText("Success!");
                        _termField.setDisable(true);
                    }
                }
            });

    }

    /**
     * Gets the images and creates the creation video.
     */
    @FXML
    private void handleGetImages() {
        if(btnSearch.isDisabled() == false || btnCheckCreationName.isDisabled() == false || !btnSaveAudioFile.getText().equals("Save and Overwrite")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Cannot Create Creation");
            alert.setHeaderText(null);
            alert.setContentText("Could not create a creation. Make sure a valid term and creation name is entered. Make sure you have a Saved Audio Queue(Highlight some text, and click save, then press Save Audio Queue. You can combine multiple audio files).");
            alert.showAndWait();
            return;
        }

        try {
            int num = Integer.parseInt(_numImageField.getText());
            if (num <=0 || num > 10) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Invalid number of images");
                alert.setHeaderText(null);
                alert.setContentText("Please enter a number between 1 and 10, inclusive.");
                alert.showAndWait();
                return;
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid number of images");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a number between 1 and 10, inclusive.");
            alert.showAndWait();
            return;
        }
        String term = _termField.getText();
        int numImages = Integer.parseInt(_numImageField.getText());
        String creationName = _creationNameField.getText();
        getImages(term,creationName,numImages);
        _numImageField.setDisable(true);
        btnImage.setDisable(true);


    }

    /**
     * Handles the create button and merges audio with video.
     * Button not used anymore!!!
     */
    @FXML
    private void mergeVideoAudio(){
/*        if (!btnImage.getText().equals("Success!")){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cannot merge video and audio file");
            alert.setHeaderText(null);
            alert.setContentText("Please check that the Make Video(under the Get image");
            alert.showAndWait();
            return;
        }*/

        String creationName = _creationNameField.getText();
        String term = _termField.getText();
        MergeTask task = new MergeTask(creationName);
        team.submit(task);

        QuizTask quizTask = new QuizTask(term, creationName);
        team.submit(quizTask);

        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                btnCreate.setText("Success!");
            }
        });
        //_homeController.updateListTree();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Creation Complete");
        alert.setHeaderText(null);
        alert.setContentText("Creation complete, please refresh list of creations.");
        alert.showAndWait();
        //_homeController.updateListTree();
    }

    /**
     * Downloads the images.
     * @param term
     * @param creationName
     * @param numImages
     */
    private void getImages(String term, String creationName, int numImages) {
        GetImagesTask task = new GetImagesTask(term, creationName, numImages);
        team.submit(task);
        _currentStage.close();
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                btnImage.setText("Success!");
                mergeVideoAudio();


            }
        });
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Creation is being created.");
        alert.setHeaderText(null);
        alert.setContentText("Creation is being created, you will get a popup when its done.");
        alert.show();
    }

    /**
     * Previews selected text
     * @throws IOException
     */

    @FXML
    public void handleAudioPreview() throws IOException {

        PreviewAudioTask previewAudioTask = new PreviewAudioTask(_textArea.getSelectedText(), getVoicesObject(voicesChoiceBox1.getSelectionModel().getSelectedItem()).getVoicePackage());

        if (_textArea.getSelectedText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Wikit Search");
            alert.setHeaderText("No words were highlighted");
            alert.setContentText("Please highlight a maximum of 20 words and try again.");
            alert.showAndWait();
        }
        else {
            String[] words = _textArea.getSelectedText().split("\\s+");
            if (words.length > 20) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Wikit Search");
                alert.setHeaderText("Word Maximum Exceeded");
                alert.setContentText("Please highlight a maximum of 20 words and try again.");
                alert.showAndWait();
            }
            else {

                team.submit(previewAudioTask);

            }
        }
    }

    /**
     * Saves the chunk of text as a wav file
     */

    @FXML
    public void handleSaveAudioBtn() {


        if (!btnSearch.getText().equals("Success!")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Wikit Search");
            alert.setHeaderText("No words were highlighted");
            alert.setContentText("Please wikit search a term and try again.");
            alert.showAndWait();
        } else if (_termField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Wikit Search");
            alert.setHeaderText("No words were highlighted");
            alert.setContentText("Please wikit search a term and try again.");
            alert.showAndWait();
        } else if (_textArea.getSelectedText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Wikit Search");
            alert.setHeaderText("No words were highlighted");
            alert.setContentText("Please highlight a maximum of 20 words and try again.");
            alert.showAndWait();
        } else  if (!_audioName.getText().matches("[a-zA-Z0-9_-]*") || _audioName.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Wikit Search");
            alert.setHeaderText("Audio file is unnamed");
            alert.setContentText("Please enter a valid name for the audio file and try again.");
            alert.showAndWait();
        } else if (!btnCheckCreationName.isDisabled()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Wikit Search");
            alert.setHeaderText("Creation is unnamed");
            alert.setContentText("Please name your creation and try again.");
            alert.showAndWait();
        } else if (_textArea.getSelectedText().split("\\s+").length > 20) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Wikit Search");
            alert.setHeaderText("Word Maximum Exceeded");
            alert.setContentText("Please highlight a maximum of 20 words and try again.");
            alert.showAndWait();
        } else if (existingAudio.contains(_audioName.getText())) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Wikit Search");
            alert.setHeaderText("Audio name taken");
            alert.setContentText("Please rename your audio and try again.");
            alert.showAndWait();
        }
        else {

            CreateAudioTask createAudioTask = new CreateAudioTask(_creationNameField.getText(), _textArea.getSelectedText(), _audioName.getText(), getVoicesObject(voicesChoiceBox.getSelectionModel().getSelectedItem()));
            team.submit(createAudioTask);
            ;

            createAudioTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent workerStateEvent) {
                    try {

                        if (createAudioTask.getError()) {
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Wikit Search");
                            alert.setHeaderText("The selected words cannot be synthesised");
                            alert.setContentText("Please highlight other words and try again. Otherwise use Voice1 for better results.");
                            alert.showAndWait();

                        } else {

                            step2.setSelected(true);

                            existingAudio.add(_audioName.getText());


                            Text audioLabel = new Text(_audioName.getText());

                            btnDeleteAudio = new Button("Delete");
                            btnDeleteAudio.setVisible(false);

                            Region region1 = new Region();

                            HBox hb = new HBox(audioLabel, region1, btnDeleteAudio);
                            hb.setHgrow(region1, Priority.ALWAYS);
                            hb.setAlignment(Pos.CENTER_LEFT);

                            _audioList.getItems().addAll(hb);

                            if ((btnStopAudio.isDisabled() && !btnPreviewAudio.isDisabled()) || (btnStopAudio.isDisabled() && btnPreviewAudio.isDisabled())) {
                                btnPreviewAudio.setDisable(false);
                            }

                            btnSaveAudioFile.setDisable(false);

                            String cmd2 = "rm '" + Main.getCreationDir() + "/" + _creationNameField.getText() + "/audio/" + _audioName.getText() + "'.wav";

                            final String cmdToDelete = cmd2;
                            final HBox hbToDelete = hb;

                            _audioList.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent mouseEvent) {

                                    for (int i = 0; i < _audioList.getItems().size(); i++) {

                                        HBox hb = _audioList.getItems().get(i);
                                        Button button = (Button) hb.getChildren().get(2);
                                        button.setVisible(false);

                                        if (_audioList.getSelectionModel().getSelectedItem().equals(hb)) {
                                            button.setVisible(true);
                                        }
                                    }
                                }
                            });

                            btnDeleteAudio.setOnAction(new EventHandler<ActionEvent>() {
                                                           @Override
                                                           public void handle(ActionEvent actionEvent) {

                                                               Text textToDelete = (Text) hbToDelete.getChildren().get(0);
                                                               existingAudio.remove(textToDelete.getText());

                                                               _audioList.getItems().remove(hbToDelete);

                                                               if (_audioList.getItems().size() == 0) {
                                                                   btnPreviewAudio.setDisable(true);
                                                                   btnSaveAudioFile.setDisable(true);
                                                               }

                                                               ProcessBuilder deleteAudiopb = new ProcessBuilder("bash", "-c", cmdToDelete);
                                                               try {
                                                                   Process deleteProcess = deleteAudiopb.start();
                                                               } catch (IOException e) {
                                                                   e.printStackTrace();
                                                               }

                                                           }
                                                       }
                            );

                        }




                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                    _audioName.clear();
                    // Add success?
                    _audioName.setPromptText("Name Selected Audio");
                }
            });
        }
    }

    /**
     * Previews the entire audio queue.
     */

    @FXML
    public void handlePreviewBtn() {

        btnPreviewAudio.setDisable(true);
        btnStopAudio.setDisable(false);

        AudioMergeTask audioMergeTask = new AudioMergeTask(_creationNameField.getText(), _audioList, btnPreviewAudio.isDisabled());
        team.submit(audioMergeTask);

        audioMergeTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                btnPreviewAudio.setDisable(false);
                btnStopAudio.setDisable(true);
                try {
                    audioMergeTask.removePreviewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        btnStopAudio.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                audioMergeTask.stopProcess();
                try {
                    audioMergeTask.removePreviewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                btnPreviewAudio.setDisable(false);
                btnStopAudio.setDisable(true);

            }
        });

    }

    /**
     * Saves the audio queue into a wav file.
     */
    @FXML
    public void handleSaveFinalAudioBtn() {
        AudioMergeTask audioMergeTask = new AudioMergeTask(_creationNameField.getText(), _audioList, btnPreviewAudio.isDisabled());
        team.submit(audioMergeTask);
        btnSaveAudioFile.setText("Save and Overwrite");
        step3.setSelected(true);
    }

    /**
     * Method to get Voice object based on choicebox.
     * @param voiceCode
     * @return
     */

    public Voices getVoicesObject(String voiceCode) {
        if (voiceCode.equals("Default")) {
            return Voices.Default;
        }
        else if (voiceCode.equals("Kiwi Male")) {
            return Voices.Kiwi_Male;
        }
        else {
            return Voices.Kiwi_Female;
        }
    }

}
