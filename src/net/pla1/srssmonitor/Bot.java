package net.pla1.srssmonitor;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Key;
import org.sikuli.script.Screen;
import org.sikuli.basics.Settings;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Bot {
    public static void main(String[] args) throws Exception {
        Bot bot = new Bot();

    }

    private void loginTwitter() throws Exception {
        Utils.killBrowser();
        Utils.startBrowser("https://twitter.com/login");
        Properties properties = new Properties();
        properties.load(new FileInputStream(Utils.PROPERTY_FILE_NAME));
        Screen s = new Screen();
        Utils.sleep(5);
        s.click("images/username_field.png");
        s.type(properties.getProperty(Utils.PROPERTY_TWITTER_USER_NAME));
        s.click("images/password_field.png");
        s.type(properties.getProperty(Utils.PROPERTY_TWITTER_PASSWORD));
        s.type(Key.ENTER);

    }

    public void tweet(String text, String imageFileName) throws Exception {
        loginTwitter();
        Screen s = new Screen();
        s.click("images/tweet_button.png");
        s.click("images/compose_new_tweet_banner.png");
        s.type(text);
        if (Utils.isNotBlank(imageFileName)) {
            s.click("images/camera_button.png");
            s.click("images/open_files_header.png");
            Utils.sleep(1);
            s.type(imageFileName);
            s.type(Key.ENTER);
            s.click("images/tweet_button_bigger.png");
        } else {
            s.click("images/tweet_button_bigger.png");
        }

    }


}

