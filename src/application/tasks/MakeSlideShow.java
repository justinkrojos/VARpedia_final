package application.tasks;

import application.Main;
import javafx.concurrent.Task;

import java.io.*;

/**
 * This class creates a slideshow of images downloaded from flickr.
 */
public class MakeSlideShow extends Task<Void> {
    private String _term;
    private String _creationName;
    private int _numImages;

    private File dir;

    public MakeSlideShow(String term, String creationName) {
        _term = term;
        _creationName = creationName;
         dir = new File(Main.getCreationDir()+"/"+ _creationName + "/");
    }

    @Override
    protected Void call() throws Exception {
        _numImages = getNumImages();
        System.out.println(_numImages);
        makeVideo();

        return null;
    }

    /**
     * Gets the integer value length of the audio file, rounded up.
     * @return
     */
    private int getAudioLength() {
        String command = "soxi -D "+Main.getCreationDir()+"/"+_creationName+"/"+_creationName+".wav";
        ProcessBuilder audioLenBuilder = new ProcessBuilder("bash","-c",command);
        try {
            Process audioLenProcess = audioLenBuilder.start();
            BufferedReader stdout = new BufferedReader(new InputStreamReader(audioLenProcess.getInputStream()));
            audioLenProcess.waitFor();
            String audioString = stdout.readLine();
            stdout.close();
            return (int)Double.parseDouble(audioString) + 1;
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Get number of images in the directory.
     * @return
     */
    private int getNumImages() {
        System.out.println(dir);
        File[] files = dir.listFiles();
        int count = 0;
        for (File f: files) {
            System.out.println(f.getName());
            if (f.getName().endsWith(".jpg")) {
                count++;
            }
        }
        return count;
    }

    /**
     * This method create the slideshow and adds sub titles.
     */
    private void makeVideo() {
        double length = getAudioLength();

        String path = Main.getCreationDir()+"/"+_creationName+"/";
        length = length/_numImages;
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(path+"cmd.txt", "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < _numImages; i++) {
            if (i==_numImages-1) {
                writer.println("file "+path+"image"+i+".jpg");
                writer.println("duration "+length);
                writer.println("file "+path+"image"+i+".jpg");
                break;
            }
            writer.println("file "+path+"image"+i+".jpg");
            writer.println("duration "+length);
        }
        writer.close();



        //String command = command1+";"+command2;

        String command1 = "ffmpeg -y -f concat -safe 0 -i "+path+"cmd.txt"+ " -pix_fmt yuv420p -r 25 -vf 'scale=trunc(iw/2)*2:trunc(ih/2)*2' " +path+"video.mp4";
        String command2 = "ffmpeg -y -i "+Main.getCreationDir()+"/"+_creationName+"/"+"video.mp4 "+ "-vf \"drawtext=fontfile=myfont.ttf:fontsize=30:fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='"+_term+"'\" "+"-r 25 "+Main.getCreationDir()+"/"+_creationName+"/"+_creationName+".mp4";
        String command = command1+";"+command2;

        //System.out.println(command);
        ProcessBuilder pbb = new ProcessBuilder("/bin/bash","-c",command);
        try {
            Process p = pbb.start();
            p.waitFor();

            //System.out.println("Done");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        // System.out.println(command);

    }


    //Old one... too afraid to delete. so im keeping it here just in case.
/*    @Override
    protected Void call() throws Exception {
        String command = "ffmpeg -framerate 1/2 -i image%01d.jpg -r 25 -vf \"pad=ceil(iw/2)*2:ceil(ih/2)*2\" /media/sf_VBoxSharedFolder/Ass3/IdeaProjects/206Assignment3/out/production/creations/213/video.mp4;ffmpeg -i /media/sf_VBoxSharedFolder/Ass3/IdeaProjects/206Assignment3/out/production/creations/213/video.mp4 -vf \"drawtext=fontfile=myfont.ttf:fontsize=30:fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='apple'\" /media/sf_VBoxSharedFolder/Ass3/IdeaProjects/206Assignment3/out/production/creations/213/213.mp4";
        ProcessBuilder pb = new ProcessBuilder("bash","-c",command);
        Process p = pb.start();
        p.waitFor();
        return null;
    }*/
}

