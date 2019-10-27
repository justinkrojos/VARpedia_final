package application.controllers;

import application.*;
import application.tasks.PreviewAudioTask;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class manages the creation scene.
 */
public class CreateCreationController {

    private ExecutorService team = Executors.newSingleThreadExecutor();

    private ArrayList<String> voicesList = new ArrayList<String>();

    @FXML
    private TextArea _textArea;

    public String _termField;

    @FXML
    private Label searchConfirmation;

    @FXML
    private ListView<Label> savedText;

    @FXML
    private Button btnNext;

    @FXML
    private ChoiceBox<String> voicesChoiceBox;

    @FXML
    private ChoiceBox<String> voicesChoiceBoxEdit;

    MediaPlayer bgmusic;

    @FXML
    private ToggleButton music;

    FXMLLoader loader;

    @FXML
    private Button btnPreviewAudioEdit;

    @FXML
    private TextArea editTextArea;

    @FXML
    private ListView<Label> savedTextEdit;

    @FXML
    private Button btnSaveChanges;

    @FXML
    private Button btnMoveDown;

    @FXML
    private Button btnMoveUp;

    @FXML
    private Pane textActions;

    @FXML
    private Pane textPane;

    @FXML
    private Pane editorPane;

    @FXML
    private Button btnDelEditor;

    String temp;


    public void initialize(){
        btnNext.setDisable(true);

        savedText.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                editorPane.setVisible(true);
                btnNext.setVisible(false);
                textPane.setVisible(false);
                textActions.setVisible(false);
                updateListViews();
            }
        });

        savedTextEdit.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (savedTextEdit.getSelectionModel().getSelectedItem() != null) {
                    temp = savedTextEdit.getSelectionModel().getSelectedItem().getText();
                    editTextArea.setText(savedTextEdit.getSelectionModel().getSelectedItem().getText());
                        voicesChoiceBoxEdit.setValue(voicesList.get(savedTextEdit.getSelectionModel().getSelectedIndex()));
                        btnDelEditor.setDisable(false);
                        btnMoveDown.setDisable(false);
                        btnMoveUp.setDisable(false);
                        btnPreviewAudioEdit.setDisable(false);
                        btnSaveChanges.setDisable(false);
                        editTextArea.setDisable(false);
                        voicesChoiceBoxEdit.setDisable(false);
                }
            }
        });

        savedText.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                savedText.setOpacity(0.7);
            }
        });

        savedText.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                savedText.setOpacity(1.0);
            }
        });

        btnNext.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                btnNext.setOpacity(0.7);
            }
        });

        btnNext.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                btnNext.setOpacity(1.0);
            }
        });



    }

    /**
     * Change to the select image and name creation scene.
     * @throws IOException
     */
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
        selectImageController.transferMusic(bgmusic, music.isSelected(), music.getText());
    }


    /**
     * Previews selected text with the users desired voice.
     * @throws IOException
     */
    @FXML
    public void handleAudioPreview() throws IOException {

        if (editorPane.isVisible()) {
            PreviewAudioTask previewAudioTask = new PreviewAudioTask(editTextArea.getText(), getVoicesObject(voicesChoiceBoxEdit.getSelectionModel().getSelectedItem()).getVoicePackage());
            team.submit(previewAudioTask);
        } else {
            PreviewAudioTask previewAudioTask = new PreviewAudioTask(_textArea.getSelectedText(), getVoicesObject(voicesChoiceBox.getSelectionModel().getSelectedItem()).getVoicePackage());

            if (_textArea.getSelectedText().split("\\s+").length > 30) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Wikit Search");
                alert.setHeaderText("Word Maximum Exceeded");
                alert.setContentText("Please highlight a maximum of 30 words and try again.");
                alert.showAndWait();
            } else {

                team.submit(previewAudioTask);

            }
        }
    }

    /**
     * Saves the chunk of text as a wav file.
     */
    @FXML
    public void handleSaveAudioBtn() {

        if (_textArea.getSelectedText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Wikit Search");
            alert.setHeaderText("No words were highlighted");
            alert.setContentText("Please highlight a maximum of 30 words and try again.");
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
     * Move selected text up one in the listview.
     */
    @FXML
    private void handleMoveUp() {
        Label l = savedTextEdit.getSelectionModel().getSelectedItem();
        int i = savedTextEdit.getSelectionModel().getSelectedIndex();
        if (i == 0) {

        }
        else {
            savedTextEdit.getItems().remove(i);
            savedTextEdit.getItems().add(i - 1, l);
            Collections.swap(voicesList, i, i - 1);
            savedTextEdit.getSelectionModel().select(i - 1);
        }


    }

    /**
     * Move selected text down one row in the listview.
     */
    @FXML
    private void handleMoveDown() {
        Label l = savedTextEdit.getSelectionModel().getSelectedItem();
        int i = savedTextEdit.getSelectionModel().getSelectedIndex();
        if (i == savedTextEdit.getItems().size() - 1) {

        }
        else {
            savedTextEdit.getItems().remove(i);
            savedTextEdit.getItems().add(i + 1, l);
            Collections.swap(voicesList, i, i + 1);
            savedTextEdit.getSelectionModel().select(i + 1);
        }

    }

    /**
     * Delete the selected text in the editor pane.
     */
    @FXML
    private void handleDelEditor() {
        savedTextEdit.getItems().remove(savedTextEdit.getSelectionModel().getSelectedIndex());
        btnDelEditor.setDisable(true);
        btnMoveDown.setDisable(true);
        btnMoveUp.setDisable(true);
        btnPreviewAudioEdit.setDisable(true);
        btnSaveChanges.setDisable(true);
        editTextArea.setDisable(true);
        voicesChoiceBoxEdit.setDisable(false);
    }

    /**
     * Save new changes made in the editor pane.
     */
    @FXML
    public void handleSaveEdit() {
        if (editTextArea.getText().trim().isBlank()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.getDialogPane().getStylesheets().add(("Alert.css"));
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Empty Text");
            alert.setContentText("No text was saved. Try deleting the text instead.");
            alert.showAndWait();
            editTextArea.setText(temp);
        }
        int i = savedTextEdit.getSelectionModel().getSelectedIndex();
        System.out.println(i);

        voicesList.add(i, voicesChoiceBoxEdit.getValue());
        voicesList.remove(i + 1);
        Label label = new Label(editTextArea.getText());
        label.setWrapText(true);
        savedTextEdit.getItems().add(i, label);
        savedTextEdit.getItems().remove(i + 1);

    }

    /**
     * Exit from editor pane.
     */
    @FXML
    public void handleExitEditor() {
        editorPane.setVisible(false);
        textPane.setVisible(true);
        textActions.setVisible(true);
        if (savedText.getItems().size() == 0) {

        }
        else {
            btnNext.setVisible(true);
        }
        updateListViews();
    }

    /**
     * Transfer new changes in editor pane to the listview.
     */
    public void updateListViews() {
        if (editorPane.isVisible()) {
            savedTextEdit.getItems().clear();
            for (int i = 0; i < savedText.getItems().size(); i++) {
                savedTextEdit.getItems().add(savedText.getItems().get(i));
                savedTextEdit.getItems().get(i).setWrapText(true);
            }
        }
        else {
            savedText.getItems().clear();
            for (int i = 0; i < savedTextEdit.getItems().size(); i++) {
                savedText.getItems().add(savedTextEdit.getItems().get(i));
            }
        }

    }

    /**
     * Getter for voice enum - dependant on choice box selection.
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

    /**
     * Go back to the main menu.
     * @throws IOException
     */
    @FXML
    private void handleBtnBack() throws IOException {
        FXMLLoader loader = Main.changeScene("resources/Welcome.fxml");
        WelcomeController welcomeController = loader.<WelcomeController>getController();
        welcomeController.transferMusic(bgmusic, music.isSelected(), music.getText());
    }

    /**
     * Toggle background mis.c
     */
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

    /**
     * Transfer music to this scene.
     * @param bgmusic
     * @param toggle
     * @param text
     */
    public void transferMusic(MediaPlayer bgmusic, Boolean toggle, String text) {
        this.bgmusic = bgmusic;
        music.setSelected(toggle);
        music.setText(text);
    }

    /**
     * Transfer wikit search information to this scene.
     * @param term
     * @param _textArea
     */
    public void transferTerm(String term, String _textArea) {
        _termField = term;
        this._textArea.setText(_textArea);
        searchConfirmation.setText(searchConfirmation.getText() + " " + _termField);
    }
}
