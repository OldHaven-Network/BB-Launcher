package net.oldhaven.utility.mod;

public enum ModType {
    Fabric, ModLoader, MCP, Unknown;

    public static ModType fromString(String s) {
        for(ModType type : values()) {
            if(s.equalsIgnoreCase(type.name()))
                return type;
        }
        return null;
    }
}
