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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane.SplitPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip.TextTooltipStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.TreeStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.ray3k.skincomposer.Main;
import java.util.Arrays;

public class StyleData implements Json.Serializable {
    public String name = "";
    public Class clazz;
    public OrderedMap<String,StyleProperty> properties;
    public boolean deletable;
    public String parent;
    public JsonData jsonData;
    private Main main;

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof StyleData && ((StyleData) o).name.equals(name);
    }
    
    public StyleData(StyleData styleData, String styleName, Main main) {
        name = styleName;
        this.main = main;

        clazz = styleData.clazz;
        properties = new OrderedMap<>();
        for (Entry<String, StyleProperty> entry : styleData.properties.entries()) {
            properties.put(entry.key, new StyleProperty(entry.value));
        }
        deletable = true;
    }
    
    public StyleData (Class clazz, String styleName, Main main) {
        name = styleName;
        this.main = main;

        this.clazz = clazz;
        properties = new OrderedMap<>();
        deletable = true;
        
        resetProperties();
    }
    
    public StyleData() {
        this.main = Main.main;
    }
    
    private void newStyleProperties(Class clazz) {
        for (Field field : ClassReflection.getFields(clazz)) {
            StyleProperty styleProperty = new StyleProperty(field.getType(), field.getName(), true);
            properties.put(field.getName(), styleProperty);
        }
    }

    public boolean hasAllNullFields() {
        boolean returnValue = true;
        for(StyleProperty property : this.properties.values()) {
            if (property.value != null) {
                if (property.value instanceof Double) {
                    if (!MathUtils.isZero((float) (double) property.value)) {
                        returnValue = false;
                    }
                } else if (property.value instanceof Integer) {
                    if ((int) property.value != 0) {
                        returnValue = false;
                    }
                } else if (property.value instanceof Float) {
                    if (!MathUtils.isZero((float) property.value)) {
                        returnValue = false;
                    }
                } else {
                    returnValue = false;
                }
                break;
            }
        }
        
        var parentStyle = findParentStyle();
        return returnValue && (parentStyle == null || parentStyle.hasAllNullFields());
    }
    
    public boolean hasMandatoryFields() {
        boolean returnValue = true;
        var propertiesList = this.properties.values().toArray();
        for (int i = 0; i < propertiesList.size; i++) {
            var property = propertiesList.get(i);
            if (!property.optional && !hasField(property)) {
                returnValue = false;
                break;
            } else if (property.type == ListStyle.class) {
                String value = (String) property.value;
                Array<StyleData> datas = jsonData.getClassStyleMap().get(List.class);
                
                boolean found = false;
                for (StyleData data : datas) {
                    if (value.equals(data.name)) {
                        found = true;
                        if (!data.hasMandatoryFields() || data.hasAllNullFields()) {
                            returnValue = false;
                        }
                        break;
                    }
                }
                
                if (!found) {
                    returnValue = false;
                }
                
                if (!returnValue) break;
            } else if (property.type == LabelStyle.class) {
                String value = (String) property.value;
                Array<StyleData> datas = jsonData.getClassStyleMap().get(Label.class);
                
                boolean found = false;
                for (StyleData data : datas) {
                    if (value.equals(data.name)) {
                        found = true;
                        if (!data.hasMandatoryFields() || data.hasAllNullFields()) {
                            returnValue = false;
                        }
                        break;
                    }
                }
                
                if (!found) {
                    returnValue = false;
                }
                
                if (!returnValue) break;
            } else if (property.type == ScrollPaneStyle.class) {
                String value = (String) property.value;
                Array<StyleData> datas = jsonData.getClassStyleMap().get(ScrollPane.class);
                
                boolean found = false;
                for (StyleData data : datas) {
                    if (value.equals(data.name)) {
                        found = true;
                        if (!data.hasMandatoryFields() || data.hasAllNullFields()) {
                            returnValue = false;
                        }
                        break;
                    }
                }
                
                if (!found) {
                    returnValue = false;
                }
                
                if (!returnValue) break;
            }
        }
        return returnValue;
    }
    
    public StyleData findParentStyle() {
        StyleData returnValue = null;
        
        if (parent != null) {
            Class recursiveClass = clazz;
            Class recursiveStyleClass = Main.basicToStyleClass(recursiveClass);
            loop:
            while (recursiveStyleClass != null && Arrays.asList(Main.STYLE_CLASSES).contains(recursiveStyleClass)) {
                var styles = main.getJsonData().getClassStyleMap().get(recursiveClass);
                for (int i = 0; i < styles.size; i++) {
                    var style = styles.get(i);
                    if (parent.equals(style.name) && !(clazz.equals(recursiveClass) && name.equals(style.name))) {
                        returnValue = style;
                        break loop;
                    }
                }

                recursiveClass = recursiveClass.getSuperclass();
                recursiveStyleClass = Main.basicToStyleClass(recursiveClass);
            }
        }

        return returnValue;
    }
    
    public boolean hasField(StyleProperty property) {
        var style = this;
        
        while (style != null) {
            var propertiesList = style.properties.values().toArray();
            for (int i = 0; i < propertiesList.size; i++) {
                var current = propertiesList.get(i);
                if (current.name.equals(property.name) && current.value != null) {
                    return true;
                }
            }
            
            style = style.findParentStyle();
        }
        return false;
    }
    
    public Object getInheritedValue(String name) {
        var style = this;
        
        while (style != null) {
            var propertiesList = style.properties.values().toArray();
            for (int i = 0; i < propertiesList.size; i++) {
                var current = propertiesList.get(i);
                if (current.name.equals(name) && current.value != null) {
                    return current.value;
                }
            }
            
            style = style.findParentStyle();
        }
        return null;
    }
    
    public static boolean validate(String name) {
        return name != null && !name.matches("^\\d.*|^-.*|.*\\s.*|.*[^a-zA-Z\\d\\s-_ñáéíóúüÑÁÉÍÓÚÜ].*|^$");
    }

    @Override
    public void write(Json json) {
        json.writeValue("name", name);
        json.writeValue("clazz", clazz.getName());
        json.writeValue("properties", properties);
        json.writeValue("deletable", deletable);
        json.writeValue("parent", parent);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        name = jsonData.getString("name");
        properties = json.readValue("properties", OrderedMap.class, jsonData);
        deletable = jsonData.getBoolean("deletable");
        try {
            clazz = ClassReflection.forName(jsonData.getString("clazz"));
        } catch (ReflectionException ex) {
            Gdx.app.error(getClass().toString(), "Error reading from serialized object" , ex);
            main.getDialogFactory().showDialogError("Read Error...","Error reading from serialized object.\n\nOpen log?");
        }
        parent = jsonData.getString("parent");
    }

    public void resetProperties() {
        properties.clear();
        parent = null;
        
        if (clazz.equals(Button.class)) {
            newStyleProperties(ButtonStyle.class);
        } else if (clazz.equals(CheckBox.class)) {
            newStyleProperties(CheckBoxStyle.class);
            properties.get("checkboxOn").optional = false;
            properties.get("checkboxOff").optional = false;
            properties.get("font").optional = false;
        } else if (clazz.equals(ImageButton.class)) {
            newStyleProperties(ImageButtonStyle.class);
        } else if (clazz.equals(ImageTextButton.class)) {
            newStyleProperties(ImageTextButtonStyle.class);
            properties.get("font").optional = false;
        } else if (clazz.equals(Label.class)) {
            newStyleProperties(LabelStyle.class);
            properties.get("font").optional = false;
        } else if (clazz.equals(List.class)) {
            newStyleProperties(ListStyle.class);
            properties.get("font").optional = false;
            properties.get("fontColorSelected").optional = false;
            properties.get("fontColorUnselected").optional = false;
            properties.get("selection").optional = false;
        } else if (clazz.equals(ProgressBar.class)) {
            newStyleProperties(ProgressBarStyle.class);
            
            //Though specified as optional in the doc, there are bugs without "background" being mandatory
            properties.get("background").optional = false;
        } else if (clazz.equals(ScrollPane.class)) {
            newStyleProperties(ScrollPaneStyle.class);
        } else if (clazz.equals(SelectBox.class)) {
            newStyleProperties(SelectBoxStyle.class);
            properties.get("font").optional = false;
            properties.get("fontColor").optional = false;
            properties.get("scrollStyle").optional = false;
            properties.get("scrollStyle").value = "default";
            properties.get("listStyle").optional = false;
            properties.get("listStyle").value = "default";
        } else if (clazz.equals(Slider.class)) {
            newStyleProperties(SliderStyle.class);
            
            //Though specified as optional in the doc, there are bugs without "background" being mandatory
            properties.get("background").optional = false;
        } else if (clazz.equals(SplitPane.class)) {
            newStyleProperties(SplitPaneStyle.class);
            properties.get("handle").optional = false;
        } else if (clazz.equals(TextButton.class)) {
            newStyleProperties(TextButtonStyle.class);
            properties.get("font").optional = false;
        } else if (clazz.equals(TextField.class)) {
            newStyleProperties(TextFieldStyle.class);
            properties.get("font").optional = false;
            properties.get("fontColor").optional = false;
        } else if (clazz.equals(TextTooltip.class)) {
            newStyleProperties(TextTooltipStyle.class);
            properties.get("label").optional = false;
            properties.get("label").value = "default";
        } else if (clazz.equals(Touchpad.class)) {
            newStyleProperties(TouchpadStyle.class);
        } else if (clazz.equals(Tree.class)) {
            newStyleProperties(TreeStyle.class);
            properties.get("plus").optional = false;
            properties.get("minus").optional = false;
        } else if (clazz.equals(Window.class)) {
            newStyleProperties(WindowStyle.class);
            properties.get("titleFont").optional = false;
        }
    }
}
