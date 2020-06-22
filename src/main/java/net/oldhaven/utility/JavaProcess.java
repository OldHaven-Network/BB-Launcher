package net.oldhaven.utility;

import com.google.gson.JsonArray;
import net.oldhaven.framework.Arguments;
import net.oldhaven.framework.Install;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class JavaProcess {
    private String output = "";
    private String javaHome = "";
    private static Class lastArg;
    private static JavaProcess jProc;
    private static Process process;
    private JsonArray args = null;

    public JavaProcess(String javaHome, JsonArray args) {
        jProc = this;
        this.javaHome = javaHome;
    }

    public static boolean isAlive() {
        return process.isAlive();
    }

    public boolean exec(Class clazz) throws IOException {
        lastArg = clazz;
        String javaBin = javaHome +
                File.separator + "bin" +
                File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        String libsPath = System.getProperty("java.libs.path");
        String className = clazz.getCanonicalName();

        String username = UserInfo.getUsername();

        File settingsFile = new File(Install.getMainPath() + "settings.txt");
        String maxmem = null;
        String minmem = null;
        try {
            BufferedReader settingsReader = new BufferedReader(new FileReader(settingsFile));
            maxmem = settingsReader.readLine();
            minmem = settingsReader.readLine();
            settingsReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //ProcessBuilder builder = new ProcessBuilder(javaBin, "-Xms"+minmem+"m", "-Xms"+maxmem+"m",
        //        "-Djava.library.path="+libsPath, "-cp", classpath, className, username);
        Arguments arguments = new Arguments();
        arguments.add(javaBin, "-Xms"+minmem+"m", "-Xms"+maxmem+"m", "-Djava.library.path="+libsPath);
        if(args != null) {
            for(int i = 0; i < args.size(); i++) {
                arguments.add(args.get(i).getAsString());
            }
        }
        arguments.add("-cp", classpath, className);
        arguments.add("--gameDir", Install.getMinecraftPath());
        arguments.add("--username", username);
        String token = UserInfo.getAccessToken();
        arguments.add("--session", token);
        arguments.add("--accessToken", token);
        arguments.add("--sessionId", token);
        String[] args = arguments.build();
        ProcessBuilder builder = new ProcessBuilder(args);
        System.out.println(Arrays.toString(args));
        builder.redirectErrorStream(true);

        process = builder.start();
        new Logger(process).start();
        return process.isAlive();
    }

    public static void restartProcess() {
        destroyProcess();
        try {
            jProc.exec(lastArg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void destroyProcess() {
        process.destroyForcibly();
        if(process != null) {
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @author ashleez_
     */
    public static class Logger extends Thread {
        Logger(Process process) {}

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
     *      *
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
