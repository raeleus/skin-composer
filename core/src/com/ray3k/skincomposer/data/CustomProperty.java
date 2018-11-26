/*******************************************************************************
 * MIT License
 * 
 * Copyright (c) 2018 Raymond Buckley
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

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ray3k.skincomposer.Main;

public class CustomProperty implements Json.Serializable {
    private String name;
    private Object value;
    private CustomStyle parentStyle;
    private PropertyType type;
    private Main main;
    
    public static enum PropertyType {
        NONE("None"), NUMBER("Number"), TEXT("Text"), RAW_TEXT("Raw Text (JSON)"), DRAWABLE("Drawable"), FONT("Font"), COLOR("Color"), BOOL("Boolean"), STYLE("Style");

        String name;
        PropertyType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public CustomProperty() {
        
    }
    
    public CustomProperty(String name, PropertyType type) {
        this.name = name;
        this.type = type;
        
        switch (type) {
            case TEXT:
            case RAW_TEXT:
            case STYLE:
                value = "";
                break;
            case NUMBER:
                value = 0.0;
                break;
            case BOOL:
                value = false;
                break;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public CustomStyle getParentStyle() {
        return parentStyle;
    }

    public void setParentStyle(CustomStyle parentStyle) {
        this.parentStyle = parentStyle;
    }

    public PropertyType getType() {
        return type;
    }

    public void setType(PropertyType type) {
        this.type = type;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    @Override
    public String toString() {
        return name;
    }
    
    public CustomProperty copy() {
        CustomProperty returnValue = new CustomProperty(name, type);
        returnValue.parentStyle = parentStyle;
        returnValue.value = value;
        returnValue.main = main;
        return returnValue;
    }

    @Override
    public void write(Json json) {
        json.writeValue("name", name);
        json.writeValue("type", type);
        
        //only write value if it is valid
        boolean writeValue = false;

        if (value instanceof Float && type == PropertyType.NUMBER
                || value instanceof Double && type == PropertyType.NUMBER
                || value instanceof Boolean && type == PropertyType.BOOL) {
            writeValue = true;
        } else if (value instanceof String) {
            if (type == PropertyType.TEXT || type == PropertyType.RAW_TEXT || type == PropertyType.STYLE) {
                writeValue = true;
            } else if (type == PropertyType.COLOR) {
                for (ColorData data : main.getJsonData().getColors()) {
                    if (data.getName().equals(value)) {
                        writeValue = true;
                        break;
                    }
                }
            } else if (type == PropertyType.DRAWABLE) {
                for (DrawableData data : main.getAtlasData().getDrawables()) {
                    if (data.name.equals(value)) {
                        writeValue = true;
                        break;
                    }
                }
            } else if (type == PropertyType.FONT) {
                for (FontData data : main.getJsonData().getFonts()) {
                    if (data.getName().equals(value)) {
                        writeValue = true;
                        break;
                    }
                }
                
                for (var data : main.getJsonData().getFreeTypeFonts()) {
                    if (data.name.equals(value)) {
                        writeValue = true;
                        break;
                    }
                }
            }
        }
        
        if (writeValue) {
            json.writeValue("value", value);
        } else {
            json.writeValue("value", (Object) null);
        }
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        name = jsonData.getString("name");
        value = json.readValue("value", null, jsonData);
        type = json.readValue("type", PropertyType.class, jsonData);
    }
}
