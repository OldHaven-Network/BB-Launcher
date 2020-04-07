package net.oldhaven.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Set;

public class JsonConfig {
    private static final Type READ_TYPE = new TypeToken<JsonObject>() {}.getType();

    private JsonObject json;
    private File file;

    public JsonConfig(String filePath) {
        this.file = new File(filePath);
        if(this.file.exists()) {
            Gson gson = new Gson();
            try {
                JsonReader reader = new JsonReader(new FileReader(filePath));
                this.json = gson.fromJson(reader, READ_TYPE);
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            this.json = new JsonObject();
        }
    }

    /*
    public JsonConfig(String filePath) {
        this.file = new File(filePath);
        if(this.file.exists()) {
            Gson gson = new Gson();
            JsonReader reader = null;
            try {
                reader = new JsonReader(new FileReader(filePath));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            assert reader != null;
            this.json = gson.fromJson(reader, READ_TYPE);
        } else {
            this.json = new JsonObject();
            this.save();
        }
    }

     */

    public void clear() {
        this.json = new JsonObject();
    }

    public File getFile() {
        return file;
    }
    public JsonObject getJson() {
        return this.json;
    }

    public boolean hasProperty(String name) {
        return json.has(name);
    }

    public void setProperty(String name, JsonElement e) {
        json.add(name, e);
    }

    public void save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(this.json);
        try {
            PrintWriter out = new PrintWriter(this.file);
            out.write(json);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
