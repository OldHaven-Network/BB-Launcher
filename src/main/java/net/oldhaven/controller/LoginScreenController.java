package net.oldhaven.controller;

import com.google.gson.JsonParser;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;
import net.chris54721.openmcauthenticator.responses.RefreshResponse;
import net.oldhaven.utility.UserInfo;
import net.oldhaven.framework.Install;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.chris54721.openmcauthenticator.OpenMCAuthenticator;
import net.chris54721.openmcauthenticator.exceptions.AuthenticationUnavailableException;
import net.chris54721.openmcauthenticator.exceptions.InvalidCredentialsException;
import net.chris54721.openmcauthenticator.exceptions.RequestException;
import net.chris54721.openmcauthenticator.exceptions.UserMigratedException;
import net.chris54721.openmcauthenticator.responses.AuthenticationResponse;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class LoginScreenController implements Initializable {
    double offset_x;
    double offset_y;
    @FXML private Button login_button;
    @FXML private Label close_button;
    @FXML private Hyperlink noaccount_link;
    @FXML public Stage primaryStage;
    @FXML public TextField username_input;
    @FXML public PasswordField password_input;
    @FXML public Label error_label;
    @FXML public TextArea news_box;
    @FXML public ComboBox<String> account_choice;
    @FXML public CheckBox rememberaccount_checkbox;
    public String savedUsername;

    public void initialize(URL url, ResourceBundle bundle) {
        final KeyFrame kf1 = new KeyFrame(Duration.seconds(0.1), e -> {
            login_button.requestFocus();

            String news = null;
            try {
                news = IOUtils.toString(new FileInputStream(Install.getMainPath() + "news.txt"));
            } catch (IOException e2) {
                e2.printStackTrace();
            }

            news_box.setText(news);
            news_box.requestFocus();
            login_button.requestFocus();

            Gson gsonAccountChoice = new Gson();
            try {
                Reader reader = Files.newBufferedReader(Paths.get(Install.getMainPath() + "players.json"));
                Map<?, ?> map = gsonAccountChoice.fromJson(reader, Map.class);
                if(map != null && map.size() > 0) {
                    for (Map.Entry<?, ?> entry : map.entrySet()) {
                        account_choice.getItems().add(entry.getKey().toString());
                    }
                }
                reader.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            account_choice.valueProperty().addListener(new ChangeListener<String>() {
                @Override public void changed(ObservableValue obs, String oldValue, String newValue) {
                    savedUsername = newValue;
                    username_input.setPromptText("No username input required");
                    password_input.setPromptText("No password input required");
                    username_input.editableProperty().setValue(false);
                    password_input.editableProperty().setValue(false);
                }
            });


            String username = null;
            if(new File(Install.getMainPath() + "currentuser.txt").exists()) {
                try {
                    FileInputStream fis = new FileInputStream(Install.getMainPath() + "currentuser.txt");
                    BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                    username = br.readLine();
                    br.close();
                    fis.close();

                    String[] split = username.split("\n");
                    username = split[0];
                    username = username.replaceAll("[^a-zA-Z0-9_?\\s]", "");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                if (username != null) {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    StringBuilder json = readPlayersJson();
                    if (!json.toString().isEmpty()) {
                        Map<String, List<String>> map = gson.fromJson(json.toString(), type);
                        if(setUserInfo(username, gson, map))
                            changeScene();
                    }
                }
            }
        });
        final Timeline timeline = new Timeline(kf1);
        Platform.runLater(timeline::play);
    }

    @FXML
    private void movableWindow(){
        Scene scene = close_button.getScene();
        Stage stage = (Stage) close_button.getScene().getWindow();
        scene.setOnMousePressed(event -> {
            offset_x = event.getSceneX();
            offset_y = event.getSceneY();
        });

        scene.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - offset_x);
            stage.setY(event.getScreenY() - offset_y);
        });
    }

    @FXML
    private void close(MouseEvent event){
        System.exit(0);
    }

    @FXML
    private void closeButtonMouseover(MouseEvent event) {
        if(event.getEventType().getName().equals("MOUSE_ENTERED")) {
            close_button.setTextFill(Color.web("#646464", 1));
        } else if(event.getEventType().getName().equals("MOUSE_EXITED")) {
            close_button.setTextFill(Color.web("#FFFFFF", 1));
        }
    }

    @FXML
    private void changeScene() {
        if(rememberaccount_checkbox.isSelected()) {
            try {
                PrintWriter usernameWriter = new PrintWriter(Install.getMainPath() + "currentuser.txt");
                usernameWriter.print(UserInfo.getUsername());
                usernameWriter.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            File file = new File(Install.getMainPath() + "currentuser.txt");
            if(file.exists())
                file.delete();
        }

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainMenuScreen.fxml"));
            Stage primaryStage = (Stage) login_button.getScene().getWindow();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showAndAlignLoginError(String error) {
        Label label = error_label;
        label.setMaxWidth(Double.MAX_VALUE);
        AnchorPane.setLeftAnchor(label, 0.0);
        AnchorPane.setRightAnchor(label, 0.0);
        label.setAlignment(Pos.CENTER);
        label.setText(error);
    }

    @FXML
    private void noAccountInfoClick() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Don't have a Mojang account?");
        alert.setHeaderText(null);
        alert.setContentText("You can uncheck the \"Use Mojang account\" box and type in a username without a password.");
        alert.show();
    }


    @FXML
    private void handleLogin() {
        String username = username_input.getText();
        username = username == null ? "" : username;
        String password = password_input.getText();
        password = password == null ? "" : password;
        if(account_choice.getSelectionModel().isEmpty()) {
            try {
                AuthenticationResponse authResponse = OpenMCAuthenticator.authenticate(username, password);
                try {
                    UserInfo.setUserInfo(authResponse.getSelectedProfile().getName(),
                            authResponse.getAccessToken(), authResponse.getClientToken());
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    Writer writer = Files.newBufferedWriter(Paths.get(Install.getMainPath() + "players.json"));

                    Map<String, LinkedList<String>> map = new LinkedHashMap<>();
                    LinkedList<String> list = new LinkedList<>();
                    list.add(UserInfo.getAccessToken());
                    list.add(UserInfo.getClientToken());
                    map.put(UserInfo.getUsername(), list);

                    gson.toJson(map, writer);
                    writer.close();
                    changeScene();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Install.setCurrentUser(authResponse.getSelectedProfile().getName());
            } catch (AuthenticationUnavailableException | RequestException e) {
                if (e instanceof InvalidCredentialsException) {
                    showAndAlignLoginError("Invalid credentials!");
                } else if (e instanceof UserMigratedException) {
                    showAndAlignLoginError("User migrated, use e-mail instead!");
                } else if (e instanceof AuthenticationUnavailableException) {
                    showAndAlignLoginError("Cannot establish connection!");
                } else {
                    showAndAlignLoginError("Unhandled exception!");
                    System.out.println(e);
                }
            }
        } else {
            if (savedUsername != null) {
                Gson gson = new GsonBuilder().create();
                StringBuilder json = readPlayersJson();
                if (!json.toString().isEmpty()) {
                    Map<String, List<String>> map = gson.fromJson(json.toString(), type);
                    boolean b = setUserInfo(savedUsername, gson, map);
                    if(b)
                        changeScene();
                }
            }
        }
    }

    private static Type type = new TypeToken<Map<String, List<String>>>() {}.getType();
    private StringBuilder readPlayersJson() {
        StringBuilder json = new StringBuilder();
        try {
            FileInputStream fstream = new FileInputStream(Install.getMainPath() + "players.json");
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String line;
            while ((line = br.readLine()) != null) {
                json.append(line);
            }
            br.close();
            fstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }
    private boolean setUserInfo(String username, Gson gson, Map<String, List<String>> map) {
        boolean validated = false;
        if(map.containsKey(username)) {
            List<String> list = map.get(username);
            String acToken = list.get(0);
            String clToken = list.get(1);
            boolean change = false;
            try {
                validated = OpenMCAuthenticator.validate(acToken, clToken);
            } catch (AuthenticationUnavailableException | RequestException ex) {
                try {
                    RefreshResponse response = OpenMCAuthenticator.refresh(acToken, clToken);
                    acToken = response.getAccessToken();
                    clToken = response.getClientToken();
                    change = validated = true;
                } catch (RequestException | AuthenticationUnavailableException exc) {
                    exc.printStackTrace();
                }
            }
            if (validated) {
                UserInfo.setUserInfo(username, acToken, clToken);
                if (change) {
                    try {
                        Writer writer = Files.newBufferedWriter(Paths.get(Install.getMainPath() + "players.json"));

                        list.clear();
                        list.add(UserInfo.getAccessToken());
                        list.add(UserInfo.getClientToken());
                        map.replace(UserInfo.getUsername(), list);

                        gson.toJson(map, writer);
                        writer.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        System.out.println(validated);
        return validated;
    }
}
