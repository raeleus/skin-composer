/*******************************************************************************
 * MIT License
 * 
 * Copyright (c) 2017 Raymond Buckley
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

public class CustomStyle implements Json.Serializable {
    private String name;
    private Array<CustomProperty> properties;
    private CustomClass parentClass;

    public CustomStyle() {
        
    }
    
    public CustomStyle(String name) {
        this.name = name;
        properties = new Array<>();
    }

    public CustomClass getParentClass() {
        return parentClass;
    }

    public void setParentClass(CustomClass parentClass) {
        this.parentClass = parentClass;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    @Override
    public CustomStyle clone() throws CloneNotSupportedException {
        CustomStyle returnValue = (CustomStyle) super.clone();

        for (CustomProperty property : properties) {
            returnValue.properties.add(property.clone());
        }

        return returnValue;
    }

    @Override
    public void write(Json json) {
        json.writeValue("name", name);
        json.writeValue("properties", properties, Array.class, CustomProperty.class);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        name = jsonData.getString("name");
        properties = json.readValue("properties", Array.class, CustomProperty.class, jsonData);
        for (CustomProperty property : properties) {
            property.setParentStyle(this);
        }
    }
}
