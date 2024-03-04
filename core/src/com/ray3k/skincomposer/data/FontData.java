/*******************************************************************************
 * MIT License
 * 
 * Copyright (c) 2024 Raymond Buckley
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package com.ray3k.skincomposer.data;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class FontData implements Json.Serializable {
    private String name;
    private int scaling = -1;
    private boolean markupEnabled;
    private boolean flip;
    public FileHandle file;

    public FontData(String name, int scaling, boolean markupEnabled, boolean flip, FileHandle file) throws NameFormatException {
        setName(name);
        this.scaling = scaling;
        this.markupEnabled = markupEnabled;
        this.flip = flip;
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
    
    public int getScaling() {
        return scaling;
    }
    
    public void setScaling(int scaling) {
        this.scaling = scaling;
    }
    
    public boolean isMarkupEnabled() {
        return markupEnabled;
    }
    
    public void setMarkupEnabled(boolean markupEnabled) {
        this.markupEnabled = markupEnabled;
    }
    
    public boolean isFlip() {
        return flip;
    }
    
    public void setFlip(boolean flip) {
        this.flip = flip;
    }
    
    @Override
    public void write(Json json) {
        json.writeValue("name", name);
        json.writeValue("flip", flip);
        json.writeValue("markupEnabled", markupEnabled);
        json.writeValue("scaling", scaling);
        if (file != null) {
            json.writeValue("file", file.path());
        } else {
            json.writeValue("file", (String) null);
        }
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        name = jsonData.getString("name");
        flip = jsonData.getBoolean("flip", false);
        markupEnabled = jsonData.getBoolean("markupEnabled", false);
        scaling = jsonData.getInt("scaling", -1);
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
        return name != null && !name.matches("^\\d.*|^-.*|.*\\s.*|.*[^a-zA-Z\\d\\s-_ñáéíóúüÑÁÉÍÓÚÜ].*|^$");
    }
    
    public static String filter(String input) {
        input = input.replaceFirst("^\\d+", "");
        input = input.replaceAll("\\s", "_");
        input = input.replaceAll("[^a-zA-Z\\d\\s-_ñáéíóúüÑÁÉÍÓÚÜ]+", "");
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
