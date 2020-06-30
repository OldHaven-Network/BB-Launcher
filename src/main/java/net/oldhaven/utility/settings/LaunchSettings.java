package net.oldhaven.utility.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cutezyash
 * @see Settings - The controller of this class
 * Taken from ModOptions in MegaMod-Mixins
 */
public enum LaunchSettings {
    SELECTED_VERSION(new LaunchOption("Selected Version", "Self", false), "b173", 0, 7),

    JAVA_LOC(new LaunchOption("Java Location", "Java", false), System.getProperty("java.home"), 0, 250),

    MEMORY_MIN(new LaunchOption("Memory_MIN", "Memory", false), true, false, 256),
    MEMORY_MAX(new LaunchOption("Memory_MAX", "Memory", false), true, false, 1028);
    
    private Section currentSection = null;
    public static class Section {
        private List<LaunchSettings> list;
        private String name;
        Section(String name) {
            list = new ArrayList<>();
            this.name = name;
        }
        Section add(LaunchSettings option) {
            this.list.add(option);
            return this;
        }
        public List<LaunchSettings> getList() {
            return list;
        }
        public String getName() {
            return name;
        }
    }
    public boolean disabled;
    private LaunchSettings setDisabled() {
        this.disabled = true;
        return this;
    }
    public static List<Section> getAllSections() {
        return LaunchOption.sectionList;
    }
    public Section getSection() {
        return currentSection;
    }
    public static Section getSectionByName(String name) {
        if(!LaunchOption.sectionMap.containsKey(name))
            return null;
        return LaunchOption.sectionMap.get(name);
    }
    public static Map<String, Section> getModOption() {
        return LaunchOption.sectionMap;
    }

    public static LaunchSettings getOptionByName(String name) {
        return LaunchOption.mapToList.get(name);
    }

    private final boolean isFloat;
    private final boolean isBoolean;
    private final String name;
    private int defaultValueInt = -83;
    private float defaultValue = -1.0158F;
    private String defaultValueString = "";
    private String startString = "OFF";
    private float start = 0.0F;
    private float times = 100.0F;
    private String slideEnd = "%";
    private float add = 0.0F;
    private String[] values = null;
    private int minString = 0;
    private int maxString = 20;
    private int ordinal = 0;
    private Object currentValue;

    public static List<LaunchSettings> getList() {
        return LaunchOption.list;
    }

    private LaunchSettings(LaunchOption launchOption, boolean floa, boolean bool) {
        this.name = launchOption.getName();
        this.isFloat = floa;
        this.isBoolean = bool;
        update(this.name);
        String name = launchOption.getSection();
        if(LaunchOption.sectionMap.containsKey(name))
            LaunchOption.sectionMap.get(name).list.add(this);
        else
            LaunchOption.sectionMap.put(name, new Section(name).add(this));
        if(!LaunchOption.sectionList.contains(LaunchOption.sectionMap.get(name)))
            LaunchOption.sectionList.add(LaunchOption.sectionMap.get(name));
        this.ordinal = LaunchOption.sectionMap.get(name).list.size();
        this.currentSection = LaunchOption.sectionMap.get(name);
        if(launchOption.disabled == null || launchOption.isDisabled()) {
            launchOption.disabled = true;
            this.setDisabled();
        }
    }
    private void update(String name) {
        LaunchOption.mapToList.put(name, this);
        LaunchOption.list.remove(this);
        LaunchOption.list.add(this);
    }
    private LaunchSettings(LaunchOption launchOption, boolean floa, boolean bool, int valueInt) {
        this(launchOption, floa, bool);
        this.defaultValue = valueInt;
    }
    private LaunchSettings(LaunchOption launchOption, boolean floa, boolean bool, float valueFloat) {
        this(launchOption, floa, bool);
        this.defaultValue = valueFloat;
    }
    private LaunchSettings(LaunchOption launchOption, String valueString, int min, int max) {
        this(launchOption, false, false);
        this.defaultValueString = valueString;
        this.defaultValue = 0.0F;
        this.minString = min;
        this.maxString = max;
    }
    private LaunchSettings(LaunchOption launchOption, boolean floa, boolean bool, String startString, Float times, String end, Float add, float defaultValue) {
        this(launchOption, floa, bool);
        this.startString = startString;
        if(times != null)
            this.times = times;
        if(end != null)
            this.slideEnd = end;
        if(add != null)
            this.add = add;
        this.defaultValue = defaultValue;
    }
    private LaunchSettings(LaunchOption launchOption, boolean floa, boolean bool, String startString, Float times, String end, Float add, float defaultValue, String[] values) {
        this(launchOption, floa, bool, startString, times, end, add, defaultValue);
        this.values = values;
    }

    public Object getCurrentValue() {
        return currentValue;
    }
    public String getStringValue() {
        try {
            float f = Float.parseFloat(String.valueOf(this.currentValue));
            return values[(int) (f * this.times)];
        } catch(NumberFormatException ignored) { }
        try {
            int i = Integer.parseInt(String.valueOf(this.currentValue));
            return values[(int) (i * this.times)];
        } catch(NumberFormatException ignored) { }
        return String.valueOf(this.currentValue);
    }
    public String getAsString() {
        return String.valueOf(currentValue);
    }
    public int getAsInt() {
        return Integer.parseInt(getAsString());
    }
    public float getAsFloat() {
        return Float.parseFloat(getAsString());
    }

    void setCurrentValue(Object currentValue) {
        this.currentValue = currentValue;
    }

    public int getMaxString() {
        return maxString;
    }

    public int getMinString() {
        return minString;
    }

    public String[] getValues() {
        return values;
    }

    public boolean getFloat() {
        return this.isFloat;
    }

    public boolean getBoolean() {
        return this.isBoolean;
    }

    public int getOrdinal() {
        return this.ordinal;
    }

    public String getName() {
        return this.name;
    }

    public String getSlideEnd() {
        return slideEnd;
    }

    public float getTimes() {
        return times;
    }

    public String getStartString() {
        return startString;
    }

    public float getAdd() { return add; }

    public String getDefaultValueString() {
        return defaultValueString;
    }

    public float getDefaultValue() {
        return defaultValue;
    }

    static class LaunchOption {
        static Map<String, LaunchSettings> mapToList = new HashMap<>();
        static List<LaunchSettings> list = new ArrayList<>();
        static List<Section> sectionList = new ArrayList<>();
        static Map<String, Section> sectionMap = new HashMap<>();

        private String name;
        private String section;
        private Boolean disabled;
        public LaunchOption(String s, String section, Boolean disabled) {
            this.name = s;
            this.section = section;
            this.disabled = disabled;
        }


        public String getName() {
            return name;
        }

        public String getSection() {
            return section;
        }

        public Boolean isDisabled() {
            return disabled;
        }
    }
}
