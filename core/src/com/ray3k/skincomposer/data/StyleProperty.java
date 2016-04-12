package com.ray3k.skincomposer.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

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
    public void read(Json json, JsonValue jsonData) {
        try {
            name = jsonData.getString("name");
            optional = jsonData.getBoolean("optional");
            if (jsonData.get("value").isNumber()) {
                type = Float.TYPE;
                value = Double.parseDouble(jsonData.getString("value"));
            } else {
                type = ClassReflection.forName(jsonData.getString("type"));
                if (jsonData.get("value").isNull()) {
                    value = null;
                } else {
                    value = jsonData.getString("value");
                }
            }
        } catch (ReflectionException ex) {
            Gdx.app.error(getClass().toString(), "Error reading from serialized object" , ex);
        }
    }
}
