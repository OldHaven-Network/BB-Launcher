package net.oldhaven.utility.lang;

public enum Lang {
    LANGUAGE_NAME,
    LANGUAGE_REGION,
    LANGUAGE_CODE,
    LANGUAGE_SELF,
    LANGUAGE_UNKNOWN,

    BRAND_NAME,

    OS_UNKNOWN,
    OS_SUPPORTED,

    CONSOLE_MEGAMOD_NONE,
    CONSOLE_MEGAMOD_LATEST,

    ALERT_TYPES_ERROR,
    ALERT_TYPES_WARNING,
    ALERT_MCRUNNING_HEADER,
    ALERT_MCRUNNING_FOOTER,
    ALERT_BUTTONS_CANCEL,
    ALERT_BUTTONS_YES,
    ALERT_BUTTONS_NO,
    ALERT_BUTTONS_OK,

    LOGIN_SELF,
    LOGIN_TEXT,
    LOGIN_FIELD_USERNAME,
    LOGIN_FIELD_PASSWORD,
    LOGIN_SAVEDACC,
    LOGIN_REMEMBER,
    LOGIN_ERROR,

    MAIN_SELF,
    MAIN_LOGIN,
    MAIN_VERSION_SELECTED,
    MAIN_VERSION_MANAGER,
    MAIN_LAUNCH,

    MODS_SELF,
    MODS_BUTTONS_ADD,
    MODS_BUTTONS_REMOVE,
    MODS_TYPES_MODLOADER,
    MODS_TYPES_FABRIC,
    MODS_TYPES_MCP,
    MODS_TYPES_NONFABRIC,
    MODS_PATH,
    MODS_TYPE,

    SETTINGS_SELF,
    SETTINGS_MEMORY_MAX,
    SETTINGS_MEMORY_MIN,
    SETTINGS_BACKGROUND_CHANGE,
    SETTINGS_BACKGROUND_RESET,
    SETTINGS_OPENFOLDER,

    PROCESS_SELF,
    PROCESS_BUTTONS_RESTART,
    PROCESS_BUTTONS_KILL,
    PROCESS_BUTTONS_CLEARLOG,
    PROCESS_LOG_MAX;

    private String key;
    Lang() {
        this.key = this.name().replace("_", ".").toLowerCase();
    }

    public String getKey() {
        return key;
    }

    public String translate() {
        return get(this);
    }
    public String translateArgs(Object... args) {
        String translate = get(this);
        for(int i=0;i < args.length;i++) {
            String toReplace = "%"+(i+1)+"%";
            translate = translate.replaceAll(toReplace, String.valueOf(args[i]));
        }
        return translate;
    }

    @Override
    public String toString() {
        return this.translate();
    }

    public static String get(Lang enu) {
        return get(enu.getKey());
    }
    public static String get(String key) {
        if(LanguageUtil.getLangMap().containsKey(key))
            return LanguageUtil.getLangMap().get(key);
        System.out.println("Unknown key '"+key+"' in LANG file");
        return "Unknown key '"+key+"' in LANG file";
    }
}
