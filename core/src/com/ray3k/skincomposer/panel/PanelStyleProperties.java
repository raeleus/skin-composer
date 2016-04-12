package com.ray3k.skincomposer.panel;

import com.ray3k.skincomposer.data.StyleData;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.OrderedMap;
import com.ray3k.skincomposer.BrowseField;
import com.ray3k.skincomposer.BrowseField.BrowseFieldStyle;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.Spinner;
import com.ray3k.skincomposer.Spinner.SpinnerStyle;
import com.ray3k.skincomposer.data.JsonData;
import com.ray3k.skincomposer.data.StyleProperty;

public class PanelStyleProperties {
    public static PanelStyleProperties instance;
    private Table table;
    private Skin skin;
    private SpinnerStyle spinnerStyle;
    private BrowseFieldStyle browseFieldStyle, colorFieldStyle, drawableFieldStyle, fontFieldStyle;
    
    
    public PanelStyleProperties(Table table, Skin skin, Stage stage) {
        spinnerStyle = new Spinner.SpinnerStyle(skin.get("spinner-minus", ButtonStyle.class), skin.get("spinner-plus", ButtonStyle.class), skin.get("spinner", TextFieldStyle.class));
        browseFieldStyle = new BrowseFieldStyle(skin.get("orange-small", TextButtonStyle.class), skin.get("alt", TextFieldStyle.class), skin.get("default", LabelStyle.class));
        
        ImageButtonStyle imageButtonStyle = new ImageButtonStyle(skin.get("orange-small", ImageButtonStyle.class));
        imageButtonStyle.imageUp = skin.getDrawable("image-color-wheel");
        colorFieldStyle = new BrowseFieldStyle(imageButtonStyle, skin.get("alt", TextFieldStyle.class), skin.get("default", LabelStyle.class));
        
        imageButtonStyle = new ImageButtonStyle(skin.get("orange-small", ImageButtonStyle.class));
        imageButtonStyle.imageUp = skin.getDrawable("image-portrait");
        drawableFieldStyle = new BrowseFieldStyle(imageButtonStyle, skin.get("alt", TextFieldStyle.class), skin.get("default", LabelStyle.class));
        
        imageButtonStyle = new ImageButtonStyle(skin.get("orange-small", ImageButtonStyle.class));
        imageButtonStyle.imageUp = skin.getDrawable("image-font");
        fontFieldStyle = new BrowseFieldStyle(imageButtonStyle, skin.get("alt", TextFieldStyle.class), skin.get("default", LabelStyle.class));
        
        instance = this;
        this.table = table;
        this.skin = skin;
    }
    
    public void populate(StyleData styleData) {
        table.clear();
        table.defaults().padLeft(10.0f).padRight(10.0f).padTop(0.0f).padBottom(0.0f);
        
        OrderedMap.Entries<String, StyleProperty> iter = styleData.properties.entries();
        while (iter.hasNext) {
            Entry<String, StyleProperty> entry = iter.next();
            String name = entry.key;
            final StyleProperty property = entry.value;
            
            
            if (property.type.equals(Float.TYPE)) {
                if (property.optional) {
                    table.add(new Label(name, skin)).padTop(10.0f);
                } else {
                    table.add(new Label(name, skin, "error")).padTop(10.0f);
                }
                
                table.row();
                
                final Spinner spinner = new Spinner((Double)property.value, 1.0, false, spinnerStyle);
                spinner.getTextField().setFocusTraversal(false);
                spinner.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        property.value = spinner.getValue();
                        PanelPreviewProperties.instance.render();
                    }
                });
                table.add(spinner).growX();
            } else if (property.type.equals(Drawable.class)) {
                final BrowseField browseField = new BrowseField(name, drawableFieldStyle);
                if (!property.optional) {
                    browseField.getLabel().setStyle(skin.get("error", LabelStyle.class));
                }
                browseField.getTextField().setText((String)property.value);
                browseField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        Main.instance.showDialogDrawables(property, new EventListener() {
                            @Override
                            public boolean handle(Event event) {
                                PanelPreviewProperties.instance.produceAtlas();
                                PanelPreviewProperties.instance.render();
                                return false;
                            }
                        });
                    }
                });
                table.add(browseField).growX().padTop(10.0f);
            } else if (property.type.equals(Color.class)) {
                final BrowseField browseField = new BrowseField(name, colorFieldStyle);
                if (!property.optional) {
                    browseField.getLabel().setStyle(skin.get("error", LabelStyle.class));
                }
                browseField.getTextField().setText((String)property.value);
                browseField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        Main.instance.showDialogColors(property, new EventListener() {
                            @Override
                            public boolean handle(Event event) {
                                PanelPreviewProperties.instance.render();
                                return false;
                            }
                        });
                    }
                });
                table.add(browseField).growX().padTop(10.0f);
            } else if (property.type.equals(BitmapFont.class)) {
                final BrowseField browseField = new BrowseField(name, fontFieldStyle);
                if (!property.optional) {
                    browseField.getLabel().setStyle(skin.get("error", LabelStyle.class));
                }
                browseField.getTextField().setText((String)property.value);
                browseField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        Main.instance.showDialogFonts(property, new EventListener() {
                            @Override
                            public boolean handle(Event event) {
                                PanelPreviewProperties.instance.render();
                                return false;
                            }
                        });
                    }
                });
                table.add(browseField).growX().padTop(10.0f);
            } else if (property.type.equals(ScrollPaneStyle.class)) {
                if (property.optional) {
                    table.add(new Label(name, skin)).padTop(10.0f);
                } else {
                    table.add(new Label(name, skin, "error")).padTop(10.0f);
                }
                
                table.row();
                
                SelectBox<StyleData> selectBox = new SelectBox(skin, "slim");
                selectBox.setItems(JsonData.getInstance().getClassStyleMap().get(ScrollPane.class));
                
                boolean found = false;
                for (StyleData data : selectBox.getItems()) {
                    if (property.value.equals(data.name)) {
                        selectBox.setSelected(data);
                        found = true;
                        break;
                    }
                }
                
                selectBox.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        property.value = selectBox.getSelected().name;
                        PanelPreviewProperties.instance.render();
                    }
                });
                
                if (!found) {
                    selectBox.setSelectedIndex(0);
                }
                
                table.add(selectBox).growX();
            } else if (property.type.equals(ListStyle.class)) {
                if (property.optional) {
                    table.add(new Label(name, skin)).padTop(10.0f);
                } else {
                    table.add(new Label(name, skin, "error")).padTop(10.0f);
                }
                
                table.row();
                
                SelectBox<StyleData> selectBox = new SelectBox(skin, "slim");
                selectBox.setItems(JsonData.getInstance().getClassStyleMap().get(List.class));
                
                boolean found = false;
                for (StyleData data : selectBox.getItems()) {
                    if (property.value.equals(data.name)) {
                        selectBox.setSelected(data);
                        found = true;
                        break;
                    }
                }
                
                selectBox.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        property.value = selectBox.getSelected().name;
                        PanelPreviewProperties.instance.render();
                    }
                });
                
                if (!found) {
                    selectBox.setSelectedIndex(0);
                }
                
                table.add(selectBox).growX();
            } else if (property.type.equals(LabelStyle.class)) {
                if (property.optional) {
                    table.add(new Label(name, skin)).padTop(10.0f);
                } else {
                    table.add(new Label(name, skin, "error")).padTop(10.0f);
                }
                
                table.row();
                
                SelectBox<StyleData> selectBox = new SelectBox(skin, "slim");
                selectBox.setItems(JsonData.getInstance().getClassStyleMap().get(Label.class));
                
                boolean found = false;
                for (StyleData data : selectBox.getItems()) {
                    if (property.value.equals(data.name)) {
                        selectBox.setSelected(data);
                        found = true;
                        break;
                    }
                }
                
                selectBox.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        property.value = selectBox.getSelected().name;
                        PanelPreviewProperties.instance.render();
                    }
                });
                
                if (!found) {
                    selectBox.setSelectedIndex(0);
                }
                
                table.add(selectBox).growX();
            }
            
            table.row();
        }
        table.getCells().peek().padBottom(20.0f);
    }
}
