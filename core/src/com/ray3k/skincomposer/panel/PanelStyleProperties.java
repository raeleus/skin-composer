package com.ray3k.skincomposer.panel;

import com.ray3k.skincomposer.data.StyleData;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
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
import com.ray3k.skincomposer.data.AtlasData;
import com.ray3k.skincomposer.data.JsonData;
import com.ray3k.skincomposer.data.StyleProperty;
import com.ray3k.skincomposer.Undoable;
import com.ray3k.skincomposer.data.ColorData;
import com.ray3k.skincomposer.data.FontData;

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
                        Main.instance.addUndoable(new FloatUndoable(spinner, property), true);
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
                        Object oldValue = property.value;
                        Main.instance.showDialogDrawables(property, new EventListener() {
                            @Override
                            public boolean handle(Event event) {
                                Main.instance.addUndoable(new DrawableUndoable(property, oldValue, property.value), true);
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
                        Object oldValue = property.value;
                        Main.instance.showDialogColors(property, new EventListener() {
                            @Override
                            public boolean handle(Event event) {
                                Main.instance.addUndoable(new ColorUndoable(property, oldValue, property.value), true);
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
                        Object oldValue = property.value;
                        Main.instance.showDialogFonts(property, new EventListener() {
                            @Override
                            public boolean handle(Event event) {
                                Main.instance.addUndoable(new FontUndoable(property, oldValue, property.value), true);
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
                        Main.instance.addUndoable(new SelectBoxUndoable(property, selectBox), true);
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
                        Main.instance.addUndoable(new SelectBoxUndoable(property, selectBox), true);
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
                        Main.instance.addUndoable(new SelectBoxUndoable(property, selectBox), true);
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
    
    private static class FloatUndoable implements Undoable {
        private Spinner spinner;
        private StyleProperty property;
        private double oldValue, newValue;

        public FloatUndoable(Spinner spinner, StyleProperty property) {
            this.spinner = spinner;
            this.property = property;
            oldValue = (Double) property.value;
            newValue = spinner.getValue();
        }
        
        @Override
        public void undo() {
            property.value = newValue;
            if (!MathUtils.isEqual((float)spinner.getValue(), (float)oldValue)) {
                spinner.setValue(oldValue);
            }
            PanelPreviewProperties.instance.render();
        }

        @Override
        public void redo() {
            property.value = newValue;
            if (!MathUtils.isEqual((float)spinner.getValue(), (float)newValue)) {
                spinner.setValue(newValue);
            }
            PanelPreviewProperties.instance.render();
        }

        @Override
        public String getUndoText() {
            return "Change Style Property " + property.name;
        }
    }
    
    private static class DrawableUndoable implements Undoable {
        private StyleProperty property;
        private Object oldValue, newValue;

        public DrawableUndoable(StyleProperty property, Object oldValue, Object newValue) {
            this.property = property;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        @Override
        public void undo() {
            PanelPreviewProperties.instance.produceAtlas();
            if (oldValue == null || AtlasData.getInstance().getDrawable((String) oldValue) != null) {
                property.value = oldValue;
            }
            PanelPreviewProperties.instance.render();
            PanelStyleProperties.instance.populate(PanelClassBar.instance.getStyleSelectBox().getSelected());
        }

        @Override
        public void redo() {
            PanelPreviewProperties.instance.produceAtlas();
            if (newValue == null || AtlasData.getInstance().getDrawable((String) newValue) != null) {
                property.value = newValue;
            }
            PanelPreviewProperties.instance.render();
            PanelStyleProperties.instance.populate(PanelClassBar.instance.getStyleSelectBox().getSelected());
        }

        @Override
        public String getUndoText() {
            return "Change Style Property " + property.name;
        }
        
    }
    
    private static class ColorUndoable implements Undoable {
        private StyleProperty property;
        private Object oldValue, newValue;

        public ColorUndoable(StyleProperty property, Object oldValue, Object newValue) {
            this.property = property;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }
        
        @Override
        public void undo() {
            if (oldValue == null) {
                property.value = oldValue;
            } else {
                for (ColorData color : JsonData.getInstance().getColors()) {
                    if (color.getName().equals((String) oldValue)) {
                        property.value = oldValue;
                        break;
                    }
                }
            }
            PanelPreviewProperties.instance.render();
            PanelStyleProperties.instance.populate(PanelClassBar.instance.getStyleSelectBox().getSelected());
        }

        @Override
        public void redo() {
            if (newValue == null) {
                property.value = newValue;
            } else {
                for (ColorData color : JsonData.getInstance().getColors()) {
                    if (color.getName().equals((String) newValue)) {
                        property.value = newValue;
                        break;
                    }
                }
            }
            PanelPreviewProperties.instance.render();
            PanelStyleProperties.instance.populate(PanelClassBar.instance.getStyleSelectBox().getSelected());
        }

        @Override
        public String getUndoText() {
            return "Change Style Property " + property.name;
        }
    }
    
    private static class FontUndoable implements Undoable {
        private StyleProperty property;
        private Object oldValue, newValue;

        public FontUndoable(StyleProperty property, Object oldValue, Object newValue) {
            this.property = property;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }
        
        @Override
        public void undo() {
            if (oldValue == null) {
                property.value = oldValue;
            } else {
                for (FontData font : JsonData.getInstance().getFonts()) {
                    if (font.getName().equals((String) oldValue)) {
                        property.value = oldValue;
                        break;
                    }
                }
            }
            
            PanelPreviewProperties.instance.render();
            PanelStyleProperties.instance.populate(PanelClassBar.instance.getStyleSelectBox().getSelected());
        }

        @Override
        public void redo() {
            if (newValue == null) {
                property.value = newValue;
            } else {
                for (FontData font : JsonData.getInstance().getFonts()) {
                    if (font.getName().equals((String) newValue)) {
                        property.value = newValue;
                        break;
                    }
                }
            }
            PanelPreviewProperties.instance.render();
            PanelStyleProperties.instance.populate(PanelClassBar.instance.getStyleSelectBox().getSelected());
        }

        @Override
        public String getUndoText() {
            return "Change Style Property " + property.name;
        }
    }
    
    private static class SelectBoxUndoable implements Undoable {
        private StyleProperty property;
        private SelectBox<StyleData> selectBox;
        private String oldValue, newValue;
        

        public SelectBoxUndoable(StyleProperty property, SelectBox<StyleData> selectBox) {
            this.property = property;
            this.selectBox = selectBox;
            
            oldValue = (String) property.value;
            newValue = selectBox.getSelected().name;
        }

        @Override
        public void undo() {
            property.value = oldValue;
            PanelPreviewProperties.instance.render();
            PanelStyleProperties.instance.populate(PanelClassBar.instance.getStyleSelectBox().getSelected());
        }

        @Override
        public void redo() {
            property.value = newValue;
            PanelPreviewProperties.instance.render();
            PanelStyleProperties.instance.populate(PanelClassBar.instance.getStyleSelectBox().getSelected());
        }

        @Override
        public String getUndoText() {
            return "Change Style Property " + property.name;
        }
    }
}
