package net.oldhaven.controller.templates;

import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import net.oldhaven.BBLauncher;
import net.oldhaven.utility.LogOutput;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import net.oldhaven.utility.JavaProcess;
import net.oldhaven.utility.enums.Scene;
import net.oldhaven.utility.lang.Lang;
import org.fxmisc.richtext.StyleClassedTextArea;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ProcessInfoScreenController implements Initializable {
    @FXML private StyleClassedTextArea process_text;
    @FXML private AnchorPane pain;
    @FXML private Pane clipPane;
    @FXML private TextField loglines_textfield;

    private ScheduledExecutorService executor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AtomicInteger loglines = new AtomicInteger();
        loglines.set(10000);
        loglines_textfield.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                loglines_textfield.setText(newValue.replaceAll("[^\\d]", ""));
            }
            try{
                // Don't worry about this. It looks cancer, and it is, but pretend that this isn't here.
                if(process_text.getText().split("\n", -1).length < Integer.parseInt(loglines_textfield.getText())) {
                    loglines.set(Integer.parseInt(loglines_textfield.getText()));
                } else {
                    loglines.set(Integer.parseInt(loglines_textfield.getText()) + 10);
                }
            } catch(NumberFormatException e){
                loglines.set(10000);
            }
        });

        process_text.setAutoScrollOnDragDesired(true);

        /*
        * I found a memory leak on this runnable
         */
        BBLauncher.createRunnableWithScene(Scene.ProcessInfo, () -> {
            String text;
            if (!(text = LogOutput.getLogOutput()).isEmpty()) {
                process_text.appendText(text);
                process_text.scrollYBy(process_text.getLength());
            }
            String textInArea = process_text.getText();
            String[] lines = textInArea.split("\n", -1);
            if (lines.length > loglines.get()) {
                process_text.appendText(Lang.PROCESS_LOG_MAX.translateArgs(loglines.get()));
                executor.shutdownNow();
            }
        }, 0, 250, TimeUnit.MILLISECONDS);
    }

    @FXML
    private void killMinecraftButton() {
        if(JavaProcess.isAlive())
            JavaProcess.destroyProcess();
    }

    @FXML
    public void restartMinecraftButton(MouseEvent event) {
        JavaProcess.restartProcess();
    }

    @FXML
    public void clearButton() {
        process_text.clear();
    }

}
