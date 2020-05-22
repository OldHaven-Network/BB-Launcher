package net.oldhaven.framework;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import javafx.scene.control.*;
import net.lingala.zip4j.ZipFile;
import net.oldhaven.utility.Launcher;
import net.oldhaven.utility.enums.Version;
import net.oldhaven.utility.mod.ModType;
import net.oldhaven.utility.mod.Mods;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class Install {

    // NOTE: Mac OS and Linux support is untested.
    // Confidence of Mac OS working: low.
    // Confidence of Linux working: high.

    private static String name = "OHLauncher";

    private static boolean isLinux(String os) {
        return os.contains("nix") || os.contains("nux") || os.contains("aix");
    }

    // Not gonna lie, this function is pretty garbage
    public static boolean isOSUnknown() {
        String os = System.getProperty("os.name").toLowerCase();
        return !os.contains("win") && !os.contains("mac") && !isLinux(os);
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
                return System.getProperty("user.home") + "/Library/Application%20Support/"+name+"/";
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

    public static String getMinecraftPath() {
        String path;
        if ("Windows".equals(getOS()))
            path = getVersionPath() + Version.selectedVersion.getName() +  "\\";
        else
            path = getVersionPath() + Version.selectedVersion.getName() + "/";
        File file = new File(path);
        if(!file.exists())
            file.mkdirs();
        return path;
    }

    public static String getVersionPath() {
        String path;
        if ("Windows".equals(getOS()))
            path = getMainPath() + "versions\\";
        else
            path = getMainPath() + "versions/";
        File file = new File(path);
        if(!file.exists())
            file.mkdirs();
        return path;
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
                    if(getOS().equals("Windows")) {
                        builder.append(";");
                    } else {
                        builder.append(":");
                    }
                }
            } else
                if(getOS().equals("Windows")) {
                    builder.append(";");
                } else {
                    builder.append(":");
                }
        }
        return builder.toString();
    }

    public static String getClassPath(Version version) {
        List<File> files = version.getFabricLibs();
        List<String> fileNames = new ArrayList<>();
        for(File file : files) {
            fileNames.add(file.getName());
        }
        /*String aV = "-7.3.1";
        String[] fLibs = new String[]{
            "fabric-loader", "asm"+aV, "asm-commons"+aV, "asm-analysis"+aV, "asm-tree"+aV, "asm-util"+aV,
            "gson", "jimfs-1.1", "jsr305-3.0.2", "log4j-api", "log4j-core", "guava-28.2-jre",
            "tiny-remapper-0.2.0.52", "tiny-mappings-parser-0.2.0.11", "sponge-mixin-0.8+build.18",
            "log4j-core", "log4j-api", "fabric-loader-sat4j-2.3.5.4"
        };*/
        String[] fLibs = fileNames.toArray(new String[0]);
        String[] libs = new String[]{
            "lwjgl", "jinput", "lwjgl_util", "json", "minecraft",
        };
        String fLibsStr = toString(getFabricPath(), fLibs, true, false);
        String libsStr = toString(getBinPath(), libs, false, true);
        return fLibsStr + libsStr;
    }

    public static String getFabricPath() {
        if ("Windows".equals(getOS()))
            return getMinecraftPath() + "bin\\fabric\\";
        return getMinecraftPath() + "bin/fabric/";

    }
    public static String getBinPath(){
        if ("Windows".equals(getOS())) {
            return getMinecraftPath() + "bin\\";
        }
        return getMinecraftPath() + "bin/";
    }

    public static String getLogsPath(){
        if ("Windows".equals(getOS())) {
            return getMainPath() + "logs\\";
        }
        return getMainPath() + "logs/";
    }

    public static String getNativesPath(){
        if ("Windows".equals(getOS())) {
            return getMinecraftPath() + "bin\\natives\\";
        }
        return getMinecraftPath() + "bin/natives/";
    }

    public static boolean installSavedServers(String baseFolder) {
        File savedServers = new File(baseFolder, "savedServers.txt");
        if(!savedServers.exists()) {
            try {
                if(savedServers.createNewFile()) {
                    PrintWriter out = new PrintWriter(savedServers);
                    out.write("OldHaven|beta.oldhaven.net");
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public static void installDefaultMods() {
        File optifineZipFile = new File(Install.getMinecraftPath() + "mods/non-fabric/OptiFine.zip");
        File reiminimapZipFile = new File(Install.getMinecraftPath() + "mods/non-fabric/ReiMinimap.zip");
        try {
            if(!optifineZipFile.exists()) {
                URL optifineZipURL = new URL("https://www.oldhaven.net/resources/launcher/OptiFine.zip");
                getFileFromURL(optifineZipURL, optifineZipFile.toString());
            }
            if(!reiminimapZipFile.exists()) {
                URL reiminimapZipURL = new URL("https://www.oldhaven.net/resources/launcher/ReiMinimap.zip");
                getFileFromURL(reiminimapZipURL, reiminimapZipFile.toString());
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void installAetherMP() {
        GitHubAPI gitHubAPI = GitHubAPI.openURL("https://api.github.com/repos/OldHaven-Network/AetherMP/releases");
        File versionFile = new File(Install.getMinecraftPath() + "mods/version.txt");
        System.out.println(" ");
        String tag = gitHubAPI.getTag();
        try {
            if (versionFile.exists()) {
                String old = new String(Files.readAllBytes(versionFile.toPath()));
                if(tag == null || tag.equals(old)) {
                    if(tag == null)
                        System.out.println("  Can't access newest version of AetherMP. Are you rate-limited?");
                    System.out.println("  No new versions of AetherMP, You have: " + old);
                    Launcher.launch();
                    return;
                }
                System.out.println("  New version of AetherMP detected! You have: " + old);
                gitHubAPI.updateText = "Updating...";
            } else
                gitHubAPI.updateText = "Installing...";
            PrintWriter out = new PrintWriter(versionFile);
            out.write(tag);
            out.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        System.out.println(" ");
        gitHubAPI.writeToFile(Install.getMainPath() + "temp/aethermp.zip", () -> {
            File aetherMpFile = new File(Install.getMainPath() + "temp/aethermp.zip");
            File tempFolder = new File(Install.getMainPath() + "temp/");
            try {
                ZipFile aetherMpZipFile = new ZipFile(aetherMpFile);
                aetherMpZipFile.extractAll(tempFolder.toString());

                aetherMpFile.delete();

                File[] fileArray = tempFolder.listFiles();
                assert fileArray != null;
                for (File file : fileArray) {
                    if (file.getAbsolutePath().contains("Aether")) {
                        FileUtils.copyDirectory(new File(file.getAbsolutePath() + "/.minecraft"),
                                new File(Install.getMinecraftPath()));
                        String jarmodsJson = Install.readJson(new File(file.getAbsolutePath() + "/mmc-pack.json")).toString();
                        JsonObject jsonObject = JsonParser.parseString(jarmodsJson).getAsJsonObject();
                        JsonArray jsonArray = jsonObject.getAsJsonArray("components");

                        for (int i = 0; i < jsonArray.size(); i++) {
                            jsonObject = jsonArray.get(i).getAsJsonObject();
                            String uid = jsonObject.get("uid").getAsString();
                            String cachedName = jsonObject.get("cachedName").getAsString();
                            if (uid.contains("org.multimc.jarmod")) {
                                uid = uid.replace("org.multimc.jarmod.", "") + ".jar";
                                FileUtils.copyFile(new File(file.getAbsolutePath() + "/jarmods/" + uid),
                                        new File(Install.getMinecraftPath() + "/mods/non-fabric/" + uid));
                                Mods.getModSectionByName("CustomMods").addMod(ModType.NonFabric, cachedName, Install.getMinecraftPath() + "mods/non-fabric/"
                                        + uid, true);
                                Mods.saveMods();
                            }
                        }
                        file.delete();
                    }
                }
                Launcher.launch();
            } catch (IOException e) {
                e.printStackTrace();
            }
            tempFolder.delete();
        });
    }

    private static String convertFabricResource(String name) {
        String origin = "https://maven.fabricmc.net/";
        String url = name;
        String[] split = url.split(":");
        String classPath = split[0].replace(".", "/");
        url = origin + classPath + "/" + split[1] + "/" + split[2] + "/" + split[1] + "-" + split[2] + ".jar";
        return url;
    }

    private static Gson gson = new Gson();
    public static void installMinecraft(Version version) {
        try {
            if(!Files.exists(Paths.get(Install.getMainPath() + "mods/non-fabric/")))
                Files.createDirectories(Paths.get(Install.getMinecraftPath() + "mods/non-fabric/"));
            if(!Files.exists(Paths.get(Install.getBinPath() + "lwjgl.jar"))) {
                URL binZipURL = new URL("https://www.oldhaven.net/resources/launcher/bin.zip");
                File binZipFile = new File(Install.getMainPath() + "temp/bin.zip");
                getFileFromURL(binZipURL, binZipFile.toString());
                ZipFile binZip = new ZipFile(binZipFile);
                binZip.extractAll(Install.getBinPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String bin = Install.getBinPath() + "fabric/";
        if(!Files.exists(Paths.get(bin)) || !Files.isDirectory(Paths.get(bin))) {
            try {
                Files.createDirectories(Paths.get(bin));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        JsonObject jsonObject = gson.fromJson(
                new InputStreamReader(Install.class.getResourceAsStream("/fabric/"+version.getJsonFile())),
                JsonObject.class);
        if(!Files.exists(Paths.get(Install.getBinPath() + "minecraft.jar"))) {
            JsonObject jarObj = jsonObject.get("mainJar").getAsJsonObject();
            String mcUrl = jarObj.get("url").getAsString();
            try {
                getFileFromURL(new URL(mcUrl), Install.getBinPath() + "minecraft.jar");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        JsonArray libs = jsonObject.get("libraries").getAsJsonArray();
        for(int i=0;i < libs.size();i++) {
            JsonObject lib = libs.get(i).getAsJsonObject();
            String compressedPath = lib.get("name").getAsString();
            String url = lib.get("url").getAsString();
            String[] split = compressedPath.split(":");
            String first = split[0].replaceAll("\\.", "/");
            String second = split[1] + "/" + split[2];
            String fileName = split[1] + "-" + split[2] + ".jar";
            String fullUrl = url + first + "/" + second + "/" + fileName;
            File file = new File(bin+fileName);
            if(!file.exists()) {
                try {
                    getFileFromURL(new URL(fullUrl), bin + fileName);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
            version.addFabricLib(fileName, file);
        }
    }

    public static void checkLauncherUpdate() {

        // This is a very dirty way of setting a version, I know. I'll change it later. Maybe.
        String currentVersion = "0.2.0";

        try {
            URL oracle = new URL("https://api.github.com/repos/OldHaven-Network/BB-Launcher/releases");
            URLConnection uc = oracle.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String s;
            while ((s = in.readLine()) != null) {
                builder.append(s);
            }
            JSONObject json = (JSONObject) new JSONArray(builder.toString()).get(0);
            String tag = (String) json.get("tag_name");

            if(!currentVersion.equals(tag) && !tag.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Launcher update");
                alert.setHeaderText("A new version of the launcher has been released!");
                alert.setContentText("Your current version is " + currentVersion + ", the latest version is " + tag + ". " +
                        "\nWould you like to open your browser to grab the latest release?");

                ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
                ButtonType no = new ButtonType("No", ButtonBar.ButtonData.NO);
                alert.getButtonTypes().setAll(yes, no);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == yes) {
                    Desktop.getDesktop().browse(new URL("https://github.com/OldHaven-Network/BB-Launcher/releases").toURI());
                    System.exit(0);
                }
            }

        } catch (IOException | URISyntaxException e) {

        }
    }

    public static void installMegaMod(final String modsFolder) {
        System.out.println(" ");
        GitHubAPI gitHubAPI = GitHubAPI.openURL("https://api.github.com/repos/OldHaven-Network/MegaMod-Mixins/releases");
        final String tag = gitHubAPI.getTag();
        System.out.println("  Latest MegaMod version: " + tag);
        try {
            File modDir = new File(modsFolder);
            if (!modDir.exists()) {
                modDir.mkdir();
            }
            final File mm = new File(modsFolder + "MegaMod-Mixins.jar");
            final File file = new File(modsFolder, "megaModVersion.txt");
            if (file.exists()) {
                String old = new String(Files.readAllBytes(file.toPath()));
                if (tag.equals(old) && mm.exists()) {
                    System.out.println("  No new versions of MegaMod, You have: " + old);
                    return;
                }
                System.out.println("  New version of MegaMod detected! You have: " + old);
            }
            gitHubAPI.writeToFile(modsFolder + "MegaMod-Mixins.jar", () -> {
                try {
                    boolean write = false;
                    if (file.exists()) {
                        boolean b = file.delete();
                        if (!b)
                            System.err.println("Can't delete " + file.getAbsolutePath());
                        else
                            write = true;
                    } else {
                        boolean b = file.createNewFile();
                        if (!b)
                            System.err.println("Can't create " + file.getAbsolutePath());
                        else
                            write = true;
                    }
                    if (write) {
                        PrintWriter out = new PrintWriter(file);
                        out.write(tag);
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(" ");
    }

    public static void setCurrentUser(String s) {
        File file = new File(getMainPath() + "currentuser.txt");
        if(s == null) {
            if(file.exists())
                file.delete();
            return;
        }
        try {
            PrintWriter usernameWriter = new PrintWriter(file);
            usernameWriter.println(s);
            usernameWriter.close();
        } catch (Exception ex2) {
            ex2.printStackTrace();
        }
    }

    public static void getFileFromURL(URL url, String targetPath) {
        try {
            URLConnection urlConnection = url.openConnection();
            urlConnection.addRequestProperty("User-Agent", "Mozilla/5.0");
            InputStream inputStream = urlConnection.getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream(targetPath);
            IOUtils.copy(inputStream, fileOutputStream);
            IOUtils.close(urlConnection);
            fileOutputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Type type = new TypeToken<Map<String, List<String>>>() {}.getType();
    private static StringBuilder readJson(File file) {
        StringBuilder json = new StringBuilder();
        try {
            FileInputStream fstream = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String line;
            while ((line = br.readLine()) != null) {
                json.append(line);
            }
            br.close();
            fstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }
}
