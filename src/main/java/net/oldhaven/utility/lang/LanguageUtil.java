package net.oldhaven.utility.lang;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class LanguageUtil {
    private static String langName = "English";
    private static String langSelf = "Language";
    private static String langRegion = "US";
    private static String langCode = "en_us";
    private static Map<String/*key*/, String/*lang*/> lang = loadLang("ru_ru");
    public static Map<String, String> loadLang(String name) {
        File file = new File(LanguageUtil.class.getResource("/lang/" + name + ".lang").getFile());
        FileInputStream fstream = null;
        Map<String, String> map = new HashMap<>();
        try {
            fstream = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream, "utf-8"));
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
                        }
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
