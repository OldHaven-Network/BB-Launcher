package net.oldhaven.framework;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import net.oldhaven.Main;
import net.oldhaven.controller.MainMenuScreenController;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class GitHubAPI {
    public static float downloadProgress;
    public static GitHubAPI openURL(String url) {
        GitHubAPI gitHubAPI = new GitHubAPI(url);
        try {
            URL oracle = new URL(url);
            URLConnection uc = oracle.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String s;
            while ((s = in.readLine()) != null) {
                builder.append(s);
            }
            JSONObject json = (JSONObject) new JSONArray(builder.toString()).get(0);
            gitHubAPI.tag = (String) json.get("tag_name");
            JSONObject assets = (JSONObject)((JSONArray)json.get("assets")).get(0);
            gitHubAPI.downloadURL = (String)assets.get("browser_download_url");
            gitHubAPI.downloadSize = (Integer)assets.get("size");
            System.out.println(json.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return gitHubAPI;
    }

    private int downloadSize;
    private String downloadURL;
    private String url;
    private String tag;
    private GitHubAPI(String url) {
        this.url = url;
    }

    public int writeToFile(final String absoluteLoc, final Runnable onFinish) {
        new Thread(() -> {
            File file = new File(absoluteLoc);
            if(file.exists()) {
                Platform.runLater(onFinish);
                Thread.currentThread().interrupt();
                return;
            }
            int i = 0;
            try {
                BufferedInputStream inputStream = new BufferedInputStream(new URL(this.getDownloadURL()).openStream());
                FileOutputStream fileOS = new FileOutputStream(absoluteLoc);
                byte[] data = new byte[1024];
                int byteContent;
                int currentByte = 0;
                Platform.runLater(() -> {
                    ((MainMenuScreenController) Main.getCurrentController()).version_picker.setDisable(true);
                    ((MainMenuScreenController) Main.getCurrentController()).progress_bar.setVisible(true);
                    ((MainMenuScreenController) Main.getCurrentController()).launch_button.setText("Installing...");
                    ((MainMenuScreenController) Main.getCurrentController()).launch_button.setDisable(true);
                });
                //int go = 0;
                while ((byteContent = inputStream.read(data, 0, data.length)) != -1) {
                    /*go++;
                    if(go > 500) {
                        System.out.println(currentByte + " / " + totalSize);
                        go = 0;
                    }*/
                    currentByte+=byteContent;
                    fileOS.write(data, 0, byteContent);
                    float progress = ((float) currentByte / downloadSize) * 100.0F;
                    GitHubAPI.downloadProgress = progress / 100.0F;
                    if(Main.getCurrentController() != null &&
                      Main.getCurrentController() instanceof MainMenuScreenController) {
                        ((MainMenuScreenController) Main.getCurrentController()).progress_bar.setProgress(downloadProgress);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                ((MainMenuScreenController) Main.getCurrentController()).version_picker.setDisable(false);
                ((MainMenuScreenController) Main.getCurrentController()).progress_bar.setVisible(false);
                ((MainMenuScreenController) Main.getCurrentController()).launch_button.setText("Launch");
                ((MainMenuScreenController) Main.getCurrentController()).launch_button.setDisable(false);
            });
            Platform.runLater(onFinish);
            Thread.currentThread().interrupt();
        }).start();
        return 0;
    }

    public String getDownloadURL() {
        return downloadURL;
    }
    public String getUrl() {
        return url;
    }
    public String getTag() {
        return tag;
    }
}
