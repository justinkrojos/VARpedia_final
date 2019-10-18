package application;

import javafx.concurrent.Task;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class CreateAudioTask extends Task<Void> {

   private String _creationNameField;
   private String[][] _savedText;


    public CreateAudioTask(String _creationNameField, String[][] savedText) {
        this._creationNameField = _creationNameField;
        this._savedText = savedText;
    }

    @Override
    protected Void call() throws Exception {

        for (int i = 0; i < _savedText.length; i++) {

            String cmd = "mkdir -p '" + Main.getCreationDir() + "/" + _creationNameField + "/audio' && " +
                    "echo \"" + _savedText[i][0] + "\" | text2wave -o '" + Main.getCreationDir() + "/" + _creationNameField + "/audio/" + i + ".wav' -eval \"" +
                    getVoicesObject(_savedText[i][1]).getVoicePackage() + "\" 2> '" + Main.getCreationDir() + "/" + _creationNameField + "/error.txt'";

            ProcessBuilder saveAudiopb = new ProcessBuilder("bash", "-c", cmd);
            Process process1 = saveAudiopb.start();
            process1.waitFor();


        }
        return null;
    }

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

    public boolean getError() throws FileNotFoundException {

        boolean error;

        File errorFile = new File(  Main.getCreationDir() + "/" + _creationNameField + "/audio/error.txt");
        Scanner sc = new Scanner(errorFile);

        if (sc.hasNextLine()) {
            error = true;
        }
        else {
            error = false;
        }

        sc.close();
        return error;
    }



}
