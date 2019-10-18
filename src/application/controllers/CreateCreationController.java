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
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateCreationController {

    private ExecutorService team = Executors.newSingleThreadExecutor();

    private ArrayList<String> voicesList = new ArrayList<String>();

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


    public String _termField;

    @FXML
    private Button btnSearch;

    @FXML
    private AnchorPane searchPane;

    @FXML
    private AnchorPane saveAudioPane;

    @FXML
    private Label searchConfirmation;

    @FXML
    private ListView<Label> savedText;

    @FXML
    private Button btnNext;

    @FXML
    private AnchorPane textActions;

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



    FXMLLoader loader;

    /**
     * Check if creation name is taken, and if so let the user pick if they want to overwrite
     */
    @FXML
    private Button btnSaveAudioFile;

    ArrayList<String> existingAudio = new ArrayList<String>();

    public void initialize(){
        voicesList.clear();
        btnNext.setDisable(true);

        // _currentStage = (Stage) _ap.getScene().getWindow();
    }

    @FXML
    private void handleBtnNext() throws IOException {

        String[][] finalText = new String[savedText.getItems().size()][2];

        for (int i = 0; i < savedText.getItems().size(); i++) {
            finalText[i][0] = savedText.getItems().get(i).getText();
            finalText[i][1] = voicesList.get(i);
        }

        loader = Main.changeScene("resources/SelectImage.fxml");
        SelectImageController selectImageController = loader.<SelectImageController>getController();
        selectImageController.transferInfo(_termField, finalText);
    }


    @FXML
    public void handleAudioPreview() throws IOException {

        PreviewAudioTask previewAudioTask = new PreviewAudioTask(_textArea.getSelectedText(), getVoicesObject(voicesChoiceBox.getSelectionModel().getSelectedItem()).getVoicePackage());

            if (_textArea.getSelectedText().split("\\s+").length > 30) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Wikit Search");
                alert.setHeaderText("Word Maximum Exceeded");
                alert.setContentText("Please highlight a maximum of 30 words and try again.");
                alert.showAndWait();
            }
            else {

                team.submit(previewAudioTask);

            }
        }


    /**
     * Saves the chunk of text as a wav file
     */

    @FXML
    public void handleSaveAudioBtn() {

        if (_textArea.getSelectedText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Wikit Search");
            alert.setHeaderText("No words were highlighted");
            alert.setContentText("Please highlight a maximum of 20 words and try again.");
            alert.showAndWait();
        }  else if (_textArea.getSelectedText().split("\\s+").length > 30) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Wikit Search");
            alert.setHeaderText("Word Maximum Exceeded");
            alert.setContentText("Please highlight a maximum of 30 words and try again.");
            alert.showAndWait();
        }

        else {
            Label selection = new Label(_textArea.getSelectedText());
            selection.setMaxWidth(savedText.getWidth() - 20.0);
            selection.setWrapText(true);
            selection.setFont(new Font("Manjari Thin", 20));
            savedText.getItems().add(selection);
            voicesList.add(voicesChoiceBox.getValue());
            btnNext.setDisable(false);
        }
    }

    /**
     * Previews the entire audio queue.
     */
/*
    @FXML
    public void handlePreviewBtn() {

        btnPreviewAudio.setDisable(true);
        btnStopAudio.setDisable(false);

        //AudioMergeTask audioMergeTask = new AudioMergeTask(_creationNameField.getText(), _audioList, btnPreviewAudio.isDisabled());
        //team.submit(audioMergeTask);

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
/*
    @FXML
    public void handleSaveFinalAudioBtn() {
        AudioMergeTask audioMergeTask = new AudioMergeTask(_creationNameField.getText(), _audioList, btnPreviewAudio.isDisabled());
        team.submit(audioMergeTask);
        btnSaveAudioFile.setText("Save and Overwrite");
        step3.setSelected(true);
    }
*/
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

    public void transferTerm(String term, String _textArea) {
        _termField = term;
        this._textArea.setText(_textArea);
        searchConfirmation.setText(searchConfirmation.getText() + " " + _termField);
    }
}
