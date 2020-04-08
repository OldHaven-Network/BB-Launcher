package net.oldhaven.utility.mod;

import com.google.gson.JsonObject;
import net.oldhaven.framework.Install;
import net.oldhaven.utility.JsonConfig;

import java.util.*;

public class Mods {
    static List<Mod> mods = new LinkedList<>();
    static List<ModSection> sections = new LinkedList<>();
    static JsonConfig config = new JsonConfig(Install.getMainPath() + "mods.json");

    public static boolean shouldUpdate = false;

    public static Mod addMod(ModType type, String name, String path, boolean defaultEnabled) {
        Mod mod = new Mod(type, name, path);
        if(!config.hasProperty(name)) {
            mod.setEnabled(defaultEnabled);
            shouldUpdate = true;
        } else {
            JsonObject json = config.getJson();
            JsonObject object = ((JsonObject)json.get(name));
            boolean enabled = object.get("Enabled").getAsBoolean();
            mod.setEnabled(enabled);
        }
        mods.add(mod);
        return mod;
    }
    public static ModSection addModSection(String name) {
        ModSection section = new ModSection(name);
        if(config.hasProperty(name)) {
            JsonObject json = config.getJson();
            JsonObject object = ((JsonObject)json.get(name));
            for(String key : object.keySet()) {
                JsonObject modObj = ((JsonObject)object.get(key));
                String path = modObj.get("Path").getAsString();
                boolean enabled = modObj.get("Enabled").getAsBoolean();
                ModType type = ModType.fromString(modObj.get("Type").getAsString());
                section.addMod(type, key, path, enabled);
            }
        } else
            shouldUpdate = true;
        sections.add(section);
        return section;
    }
    public static void removeMod(Mod mod) {
        mods.remove(mod);
    }

    public static Mod getModByName(String name) {
        for(Mod mod : mods) {
            if(mod.getName().equalsIgnoreCase(name))
                return mod;
        }
        return null;
    }
    public static ModSection getModSectionByName(String name) {
        for(ModSection section : sections) {
            if(section.getName().equalsIgnoreCase(name))
                return section;
        }
        return null;
    }

    public static List<Mod> getMods() {
        return mods;
    }
    public static List<ModSection> getSections() {
        return sections;
    }

    public static void saveMods() {
        config.clear();
        for(ModSection section : sections)
            config.setProperty(section.getName(), section.toJson());
        for(Mod mod : mods) {
            if(mod.getSection() != null)
                /* Mod belongs to a section */
                continue;
            JsonObject object = new JsonObject();
            object.addProperty("Path", mod.getFile().getAbsolutePath());
            object.addProperty("Enabled", mod.isEnabled());
            object.addProperty("Type", mod.getType().name());
            config.setProperty(mod.getName(), object);
        }
        config.save();
        shouldUpdate = false;
    }
}
