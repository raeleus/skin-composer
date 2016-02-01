package com.ray3k.skincomposer;

public class StyleProperty {
    public Class type;
    public String name;
    public boolean optional;
    public Object value;

    public StyleProperty(Class type, String name, boolean optional) {
        this.type = type;
        this.name = name;
        this.optional = optional;
        
        if (type.equals(Float.TYPE)) {
            value = 0.0f;
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
}
