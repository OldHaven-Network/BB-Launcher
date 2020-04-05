package cf.dejf.framework;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;

public class Install {

    // NOTE: Mac OS and Linux support is untested.
    // Confidence of Mac OS working: low.
    // Confidence of Linux working: high.

    private static String name = "OHLauncher";

    private static boolean isLinux(String os) {
        if(os.contains("nix") || os.contains("nux") || os.contains("aix"))
            return true;
        return false;
    }

    public static boolean isOSUnknown() {
        String os = System.getProperty("os.name").toLowerCase();
        return !os.contains("win") && !os.equals("osx") && !isLinux(os);
    }

    public static String getOS(){
        String os = System.getProperty("os.name").toLowerCase();
        if(os.contains("win"))
            return "Windows";
        if(os.contains("mac"))
            return "Mac OS";
        return "Linux";
    }

    public static String getMainPath(){
        switch(getOS()){
            case "Windows":
                return (System.getProperty("user.home") + "/AppData/Roaming/."+name+"/").replaceAll("/", "\\\\");
            case "Mac OS":
                return "~/Library/Application Support/"+name+"/";
            default:
                String linuxMainPath = (System.getProperty("user.home") + "/."+name+"/");
                String linuxUser = System.getenv("USER");
                if(!linuxMainPath.contains("/")) {
                    String linuxHome = System.getenv("HOME");
                    if(!linuxHome.contains("/") && linuxUser.equals("root"))
                        return "/root" + "/."+name+"/";
                    else
                        return linuxHome;
                }
                return "/home/" + linuxUser + "/."+name+"/";
        }
    }

    private static String toString(String path, String[] atPaths, boolean addEnd, boolean addJar) {
        StringBuilder builder = new StringBuilder();
        for(int i=0;i < atPaths.length;i++) {
            String p = path + atPaths[i];
            builder.append(p);
            if(addJar)
                builder.append(".jar");
            if(!addEnd) {
                if (i != atPaths.length - 1) {
                    builder.append(";");
                }
            } else
                builder.append(";");
        }
        return builder.toString();
    }

    public static String getClassPath() {
        String aV = "-7.3.1";
        String[] fLibs = new String[]{
            "fabric-loader", "asm"+aV, "asm-commons"+aV, "asm-analysis"+aV, "asm-tree"+aV, "asm-util"+aV,
            "gson", "jimfs-1.1", "jsr305-3.0.2", "log4j-api", "log4j-core", "guava-28.2-jre",
            "tiny-remapper-0.2.0.52", "tiny-mappings-parser-0.2.0.11", "sponge-mixin-0.8+build.18",
            "log4j-core", "log4j-api", "fabric-loader-sat4j-2.3.5.4"
        };
        String[] libs = new String[]{
            "lwjgl", "jinput", "lwjgl_util", "json", "minecraft",
        };
        String fLibsStr = toString(getFabricPath(), fLibs, true, true);
        String libsStr = toString(getBinPath(), libs, false, true);
        return fLibsStr + libsStr;
    }

    public static String getFabricPath(){
        switch(getOS()){
            case "Windows":
                return getMainPath() + "bin\\fabric\\";
            default:
                return getMainPath() + "bin/fabric/";
        }
    }
    public static String getBinPath(){
        switch(getOS()){
            case "Windows":
                return getMainPath() + "bin\\";
            case "Mac OS":
                return getMainPath() + "/bin/";
            default:
                return getMainPath() + "bin/";
        }
    }

    public static String getLogsPath(){
        switch(getOS()){
            case "Windows":
                return getMainPath() + "logs\\";
            case "Mac OS":
                return getMainPath() + "/logs";
            default:
                return getMainPath() + "logs/";
        }
    }

    public static String getNativesPath(){
        switch(getOS()){
            case "Windows":
                return getMainPath() + "bin\\natives\\";
            case "Mac OS":
                return getMainPath() + "/bin/natives";
            default:
                return getMainPath() + "bin/natives/";
        }
    }

    public static boolean installMegaMod(String baseFolder, String modsFolder) {
        System.out.println(" ");
        boolean success = false;
        try {
            URL oracle = new URL("https://api.github.com/repos/OldHaven-Network/MegaMod-Mixins/releases");
            URLConnection uc = oracle.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String s;
            while((s = in.readLine()) != null) {
                builder.append(s);
            }
            JSONObject json = (JSONObject)new JSONArray(builder.toString()).get(0);
            String tag = (String)json.get("tag_name");
            System.out.println("  Latest MegaMod version: " + tag);
            File modDir = new File(modsFolder);
            if(!modDir.exists()) {
                modDir.mkdir();
            }
            File file = new File(modsFolder, "megaModVersion.txt");
            if(file.exists()) {
                String old = new String(Files.readAllBytes(file.toPath()));
                if(tag.equals(old)) {
                    System.out.println("  No new versions of MegaMod, You have: " + old);
                    return true;
                }
                System.out.println("  New version of MegaMod detected! You have: " + old);
            }
            boolean write = false;
            if(file.exists()) {
                boolean b = file.delete();
                if(!b)
                    System.err.println("Can't delete "+file.getAbsolutePath());
                else
                    write = true;
            } else {
                boolean b = file.createNewFile();
                if(!b)
                    System.err.println("Can't create "+file.getAbsolutePath());
                else
                    write = true;
            }
            if(write) {
                PrintWriter out = new PrintWriter(file);
                out.write(tag);
                out.close();
            }
            JSONObject assets = (JSONObject)((JSONArray)json.get("assets")).get(0);
            String url = (String)assets.get("browser_download_url");

            BufferedInputStream inputStream = new BufferedInputStream(new URL(url).openStream());
            FileOutputStream fileOS = new FileOutputStream(modsFolder + "MegaMod-Mixins.jar");
            byte[] data = new byte[1024];
            int byteContent;
            while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
                fileOS.write(data, 0, byteContent);
            }

            File savedServers = new File(baseFolder, "savedServers.txt");
            if(!savedServers.exists()) {
                PrintWriter out = new PrintWriter(file);
                out.write("OldHaven|beta.oldhaven.net");
                out.close();
            }

            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(" ");
        return success;
    }
}
