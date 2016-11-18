/**
 * *****************************************************************************
 * MIT License
 *
 * Copyright (c) 2016 Raymond Buckley
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
 *****************************************************************************
 */
package com.ray3k.skincomposer.panel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane.SplitPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip.TextTooltipStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TooltipManager;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.TreeStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.ray3k.skincomposer.BrowseField;
import com.ray3k.skincomposer.BrowseField.BrowseFieldStyle;
import com.ray3k.skincomposer.IbeamListener;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.Spinner;
import com.ray3k.skincomposer.Spinner.SpinnerStyle;
import com.ray3k.skincomposer.data.AtlasData;
import com.ray3k.skincomposer.data.ColorData;
import com.ray3k.skincomposer.data.DrawableData;
import com.ray3k.skincomposer.data.FontData;
import com.ray3k.skincomposer.data.JsonData;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.DialogColorPicker;
import com.ray3k.skincomposer.dialog.DialogError;

public class PanelPreviewProperties {

    private Skin skin;
    private Stage stage;
    private Table table;
    public static PanelPreviewProperties instance;
    private SpinnerStyle spinnerStyle;
    private BrowseFieldStyle colorFieldStyle;
    private String paragraphSample;
    private String textSample;
    private String paragraphExtendedSample;
    private ObjectMap<String, Object> properties = new ObjectMap<>();
    private TextureAtlas atlas;
    private ObjectMap<String, Drawable> drawablePairs = new ObjectMap<>();
    private Array<BitmapFont> fonts = new Array<>();
    private SelectBox<String> sizeSelectBox;
    private final String[] defaultSizes = {"small", "default", "large", "growX", "growY", "grow", "custom"};
    private static Color bgColor;

    public PanelPreviewProperties(Table table, Skin skin, Stage stage) {
        instance = this;
        this.table = table;
        this.skin = skin;
        this.stage = stage;
        spinnerStyle = new SpinnerStyle(skin.get("spinner-minus", Button.ButtonStyle.class), skin.get("spinner-plus", Button.ButtonStyle.class), skin.get("spinner", TextFieldStyle.class));
        ImageButton.ImageButtonStyle imageButtonStyle = new ImageButton.ImageButtonStyle(skin.get("orange-small", TextButton.TextButtonStyle.class));
        imageButtonStyle.imageUp = skin.getDrawable("image-color-wheel");
        colorFieldStyle = new BrowseField.BrowseFieldStyle(imageButtonStyle, skin.get("alt", TextFieldStyle.class), skin.get("default", LabelStyle.class));
        textSample = "Lorem ipsum dolor sit";
        paragraphSample = "Lorem ipsum dolor sit"
                + "amet, consectetur adipiscing elit, sed do eiusmod"
                + "tempor incididunt ut labore et dolore magna aliqua.\n"
                + "Ut enim ad minim veniam, quis nostrud exercitation"
                + "ullamco laboris nisi ut aliquip ex ea commodo"
                + "consequat.\nDuis aute irure dolor in reprehenderit in"
                + "voluptate velit esse cillum dolore eu fugiat nulla"
                + "pariatur.\nExcepteur sint occaecat cupidatat non"
                + "proident, sunt in culpa qui officia deserunt mollit"
                + "anim id est laborum.";
        paragraphExtendedSample = paragraphSample + "\n\n\n" + paragraphSample + "\n\n\n" + paragraphSample + "\n\n\n" + paragraphSample;
        produceAtlas();
        populate();
    }

    public void populate() {
        table.clear();

        properties.clear();

        Table t = new Table();
        table.add(t).center().expand();
        t.defaults().pad(3.0f);
        
        int classIndex = PanelClassBar.instance.classSelectBox.getSelectedIndex();
        if (classIndex >= StyleData.CLASSES.length) {
            populateThirdParty(t);
        } else {
            populateClassic(t);
        }

        render();
    }
    
    private void populateClassic(Table t) {
        t.add(new Label("Stage Color: ", skin));
        BrowseField browseField = new BrowseField(null, colorFieldStyle);
        browseField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Main.instance.showDialogColorPicker((Color) properties.get("bgcolor"), new DialogColorPicker.ColorListener() {
                    @Override
                    public void selected(Color color) {
                        if (color != null) {
                            browseField.getTextField().setText((int) (color.r * 255) + "," + (int) (color.g * 255) + "," + (int) (color.b * 255) + "," + (int) (color.a * 255));
                            properties.put("bgcolor", color);
                            bgColor = color;
                            render();
                        }
                    }
                });
            }
        });
        t.add(browseField).growX();
        if (bgColor == null) {
            bgColor = Color.WHITE;
        }
        properties.put("bgcolor", bgColor);
        browseField.getTextField().setText((int) (bgColor.r * 255) + "," + (int) (bgColor.g * 255) + "," + (int) (bgColor.b * 255) + "," + (int) (bgColor.a * 255));

        t.row();
        t.add(new Label("Size: ", skin)).right();

        sizeSelectBox = new SelectBox<>(skin, "slim");
        sizeSelectBox.setItems(defaultSizes);
        sizeSelectBox.setSelectedIndex(1);
        t.add(sizeSelectBox).growX().minWidth(200.0f);

        if (PanelClassBar.instance.classSelectBox.getSelectedIndex() >= 0) {
            populateClassicExtended(PanelClassBar.instance.classSelectBox.getSelectedIndex(), t);
        }

        sizeSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                properties.put("size", sizeSelectBox.getSelectedIndex());
                if (sizeSelectBox.getSelectedIndex() != 7) {
                    render();
                }
            }
        });
        properties.put("size", sizeSelectBox.getSelectedIndex());
    }

    private void populateClassicExtended(int classIndex, Table t) {
        Class clazz = StyleData.CLASSES[classIndex];
        if (clazz.equals(Button.class)) {
            t.row();
            t.add(new Label("Disabled: ", skin)).right();
            CheckBox disabledCheckBox = new CheckBox("", skin, "switch-text");
            disabledCheckBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("disabled", disabledCheckBox.isChecked());
                    render();
                }
            });
            properties.put("disabled", disabledCheckBox.isChecked());
            t.add(disabledCheckBox).left();
        } else if (clazz.equals(CheckBox.class)) {
            t.row();
            t.add(new Label("Disabled: ", skin)).right();
            CheckBox disabledCheckBox = new CheckBox("", skin, "switch-text");
            disabledCheckBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("disabled", disabledCheckBox.isChecked());
                    render();
                }
            });
            properties.put("disabled", disabledCheckBox.isChecked());
            t.add(disabledCheckBox).left();

            t.row();
            t.add(new Label("Text: ", skin));
            TextField previewTextField = new TextField(textSample, skin);
            previewTextField.setFocusTraversal(false);
            previewTextField.addListener(IbeamListener.get());
            previewTextField.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("text", previewTextField.getText());
                    render();
                }
            });
            properties.put("text", previewTextField.getText());
            t.add(previewTextField).growX();

        } else if (clazz.equals(ImageButton.class)) {
            t.row();
            t.add(new Label("Disabled: ", skin)).right();
            CheckBox disabledCheckBox = new CheckBox("", skin, "switch-text");
            disabledCheckBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("disabled", disabledCheckBox.isChecked());
                    render();
                }
            });
            properties.put("disabled", disabledCheckBox.isChecked());
            t.add(disabledCheckBox).left();
        } else if (clazz.equals(ImageTextButton.class)) {
            t.row();
            t.add(new Label("Disabled: ", skin)).right();
            CheckBox disabledCheckBox = new CheckBox("", skin, "switch-text");
            disabledCheckBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("disabled", disabledCheckBox.isChecked());
                    render();
                }
            });
            properties.put("disabled", disabledCheckBox.isChecked());
            t.add(disabledCheckBox).left();

            t.row();
            t.add(new Label("Text: ", skin));
            TextField previewTextField = new TextField(textSample, skin);
            previewTextField.setFocusTraversal(false);
            previewTextField.addListener(IbeamListener.get());
            previewTextField.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("text", previewTextField.getText());
                    render();
                }
            });
            properties.put("text", previewTextField.getText());
            t.add(previewTextField).growX();

        } else if (clazz.equals(Label.class)) {
            t.row();
            t.add(new Label("Text: ", skin));
            TextField previewTextField = new TextField(textSample, skin);
            previewTextField.setFocusTraversal(false);
            previewTextField.addListener(IbeamListener.get());
            previewTextField.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("text", previewTextField.getText());
                    render();
                }
            });
            properties.put("text", previewTextField.getText());
            t.add(previewTextField).growX();

        } else if (clazz.equals(List.class)) {
            t.row();
            t.add(new Label("List Items: ", skin)).right();
            TextArea listItemsTextArea = new TextArea("Lorem ipsum\ndolor sit\namet, consectetur", skin);
            listItemsTextArea.setFocusTraversal(false);
            listItemsTextArea.setPrefRows(3);
            listItemsTextArea.addListener(IbeamListener.get());
            listItemsTextArea.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("text", listItemsTextArea.getText());
                    render();
                }
            });
            properties.put("text", listItemsTextArea.getText());
            t.add(listItemsTextArea).growX();

        } else if (clazz.equals(ProgressBar.class)) {
            t.row();
            t.add(new Label("Disabled: ", skin)).right();
            CheckBox disabledCheckBox = new CheckBox("", skin, "switch-text");
            disabledCheckBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("disabled", disabledCheckBox.isChecked());
                    render();
                }
            });
            properties.put("disabled", disabledCheckBox.isChecked());
            t.add(disabledCheckBox).left();

            t.row();
            t.add(new Label("Value: ", skin)).right();
            Spinner valueSpinner = new Spinner(0.0f, 1.0f, false, spinnerStyle);
            valueSpinner.getTextField().setFocusTraversal(false);
            valueSpinner.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("value", valueSpinner.getValue());
                    render();
                }
            });
            properties.put("value", valueSpinner.getValue());
            t.add(valueSpinner).growX();

            t.row();
            t.add(new Label("Minimum: ", skin)).right();
            Spinner minimumSpinner = new Spinner(0.0f, 1.0f, false, spinnerStyle);
            minimumSpinner.getTextField().setFocusTraversal(false);
            minimumSpinner.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("minimum", minimumSpinner.getValue());
                    render();
                }
            });
            properties.put("minimum", minimumSpinner.getValue());
            t.add(minimumSpinner).growX();

            t.row();
            t.add(new Label("Maximum: ", skin)).right();
            Spinner maximumSpinner = new Spinner(100.0f, 1.0f, false, spinnerStyle);
            maximumSpinner.getTextField().setFocusTraversal(false);
            maximumSpinner.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("maximum", maximumSpinner.getValue());
                    render();
                }
            });
            properties.put("maximum", maximumSpinner.getValue());
            t.add(maximumSpinner).growX();

            t.row();
            t.add(new Label("Increment: ", skin)).right();
            Spinner incrementSpinner = new Spinner(1.0f, 1.0f, false, spinnerStyle);
            incrementSpinner.getTextField().setFocusTraversal(false);
            incrementSpinner.setMinimum(1);
            incrementSpinner.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("increment", incrementSpinner.getValue());
                    render();
                }
            });
            properties.put("increment", incrementSpinner.getValue());
            t.add(incrementSpinner).growX();

            t.row();
            t.add(new Label("Orientation: ", skin)).right();
            SelectBox<String> selectBox = new SelectBox<>(skin, "slim");
            selectBox.setItems(new String[]{"Horizontal", "Vertical"});
            if (PanelClassBar.instance.getStyleSelectBox().getSelected().getName().contains("vert")) {
                properties.put("orientation", true);
                selectBox.setSelectedIndex(1);
            } else {
                properties.put("orientation", false);
            }
            selectBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    if (selectBox.getSelectedIndex() == 0) {
                        properties.put("orientation", false);
                    } else {
                        properties.put("orientation", true);
                    }
                    render();
                }
            });
            t.add(selectBox).growX();

        } else if (clazz.equals(ScrollPane.class)) {
            t.row();
            t.add(new Label("Scrollbars On Top: ", skin)).right();
            CheckBox onTopCheckBox = new CheckBox("", skin, "switch-text");
            onTopCheckBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("scrollbarsOnTop", onTopCheckBox.isChecked());
                    render();
                }
            });
            t.add(onTopCheckBox).left();
            properties.put("scrollbarsOnTop", onTopCheckBox.isChecked());

            t.row();
            t.add(new Label("H ScrollBar Position: ", skin)).right();
            SelectBox<String> hScrollPosBox = new SelectBox<>(skin, "slim");
            hScrollPosBox.setItems(new String[]{"Top", "Bottom"});
            hScrollPosBox.setSelectedIndex(1);
            hScrollPosBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    if (hScrollPosBox.getSelectedIndex() == 0) {
                        properties.put("hScrollBarPosition", false);
                    } else {
                        properties.put("hScrollBarPosition", true);
                    }
                    render();
                }
            });
            t.add(hScrollPosBox).growX();
            properties.put("hScrollBarPosition", true);

            t.row();
            t.add(new Label("V ScrollBar Position: ", skin)).right();
            SelectBox<String> vScrollPosBox = new SelectBox<>(skin, "slim");
            vScrollPosBox.setItems(new String[]{"Left", "Right"});
            vScrollPosBox.setSelectedIndex(1);
            vScrollPosBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    if (vScrollPosBox.getSelectedIndex() == 0) {
                        properties.put("vScrollBarPosition", false);
                    } else {
                        properties.put("vScrollBarPosition", true);
                    }
                    render();
                }
            });
            t.add(vScrollPosBox).growX();
            properties.put("vScrollBarPosition", true);

            t.row();
            t.add(new Label("H Scrolling Disabled: ", skin)).right();
            CheckBox hScrollCheckBox = new CheckBox("", skin, "switch-text");
            hScrollCheckBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("hScrollDisabled", hScrollCheckBox.isChecked());
                    render();
                }
            });
            t.add(hScrollCheckBox).left();
            properties.put("hScrollDisabled", hScrollCheckBox.isChecked());

            t.row();
            t.add(new Label("V Scrolling Disabled: ", skin)).right();
            CheckBox vScrollCheckBox = new CheckBox("", skin, "switch-text");
            vScrollCheckBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("vScrollDisabled", vScrollCheckBox.isChecked());
                    render();
                }
            });
            t.add(vScrollCheckBox).left();
            properties.put("vScrollDisabled", vScrollCheckBox.isChecked());

            t.row();
            t.add(new Label("Force H Scroll: ", skin)).right();
            CheckBox forceHScrollCheckBox = new CheckBox("", skin, "switch-text");
            forceHScrollCheckBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("forceHscroll", forceHScrollCheckBox.isChecked());
                    render();
                }
            });
            t.add(forceHScrollCheckBox).left();
            properties.put("forceHscroll", forceHScrollCheckBox.isChecked());

            t.row();
            t.add(new Label("Force V Scroll: ", skin)).right();
            CheckBox forceVScrollCheckBox = new CheckBox("", skin, "switch-text");
            forceVScrollCheckBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("forceVscroll", forceVScrollCheckBox.isChecked());
                    render();
                }
            });
            t.add(forceVScrollCheckBox).left();
            properties.put("forceVscroll", forceVScrollCheckBox.isChecked());

            t.row();
            t.add(new Label("Variable Size Knobs: ", skin)).right();
            CheckBox variableSizeKnobsCheckBox = new CheckBox("", skin, "switch-text");
            variableSizeKnobsCheckBox.setChecked(true);
            variableSizeKnobsCheckBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("variableSizeKnobs", variableSizeKnobsCheckBox.isChecked());
                    render();
                }
            });
            t.add(variableSizeKnobsCheckBox).left();
            properties.put("variableSizeKnobs", variableSizeKnobsCheckBox.isChecked());

            t.row();
            t.add(new Label("H Overscroll: ", skin)).right();
            CheckBox hOverscrollCheckBox = new CheckBox("", skin, "switch-text");
            hOverscrollCheckBox.setChecked(true);
            hOverscrollCheckBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("hOverscroll", hOverscrollCheckBox.isChecked());
                    render();
                }
            });
            t.add(hOverscrollCheckBox).left();
            properties.put("hOverscroll", hOverscrollCheckBox.isChecked());

            t.row();
            t.add(new Label("V Overscroll: ", skin)).right();
            CheckBox vOverscrollCheckBox = new CheckBox("", skin, "switch-text");
            vOverscrollCheckBox.setChecked(true);
            vOverscrollCheckBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("vOverscroll", vOverscrollCheckBox.isChecked());
                    render();
                }
            });
            t.add(vOverscrollCheckBox).left();
            properties.put("vOverscroll", vOverscrollCheckBox.isChecked());

            t.row();
            t.add(new Label("Fade Scroll Bars: ", skin)).right();
            CheckBox fadeScrollCheckBox = new CheckBox("", skin, "switch-text");
            fadeScrollCheckBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("fadeScroll", fadeScrollCheckBox.isChecked());
                    render();
                }
            });
            t.add(fadeScrollCheckBox).left();
            properties.put("fadeScroll", fadeScrollCheckBox.isChecked());

            t.row();
            t.add(new Label("Smooth Scrolling: ", skin)).right();
            CheckBox smoothScrollCheckBox = new CheckBox("", skin, "switch-text");
            smoothScrollCheckBox.setChecked(true);
            smoothScrollCheckBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("smoothScroll", smoothScrollCheckBox.isChecked());
                    render();
                }
            });
            t.add(smoothScrollCheckBox).left();
            properties.put("smoothScroll", smoothScrollCheckBox.isChecked());

            t.row();
            t.add(new Label("Flick Scroll: ", skin)).right();
            CheckBox flickScrollCheckBox = new CheckBox("", skin, "switch-text");
            flickScrollCheckBox.setChecked(true);
            flickScrollCheckBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("flickScroll", flickScrollCheckBox.isChecked());
                    render();
                }
            });
            t.add(flickScrollCheckBox).left();
            properties.put("flickScroll", flickScrollCheckBox.isChecked());

            t.row();
            t.add(new Label("Clamp: ", skin)).right();
            CheckBox clampCheckBox = new CheckBox("", skin, "switch-text");
            clampCheckBox.setChecked(true);
            clampCheckBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("clamp", clampCheckBox.isChecked());
                    render();
                }
            });
            t.add(clampCheckBox).left();
            properties.put("clamp", clampCheckBox.isChecked());

            t.row();
            t.add(new Label("Sample Text: ", skin)).right();
            TextArea previewTextArea = new TextArea(paragraphExtendedSample, skin);
            previewTextArea.setFocusTraversal(false);
            previewTextArea.setPrefRows(5);
            previewTextArea.addListener(IbeamListener.get());
            previewTextArea.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("text", previewTextArea.getText());
                    render();
                }
            });
            properties.put("text", previewTextArea.getText());
            t.add(previewTextArea).growX();

            sizeSelectBox.setSelectedIndex(2);
        } else if (clazz.equals(SelectBox.class)) {
            t.row();
            t.add(new Label("Disabled: ", skin)).right();
            CheckBox disabledCheckBox = new CheckBox("", skin, "switch-text");
            disabledCheckBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("disabled", disabledCheckBox.isChecked());
                    render();
                }
            });
            properties.put("disabled", disabledCheckBox.isChecked());
            t.add(disabledCheckBox).left();

            t.row();
            t.add(new Label("Max List Count: ", skin)).right();
            Spinner spinner = new Spinner(3, 1, true, spinnerStyle);
            spinner.getTextField().setFocusTraversal(false);
            spinner.setMinimum(1);
            t.add(spinner).growX();

            t.row();
            t.add(new Label("List Items: ", skin)).right();
            TextArea listItemsTextArea = new TextArea("Lorem ipsum\ndolor sit\namet, consectetur", skin);
            listItemsTextArea.setFocusTraversal(false);
            listItemsTextArea.setPrefRows(3);
            listItemsTextArea.addListener(IbeamListener.get());
            listItemsTextArea.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("text", listItemsTextArea.getText());
                    render();
                }
            });
            properties.put("text", listItemsTextArea.getText());
            t.add(listItemsTextArea).growX();

        } else if (clazz.equals(Slider.class)) {
            t.row();
            t.add(new Label("Disabled: ", skin)).right();
            CheckBox disabledCheckBox = new CheckBox("", skin, "switch-text");
            disabledCheckBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("disabled", disabledCheckBox.isChecked());
                    render();
                }
            });
            properties.put("disabled", disabledCheckBox.isChecked());
            t.add(disabledCheckBox).left();

            t.row();
            t.add(new Label("Minimum: ", skin)).right();
            Spinner minimumSpinner = new Spinner(0.0f, 1.0f, false, spinnerStyle);
            minimumSpinner.getTextField().setFocusTraversal(false);
            minimumSpinner.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("minimum", minimumSpinner.getValue());
                    render();
                }
            });
            t.add(minimumSpinner).growX();
            properties.put("minimum", minimumSpinner.getValue());

            t.row();
            t.add(new Label("Maximum: ", skin)).right();
            Spinner maximumSpinner = new Spinner(100.0f, 1.0f, false, spinnerStyle);
            maximumSpinner.getTextField().setFocusTraversal(false);
            maximumSpinner.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("maximum", maximumSpinner.getValue());
                    render();
                }
            });
            t.add(maximumSpinner).growX();
            properties.put("maximum", maximumSpinner.getValue());

            t.row();
            t.add(new Label("Increment: ", skin)).right();
            Spinner incrementSpinner = new Spinner(1.0f, 1.0f, false, spinnerStyle);
            incrementSpinner.getTextField().setFocusTraversal(false);
            incrementSpinner.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("increment", incrementSpinner.getValue());
                    render();
                }
            });
            t.add(incrementSpinner).growX();
            properties.put("increment", incrementSpinner.getValue());

            t.row();
            t.add(new Label("Orientation: ", skin)).right();
            SelectBox<String> selectBox = new SelectBox<>(skin, "slim");
            selectBox.setItems(new String[]{"Horizontal", "Vertical"});
            if (PanelClassBar.instance.getStyleSelectBox().getSelected().getName().contains("vert")) {
                properties.put("orientation", true);
                selectBox.setSelectedIndex(1);
            } else {
                properties.put("orientation", false);
            }
            selectBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    if (selectBox.getSelectedIndex() == 0) {
                        properties.put("orientation", false);
                    } else {
                        properties.put("orientation", true);
                    }
                    render();
                }
            });
            t.add(selectBox).growX();
        } else if (clazz.equals(SplitPane.class)) {
            t.row();
            t.add(new Label("Orientation: ", skin)).right();
            SelectBox<String> selectBox = new SelectBox<>(skin, "slim");
            selectBox.setItems(new String[]{"Horizontal", "Vertical"});
            if (PanelClassBar.instance.getStyleSelectBox().getSelected().getName().contains("vert")) {
                properties.put("orientation", true);
                selectBox.setSelectedIndex(1);
            } else {
                properties.put("orientation", false);
            }
            selectBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    if (selectBox.getSelectedIndex() == 0) {
                        properties.put("orientation", false);
                    } else {
                        properties.put("orientation", true);
                    }
                    render();
                }
            });
            t.add(selectBox).growX();

            t.row();
            t.add(new Label("Sample Text: ", skin)).right();
            TextArea textArea = new TextArea(paragraphSample, skin);
            textArea.setFocusTraversal(false);
            textArea.setPrefRows(5);
            textArea.addListener(IbeamListener.get());
            textArea.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("text", textArea.getText());
                    render();
                }
            });
            properties.put("text", textArea.getText());
            t.add(textArea).growX();

            sizeSelectBox.setSelectedIndex(2);
        } else if (clazz.equals(TextButton.class)) {
            t.row();
            t.add(new Label("Disabled: ", skin)).right();
            CheckBox disabledCheckBox = new CheckBox("", skin, "switch-text");
            disabledCheckBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("disabled", disabledCheckBox.isChecked());
                    render();
                }
            });
            properties.put("disabled", disabledCheckBox.isChecked());
            t.add(disabledCheckBox).left();

            t.row();
            t.add(new Label("Text: ", skin));
            TextField previewTextField = new TextField(textSample, skin);
            previewTextField.setFocusTraversal(false);
            previewTextField.addListener(IbeamListener.get());
            previewTextField.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("text", previewTextField.getText());
                    render();
                }
            });
            properties.put("text", previewTextField.getText());
            t.add(previewTextField).growX();

        } else if (clazz.equals(TextField.class)) {
            t.row();
            t.add(new Label("Disabled: ", skin)).right();
            CheckBox disabledCheckBox = new CheckBox("", skin, "switch-text");
            disabledCheckBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("disabled", disabledCheckBox.isChecked());
                    render();
                }
            });
            properties.put("disabled", disabledCheckBox.isChecked());
            t.add(disabledCheckBox).left();

            t.row();
            t.add(new Label("Password Mode: ", skin)).right();
            CheckBox checkBox = new CheckBox("", skin, "switch-text");
            checkBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("passwordMode", checkBox.isChecked());
                    render();
                }
            });
            t.add(checkBox).left();
            properties.put("passwordMode", checkBox.isChecked());

            t.row();
            t.add(new Label("Password Character: ", skin));
            TextField pcTextField = new TextField("*", skin);
            pcTextField.setFocusTraversal(false);
            pcTextField.addListener(IbeamListener.get());
            pcTextField.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("password", pcTextField.getText());
                    render();
                }
            });
            properties.put("password", pcTextField.getText());
            t.add(pcTextField).growX();

            t.row();
            t.add(new Label("Text Alignment: ", skin)).right();
            SelectBox<String> selectBox = new SelectBox<>(skin, "slim");
            selectBox.setItems(new String[]{"Left", "Center", "Right"});
            selectBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    switch (selectBox.getSelectedIndex()) {
                        case 0:
                            properties.put("alignment", Align.left);
                            break;
                        case 1:
                            properties.put("alignment", Align.center);
                            break;
                        case 2:
                            properties.put("alignment", Align.right);
                            break;
                    }
                }
            });
            t.add(selectBox).growX();
            properties.put("alignment", Align.left);

            t.row();
            t.add(new Label("Text: ", skin));
            TextField previewTextField = new TextField(textSample, skin);
            previewTextField.setFocusTraversal(false);
            previewTextField.addListener(IbeamListener.get());
            previewTextField.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("text", previewTextField.getText());
                    render();
                }
            });
            properties.put("text", previewTextField.getText());
            t.add(previewTextField).growX();

            t.row();
            t.add(new Label("Message Text: ", skin));
            TextField messageTextField = new TextField(textSample, skin);
            messageTextField.setFocusTraversal(false);
            messageTextField.addListener(IbeamListener.get());
            messageTextField.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("message", messageTextField.getText());
                    render();
                }
            });
            properties.put("message", messageTextField.getText());
            t.add(messageTextField).growX();

        } else if (clazz.equals(TextTooltip.class)) {
            t.row();
            t.add(new Label("Text: ", skin));
            TextField previewTextField = new TextField(textSample, skin);
            previewTextField.setFocusTraversal(false);
            previewTextField.addListener(IbeamListener.get());
            previewTextField.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("text", previewTextField.getText());
                    render();
                }
            });
            properties.put("text", previewTextField.getText());
            t.add(previewTextField).growX();

        } else if (clazz.equals(Touchpad.class)) {

        } else if (clazz.equals(Tree.class)) {
            t.row();
            t.add(new Label("Icon Spacing: ", skin)).right();
            Spinner spinner = new Spinner(0.0, 1.0, false, spinnerStyle);
            spinner.getTextField().setFocusTraversal(false);
            spinner.setMinimum(1);
            t.add(spinner).growX();

            t.row();
            t.add(new Label("Y Spacing: ", skin)).right();
            spinner = new Spinner(0.0, 1.0, false, spinnerStyle);
            spinner.getTextField().setFocusTraversal(false);
            spinner.setMinimum(1);
            t.add(spinner).growX();

        } else if (clazz.equals(Window.class)) {
            t.row();
            t.add(new Label("Title Text: ", skin));
            TextField previewTextField = new TextField(textSample, skin);
            previewTextField.setFocusTraversal(false);
            previewTextField.addListener(IbeamListener.get());
            previewTextField.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("title", previewTextField.getText());
                    render();
                }
            });
            properties.put("title", previewTextField.getText());
            t.add(previewTextField).growX();

            t.row();
            t.add(new Label("Sample Text Color: ", skin));
            BrowseField textColorField = new BrowseField(null, colorFieldStyle);
            t.add(textColorField).growX();

            t.row();
            t.add(new Label("Sample Text: ", skin)).right();
            TextArea textArea = new TextArea(paragraphSample, skin);
            textArea.setFocusTraversal(false);
            textArea.setPrefRows(5);
            textArea.addListener(IbeamListener.get());
            textArea.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    properties.put("text", textArea.getText());
                    render();
                }
            });
            properties.put("text", textArea.getText());
            t.add(textArea).growX();
        }
    }

    private void populateThirdParty(Table t) {
        t.add(new Label("No preview properties are available for third party classes.", skin));
        render();
    }
    
    private <T> T createStyle(Class<T> clazz, StyleData styleData) {
        T returnValue = null;
        try {
            returnValue = ClassReflection.newInstance(clazz);
            Field[] fields = ClassReflection.getFields(clazz);
            for (Field field : fields) {
                Object value = styleData.getProperties().get(field.getName()).value;
                if (value != null) {
                    if (field.getType().equals(Drawable.class)) {
                        field.set(returnValue, drawablePairs.get((String) value));
                    } else if (field.getType().equals(Color.class)) {
                        for (ColorData data : JsonData.getInstance().getColors()) {
                            if (value.equals(data.getName())) {
                                field.set(returnValue, data.color);
                                break;
                            }
                        }
                    } else if (field.getType().equals(BitmapFont.class)) {
                        for (FontData data : JsonData.getInstance().getFonts()) {
                            if (value.equals(data.getName())) {
                                BitmapFont font = new BitmapFont(data.file);
                                fonts.add(font);
                                field.set(returnValue, font);
                            }
                        }
                    } else if (field.getType().equals(Float.TYPE)) {
                        field.set(returnValue, (float) value);
                    } else if (field.getType().equals(ListStyle.class)) {
                        Array<StyleData> datas = JsonData.getInstance().getClassStyleMap().get(List.class);

                        for (StyleData data : datas) {
                            if (value.equals(data.getName())) {
                                ListStyle style = createStyle(ListStyle.class, data);
                                field.set(returnValue, style);
                                break;
                            }
                        }
                    } else if (field.getType().equals(ScrollPaneStyle.class)) {
                        Array<StyleData> datas = JsonData.getInstance().getClassStyleMap().get(ScrollPane.class);

                        for (StyleData data : datas) {
                            if (value.equals(data.getName())) {
                                ScrollPaneStyle style = createStyle(ScrollPaneStyle.class, data);
                                field.set(returnValue, style);
                                break;
                            }
                        }
                    } else if (field.getType().equals(LabelStyle.class)) {
                        Array<StyleData> datas = JsonData.getInstance().getClassStyleMap().get(Label.class);

                        for (StyleData data : datas) {
                            if (value.equals(data.getName())) {
                                LabelStyle style = createStyle(LabelStyle.class, data);
                                field.set(returnValue, style);
                                break;
                            }
                        }
                    }
                }
            }
        } finally {
            return returnValue;
        }
    }

    public void render() {
        int classIndex = PanelClassBar.instance.classSelectBox.getSelectedIndex();
        if (classIndex < StyleData.CLASSES.length) {
            renderClassic();
        } else {
            renderThirdParty();
        }
    }

    public void renderClassic() {
        Table t = PanelPreview.instance.contentTable;
        t.clear();
        t.setColor((Color) properties.get("bgcolor"));

        for (BitmapFont font : fonts) {
            font.dispose();
        }

        if (PanelClassBar.instance.classSelectBox.getSelectedIndex() >= 0) {
            StyleData styleData = PanelClassBar.instance.getStyleSelectBox().getSelected();
            Class clazz = StyleData.CLASSES[PanelClassBar.instance.classSelectBox.getSelectedIndex()];

            if (!styleData.hasMandatoryFields()) {
                Label label;
                if (clazz.equals(SelectBox.class)) {
                    label = new Label("Please fill all mandatory fields\n(Highlighted in Maroon)\n\nscrollStyle and listStyle\nmust already be defined", skin);
                } else if (clazz.equals(TextTooltip.class)) {
                    label = new Label("Please fill all mandatory fields\n(Highlighted in Maroon)\n\nlabel must already be defined", skin);
                } else {
                    label = new Label("Please fill all mandatory fields\n(Highlighted in Maroon)", skin);
                }
                label.setAlignment(Align.center);
                t.add(label);
            } else if (styleData.hasAllNullFields()) {
                Label label;
                label = new Label("All fields are empty!\nEmpty classes are not exported\nAdd style properties in the menu to the left", skin);
                label.setAlignment(Align.center);
                t.add(label);
            } else {
                Actor widget = null;
                if (clazz.equals(Button.class)) {
                    ButtonStyle style = createStyle(ButtonStyle.class, styleData);
                    widget = new Button(style);
                    ((Button) widget).setDisabled((boolean) properties.get("disabled"));
                } else if (clazz.equals(CheckBox.class)) {
                    CheckBoxStyle style = createStyle(CheckBoxStyle.class, styleData);
                    widget = new CheckBox("", style);
                    ((CheckBox) widget).setDisabled((boolean) properties.get("disabled"));
                    ((CheckBox) widget).setText((String) properties.get("text"));
                } else if (clazz.equals(ImageButton.class)) {
                    ImageButtonStyle style = createStyle(ImageButtonStyle.class, styleData);
                    widget = new ImageButton(style);
                    ((ImageButton) widget).setDisabled((boolean) properties.get("disabled"));
                } else if (clazz.equals(ImageTextButton.class)) {
                    ImageTextButtonStyle style = createStyle(ImageTextButtonStyle.class, styleData);
                    widget = new ImageTextButton("", style);
                    ((ImageTextButton) widget).setDisabled((boolean) properties.get("disabled"));
                    ((ImageTextButton) widget).setText((String) properties.get("text"));
                } else if (clazz.equals(Label.class)) {
                    LabelStyle style = createStyle(LabelStyle.class, styleData);
                    widget = new Label("", style);
                    ((Label) widget).setText((String) properties.get("text"));
                } else if (clazz.equals(List.class)) {
                    ListStyle style = createStyle(ListStyle.class, styleData);
                    widget = new List(style);
                    Array<String> items = new Array<>(((String) properties.get("text")).split("\\n"));
                    ((List) widget).setItems(items);
                } else if (clazz.equals(ProgressBar.class)) {
                    ProgressBarStyle style = createStyle(ProgressBarStyle.class, styleData);
                    widget = new ProgressBar((float) (double) properties.get("minimum"), (float) (double) properties.get("maximum"), (float) (double) properties.get("increment"), (boolean) properties.get("orientation"), style);
                    ((ProgressBar) widget).setValue((float) (double) properties.get("value"));
                    ((ProgressBar) widget).setDisabled((boolean) properties.get("disabled"));
                } else if (clazz.equals(ScrollPane.class)) {
                    ScrollPaneStyle style = createStyle(ScrollPaneStyle.class, styleData);
                    Label label = new Label("", skin);
                    widget = new ScrollPane(label, style);
                    ((ScrollPane) widget).setScrollbarsOnTop((boolean) properties.get("scrollbarsOnTop"));
                    ((ScrollPane) widget).setScrollBarPositions((boolean) properties.get("hScrollBarPosition"), (boolean) properties.get("vScrollBarPosition"));
                    ((ScrollPane) widget).setScrollingDisabled((boolean) properties.get("hScrollDisabled"), (boolean) properties.get("vScrollDisabled"));
                    ((ScrollPane) widget).setForceScroll((boolean) properties.get("forceHscroll"), (boolean) properties.get("forceVscroll"));
                    ((ScrollPane) widget).setVariableSizeKnobs((boolean) properties.get("variableSizeKnobs"));
                    ((ScrollPane) widget).setOverscroll((boolean) properties.get("hOverscroll"), (boolean) properties.get("vOverscroll"));
                    ((ScrollPane) widget).setFadeScrollBars((boolean) properties.get("fadeScroll"));
                    ((ScrollPane) widget).setSmoothScrolling((boolean) properties.get("smoothScroll"));
                    ((ScrollPane) widget).setFlickScroll((boolean) properties.get("flickScroll"));
                    ((ScrollPane) widget).setClamp((boolean) properties.get("clamp"));
                    label.setText((String) properties.get("text"));
                } else if (clazz.equals(SelectBox.class)) {
                    SelectBoxStyle style = createStyle(SelectBoxStyle.class, styleData);
                    widget = new SelectBox(style);
                    ((SelectBox) widget).setDisabled((boolean) properties.get("disabled"));
                    Array<String> items = new Array<>(((String) properties.get("text")).split("\\n"));
                    ((SelectBox) widget).setItems(items);
                } else if (clazz.equals(Slider.class)) {
                    SliderStyle style = createStyle(SliderStyle.class, styleData);
                    widget = new Slider((float) (double) properties.get("minimum"), (float) (double) properties.get("maximum"), (float) (double) properties.get("increment"), (boolean) properties.get("orientation"), style);
                    ((Slider) widget).setDisabled((boolean) properties.get("disabled"));
                } else if (clazz.equals(SplitPane.class)) {
                    SplitPaneStyle style = createStyle(SplitPaneStyle.class, styleData);
                    Label label1 = new Label("", skin);
                    Label label2 = new Label("", skin);
                    widget = new SplitPane(label1, label2, (boolean) properties.get("orientation"), style);
                    label1.setText((String) properties.get("text"));
                    label2.setText((String) properties.get("text"));
                } else if (clazz.equals(TextButton.class)) {
                    TextButtonStyle style = createStyle(TextButtonStyle.class, styleData);
                    widget = new TextButton("", style);
                    ((TextButton) widget).setDisabled((boolean) properties.get("disabled"));
                    ((TextButton) widget).setText((String) properties.get("text"));
                } else if (clazz.equals(TextField.class)) {
                    TextFieldStyle style = createStyle(TextFieldStyle.class, styleData);
                    widget = new TextField("", style);
                    ((TextField) widget).setFocusTraversal(false);
                    ((TextField) widget).setDisabled((boolean) properties.get("disabled"));
                    ((TextField) widget).setPasswordMode((boolean) properties.get("passwordMode"));
                    ((TextField) widget).setAlignment((int) properties.get("alignment"));
                    ((TextField) widget).setText((String) properties.get("text"));
                    ((TextField) widget).setMessageText((String) properties.get("message"));
                    String string = (String) properties.get("password");
                    if (string.length() > 0) {
                        ((TextField) widget).setPasswordCharacter(string.charAt(0));
                    }
                } else if (clazz.equals(TextTooltip.class)) {
                    TextTooltipStyle style = createStyle(TextTooltipStyle.class, styleData);

                    TooltipManager manager = new TooltipManager();
                    manager.animations = false;
                    manager.initialTime = 0.0f;
                    manager.resetTime = 0.0f;
                    manager.subsequentTime = 0.0f;
                    manager.hideAll();
                    manager.instant();
                    TextTooltip toolTip = new TextTooltip((String) properties.get("text"), manager, style);

                    widget = new Label("Hover over me", skin);
                    widget.addListener(toolTip);
                } else if (clazz.equals(Touchpad.class)) {
                    TouchpadStyle style = createStyle(TouchpadStyle.class, styleData);
                    widget = new Touchpad(0, style);
                } else if (clazz.equals(Tree.class)) {
                    TreeStyle style = createStyle(TreeStyle.class, styleData);
                    widget = new Tree(style);
                    String[] lines = {"this", "is", "a", "test"};
                    Node parentNode = null;
                    for (String line : lines) {
                        Label label = new Label(line, skin);
                        Node node = new Tree.Node(label);
                        if (parentNode == null) {
                            ((Tree) widget).add(node);
                        } else {
                            parentNode.add(node);
                        }
                        parentNode = node;
                    }
                } else if (clazz.equals(Window.class)) {
                    WindowStyle style = createStyle(WindowStyle.class, styleData);

                    Label sampleText = new Label("", skin);
                    sampleText.setText((String) properties.get("text"));

                    widget = new Window((String) properties.get("title"), style);
                    ((Window) widget).add(sampleText);
                }

                if (widget != null) {
                    switch ((int) properties.get("size")) {
                        case (0):
                            t.add(widget).size(10.0f);
                            sizeSelectBox.setItems(defaultSizes);
                            break;
                        case (1):
                            t.add(widget);
                            sizeSelectBox.setItems(defaultSizes);
                            break;
                        case (2):
                            t.add(widget).size(200.0f);
                            sizeSelectBox.setItems(defaultSizes);
                            break;
                        case (3):
                            t.add(widget).growX();
                            sizeSelectBox.setItems(defaultSizes);
                            break;
                        case (4):
                            t.add(widget).growY();
                            sizeSelectBox.setItems(defaultSizes);
                            break;
                        case (5):
                            t.add(widget).grow();
                            sizeSelectBox.setItems(defaultSizes);
                            break;
                        case (6):
                            Actor addWidget = widget;
                            TextField widthField = new TextField("", skin);
                            TextField heightField = new TextField("", skin);
                            Dialog dialog = new Dialog("Enter dimensions...", skin) {
                                @Override
                                protected void result(Object object) {
                                    if ((boolean) object) {
                                        t.add(addWidget).size(Integer.parseInt(widthField.getText()), Integer.parseInt(heightField.getText()));
                                        Array<String> items = new Array<>(defaultSizes);
                                        items.add(widthField.getText() + "x" + heightField.getText());
                                        properties.put("sizeX", Integer.parseInt(widthField.getText()));
                                        properties.put("sizeY", Integer.parseInt(heightField.getText()));
                                        sizeSelectBox.setItems(items);
                                        sizeSelectBox.setSelectedIndex(7);
                                    } else {
                                        sizeSelectBox.setSelectedIndex(1);
                                    }
                                }
                            };
                            dialog.text("Enter the preview dimensions: ");
                            dialog.getContentTable().row();
                            Table sizeTable = new Table();
                            sizeTable.add(widthField);
                            sizeTable.add(new Label(" x ", skin));
                            sizeTable.add(heightField);
                            dialog.getContentTable().add(sizeTable);
                            dialog.button("OK", true);
                            dialog.button("Cancel", false);
                            dialog.key(Keys.ESCAPE, false);
                            dialog.key(Keys.ENTER, true);
                            dialog.show(stage);
                            stage.setKeyboardFocus(widthField);
                            break;
                        case (7):
                            t.add(widget).size((int) properties.get("sizeX"), (int) properties.get("sizeY"));
                            break;
                    }
                }
            }
        }
    }

    public void renderThirdParty() {
        Table t = PanelPreview.instance.contentTable;
        t.clear();
        t.add(new Label("No preview is available for third party classes.", skin));
    }

    /**
     * Writes a TextureAtlas based on drawables list. Creates drawables to be
     * displayed on screen
     *
     * @return
     */
    public boolean produceAtlas() {
        try {
            if (atlas != null) {
                atlas.dispose();
                atlas = null;
            }

            if (!AtlasData.getInstance().atlasCurrent) {
                AtlasData.getInstance().writeAtlas();
                AtlasData.getInstance().atlasCurrent = true;
            }
            atlas = AtlasData.getInstance().getAtlas();

            for (DrawableData data : AtlasData.getInstance().getDrawables()) {
                String name = data.file.name();
                name = DrawableData.proper(name);

                Drawable drawable;
                if (data.file.name().matches(".*\\.9\\.[a-zA-Z0-9]*$")) {
                    drawable = new NinePatchDrawable(atlas.createPatch(name));
                    if (data.tint != null) {
                        drawable = ((NinePatchDrawable) drawable).tint(data.tint);
                    } else if (data.tintName != null) {
                        drawable = ((NinePatchDrawable) drawable).tint(JsonData.getInstance().getColorByName(data.tintName).color);
                    }
                } else {
                    drawable = new SpriteDrawable(atlas.createSprite(name));
                    if (data.tint != null) {
                        drawable = ((SpriteDrawable) drawable).tint(data.tint);
                    } else if (data.tintName != null) {
                        drawable = ((SpriteDrawable) drawable).tint(JsonData.getInstance().getColorByName(data.tintName).color);
                    }
                }

                drawablePairs.put(data.name, drawable);
            }
            return true;
        } catch (Exception e) {
            Gdx.app.error(getClass().getName(), "Error while attempting to generate drawables.", e);
            DialogError.showError("Atlas Error...", "Error while attempting to generate drawables.\n\nOpen log?");
            return false;
        }
    }
}
