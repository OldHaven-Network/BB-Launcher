package cf.dejf.controller;

import cf.dejf.framework.Install;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import net.fabricmc.loader.launch.knot.KnotClient;
import xyz.ashleyz.JavaProcess;

import java.io.*;
import java.util.Scanner;

public class MainMenuScreenController {

    double offset_x;
    double offset_y;

    @FXML
    private Button launch_button;

    @FXML
    private Label close_button;

    @FXML
    private void close(MouseEvent event){
        System.exit(0);
    }

    @FXML
    private void closeButtonMouseover(MouseEvent event){
        if(event.getEventType().getName() == "MOUSE_ENTERED") {
            close_button.setTextFill(Color.web("#646464", 1));
        } else if(event.getEventType().getName() == "MOUSE_EXITED") {
            close_button.setTextFill(Color.web("#FFFFFF", 1));
        }
    }

    @FXML
    private void movableWindow(){
        Scene scene = launch_button.getScene();
        Stage stage = (Stage) launch_button.getScene().getWindow();
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
    private void handleLaunch() throws IOException, InterruptedException {

        StringBuilder libraryBuilder = new StringBuilder();
        String[] libraries = new String[]{"minecraft.jar", "jinput.jar", "lwjgl.jar", "lwjgl_util.jar", "json.jar"};
        for(String libraryAppend : libraries) {
            if (Install.getOS().equals("Windows"))
                libraryBuilder.append(Install.getBinPath()).append(libraryAppend).append(";");
            else
                libraryBuilder.append(Install.getBinPath()).append(libraryAppend).append(":");
        }
        String libraryFinal = libraryBuilder.toString();

        String username = "ToddHoward";
        String id = "7ae9007b9909de05ea58e94199a33b30c310c69c";

        System.setProperty("java.class.path", Install.getClassPath());
        System.setProperty("java.libs.path", Install.getNativesPath());
        new JavaProcess(System.getProperty("java.home")).exec(KnotClient.class, "--username", username, "--gameDir", Install.getMainPath());

        if(null == null)
            return;
        ProcessBuilder launch = new ProcessBuilder(
                "java", "-Xms256M", "-Xmx1G",
                "-Djava.library.path=" + Install.getNativesPath(), "-cp", "\"" + libraryFinal + "\"", "net.minecraft.client.Minecraft",
                username, id);
        launch.directory(new File(Install.getBinPath()));
        Process process = launch.start();
        System.out.println(launch.command());
        Scanner s = new Scanner(process.getInputStream());
        StringBuilder text = new StringBuilder();
        while (s.hasNextLine()) {
            text.append(s.nextLine());
            text.append("\n");
        }
        s.close();
        int result = process.waitFor();
        System.out.printf( "Process exited with result %d and output %s%n", result, text );

        /*
        System.out.println(launch.command().toString());
        launch.directory(new File(Objects.requireNonNull(Install.getBinPath())));
        System.out.println((new File(Objects.requireNonNull(Install.getBinPath()))).getAbsolutePath());
        try {
            Process process = launch.start();
            InputStream in = process.getInputStream();
            for (int i = 0; i < in.available(); i++) {
                System.out.println("" + in.read());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
         */

        Runtime rt = Runtime.getRuntime();
        String[] fuck = {"java", "-Xmx 1G", "-Djava.library.path=" + Install.getNativesPath(), "-cp " + "\"" + libraryFinal + "\"", "net.minecraft.client.Minecraft", username, id};
        rt.exec(fuck);
    }

    private void initialize(){

    }
}
