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

    private ExecutorService _team = Executors.newSingleThreadExecutor();
    private ArrayList<String> voicesList = new ArrayList<String>();
    private String _termField;
    private String _originalText;
    private MediaPlayer _bgmusic;

    @FXML
    private Label searchConfirmation;

    @FXML
    private ListView<Label> savedText;
    @FXML
    private ListView<Label> savedTextEditor;

    @FXML
    private ChoiceBox<String> voicesChoiceBoxEditor;
    @FXML
    private ChoiceBox<String> voicesChoiceBox;

    @FXML
    private TextArea textEditor;
    @FXML
    private TextArea wikitResults;

    @FXML
    private Button btnPreviewEditor;
    @FXML
    private Button btnMoveUpEditor;
    @FXML
    private Button btnMoveDownEditor;
    @FXML
    private Button btnDelEditor;
    @FXML
    private Button btnSaveEditor;
    @FXML
    private Button btnNext;

    @FXML
    private ToggleButton btnMusic;

    @FXML
    private Pane actionsPane;
    @FXML
    private Pane textPane;
    @FXML
    private Pane editorPane;


    public void initialize(){

        // Initialise listview to display editor pane when clicked.

        savedText.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                editorPane.setVisible(true);

                btnNext.setVisible(false);
                textPane.setVisible(false);
                actionsPane.setVisible(false);

                updateListViews();

            }
        });

        // Initialise listview in editor to enable editor components, and display the saved information for that label

        savedTextEditor.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                if (savedTextEditor.getSelectionModel().getSelectedItem() != null) {
                    _originalText = savedTextEditor.getSelectionModel().getSelectedItem().getText();

                    textEditor.setText(_originalText);
                    voicesChoiceBoxEditor.setValue(voicesList.get(savedTextEditor.getSelectionModel().getSelectedIndex()));

                    btnDelEditor.setDisable(false);
                    btnMoveUpEditor.setDisable(false);
                    btnMoveDownEditor.setDisable(false);
                    btnPreviewEditor.setDisable(false);
                    btnSaveEditor.setDisable(false);
                    textEditor.setDisable(false);
                    voicesChoiceBoxEditor.setDisable(false);

                }
            }
        });

        // Initialise dynamic style changes when hovering over components

        savedText.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                savedText.setOpacity(0.7);
            }
        });

        savedText.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (!savedText.isDisabled()) {
                    savedText.setOpacity(1.0);
                }
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
                if (!btnNext.isDisabled()) {
                    btnNext.setOpacity(1.0);
                }
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

        FXMLLoader loader = Main.changeScene("resources/SelectImage.fxml");
        SelectImageController selectImageController = loader.<SelectImageController>getController();
        selectImageController.transferInfo(_termField, finalText);
        selectImageController.transferMusic(_bgmusic, btnMusic.isSelected(), btnMusic.getText());
    }


    /**
     * Previews selected text with the users desired voice for the wikit term, and the editor.
     * @throws IOException
     */
    @FXML
    public void handleAudioPreview() throws IOException {

        if (editorPane.isVisible()) {
            PreviewAudioTask previewAudioTask = new PreviewAudioTask(textEditor.getText(), getVoicesObject(voicesChoiceBoxEditor.getSelectionModel().getSelectedItem()).getVoicePackage());
            _team.submit(previewAudioTask);

        } else {
            PreviewAudioTask previewAudioTask = new PreviewAudioTask(wikitResults.getSelectedText(), getVoicesObject(voicesChoiceBox.getSelectionModel().getSelectedItem()).getVoicePackage());

            if (wikitResults.getSelectedText().split("\\s+").length > 30) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.getDialogPane().getStylesheets().add(("Alert.css"));
                alert.setTitle("Word Limit Reached");
                alert.setContentText("Please select a maximum of 30 words and try again.");
                alert.showAndWait();

            } else {
                _team.submit(previewAudioTask);

            }
        }
    }

    /**
     * Saves the chunk of text as a wav file.
     */
    @FXML
    public void handleSaveAudioBtn() {

        if (wikitResults.getSelectedText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.getDialogPane().getStylesheets().add(("Alert.css"));
            alert.setTitle("Warning: No words were highlighted");
            alert.setContentText("Please highlight a maximum of 30 words and try again.");
            alert.showAndWait();

        }  else if (wikitResults.getSelectedText().split("\\s+").length > 30) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.getDialogPane().getStylesheets().add(("Alert.css"));
            alert.setTitle("Warning: Word Limit Reached");
            alert.setContentText("Please select a maximum of 30 words and try again.");
            alert.showAndWait();
        }

        else {
            Label selection = new Label(wikitResults.getSelectedText());
            selection.setMaxWidth(savedText.getWidth() - 20.0);
            selection.setWrapText(true);
            selection.setFont(new Font("Manjari Thin", 20));

            savedText.getItems().add(selection);
            voicesList.add(voicesChoiceBox.getValue());

            btnNext.setVisible(true);

        }
    }

    /**
     * Move selected text up one in the listview.
     */
    @FXML
    private void handleMoveUp() {

        Label l = savedTextEditor.getSelectionModel().getSelectedItem();
        int i = savedTextEditor.getSelectionModel().getSelectedIndex();

        if (i == 0) {
            return;

        }
        else {
            savedTextEditor.getItems().remove(i);
            savedTextEditor.getItems().add(i - 1, l);
            Collections.swap(voicesList, i, i - 1);
            savedTextEditor.getSelectionModel().select(i - 1);

        }
    }

    /**
     * Move selected text down one row in the listview.
     */
    @FXML
    private void handleMoveDown() {

        Label l = savedTextEditor.getSelectionModel().getSelectedItem();
        int i = savedTextEditor.getSelectionModel().getSelectedIndex();

        if (i == savedTextEditor.getItems().size() - 1) {
            return;

        }
        else {
            savedTextEditor.getItems().remove(i);
            savedTextEditor.getItems().add(i + 1, l);
            Collections.swap(voicesList, i, i + 1);
            savedTextEditor.getSelectionModel().select(i + 1);

        }
    }

    /**
     * Delete the selected text in the editor pane.
     */
    @FXML
    private void handleDelEditor() {

        savedTextEditor.getItems().remove(savedTextEditor.getSelectionModel().getSelectedIndex());

        btnMoveUpEditor.setDisable(true);
        btnMoveDownEditor.setDisable(true);
        btnDelEditor.setDisable(true);
        btnPreviewEditor.setDisable(true);
        btnSaveEditor.setDisable(true);
        textEditor.setDisable(true);
        voicesChoiceBoxEditor.setDisable(false);

    }

    /**
     * Save new changes made in the editor pane.
     */
    @FXML
    public void handleSaveEditor() {

        if (textEditor.getText().trim().isBlank()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.getDialogPane().getStylesheets().add(("Alert.css"));
            alert.setTitle("Warning: Empty Text");
            alert.setContentText("No text was saved. Try deleting the text instead.");
            alert.showAndWait();
            textEditor.setText(_originalText);
            return;

        }

        int i = savedTextEditor.getSelectionModel().getSelectedIndex();

        voicesList.add(i, voicesChoiceBoxEditor.getValue());
        voicesList.remove(i + 1);

        Label label = new Label(textEditor.getText());
        label.setWrapText(true);

        savedTextEditor.getItems().add(i, label);
        savedTextEditor.getItems().remove(i + 1);

    }

    /**
     * Exit from editor pane.
     */
    @FXML
    public void handleExitEditor() {

        editorPane.setVisible(false);
        textPane.setVisible(true);
        actionsPane.setVisible(true);

        if (savedText.getItems().size() == 0) {
            return;

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
            savedTextEditor.getItems().clear();

            for (int i = 0; i < savedText.getItems().size(); i++) {
                savedTextEditor.getItems().add(savedText.getItems().get(i));
                savedTextEditor.getItems().get(i).setWrapText(true);
            }

        }
        else {
            savedText.getItems().clear();

            for (int i = 0; i < savedTextEditor.getItems().size(); i++) {
                savedText.getItems().add(savedTextEditor.getItems().get(i));
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
        welcomeController.transferMusic(_bgmusic, btnMusic.isSelected(), btnMusic.getText());

    }

    /**
     * Toggle background music
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

    /**
     * Transfer wikit search information to this scene.
     * @param term
     * @param wikitResults
     */
    public void transferTerm(String term, String wikitResults) {

        _termField = term;
        this.wikitResults.setText(wikitResults);
        searchConfirmation.setText(searchConfirmation.getText() + " " + _termField);

    }
}
