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

public class CustomClass implements Json.Serializable {
    private String fullyQualifiedName;
    private String displayName;
    private Array<CustomStyle> styles;
    private CustomStyle templateStyle;

    public CustomClass() {
        
    }
    
    public CustomClass(String fullyQualifiedName, String displayName) {
        this.fullyQualifiedName = fullyQualifiedName;
        this.displayName = displayName;
        styles = new Array<>();
        CustomStyle defaultStyle = new CustomStyle("default");
        defaultStyle.setDeletable(false);
        defaultStyle.setParentClass(this);
        styles.add(defaultStyle);
    }

    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }

    public void setFullyQualifiedName(String fullyQualifiedName) {
        this.fullyQualifiedName = fullyQualifiedName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Array<CustomStyle> getStyles() {
        return styles;
    }

    public CustomStyle getTemplateStyle() {
        return templateStyle;
    }

    @Override
    public String toString() {
        return displayName;
    }

    @Override
    public void write(Json json) {
        json.writeValue("fullyQualifiedName", fullyQualifiedName);
        json.writeValue("displayName", displayName);
        json.writeValue("styles", styles, Array.class, CustomStyle.class);
        json.writeValue("templateStyle", templateStyle);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        fullyQualifiedName = jsonData.getString("fullyQualifiedName");
        displayName = jsonData.getString("displayName");
        styles = json.readValue("styles", Array.class, CustomStyle.class, jsonData);
        for (CustomStyle style : styles) {
            style.setParentClass(this);
        }
        templateStyle = json.readValue("templateStyle", CustomStyle.class, jsonData);
    }
    
    public CustomClass copy() {
        CustomClass returnValue = new CustomClass(fullyQualifiedName, displayName);
        
        for (CustomStyle style : styles) {
            returnValue.styles.add(style.copy());
        }
        
        if (templateStyle != null) {
            returnValue.templateStyle = templateStyle.copy();
        }
        
        return returnValue;
    }
}
