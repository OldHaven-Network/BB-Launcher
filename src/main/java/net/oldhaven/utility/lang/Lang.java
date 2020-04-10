package net.oldhaven.utility.lang;

public enum Lang {
    BRAND_NAME,

    LANG_UNKNOWN,

    ALERT_ERROR,

    OS_UNKNOWN,
    OS_SUPPORTED,

    BTN_OK,
    BTN_CANCEL;

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
    public String translateArgs(String... args) {
        String translate = get(this);
        for(int i=0;i < args.length;i++) {
            String toReplace = "%"+(i+1)+"%";
            translate = translate.replaceAll(toReplace, args[i]);
        }
        return translate;
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
