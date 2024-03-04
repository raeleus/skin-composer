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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.ray3k.skincomposer.dialog.DialogFactory;

public class StyleProperty implements Json.Serializable{
    public Class type;
    public String name;
    public boolean optional;
    public Object value;

    public StyleProperty(Class type, String name, boolean optional) {
        this.type = type;
        this.name = name;
        this.optional = optional;
        
        if (type.equals(Float.TYPE)) {
            value = 0.0;
        } else {
            value = null;
        }
    }
    
    public StyleProperty(StyleProperty styleProperty) {
        this.type = styleProperty.type;
        this.name = styleProperty.name;
        this.optional = styleProperty.optional;
        this.value = styleProperty.value;
    }
    
    public StyleProperty() {
    
    }

    @Override
    public void write(Json json) {
        json.writeValue("type", type.getName());
        json.writeValue("name", name);
        json.writeValue("optional", optional);
        json.writeValue("value", value);
    }

    @Override
    public void read(Json json, JsonValue jsonValue) {
        try {
            name = jsonValue.getString("name");
            optional = jsonValue.getBoolean("optional");
            if (jsonValue.get("value").isNumber()) {
                type = Float.TYPE;
                value = Double.parseDouble(jsonValue.getString("value"));
            } else {
                type = ClassReflection.forName(jsonValue.getString("type"));
                if (jsonValue.get("value").isNull()) {
                    value = null;
                } else {
                    value = jsonValue.getString("value");
                }
            }
        } catch (ReflectionException ex) {
            Gdx.app.error(getClass().toString(), "Error reading from serialized object" , ex);
            DialogFactory.showDialogErrorStatic("Read Error...","Error reading from serialized object.\n\nOpen log?");
        }
    }
}
