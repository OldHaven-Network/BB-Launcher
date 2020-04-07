package cf.dejf.utility;

public class LogOutput {

    private static String log = "";

    private LogOutput() {}

    public static void appendLogOutput(String newLogLine) {
        log = log + newLogLine + "\n";
    }

    public static String getLogOutput() {
        return log;
    }
}
