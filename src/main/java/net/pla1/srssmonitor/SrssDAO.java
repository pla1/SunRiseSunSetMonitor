package net.pla1.srssmonitor;

import com.google.gson.Gson;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class SrssDAO {
    private LoginResponse loginResponse;

    public SrssDAO() throws Exception {
        loginSunburst();
    }

    public static void main(String[] args) {
        System.out.format("Arguments length: %d\n", args.length);
        for (int i = 0; i < args.length; i++) {
            System.out.format("Argument index: %d value: \"%s\"\n", i, args[i]);
        }
        try {
            SrssDAO dao = new SrssDAO();
            if (args.length == 4) {
                int i = 0;
                boolean sunrise = "sunrise".equalsIgnoreCase(args[i++]);
                double latitude = Double.parseDouble(args[i++]);
                double longitude = Double.parseDouble(args[i++]);
                String location = args[i++];
                dao.postTweet(sunrise, latitude, longitude, location);
                System.exit(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }

    private File getImage(boolean sunrise) throws IOException {
        String type = sunrise ? "sunrise" : "sunset";
        String imageFileName = String.format("/tmp/%s.png", type);
        String imageUrlString = String.format("https://sunsetwx.com/%s/%s_et.png", type, type);
        System.out.format("Image URL: %s.\n", imageUrlString);
        Utils.saveImage(imageUrlString, imageFileName);
        return new File(imageFileName);
    }

    public void postTweet(boolean sunrise, double latitude, double longitude, String location) throws Exception {
        Properties properties = new Properties();
        properties.load(new FileReader(String.format("/etc/%s.properties", this.getClass().getCanonicalName())));
        Twitter twitter = TwitterFactory.getSingleton();
        twitter.setOAuthConsumer(properties.getProperty("twitterConsumerKey"), properties.getProperty("twitterConsumerSecret"));
        AccessToken accessToken = new AccessToken(properties.getProperty("twitterAccessToken"), properties.getProperty("twitterAccessTokenSecret"));
        twitter.setOAuthAccessToken(accessToken);
        System.out.format("Token ID: %s Secret token: %s\n", twitter.verifyCredentials().getId(), accessToken);
        System.out.format("Sunrise: %s Latitude: %s Longitude: %s Location: %s\n", sunrise, latitude, longitude, location);
        String text = getTweetText(sunrise, latitude, longitude, location);
        if (text == null) {
            System.out.println("Tweet text is null. Not tweeting.");
            return;
        }
        StatusUpdate statusUpdate = new StatusUpdate(text);
        statusUpdate.setMedia(getImage(sunrise));
        twitter.updateStatus(statusUpdate);
    }

    private String getTweetText(boolean sunrise, double latitude, double longitude, String location) throws IOException {
        Quality quality = getQuality(sunrise, latitude, longitude);
        if (quality == null || quality.getFeatures().size() == 0) {
            System.out.println("Quality does have any features. Returning null.");
            return null;
        }
        String type = sunrise ? "Sunrise" : "Sunset";
        String qualityString = quality.getFeatures().get(0).getProperties().getQuality();
        qualityString = Utils.toLower(qualityString);
        System.out.format("%s quality is %s.\n", type, qualityString);
        if (!"good".equals(qualityString)
                && !"great".equals(qualityString)) {
            System.out.println("Quality is not good or great. Returning null.");
            return null;
        }
        TimeDAO timeDAO = new TimeDAO();
        Time time;
        try {
            time = timeDAO.get(longitude, latitude);
            System.out.format("Time %s\n", time);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.format("Could not get time of %s. Returning null.\n", type);
            return null;
        }
        String timeDisplay = sunrise ? time.getResults().getSunriseDateDisplay() : time.getResults().getSunsetDateDisplay();
        String tweetText = String.format("%s quality is %s for %s. %s is at %s. #chswx",
                type, qualityString, location, type, timeDisplay);
        System.out.println(tweetText);
        return tweetText;
    }

    private Quality getQuality(boolean sunrise, double latitude, double longitude) throws IOException {
        String sunriseOrSunset = "sunset";
        if (sunrise) {
            sunriseOrSunset = "sunrise";
        }
        String urlString = String.format("https://sunburst.sunsetwx.com/v1/quality?type=%s&coords=%f%%2C%f", sunriseOrSunset, longitude, latitude);
        System.out.format("URL: %s\n", urlString);
        URL url = new URL(urlString);
        URLConnection urlConnection = url.openConnection();
        urlConnection.setRequestProperty("Method", "GET");
        urlConnection.setRequestProperty("Authorization", String.format("Bearer %s", loginResponse.getToken()));
        InputStream inputStream = urlConnection.getInputStream();
        Gson gson = new Gson();
        Quality quality = gson.fromJson(new InputStreamReader(inputStream), Quality.class);
        System.out.format("%s Feature quantity: %d\n", quality.getType(), quality.getFeatures().size());
        for (Feature f : quality.getFeatures()) {
            Property property = f.getProperties();
            System.out.format("%s Property quality: %s\n", f.getGeometry().toString(), property.getQuality());
        }
        return quality;
    }

    private void loginSunburst() throws IOException {
        String urlString = "https://sunburst.sunsetwx.com/v1/login";
        URL url = new URL(urlString);
        Properties properties = new Properties();
        properties.load(new FileReader(String.format("/etc/%s.properties", this.getClass().getCanonicalName())));
        URLConnection urlConnection = url.openConnection();
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);
        String urlParameters = String.format("email=%s&password=%s", Utils.encode(properties.getProperty("emailAddress")), Utils.encode(properties.getProperty("password")));
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        urlConnection.setRequestProperty("Method", "POST");
        urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        urlConnection.setRequestProperty("charset", "utf-8");
        urlConnection.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        urlConnection.setUseCaches(false);
        try (DataOutputStream writer = new DataOutputStream(urlConnection.getOutputStream())) {
            writer.write(postData);
        }
        InputStream inputStream = urlConnection.getInputStream();
        Gson gson = new Gson();
        loginResponse = gson.fromJson(new InputStreamReader(inputStream), LoginResponse.class);
        System.out.println(loginResponse);
    }

}
