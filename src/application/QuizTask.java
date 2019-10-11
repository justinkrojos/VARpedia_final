package application;

import javafx.concurrent.Task;

public class QuizTask extends Task<Void> {
    private String _term;
    private String _creationName;

    public QuizTask(String term, String creation) {
        _term = term;
        _creationName = creation;
    }

    @Override
    protected Void call() throws Exception {
        String command = "ffmpeg -y -i "+Main.getCreationDir()+"/"+_creationName+"/"+"video.mp4 -i "+Main.getCreationDir()+"/"+_creationName+"/"+_creationName+".wav -r 25 " + Main.getQuizDir()+"/"+_term+".mp4";
        ProcessBuilder pb = new ProcessBuilder("bash","-c",command);
        Process p = pb.start();
        p.waitFor();

        return null;
    }
}
