package application.tasks;

import application.Main;
import application.Voices;
import javafx.concurrent.Task;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * This task creates an audio files with different voices, and merges them into one wav file.
 */
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
        String cmd = "sox";

        for (int i = 0; i < _savedText.length; i++) {
            cmd = cmd + " '" + Main.getCreationDir() + "/" + _creationNameField + "/audio/" + i + ".wav'";

        }

        cmd = cmd + " '" + Main.getCreationDir() + "/" + _creationNameField + "/" + _creationNameField + ".wav'";

        ProcessBuilder playFullAudiopb = new ProcessBuilder("bash", "-c", cmd);
        Process playAudioProcess = playFullAudiopb.start();
        playAudioProcess.waitFor();

        return null;
    }

    /**
     * Get voice package from enum class.
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
     * Look for unintelligible words.
     * @return
     * @throws FileNotFoundException
     */
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
