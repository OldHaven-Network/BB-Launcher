package net.oldhaven.utility.lang;

import net.oldhaven.BBLauncher;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class LanguageUtil {
    private static String langName = "English";
    private static String langSelf = "Language";
    private static String langRegion = "??";
    private static String langCode = "??_??";
    private static String langTranslator = "N/A";
    private static Map<String/*key*/, String/*lang*/> lang = loadLang("de_de");
    public static Map<String, String> loadLang(String name) {
        Map<String, String> map = new HashMap<>();
        try {
            InputStream is = BBLauncher.class.getResourceAsStream("/lang/" + name + ".lang");
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
            String line;
            while ((line = br.readLine()) != null) {
                if(!line.isEmpty()) {
                    String[] split = line.split("=");
                    if(split.length < 2)
                        continue;
                    if(split[0].startsWith("language.")) {
                        String[] dot = split[0].split("\\.");
                        switch(dot[1]) {
                            case "name":
                                langName = split[1];break;
                            case "region":
                                langRegion = split[1];break;
                            case "self":
                                langSelf = split[1];break;
                            case "code":
                                langCode = split[1];break;
                            case "":

                        }
                    } else
                        map.put(split[0], split[1]);
                }
            }
            br.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    static Map<String, String> getLangMap() {
        return lang;
    }

    public static String getLangRegion() {
        return langRegion;
    }
    public static String getLangName() {
        return langName;
    }
    public static String getLangSelf() {
        return langSelf;
    }
    public static String getLangCode() {
        return langCode;
    }
}
