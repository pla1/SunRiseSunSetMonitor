package net.pla1.srssmonitor;

import org.sikuli.script.Screen;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class Utils {
    public static final String PROPERTY_TWITTER_USER_NAME = "twitterUsername";
    public static final String PROPERTY_TWITTER_PASSWORD = "twitterPassword";
    public static final String PROPERTY_FILE_NAME = "/etc/net.pla1.srssmonitor.SrssDAO.properties";
    public static final String PROPERTY_UNLOCK_PASSWORD = "unlockPassword";

    public static void sleep(int seconds) {
        try {
            System.out.format("Sleeping %d seconds.\n", seconds);
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        System.out.println(getDateDisplay(calendar.getTime()));
        calendar.add(Calendar.YEAR, 1);
        System.out.println(getDateDisplay(calendar.getTime()));
    }

    public static String toLower(String s) {
        if (s == null) {
            return "";
        } else {
            return s.toLowerCase();
        }
    }

    public static String encode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String killBrowser() {
        String[] commandParts = {"/usr/bin/killall", "chromium-browser"};
        return run(commandParts);
    }

    public static void startBrowser(String urlString) {
        String[] commandParts = {"/usr/bin/chromium-browser", "--incognito", "--kiosk", urlString};
        runNoOutput(commandParts);
    }

    public static boolean isBlank(String s) {
        return s == null || s.trim().length() == 0;
    }

    public static boolean isNotBlank(String s) {
        return !isBlank(s);
    }

    public static void saveImage(String imageUrl, String destinationFile) throws IOException {
        URL url = new URL(imageUrl);
        InputStream is = url.openStream();
        OutputStream os = new FileOutputStream(destinationFile);
        byte[] b = new byte[2048];
        int length;
        while ((length = is.read(b)) != -1) {
            os.write(b, 0, length);
        }
        is.close();
        os.close();
    }

    public static void runNoOutput(String[] commandParts) {
        System.out.println("Command parts quantity: " + commandParts.length);
        for (String part : commandParts) {
            System.out.println("Command part: " + part);
        }
        try {
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(commandParts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String run(String[] commandParts) {
        System.out.println("Command parts quantity: " + commandParts.length);
        for (String part : commandParts) {
            System.out.println("Command part: " + part);
        }
        BufferedReader reader = null;
        StringBuilder output = new StringBuilder();
        int exitValue = 0;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(Arrays.asList(commandParts));
            Process process = processBuilder.start();
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String lineRead = null;
            while ((lineRead = reader.readLine()) != null) {
                output.append(lineRead);
                output.append("\n");
            }
            exitValue = process.waitFor();
        } catch (Exception e) {
            output.append("Exception: " + e.getLocalizedMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
        System.out.println("\nProcess exit value: " + exitValue);
        return output.toString();
    }


    public static String getDateDisplay(Date date) {
        if (isToday(date)) {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            return sdf.format(date);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE MMMM dd, yyyy hh:mm a");
            return sdf.format(date);
        }
    }

    public static boolean isToday(Date date) {
        Calendar today = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return today.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR)
                && today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR);
    }

    public static void screenshot(Screen s) {
        try {
            File fromFile = new File(s.capture().getFile());
            File toFile = File.createTempFile("screenshot_", ".png");
            copyFile(fromFile, toFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copyFile(File from, File to) throws IOException {
        Files.copy(from.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);
        System.out.format("Copied %s to %s.\n", from.getAbsoluteFile(), to.getAbsoluteFile());
    }
}
