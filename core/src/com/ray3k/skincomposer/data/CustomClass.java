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

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ray3k.skincomposer.Main;

public class CustomClass implements Json.Serializable {
    private String fullyQualifiedName;
    private String displayName;
    private boolean declareAfterUIclasses;
    private Array<CustomStyle> styles;
    private CustomStyle templateStyle;
    private Main main;

    public CustomClass() {
        
    }
    
    public CustomClass(String fullyQualifiedName, String displayName) {
        this.fullyQualifiedName = fullyQualifiedName;
        this.displayName = displayName;
        declareAfterUIclasses = false;
        styles = new Array<>();
        CustomStyle defaultStyle = new CustomStyle("default");
        defaultStyle.setDeletable(false);
        defaultStyle.setParentClass(this);
        styles.add(defaultStyle);
        templateStyle = new CustomStyle("template");
        templateStyle.setParentClass(this);
    }

    public CustomStyle getStyle(String name) {
        for (CustomStyle style : styles) {
            if (style.getName().equals(name)) {
                return style;
            }
        }
        
        return null;
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

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
        for (CustomStyle style : styles) {
            style.setMain(main);
        }
        templateStyle.setMain(main);
    }

    public boolean isDeclareAfterUIclasses() {
        return declareAfterUIclasses;
    }

    public void setDeclareAfterUIclasses(boolean declareAfterUIclasses) {
        this.declareAfterUIclasses = declareAfterUIclasses;
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
        json.writeValue("declareAfterUIclasses", declareAfterUIclasses);
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
        templateStyle.setParentClass(this);
        declareAfterUIclasses = jsonData.getBoolean("declareAfterUIclasses", false);
    }
    
    public CustomClass copy() {
        CustomClass returnValue = new CustomClass(fullyQualifiedName, displayName);
        returnValue.styles.clear();
        
        for (CustomStyle style : styles) {
            returnValue.styles.add(style.copy());
        }
        
        if (templateStyle != null) {
            returnValue.templateStyle = templateStyle.copy();
        }
        
        returnValue.declareAfterUIclasses = declareAfterUIclasses;
        
        returnValue.main = main;
        
        return returnValue;
    }
}
