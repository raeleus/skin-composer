package com.ray3k.skincomposer.data;

import com.badlogic.gdx.graphics.Color;

public class ColorData {
    public Color color;
    private String name;

    public ColorData(String name, Color color) throws NameFormatException {
        setName(name);
        this.color = color;
    }
    
    public ColorData() {
        
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
    
    public static class NameFormatException extends Exception {

        public NameFormatException() {
            super("Color names must be non-null and not include white spaces, start with a digit, or include non alpha-numeric characters");
        }
        
    }

    @Override
    public String toString() {
        return name;
    }
    
    public static boolean validate(String name) {
        return name != null && !name.matches("^\\d.*|^-.*|.*\\s.*|.*[^a-zA-Z\\d\\s-_].*|^$");
    }
}
