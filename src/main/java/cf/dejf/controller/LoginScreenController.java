package cf.dejf.controller;

import cf.dejf.utility.UserInfo;
import cf.dejf.framework.Install;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;


public class LoginScreenController {

    @FXML
    private Button login_button;
    @FXML
    private Label close_button;
    @FXML
    private Hyperlink noaccount_link;
    @FXML
    public Stage primaryStage;
    @FXML
    public TextField username_input;
    @FXML
    public PasswordField password_input;
    @FXML
    public Label error_label;
    @FXML
    public CheckBox usemojangaccount_checkbox;

    @FXML
    private void close(MouseEvent event){
        System.exit(0);
    }

    @FXML
    private void closeButtonMouseover(MouseEvent event) {
        if(event.getEventType().getName() == "MOUSE_ENTERED") {
            close_button.setTextFill(Color.web("#646464", 1));
        } else if(event.getEventType().getName() == "MOUSE_EXITED") {
            close_button.setTextFill(Color.web("#FFFFFF", 1));
        }
    }

    @FXML
    private void changeScene(String newScene) {
        try{
            Parent root = FXMLLoader.load(getClass().getResource(newScene));
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
    }


    @FXML
    private void handleLogin() {
        String username = username_input.getText();
        String password = password_input.getText();
        if((username == null || username.isEmpty() || password == null || password.isEmpty()) && !usemojangaccount_checkbox.isSelected()){
            showAndAlignLoginError("Missing username/password!");
            // For testing, if you do not input your credentials, you will reach the main menu.
            changeScene("/fxml/MainMenuScreen.fxml");
        } else if(usemojangaccount_checkbox.isSelected()){
            try {
                AuthenticationResponse authResponse = OpenMCAuthenticator.authenticate(username, password);
                try {
                    UserInfo userInfo = new UserInfo(authResponse.getSelectedProfile().getName(), authResponse.getAccessToken(), authResponse.getClientToken());
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    Writer writer = Files.newBufferedWriter(Paths.get(Install.getMainPath() + "players.json"));
                    gson.toJson(userInfo, writer);
                    writer.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                try {
                    PrintWriter usernameWriter = new PrintWriter(Install.getMainPath() + "currentuser.txt");
                    usernameWriter.print(authResponse.getSelectedProfile().getName());
                } catch (Exception ex2) {
                    ex2.printStackTrace();
                }
            } catch (AuthenticationUnavailableException | RequestException e) {
                if(e instanceof InvalidCredentialsException) {
                    showAndAlignLoginError("Invalid credentials!");
                } else if(e instanceof UserMigratedException) {
                    showAndAlignLoginError("User migrated, use e-mail instead!");
                } else if(e instanceof AuthenticationUnavailableException) {
                    showAndAlignLoginError("Cannot establish connection!");
                } else {
                    showAndAlignLoginError("Unhandled exception!");
                    System.out.println(e);
                }
            }

        } else {
            try {
                PrintWriter usernameWriter = new PrintWriter(Install.getMainPath() + "currentuser.txt");
                usernameWriter.print(username_input.getText());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }


    }

    @FXML
    private void initialize() {
        final KeyFrame kf1 = new KeyFrame(Duration.seconds(0.1), e -> login_button.requestFocus());
        final Timeline timeline = new Timeline(kf1);
        Platform.runLater(timeline::play);
    }

}
