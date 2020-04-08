package net.oldhaven.utility;

import net.oldhaven.framework.Install;

import java.io.*;

public final class JavaProcess {
    private String output = "";
    private String javaHome = "";
    private static Class lastArg;
    private static JavaProcess jProc;
    private static Process process;


    public JavaProcess(String javaHome) {
        jProc = this;
        this.javaHome = javaHome;
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

        ProcessBuilder builder = new ProcessBuilder(javaBin, "-Xms"+"100M", "-Xms"+"2G",
                "-Djava.library.path="+libsPath, "-cp", classpath, className, "--gameDir", Install.getMinecraftPath(), "--username", username);

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
