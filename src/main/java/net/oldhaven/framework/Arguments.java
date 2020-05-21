package net.oldhaven.framework;

import java.util.*;

public class Arguments {
    private List<String> args;
    private Map<String, String> argMap;
    public Arguments() {
        this.args = new ArrayList<>();
        this.argMap = new LinkedHashMap<>();
    }
    public Arguments add(String... args) {
        for(int i=0;i < args.length;i++) {
            String arg = args[i];
            if(arg.startsWith("--")) {
                argMap.put(arg.substring(2), args[i+1]);
                i++;
            }
        }
        this.args.addAll(Arrays.asList(args));
        return this;
    }
    public String getArgument(String name) {
        if(argMap.containsKey(name))
            return argMap.get(name);
        return null;
    }
    public String[] build() {
        return this.args.toArray(new String[0]);
    }
}
