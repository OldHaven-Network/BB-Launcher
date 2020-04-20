package net.oldhaven.utility.mod;

public enum ModType {
    Fabric, ModLoader, NonFabric, Unknown;

    public static ModType fromString(String s) {
        for(ModType type : values()) {
            if(s.equalsIgnoreCase(type.name()))
                return type;
        }
        return null;
    }
}
