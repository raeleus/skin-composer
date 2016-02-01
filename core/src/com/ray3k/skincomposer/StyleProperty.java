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
    }
}
