package com.ray3k.skincomposer.dialog;

import com.ray3k.skincomposer.panel.PanelStatusBar;
import com.ray3k.skincomposer.data.ColorData;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Sort;
import com.ray3k.skincomposer.IbeamListener;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.data.JsonData;
import com.ray3k.skincomposer.data.ProjectData;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.data.StyleProperty;
import com.ray3k.skincomposer.panel.PanelClassBar;
import com.ray3k.skincomposer.panel.PanelPreviewProperties;
import com.ray3k.skincomposer.panel.PanelStyleProperties;
import com.ray3k.skincomposer.utils.Utils;
import java.util.Comparator;

public class DialogColors extends Dialog {
    private Array<ColorData> colors;
    private Table colorTable;
    private Skin skin;
    private StyleProperty styleProperty;
    private SelectBox<String> selectBox;
    private EventListener listener;
    private ScrollPane scrollPane;
    
    public DialogColors(Skin skin, StyleProperty styleProperty, EventListener listener) {
        this(skin, "default", styleProperty, listener);
    }
    
    public DialogColors(final Skin skin, String styleName, StyleProperty styleProperty, EventListener listener) {
        super("", skin, styleName);
        
        Main.instance.setListeningForKeys(false);
        
        this.listener = listener;
        this.skin = skin;
        this.styleProperty = styleProperty;
        colors = JsonData.getInstance().getColors();
        getContentTable().defaults().expandX();
        if (styleProperty != null) {
            Label label = new Label("Select a color...", skin, "title");
            label.setAlignment(Align.center);
            getContentTable().add(label);
            getContentTable().row();
        } else {
            Label label = new Label("Colors", skin, "title");
            label.setAlignment(Align.center);
            getContentTable().add(label);
            getContentTable().row();
        }
        
        Table table = new Table();
        
        table.defaults().pad(2.0f);
        
        table.add(new Label("Sort by: ", skin)).padLeft(20.0f);
        selectBox = new SelectBox<String>(skin);
        selectBox.setItems(new String[] {"A-Z", "Z-A"});
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                sortBySelectedMode();
            }
        });
        table.add(selectBox);
        
        ImageTextButtonStyle imageButtonStyle = new ImageTextButtonStyle();
        imageButtonStyle.imageUp = skin.getDrawable("image-plus");
        imageButtonStyle.imageDown = skin.getDrawable("image-plus-down");
        imageButtonStyle.up = skin.getDrawable("button-orange");
        imageButtonStyle.down = skin.getDrawable("button-orange-down");
        imageButtonStyle.font = skin.getFont("font");
        imageButtonStyle.fontColor = skin.getColor("white");
        imageButtonStyle.downFontColor = skin.getColor("maroon");
        ImageTextButton imageButton = new ImageTextButton(" New Color", imageButtonStyle);
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                showColorPicker();
            }
        });
        table.add(imageButton).expandX().left();
        getContentTable().add(table).left().expandX();
        getContentTable().row();
        colorTable = new Table();
        populate();
        
        table = new Table();
        table.add(colorTable).pad(5.0f);
        scrollPane = new ScrollPane(table, skin, "no-bg");
        scrollPane.setFadeScrollBars(false);
        getContentTable().add(scrollPane).grow();
        
        if (styleProperty != null) {
            button("Clear Color", true);
            button("Cancel", false);
        } else {
            button("Close", false);
        }
        key(Keys.ESCAPE, false);
    }
    
    @Override
    public Dialog show(Stage stage) {
        Dialog dialog = super.show(stage);
        stage.setScrollFocus(scrollPane);
        return dialog;
    }

    @Override
    public boolean remove() {
        Main.instance.setListeningForKeys(true);
        return super.remove();
    }
    
    private void showColorPicker() {
        Main.instance.showDialogColorPicker(new DialogColorPicker.ColorListener() {
            @Override
            public void selected(Color color) {
                if (color != null) {
                    final TextField field = new TextField("RGBA_" + (int) (color.r * 255) + "_" + (int) (color.g * 255) + "_" + (int) (color.b * 255) + "_" + (int) (color.a * 255), skin);
                    final Dialog dialog = new Dialog("Color name...", skin, "dialog") {
                        @Override
                        protected void result(Object object) {
                            if ((Boolean) object == true) {
                                newColor(field.getText(), color);
                            }
                        }
                    };
                    dialog.button("Ok", true).button("Cancel", false).key(Keys.ESCAPE, false);
                    final TextButton button = (TextButton) dialog.getButtonTable().getCells().first().getActor();

                    field.setTextFieldListener(new TextField.TextFieldListener() {
                        @Override
                        public void keyTyped(TextField textField, char c) {
                            if (c == '\n') {
                                if (!button.isDisabled()) {
                                    String name = field.getText();
                                    if (newColor(name, color)) {
                                        dialog.hide();
                                    }
                                }
                                Main.instance.getStage().setKeyboardFocus(textField);
                            }
                        }
                    });
                    
                    field.addListener(IbeamListener.get());

                    dialog.getContentTable().padLeft(10.0f).padRight(10.0f);
                    dialog.text("Please enter a name for the new color: ");
                    dialog.getContentTable().row();
                    dialog.getContentTable().add(field).growX();
                    dialog.getContentTable().row();
                    dialog.text("Preview:");
                    dialog.getContentTable().row();
                    Table table = new Table(skin);
                    table.setBackground("white");
                    table.setColor(color);
                    dialog.getContentTable().add(table).minSize(50.0f);
                    button.setDisabled(!ColorData.validate(field.getText()));
                    field.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            boolean disable = !ColorData.validate(field.getText());
                            if (!disable) {
                                for (ColorData data : JsonData.getInstance().getColors()) {
                                    if (data.getName().equals(field.getText())) {
                                        disable = true;
                                        break;
                                    }
                                }
                            }
                            button.setDisabled(disable);
                        }
                    });
                    dialog.show(getStage());
                    getStage().setKeyboardFocus(field);
                    field.selectAll();
                }
            }
        });
    }
    
    public void populate() {
        colorTable.clear();
        
        if (colors.size > 0) {
            colorTable.defaults().padTop(5.0f);
            for (ColorData color : colors) {
                Button button = new Button(skin);
                Label label = new Label(color.toString(), skin, "white");
                label.setTouchable(Touchable.disabled);
                float brightness = Utils.brightness(color.color);
                if (brightness > .35f) {
                    label.setColor(Color.BLACK);
                } else {
                    label.setColor(Color.WHITE);
                }
                
                Color bgColor = color.color;
                Table table = new Table(skin);
                table.setBackground("white");
                table.setColor(bgColor);
                table.add(label).pad(3.0f);
                if (styleProperty == null) {
                    table.addCaptureListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            event.setBubbles(false);
                            populate();
                        }
                    });
                    table.addCaptureListener(new InputListener() {
                        @Override
                        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                            event.setBubbles(false);
                            return true;
                        }
                    });
                }
                button.add(table).growX();
                
                label = new Label("(" + ((int)(color.color.r * 255)) + ", " + ((int)(color.color.g * 255)) + ", " + ((int)(color.color.b * 255)) + ", " + ((int)(color.color.a * 255)) + ")", skin, "white");
                label.setTouchable(Touchable.disabled);
                label.setAlignment(Align.center);
                if (styleProperty == null) {
                    label.addCaptureListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            event.setBubbles(false);
                            populate();
                        }
                    });
                    label.addCaptureListener(new InputListener() {
                        @Override
                        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                            event.setBubbles(false);
                            return true;
                        }

                    });
                }
                button.add(label).padLeft(5.0f).minWidth(160.0f);
                
                Button closeButton = new Button(skin, "close");
                final ColorData deleteColor = color;
                closeButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        colors.removeValue(deleteColor, true);
                        
                        for (Array<StyleData> datas : JsonData.getInstance().getClassStyleMap().values()) {
                            for (StyleData data : datas) {
                                for (StyleProperty property : data.properties.values()) {
                                    if (property != null && property.type.equals(Color.class) && property.value != null && property.value.equals(deleteColor.getName())) {
                                        property.value = null;
                                    }
                                }
                            }
                        }
                        
                        Main.instance.clearUndoables();
                        
                        PanelStyleProperties.instance.populate(PanelClassBar.instance.getStyleSelectBox().getSelected());
                        PanelPreviewProperties.instance.render();
                        
                        event.setBubbles(false);
                        populate();
                    }
                });
                closeButton.addListener(new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        event.setBubbles(false);
                        return true;
                    }
                    
                });
                
                button.add(closeButton).padLeft(5.0f);
                if (styleProperty == null) {
                    button.setTouchable(Touchable.childrenOnly);
                } else {
                    setObject(button, color);
                    final ColorData result = color;
                    button.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            result(result);
                            hide();
                        }
                    });
                }
                colorTable.add(button).growX();
                colorTable.row();
            }
        } else {
            colorTable.add(new Label("No colors have been set!", skin, "error"));
        }
    }
    
    private boolean newColor(String name, Color color) {
        if (ColorData.validate(name)) {
            try {
                ProjectData.instance().setChangesSaved(false);
                colors.add(new ColorData(name, color));
                sortBySelectedMode();
                populate();
                return true;
            } catch (Exception e) {
                Gdx.app.log(getClass().getName(), "Error trying to add color.", e);
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    protected void result(Object object) {
        if (styleProperty != null) {
            if (object instanceof ColorData) {
                ProjectData.instance().setChangesSaved(false);
                ColorData color = (ColorData) object;
                PanelStatusBar.instance.message("Selected Color: " + color.getName());
                styleProperty.value = color.getName();
                PanelStyleProperties.instance.populate(PanelClassBar.instance.getStyleSelectBox().getSelected());
            } else if (object instanceof Boolean) {
                if ((boolean) object) {
                    styleProperty.value = null;
                    PanelStyleProperties.instance.populate(PanelClassBar.instance.getStyleSelectBox().getSelected());
                } else {
                    boolean hasColor = false;
                    for (ColorData color : JsonData.getInstance().getColors()) {
                        if (color.getName().equals(styleProperty.value)) {
                            hasColor = true;
                            break;
                        }
                    }

                    if (!hasColor) {
                        styleProperty.value = null;
                        PanelStyleProperties.instance.populate(PanelClassBar.instance.getStyleSelectBox().getSelected());
                    }
                }
            }
        }
        
        if (listener != null) {
            listener.handle(null);
        }
    }
    
    private void sortBySelectedMode() {
        switch (selectBox.getSelectedIndex()) {
            case 0:
                sortFontsAZ();
                break;
            case 1:
                sortFontsZA();
                break;
        }
    }
    
    private void sortFontsAZ() {
        Sort.instance().sort(colors, new Comparator<ColorData>() {
            @Override
            public int compare(ColorData o1, ColorData o2) {
                return o1.toString().compareToIgnoreCase(o2.toString());
            }
        });
        populate();
    }
    
    private void sortFontsZA() {
        Sort.instance().sort(colors, new Comparator<ColorData>() {
            @Override
            public int compare(ColorData o1, ColorData o2) {
                return o1.toString().compareToIgnoreCase(o2.toString()) * -1;
            }
        });
        populate();
    }
}
