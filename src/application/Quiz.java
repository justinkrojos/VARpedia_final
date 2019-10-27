package application;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * This class manages the quiz.
 */
public class Quiz {
    private final File _folder = new File(Main.getQuizDir());
    private String _term;

    public String getTerm() {
        return _term;
    }

    private void setTerm(String string) {
        _term = string;
    }

    /**
     * Get a random quiz creation from the quiz folder.
     * @return
     */
    public String play() {
        ArrayList<String> list = listFilesOfFolder(_folder);
        if (list.size() == 0) {
            return null;
        }
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
