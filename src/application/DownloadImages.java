package application;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.photos.*;
import javafx.concurrent.Task;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class DownloadImages extends Task<Void> {

    private String _creationName;
    private String _term;

    public DownloadImages(String creationName, String term) {
        _creationName = creationName;
        _term = term;
    }

    @Override
    protected Void call() throws Exception {
        flickr();
        return null;
    }

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
            int resultsPerPage = 12;
            int page = 0;

            new File(Main.getCreationDir()+"/"+_creationName+"/"+"images").mkdirs();

            PhotosInterface photos = flickr.getPhotosInterface();
            SearchParameters params = new SearchParameters();
            params.setSort(SearchParameters.RELEVANCE);
            params.setMedia("photos");
            params.setText(query);

            PhotoList<Photo> results = photos.search(params, resultsPerPage, page);
            // System.out.println("Retrieving " + results.size()+ " results");
            int count = 0;
            for (Photo photo: results) {
                try {
                    BufferedImage image = photos.getImage(photo, Size.LARGE);
                    //String filename = query.trim().replace(' ', '-')+"-"+System.currentTimeMillis()+"-"+photo.getId()+".jpg";
                    String filename = "image"+count+".jpg";
                    File outputfile = new File(Main.getCreationDir()+"/"+_creationName+"/"+"images",filename);
                    ImageIO.write(image, "jpg", outputfile);
                    //System.out.println("Downloaded "+filename);
                    count++;
                } catch (FlickrException | IOException fe) {
                    // System.err.println("Ignoring image " +photo.getId() +": "+ fe.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // System.out.println("\nDone");
    }

    /**
     * Get the api keys from a txt file.
     * @param key
     * @return
     * @throws Exception
     */
    public static String getAPIKey(String key) throws Exception {
        // TODO fix the following based on where you will have your config file stored

/*        String config = System.getProperty("user.dir")
                + System.getProperty("file.separator")+ "flickr-api-keys.txt";*/
        String creationsDir = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath();
        creationsDir = creationsDir.substring(0,creationsDir.lastIndexOf("/"));
        String config = creationsDir+ "/flickr-api-keys.txt";

//		String config = System.getProperty("user.home")
//				+ System.getProperty("file.separator")+ "bin"
//				+ System.getProperty("file.separator")+ "flickr-api-keys.txt";
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
