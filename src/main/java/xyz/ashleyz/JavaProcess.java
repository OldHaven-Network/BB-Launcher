package xyz.ashleyz;

import cf.dejf.Main;
import cf.dejf.controller.LoginScreenController;
import cf.dejf.controller.ProcessInfoScreenController;
import cf.dejf.framework.Install;
import cf.dejf.utility.LogOutput;
import cf.dejf.utility.UserInfo;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Arrays;

public final class JavaProcess {
    private String output = "";
    private String javaHome = "";

    public JavaProcess(String javaHome) {
        this.javaHome = javaHome;
    }

    public boolean exec(Class klass) throws IOException {
        String javaBin = javaHome +
                File.separator + "bin" +
                File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        String libsPath = System.getProperty("java.libs.path");
        String className = klass.getCanonicalName();

        /*System.out.println(javaBin);
        System.out.println(libsPath);
        System.out.println(classpath);
        System.out.println(className);*/

        String username = UserInfo.getUsername();

        ProcessBuilder builder = new ProcessBuilder(javaBin, "-Xms"+"100M", "-Xms"+"2G",
                "-Djava.library.path="+libsPath, "-cp", classpath, className, "--gameDir", Install.getMainPath(), "--username", username);

        builder.redirectErrorStream(true);

        Process process = builder.start();
        new Logger(process).start();
        return process.isAlive();
    }

    public static class Logger extends Thread {
        public static Process process;
        Logger(Process process) {
            Logger.process = process;
        }

        public static void destroyProcess() {
            process.destroyForcibly();
        }

        @Override
        public void run() {
            String s;
            try {
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));

                while ((s = stdInput.readLine()) != null) {
                    System.out.println(s);
                    LogOutput.appendLogOutput(s);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Read and process the command output, doesn't include error.
     *
     * @param input
     */
    private void readOutput(final InputStream input) {
        new Thread(() -> {
            StringBuilder sb = new StringBuilder();
            int data;
            try {
                while((data = input.read()) != -1) {
                    sb.append((char)data);
                }

                output = sb.toString();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
