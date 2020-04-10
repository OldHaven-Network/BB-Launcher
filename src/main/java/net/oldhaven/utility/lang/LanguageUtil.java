package net.oldhaven.utility.lang;

import net.oldhaven.Main;
import net.oldhaven.framework.Install;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class LanguageUtil {
    private static String langName = "en_us";
    private static String region = "US";
    private static Map<String/*key*/, String/*lang*/> lang = loadLang("en_us");
    public static Map<String, String> loadLang(String name) {
        File file = new File(Main.class.getResource("/lang/" + name + ".lang").getFile());
        FileInputStream fstream = null;
        Map<String, String> map = new HashMap<>();
        try {
            fstream = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String line;
            while ((line = br.readLine()) != null) {
                if(!line.isEmpty()) {
                    String[] split = line.split("=");
                    if(split.length < 2)
                        continue;
                    if(split[0].startsWith("language.")) {
                        String[] dot = split[0].split("\\.");
                        if(dot[1].equals("name"))
                            langName = split[1];
                        if(dot[1].equals("region"))
                            region = split[1];
                    } else
                        map.put(split[0], split[1]);
                }
            }
            br.close();
            fstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    static Map<String, String> getLangMap() {
        return lang;
    }

    public static String getRegion() {
        return region;
    }
    public static String getLangName() {
        return langName;
    }
}
