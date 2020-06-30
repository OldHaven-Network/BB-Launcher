package net.oldhaven.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.Toolkit;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import net.oldhaven.BBLauncher;
import net.oldhaven.framework.Install;
import net.oldhaven.utility.UserInfo;
import net.oldhaven.utility.enums.Scene;
import net.oldhaven.utility.lang.Lang;
import net.oldhaven.utility.lang.LanguageUtil;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainScreenController implements Initializable {
    double offset_x;
    double offset_y;

    @FXML private ImageView background;
    @FXML private Label close_button, main_button, settings_button, processInfo_button, logout_button, language_button;
    @FXML public Pane topbar, content, clipPane;
    @FXML private Line line_main, line_settings, line_processInfo;

    private static final Text testText;

    static {
        testText = new Text();
        new javafx.scene.Scene(new Group(testText));
    }

    private double getStrWidth(Font font, String str) {
        FontLoader fontLoader = Toolkit.getToolkit().getFontLoader();
        return fontLoader.computeStringWidth(str, font) / 2;
    }

    private Scene lastScene = null;
    public void setScene(Scene scene) {
        if(scene == Scene.Login) {
            topbar.setVisible(false);
            logout_button.setVisible(false);
        } else {
            topbar.setVisible(true);
            logout_button.setVisible(true);
            topbar.toFront();
        }
        switch(scene) {
            case MainMenu:
                line_main.setVisible(true);
                line_settings.setVisible(false);
                line_processInfo.setVisible(false);
                line_main.setEndX(main_button.widthProperty().get());
                break;
            case Settings:
                line_main.setVisible(false);
                line_settings.setVisible(true);
                line_processInfo.setVisible(false);
                line_settings.setEndX(settings_button.widthProperty().get());
                break;
            case ProcessInfo:
                line_main.setVisible(false);
                line_settings.setVisible(false);
                line_processInfo.setVisible(true);
                line_processInfo.setEndX(processInfo_button.widthProperty().get());
                break;
            default:
                line_main.setVisible(false);
                line_settings.setVisible(false);
                line_processInfo.setVisible(false);
                break;
        }
        this.content.getChildren().removeAll();
        this.content.getChildren().setAll(scene.load());
        if(lastScene != null)
            BBLauncher.cancelRunnablesWithScene(lastScene);
        lastScene = scene;
    }

    @FXML
    public void languageButton_onClick() {
        Alert popupWindow = new Alert(Alert.AlertType.NONE);
        popupWindow.setTitle(LanguageUtil.getLangSelf());
        popupWindow.setHeaderText(null);
        popupWindow.initStyle(StageStyle.UTILITY);
        final Button button = new Button(Lang.ALERT_BUTTONS_OK.translate());
        button.setOnAction(event -> popupWindow.close());
        button.setPrefSize(75, 20);

        TreeView<String> treeView = new TreeView<>();


        final GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setMaxWidth(Double.MAX_VALUE);
        gridPane.setMaxHeight(Double.MAX_VALUE);
        gridPane.add(button, 1, 1);
        popupWindow.getDialogPane().setContent(gridPane);
        Window window = popupWindow.getDialogPane().getScene().getWindow();
        window.setOnCloseRequest(event -> popupWindow.close());
        popupWindow.show();

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        language_button.setText(LanguageUtil.getLangSelf());
        main_button.setText(Lang.MAIN_SELF.translate());
        settings_button.setText(Lang.SETTINGS_SELF.translate());
        processInfo_button.setText(Lang.PROCESS_SELF.translate());
        this.setScene(Scene.Login);
        BBLauncher.setCurrentController(this);
        File[] fileArray = new File(Install.getMainPath()).listFiles();
        assert fileArray != null;
        for(File file : fileArray) {
            if(file.getAbsolutePath().contains("launcherbg")) {
                Image image = new Image(file.toURI().toString());
                background.setImage(image);
                background.fitWidthProperty().bind(clipPane.widthProperty());
            }
        }
    }

    @FXML
    private void clickMainButton() {
        this.setScene(Scene.MainMenu);
    }

    @FXML
    private void clickSettingsButton() {
        this.setScene(Scene.Settings);
    }

    @FXML
    private void clickProcessInfoButton() {
        this.setScene(Scene.ProcessInfo);
    }

    @FXML
    private void logoutButton_onClick() throws IOException {

        // Show the user a dialog box to help him decide.
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Are you sure you want to log out?");
        alert.setContentText("If you also want to erase your account details, click \"Logout and forget\".");

        ButtonType logoutAndForget = new ButtonType("Logout and forget", ButtonBar.ButtonData.LEFT);
        ButtonType logout = new ButtonType("Logout", ButtonBar.ButtonData.OTHER);
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(logoutAndForget, logout, cancel);

        // Get the button pressed.
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == logoutAndForget){
            // Read JSON to JsonObject, remove user's account, pretty print JSON back to players.json, clear current user, switch to login scene.
            try {
                JsonObject jsonObject = (JsonObject) JsonParser.parseReader(new FileReader(Install.getMainPath() + "players.json"));
                jsonObject.remove(UserInfo.getUsername());
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Writer writer = Files.newBufferedWriter(Paths.get(Install.getMainPath() + "players.json"));
                gson.toJson(jsonObject, writer);
                writer.close();
                Install.setCurrentUser(null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            this.setScene(Scene.Login);
        } else if (result.get() == logout) {
            // Only switch to login scene.
            Install.setCurrentUser(null);
            this.setScene(Scene.Login);
        }
    }

    @FXML
    private void movableWindow(){
        javafx.scene.Scene scene = close_button.getScene();
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
    private void closeButtonMouseover(MouseEvent event){
        if(event.getEventType().getName().equals("MOUSE_ENTERED")) {
            close_button.setTextFill(Color.web("#646464", 1));
        } else if(event.getEventType().getName().equals("MOUSE_EXITED")) {
            close_button.setTextFill(Color.web("#FFFFFF", 1));
        }
    }

    @FXML
    private void mainButtonMouseover(MouseEvent event){
        if(event.getEventType().getName().equals("MOUSE_ENTERED")) {
            main_button.setTextFill(Color.web("#646464", 1));
        } else if(event.getEventType().getName().equals("MOUSE_EXITED")) {
            main_button.setTextFill(Color.web("#000000", 1));
        }
    }

    @FXML
    private void settingsButtonMouseover(MouseEvent event){
        if(event.getEventType().getName().equals("MOUSE_ENTERED")) {
            settings_button.setTextFill(Color.web("#646464", 1));
        } else if(event.getEventType().getName().equals("MOUSE_EXITED")) {
            settings_button.setTextFill(Color.web("#000000", 1));
        }
    }

    @FXML
    private void processInfoButtonMouseover(MouseEvent event){
        if(event.getEventType().getName().equals("MOUSE_ENTERED")) {
            processInfo_button.setTextFill(Color.web("#646464", 1));
        } else if(event.getEventType().getName().equals("MOUSE_EXITED")) {
            processInfo_button.setTextFill(Color.web("#000000", 1));
        }
    }

    @FXML
    private void logoutButton_mouseOver(MouseEvent event){
        if(event.getEventType().getName().equals("MOUSE_ENTERED")) {
            logout_button.setTextFill(Color.web("#646464", 1));
        } else if(event.getEventType().getName().equals("MOUSE_EXITED")) {
            logout_button.setTextFill(Color.web("#FFFFFF", 1));
        }
    }

    @FXML
    private void close(MouseEvent event){
        System.exit(0);
    }
}
