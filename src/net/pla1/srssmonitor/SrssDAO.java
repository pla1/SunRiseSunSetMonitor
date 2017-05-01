package net.pla1.srssmonitor;

import com.google.gson.Gson;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class SrssDAO {
    private static final double LATITUDE_CHARLESTON_BATTERY = 32.769484;
    private static final double LONGITUDE_CHARLESTON_BATTERY = -79.929071;
    private LoginResponse loginResponse;

    public static void main(String[] args) throws Exception {
        double latitude = LATITUDE_CHARLESTON_BATTERY;
        double longitude = LONGITUDE_CHARLESTON_BATTERY;
        String location = "Charleston SC";
        boolean sunrise = true;
        SrssDAO dao = new SrssDAO();
        if (args.length == 4) {
            int i = 0;
            sunrise = "sunrise".equalsIgnoreCase(args[i++]);
            latitude = Double.parseDouble(args[i++]);
            longitude = Double.parseDouble(args[i++]);
            location = args[i++];
            dao.sendTweet(sunrise, latitude, longitude, location);
            System.exit(0);
        }
        if (false) {
            Quality quality = dao.getQuality(sunrise, latitude, longitude);
            Dusk dusk = quality.getFeatures().get(0).getProperties().getDusk();
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").parse(dusk.getCivil());
            System.out.format("Astronomical: %s Civil: %s Nautical: %s\n", dusk.getAstronomical(), date, dusk.getNautical());
            System.exit(0);
        }
        if (false) {
            System.out.println(dao.getTweet(sunrise, latitude, longitude, location));
            System.exit(0);

        }
        if (true) {
            dao.sendTweet(sunrise, latitude, longitude, location);
            System.exit(0);
        }
    }

    public SrssDAO() throws Exception {
        login();
    }

    public void sendTweet(boolean sunrise, double latitude, double longitude, String location) {
        try {
            String text = getTweet(sunrise, latitude, longitude, location);
            Bot bot = new Bot();
            String type = sunrise ? "sunrise" : "sunset";
            String imageFileName = String.format("/tmp/%s.png", type);
            Utils.saveImage(String.format("https://sunsetwx.com/%s/%s_et.png", type, type), imageFileName);
            bot.tweet(String.format("%s %s %s", "@pla1", text, "https://sunsetwx.com/"), imageFileName);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.format("Could not send tweet. Error: %s.\n", e.getLocalizedMessage());
            return;
        }
    }

    private String getTweet(boolean sunrise, double latitude, double longitude, String location) throws IOException {
        Quality quality = getQuality(sunrise, latitude, longitude);
        if (quality == null || quality.getFeatures().size() == 0) {
            System.out.println("Quality does have any features. Returning null.");
            return null;
        }
        String type = sunrise ? "Sunrise" : "Sunset";
        String qualityString = quality.getFeatures().get(0).getProperties().getQuality();
        qualityString = Utils.toLower(qualityString);
        System.out.format("%s quality is %s.\n", type, qualityString);

        if (!"Good".equalsIgnoreCase(qualityString)
                && !"Great".equalsIgnoreCase(qualityString)) {
            System.out.println("Quality is not good or great. Returning null.");
            //TODO Uncomment following line before production.
            //       return null;
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
        return String.format("%s quality is %s for %s. %s is at %s.",
                type, qualityString, location, type, timeDisplay);
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

    private void login() throws IOException {
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
