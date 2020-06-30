package net.oldhaven.utility.settings;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author cutezyash
 * Taken from CustomGameSettings in MegaMod-Mixins
 *
 * Instead of using this, just use the following example:
 * int i = LaunchSettings.MEMORY_MAX.getAsInt();
 */
public class Settings {
    /**
     * Ini file given from another class
     * @see net.oldhaven.BBLauncher
     */
    public File optionsFile;
    /**
     * The HashMap for easily saving all of the settings
     */
    private LinkedHashMap<String, Object> map;

    /**
     * Loaded from another class
     * @see net.oldhaven.BBLauncher
     */
    public Settings() {
        map = new LinkedHashMap<>();
    }

    /**
     * Sets the option as on or off depending on what it already is
     * @see LaunchSettings
     * @param name Name of the option
     */
    public void setOptionBtn(String name) {
        int newValue = 1;
        if(map.containsKey(name))
            newValue = (Integer)map.get(name) == 1 ? 0 : 1;
        LaunchSettings option = LaunchSettings.getOptionByName(name);
        if(option != null)
            option.setCurrentValue(newValue);
        map.replace(name, newValue);
    }

    /**
     * Sets the option given to us
     * @see LaunchSettings
     * @param name Name of the option
     * @param obj Object to set it's value to
     */
    public void setOption(String name, Object obj) {
        LaunchSettings option = LaunchSettings.getOptionByName(name);
        if(option != null)
            option.setCurrentValue(obj);
        map.remove(name);
        map.put(name, obj);
    }

    public String getOptionS(String name) {
        Object obj = getOption(name);
        if(obj != null)
            return String.valueOf(obj);
        return null;
    }
    public Float getOptionF(String name) {
        Object obj = getOption(name);
        if(obj != null)
            return Float.valueOf(String.valueOf(obj));
        return null;
    }
    public Integer getOptionI(String name) {
        Object obj = getOption(name);
        if(obj != null)
            return Integer.parseInt(String.valueOf(obj));
        return null;
    }
    public Object getOption(String name) {
        if(!map.containsKey(name))
            return null;
        return map.get(name);
    }
    public void removeOption(String name) {
        map.remove(name);
    }

    /**
     * @see net.oldhaven.BBLauncher - Gives file instruction
     * Saves all settings to the ini
     */
    public void saveSettings() {
        try {
            PrintWriter printwriter = new PrintWriter(new FileWriter(optionsFile));
            LaunchSettings.Section currentSection = null;
            Map<String, Object> disabled = new HashMap<>();
            int i = 0;
            for(LaunchSettings.Section section : LaunchSettings.getAllSections()) {
                for(LaunchSettings option : section.getList()) {
                    if (option != null) {
                        if (option.disabled) {
                            disabled.put(option.getName(), option.getCurrentValue());
                            continue;
                        }
                        if (section != null && currentSection != section) {
                            if (i != 0)
                                printwriter.println(" ");
                            printwriter.println("[" + section.getName() + "]");
                            currentSection = section;
                        }
                        printwriter.println(option.getName() + ":" + option.getCurrentValue());
                        i++;
                    }
                }
            }
            printwriter.println(" ");
            printwriter.println("[DISABLED]");
            for(Map.Entry<String, Object> entry : disabled.entrySet()) {
                printwriter.println(entry.getKey() + ":" + entry.getValue());
            }
            printwriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see net.oldhaven.BBLauncher - Gives file instruction
     * Loads all settings from the ini
     */
    public void readSettings() {
        if(optionsFile.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(optionsFile));
                for (String s; (s = reader.readLine()) != null; ) {
                    s = s.trim();
                    if (s.startsWith("SECTION"))
                        continue;
                    String[] as = s.split(":");
                    if (as.length > 1) {
                        map.put(as[0], as[1]);
                    }
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        boolean save = false;
        /*
        * I was going to do this but you already have
        * a method defining a method for something
        * similar. Good luck.

        if(!map.containsKey("BB Version")) {
            MegaMod.hasUpdated = true;
        } else {
            if(!String.valueOf(map.get("MM Version")).equals(MegaMod.version)) {
                System.out.println(map.get("MM Version"));
                MegaMod.hasUpdated = true;
                map.replace("MM Version", LaunchSettings.getOptionByName("MM Version").getDefaultValueString());
                save = true;
            }
        }*/
        for(LaunchSettings enu : LaunchSettings.getList()) {
            String name = enu.getName();
            float f = enu.getDefaultValue();
            if(f != -1.0158F && !map.containsKey(name)) {
                if(enu.getBoolean() && !enu.getFloat())
                    map.put(name, (int)f);
                else if(enu.getFloat() && !enu.getBoolean())
                    map.put(name, f);
                else
                    map.put(name, enu.getDefaultValueString());
                save = true;
            }
        }
        for(Map.Entry<String, Object> entry : map.entrySet()) {
            String name = entry.getKey();
            LaunchSettings option = LaunchSettings.getOptionByName(name);
            if(option != null)
                option.setCurrentValue(entry.getValue());
        }
        if(save) {
            this.saveSettings();
            System.out.println("Saving settings?");
        }
    }
}
