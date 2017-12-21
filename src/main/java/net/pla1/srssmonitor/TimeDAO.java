package net.pla1.srssmonitor;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeDAO {
    public static void main(String[] args) throws Exception {
        TimeDAO dao = new TimeDAO();
        double longitude = -79.929071;
        double latitude = 32.769484;
        Time time = dao.get(longitude, latitude);
        System.out.println(time);
    }

    public TimeDAO() {
    }

    public Time get(double longitude, double latitude) throws Exception {
        String urlString = String.format("https://api.sunrise-sunset.org/json?lat=%f&lng=%f&formatted=0", latitude, longitude);
        URL url = new URL(urlString);
        URLConnection urlConnection = url.openConnection();
        urlConnection.setRequestProperty("Method", "GET");
        InputStream inputStream = urlConnection.getInputStream();
        Gson gson = new Gson();
        Time time = gson.fromJson(new InputStreamReader(inputStream), Time.class);
        return time;
    }
}
