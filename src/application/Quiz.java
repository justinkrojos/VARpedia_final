package application;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Quiz {
    private final File _folder = new File(Main.getQuizDir());
    private String _term;

    public boolean checkAns(String answer) {
        if (answer.equals(_term)) {
            return true;
        }
        return false;
    }
    public String getTerm() {
        return _term;
    }

    private void setTerm(String string) {
        _term = string;
    }

    public String play() {
        ArrayList<String> list = listFilesOfFolder(_folder);
        Random rand = new Random();
        String randomTerm = list.get(rand.nextInt(list.size()));
        setTerm(randomTerm);
        return _term;
    }
    /**
     * Get the creations in the folder into an arrylist sorted.
     * @param folder
     * @return
     */
    private ArrayList<String> listFilesOfFolder(final File folder) {
        ArrayList<String> list = new ArrayList<String>();

        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isDirectory()) {
                list.add(fileEntry.getName().replace(".mp4", ""));//.replace(".mp4", ""));
            }
        }

        Collections.shuffle(list);
        return list;
    }
}
