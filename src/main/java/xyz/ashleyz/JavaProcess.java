package xyz.ashleyz;

import cf.dejf.framework.Install;

import java.io.*;

public final class JavaProcess {
    private String output = "";
    private String javaHome = "";

    public JavaProcess(String javaHome) {
        this.javaHome = javaHome;
    }

    public boolean exec(Class klass, String... args) throws IOException {
        String javaBin = javaHome +
                File.separator + "bin" +
                File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        String libsPath = System.getProperty("java.libs.path");
        String className = klass.getCanonicalName();

        StringBuilder sb = new StringBuilder();
        for(int i=0;i < args.length;i++) {
            sb.append(args[i]).append(" ");
        }

        System.out.println(javaBin);
        System.out.println(libsPath);
        System.out.println(classpath);
        System.out.println(className);
        System.out.println(sb.toString());

ProcessBuilder builder = new ProcessBuilder(javaBin, "-Xms"+"100M", "-Xms"+"2G",
        "-Djava.library.path="+libsPath, "-cp", classpath, className, sb.toString());

        builder.redirectErrorStream(true);

        Process process = builder.start();
        new Logger(process).start();
        return process.isAlive();
    }

    private class Logger extends Thread {
        private Process process;
        Logger(Process process) {
            this.process = process;
        }

        @Override
        public void run() {
            try {
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String s;
                while ((s = stdInput.readLine()) != null) {
                    System.out.println(s);
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
