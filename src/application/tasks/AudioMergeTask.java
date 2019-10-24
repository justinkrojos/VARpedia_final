package application.tasks;

import application.Main;
import javafx.concurrent.Task;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.control.ListView;

import java.io.IOException;

public class AudioMergeTask extends Task<Void> {

    private String term;
    private int count;
    private Process playAudioProcess;
    private String cmd;

    public AudioMergeTask(String term, int count) {
        this.term = term;
        this.count = count;
    }

    @Override
    protected Void call() throws Exception {
        cmd = "sox";

        for (int i = 0; i < count; i++) {

            cmd = cmd + " '" + Main.getCreationDir() + "/" + term + "/audio/" + count + ".wav'";
        }

            cmd = cmd + " '" + Main.getCreationDir() + "/" + term + "/" + term + ".wav'";

System.out.println(cmd);

        ProcessBuilder playFullAudiopb = new ProcessBuilder("bash", "-c", cmd);
        playAudioProcess = playFullAudiopb.start();
        playAudioProcess.waitFor();

        return null;

    }

    public void stopProcess() {
        playAudioProcess.destroyForcibly();
    }

    public void removePreviewFile() throws IOException {
        String cmd = "rm '" + Main.getCreationDir() + "/" + term + "/" + term + "preview.wav'";
        ProcessBuilder removePreviewpb = new ProcessBuilder("bash", "-c", cmd);
        Process removePreviewProcess = removePreviewpb.start();
    }


}
