package com.ray3k.skincomposer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.OrderedMap;
import com.ray3k.skincomposer.BrowseField.BrowseFieldStyle;
import com.ray3k.skincomposer.Spinner.SpinnerStyle;

public class PanelStyleProperties {
    public static PanelStyleProperties actor;
    private Table table;
    private Skin skin;
    private SpinnerStyle spinnerStyle;
    private BrowseFieldStyle browseFieldStyle;
    
    public PanelStyleProperties(Table table, Skin skin, Stage stage) {
        spinnerStyle = new Spinner.SpinnerStyle(skin.get("spinner-minus", ButtonStyle.class), skin.get("spinner-plus", ButtonStyle.class), skin.get("spinner", TextFieldStyle.class));
        browseFieldStyle = new BrowseFieldStyle(skin.get("orange-small", TextButtonStyle.class), skin.get(TextFieldStyle.class));
        actor = this;
        this.table = table;
        this.skin = skin;
        table.setBackground("bleached-peach");
    }
    
    public void populate(StyleData styleData) {
        table.clear();
        table.defaults().padLeft(10.0f).padRight(10.0f).padTop(3.0f).padBottom(3.0f);
        OrderedMap.Entries<String, StyleProperty> iter = styleData.properties.entries();
        while (iter.hasNext) {
            Entry<String, StyleProperty> entry = iter.next();
            String name = entry.key;
            StyleProperty property = entry.value;
            
            if (property.optional) {
                table.add(new Label(name, skin)).padTop(10.0f);
            } else {
                table.add(new Label(name, skin, "error")).padTop(10.0f);
            }
            table.row();
            if (property.type.equals(Float.TYPE)) {
                Spinner spinner = new Spinner(0.0, 1.0, false, spinnerStyle);
                spinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        
                    }
                });
                table.add(spinner).growX();
            } else if (property.type.equals(Drawable.class)) {
                BrowseField browseField = new BrowseField(browseFieldStyle);
                browseField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        
                    }
                });
                table.add(browseField).growX();
            } else if (property.type.equals(Color.class)) {
                BrowseField browseField = new BrowseField(browseFieldStyle);
                browseField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        
                    }
                });
                table.add(browseField).growX();
            } else if (property.type.equals(BitmapFont.class)) {
                BrowseField browseField = new BrowseField(browseFieldStyle);
                browseField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        
                    }
                });
                table.add(browseField).growX();
            } else if (property.type.equals(ScrollPaneStyle.class)) {
                BrowseField browseField = new BrowseField(browseFieldStyle);
                browseField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        
                    }
                });
                table.add(browseField).growX();
            } else if (property.type.equals(ListStyle.class)) {
                BrowseField browseField = new BrowseField(browseFieldStyle);
                browseField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        
                    }
                });
                table.add(browseField).growX();
            } else if (property.type.equals(LabelStyle.class)) {
                BrowseField browseField = new BrowseField(browseFieldStyle);
                browseField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        
                    }
                });
                table.add(browseField).growX();
            }
            
            table.row();
        }
        table.getCells().peek().padBottom(20.0f);
    }
}
