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

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ray3k.skincomposer.Main;

public class CustomStyle implements Json.Serializable {
    private String name;
    private Array<CustomProperty> properties;
    private CustomClass parentClass;
    private boolean deletable;

    public CustomStyle() {
        
    }
    
    public CustomStyle(String name) {
        this.name = name;
        properties = new Array<>();
        deletable = true;
    }

    public CustomProperty getProperty(String name) {
        for (CustomProperty property : properties) {
            if (property.getName().equals(name)) {
                return property;
            }
        }
        
        return null;
    }
    
    public CustomClass getParentClass() {
        return parentClass;
    }

    public void setParentClass(CustomClass parentClass) {
        this.parentClass = parentClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Array<CustomProperty> getProperties() {
        return properties;
    }

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    public CustomStyle copy() {
        CustomStyle returnValue = new CustomStyle(name);

        for (CustomProperty property : properties) {
            returnValue.properties.add(property.copy());
        }
        
        returnValue.parentClass = parentClass;
        returnValue.deletable = deletable;

        return returnValue;
    }

    @Override
    public void write(Json json) {
        json.writeValue("name", name);
        json.writeValue("properties", properties, Array.class, CustomProperty.class);
        json.writeValue("deletable", deletable);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        name = jsonData.getString("name");
        properties = json.readValue("properties", Array.class, CustomProperty.class, jsonData);
        for (CustomProperty property : properties) {
            property.setParentStyle(this);
        }
        deletable = jsonData.getBoolean("deletable");
    }
}
