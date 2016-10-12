/*******************************************************************************
 * MIT License
 * 
 * Copyright (c) 2016 Raymond Buckley
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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ray3k.skincomposer.utils.Utils;

public class DrawableData implements Json.Serializable{

    public static String proper(String name) {
        return name.replaceFirst("(\\.9)?\\.[a-zA-Z0-9]*$", "");
    }

    public static boolean validate(String name) {
        return name != null && !name.matches("^\\d.*|^-.*|.*\\s.*|.*[^a-zA-Z\\d\\s-_].*|^$");
    }
    
    public FileHandle file;
    public Color bgColor;
    public boolean visible;
    public Color tint;
    public String tintName;
    public String name;

    public DrawableData(FileHandle file) {
        this.file = file;
        Color temp = Utils.averageEdgeColor(file);
        if (Utils.brightness(temp) > .5f) {
            bgColor = Color.BLACK;
        } else {
            bgColor = Color.WHITE;
        }
        visible = true;
        this.name = proper(file.name());
    }
    
    public DrawableData() {
        
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        boolean returnValue = false;
        if (obj instanceof DrawableData) {
            DrawableData dd = (DrawableData) obj;

            if (dd.file.equals(file) && ((tint == null && dd.tint == null) || (tint != null && tint.equals(dd.tint))) && ((tintName == null && dd.tintName == null) || (tintName != null && tintName.equals(dd.tintName)))) {
                returnValue = true;
            }
        }
        return returnValue;
    }

    @Override
    public void write(Json json) {
        if (file != null) {
            json.writeValue("file", file.path());
        } else {
            json.writeValue("file", (String) null);
        }
        json.writeValue("bgColor", bgColor);
        json.writeValue("visible", visible);
        json.writeValue("tint", tint);
        json.writeValue("tintName", tintName);
        json.writeValue("name", name);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        if (!jsonData.get("file").isNull()) {
            file = new FileHandle(jsonData.getString("file"));
        }
        bgColor = json.readValue("bgColor", Color.class, jsonData);
        visible = json.readValue("visible", Boolean.TYPE, jsonData);
        tint = json.readValue("tint", Color.class, jsonData);
        tintName = json.readValue("tintName", String.class, jsonData);
        name = json.readValue("name", String.class, jsonData);
    }
}
