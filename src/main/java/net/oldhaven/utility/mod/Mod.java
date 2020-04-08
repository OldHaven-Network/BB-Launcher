package net.oldhaven.utility.mod;

import java.io.File;

public class Mod {
    private final File file;
    private final String name;
    private final ModType type;
    private ModSection section;
    private boolean enabled = false;
    Mod(ModType type, String name, String path) {
        this.type = type;
        this.name = name;
        this.file = new File(path);
    }

    Mod(ModType type, ModSection section, String name, String path) {
        this(type, name, path);
        this.section = section;
    }

    public ModType getType() {
        return type;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    public boolean isEnabled() {
        return enabled;
    }

    public ModSection getSection() {
        return section;
    }

    public String getName() {
        return name;
    }
    public File getFile() {
        return file;
    }
}
