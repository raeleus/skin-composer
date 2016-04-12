package com.ray3k.skincomposer.data;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class FontData implements Json.Serializable {
    private String name;
    public FileHandle file;

    public FontData(String name, FileHandle file) throws NameFormatException {
        setName(name);
        this.file = file;
    }
    
    public FontData() {
        
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws NameFormatException {
        if (!validate(name)) {
            throw new NameFormatException();
        } else {
            this.name = name;
        }
    }

    @Override
    public void write(Json json) {
        json.writeValue("name", name);
        if (file != null) {
            json.writeValue("file", file.path());
        } else {
            json.writeValue("file", (String) null);
        }
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        name = jsonData.getString("name");
        if (!jsonData.get("file").isNull()) {
            file = new FileHandle(jsonData.getString("file"));
        }
    }
    
    public static class NameFormatException extends Exception {

        public NameFormatException() {
            super("Font names must be non-null and not include white spaces, start with a digit, or include non alpha-numeric characters");
        }
        
    }

    @Override
    public String toString() {
        return name;
    }
    
    public static boolean validate(String name) {
        return name != null && !name.matches("^\\d.*|^-.*|.*\\s.*|.*[^a-zA-Z\\d\\s-_].*|^$");
    }
    
    public static String filter(String input) {
        input = input.replaceFirst("^\\d+", "");
        input = input.replaceAll("\\s", "_");
        input = input.replaceAll("[^a-zA-Z\\d\\s-_]+", "");
        return input;
    }

    @Override
    public boolean equals(Object obj) {
        boolean returnValue = false;
        if (obj instanceof FontData) {
            FontData other = (FontData) obj;
            if (other.name.equals(this.name)) {
                returnValue = true;
            }
        }
        
        return returnValue;
    }
}
