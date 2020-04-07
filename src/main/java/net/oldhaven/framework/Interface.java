package net.oldhaven.framework;

import net.oldhaven.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Interface {

    private static double offset_x;
    private static double offset_y;


    public static void changeScene(String newScene) {
        try{
            Stage stage = Main.getPrimaryStage();

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Interface.class.getResource(newScene));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);

            scene.setOnMousePressed(event -> {
                offset_x = event.getSceneX();
                offset_y = event.getSceneY();
            });

            scene.setOnMouseDragged(event -> {
                stage.setX(event.getScreenX() - offset_x);
                stage.setY(event.getScreenY() - offset_y);
            });

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
