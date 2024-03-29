package application.tasks;

import application.Main;
import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.photos.*;
import javafx.concurrent.Task;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class downloads 10 images from flickr associated with the searched term, using the flickr api.
 */
public class DownloadImages extends Task<Void> {

    private String _creationName;
    private String _term;

    private List<Image> _images = new ArrayList<Image>();

    public DownloadImages(String creationName, String term) {
        _creationName = creationName;
        _term = term;
    }

    @Override
    protected Void call() {
        flickr();
        return null;
    }

    /**
     * Download images from flickr
     * Code written with the help of:
     * ACP 206_FlickrExample
     */
    private void flickr() {

        String apiKey = "e37d6b63e1b4bceb47a42a3a37f316e3";
        String sharedSecret = "42ccf0520e0515f1";
        try{

            try {
                apiKey = getAPIKey("apiKey");
                sharedSecret = getAPIKey("sharedSecret");

            } catch (Exception e) {
                apiKey = "e37d6b63e1b4bceb47a42a3a37f316e3";
                sharedSecret = "42ccf0520e0515f1";

            }

            Flickr flickr = new Flickr(apiKey, sharedSecret, new REST());
            String query = _term;
            int resultsPerPage = 10;
            int page = 0;

            new File(Main.getCreationDir()+"/"+_creationName+"/"+"images").mkdirs();

            PhotosInterface photos = flickr.getPhotosInterface();
            SearchParameters params = new SearchParameters();
            params.setSort(SearchParameters.RELEVANCE);
            params.setMedia("photos");
            params.setText(query);

            if (this.isCancelled()) {
                return;

            }
            PhotoList<Photo> results = photos.search(params, resultsPerPage, page);
            int count = 0;

            for (Photo photo: results) {
                if (this.isCancelled()) {
                    return;

                }
                try {
                    BufferedImage image = photos.getImage(photo, Size.LARGE);

                    String filename = "image"+count+".jpg";
                    File outputfile = new File(Main.getCreationDir()+"/"+_creationName+"/"+"images",filename);
                    ImageIO.write(image, "jpg", outputfile);

                    count++;
                } catch (FlickrException | IOException fe) {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * Get the api keys from a txt file.
     * Code written with the help of
     * ACP 206_FlickrExample
     * @param key
     * @return
     * @throws Exception
     */
    public static String getAPIKey(String key) throws Exception {

        String creationsDir = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath();
        creationsDir = creationsDir.substring(0,creationsDir.lastIndexOf("/"));
        String config = creationsDir+ "/flickr-api-keys.txt";

        File file = new File(config);
        BufferedReader br = new BufferedReader(new FileReader(file));

        String line;
        while ( (line = br.readLine()) != null ) {
            if (line.trim().startsWith(key)) {
                br.close();
                return line.substring(line.indexOf("=")+1).trim();

            }
        }
        br.close();
        throw new RuntimeException("Couldn't find " + key +" in config file "+file.getName());

    }
}
