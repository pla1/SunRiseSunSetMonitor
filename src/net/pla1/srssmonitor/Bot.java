package net.pla1.srssmonitor;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Key;
import org.sikuli.script.Screen;
import org.sikuli.basics.Settings;

import java.io.FileInputStream;
import java.util.Properties;

public class Bot {
    public static void main(String[] args) throws Exception {
        Bot bot = new Bot();
        if (true) {
            bot.unlockScreen();
            System.exit(0);
        }
    }

    private void unlockScreen() throws Exception {
        Screen s = new Screen();
        s.setAutoWaitTimeout(30);
        Utils.sleep(5);
        s.type(Key.ESC);
        Utils.sleep(2);
      //  String[] commandParts = {"/usr/bin/gnome-screenshot", "-f", "/tmp/screenshot.png"};
     //   Utils.run(commandParts);
        Utils.screenshot(s);
        if (s.exists("images/lock_screen_password_field.png") != null) {
            System.out.format("Screen is locked. Clicking on password field.\n");
            s.click("images/lock_screen_password_field.png");
            System.out.format("Typing password to unlock screen\n");
            Properties properties = new Properties();
            properties.load(new FileInputStream(Utils.PROPERTY_FILE_NAME));
            s.type(properties.getProperty(Utils.PROPERTY_UNLOCK_PASSWORD));
            s.type(Key.ENTER);
        } else {
            Utils.screenshot(s);
            System.out.format("Screen is not locked\n");
        }
    }

    private void loginTwitter() throws Exception {
        Utils.killBrowser();
        Utils.startBrowser("https://twitter.com/login");
        Properties properties = new Properties();
        properties.load(new FileInputStream(Utils.PROPERTY_FILE_NAME));
        Screen s = new Screen();
        s.setAutoWaitTimeout(30);
        s.wait("images/username_field.png", 60);
        s.click("images/username_field.png");
        s.type(properties.getProperty(Utils.PROPERTY_TWITTER_USER_NAME));
        s.click("images/password_field.png");
        s.type(properties.getProperty(Utils.PROPERTY_TWITTER_PASSWORD));
        s.type(Key.ENTER);
    }

    public void tweet(String text, String imageFileName) throws Exception {
       // unlockScreen();
        loginTwitter();
        Screen s = new Screen();
        s.setAutoWaitTimeout(30);
        Utils.sleep(5);
        System.out.println("About to click tweet button");
        s.click("images/tweet_button.png");
        System.out.println("Clicked tweet button");
        s.click("images/compose_new_tweet_banner.png");
        System.out.println("Clicked compose new tweet banner");
        s.type(text);
        if (Utils.isNotBlank(imageFileName)) {
            s.click("images/camera_button.png");
          //  s.click("images/open_files_header.png");
            s.click("images/file_system_ubuntu_mate.png");
            Utils.sleep(1);
            s.type(imageFileName);
            s.type(Key.ENTER);
            s.click("images/tweet_button_bigger.png");
        } else {
            s.click("images/tweet_button_bigger.png");
        }

    }


}

