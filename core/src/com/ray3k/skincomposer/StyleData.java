package com.ray3k.skincomposer;

import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane.SplitPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip.TextTooltipStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.TreeStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;

public class StyleData {
    public static enum ClassName {Button, Checkbox, ImageButton, ImageTextButton,
    Label, List, ProgressBar, ScrollPane, SelectBox, Slider, SplitPane,
    TextButton, TextField, TextTooltip, Touchpad, Tree, Window};
    
    public String name = "";
    public ClassName className;
    public OrderedMap<String,StyleProperty> properties;
    public boolean deletable;

    @Override
    public String toString() {
        return name;
    }
    
    public StyleData(StyleData styleData, String styleName) {
        name = styleName;
        className = styleData.className;
        properties = new OrderedMap<String, StyleProperty>();
        for (Entry<String, StyleProperty> entry : styleData.properties.entries()) {
            properties.put(entry.key, new StyleProperty(entry.value));
        }
        deletable = true;
    }
    
    public StyleData (ClassName className, String styleName) {
        name = styleName;
        this.className = className;
        properties = new OrderedMap<String, StyleProperty>();
        deletable = true;
        switch (className) {
            case Button:
                newStyleProperties(ButtonStyle.class);
                break;
            case Checkbox:
                newStyleProperties(CheckBoxStyle.class);
                properties.get("checkboxOn").optional = false;
                properties.get("checkboxOff").optional = false;
                break;
            case ImageButton:
                newStyleProperties(ImageButtonStyle.class);
                break;
            case ImageTextButton:
                newStyleProperties(ImageTextButtonStyle.class);
                break;
            case Label:
                newStyleProperties(LabelStyle.class);
                break;
            case List:
                newStyleProperties(ListStyle.class);
                break;
            case ProgressBar:
                newStyleProperties(ProgressBarStyle.class);
                break;
            case ScrollPane:
                newStyleProperties(ScrollPaneStyle.class);
                break;
            case SelectBox:
                newStyleProperties(SelectBoxStyle.class);
                break;
            case Slider:
                newStyleProperties(SliderStyle.class);
                break;
            case SplitPane:
                newStyleProperties(SplitPaneStyle.class);
                break;
            case TextButton:
                newStyleProperties(TextButtonStyle.class);
                break;
            case TextField:
                newStyleProperties(TextFieldStyle.class);
                properties.get("font").optional = false;
                break;
            case TextTooltip:
                newStyleProperties(TextTooltipStyle.class);
                break;
            case Touchpad:
                newStyleProperties(TouchpadStyle.class);
                break;
            case Tree:
                newStyleProperties(TreeStyle.class);
                break;
            case Window:
                newStyleProperties(WindowStyle.class);
                break;
        }
    }
    
    private void newStyleProperties(Class clazz) {
        for (Field field : ClassReflection.getFields(clazz)) {
            StyleProperty styleProperty = new StyleProperty(field.getType(), field.getName(), true);
            properties.put(field.getName(), styleProperty);
        }
    }
}
