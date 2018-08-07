/*
 * The MIT License
 *
 * Copyright 2018 Raymond Buckley.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.ray3k.skincomposer.dialog;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.Spinner;
import com.ray3k.skincomposer.data.ColorData;
import com.ray3k.skincomposer.data.FreeTypeFontData;
import com.ray3k.skincomposer.data.StyleProperty;
import com.ray3k.skincomposer.utils.Utils;
import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class DialogBitmapFont extends Dialog {
    private Main main;
    private Skin skin;
    private Table root;
    private Table buttons;
    private FreeTypeFontData data;
    private FileHandle target;
    private static DecimalFormat df;
    private Array<DialogBitmapFontListener> listeners;
    private TextFieldStyle previewStyle;
    private String previewText;
    
    public DialogBitmapFont(Main main) {
        super("Create new Bitmap Font", main.getSkin(), "bg");
        
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);
        df = new DecimalFormat("#.#", decimalFormatSymbols);

        this.data = new FreeTypeFontData();
        this.data.useCustomSerializer = true;

        FileHandle previewFontsPath = Main.appFolder.child("preview fonts");
        if (previewFontsPath.exists()) {
            FileHandle[] files = previewFontsPath.list("ttf");
            if (files.length > 0) {
                data.previewTTF = files[0].nameWithoutExtension();
            }
        }
        
        this.main = main;
        skin = main.getSkin();
        
        previewStyle = new TextFieldStyle(skin.get("free-type-preview", TextFieldStyle.class));
        previewText = "Lorem ipsum dolor sit";
        
        getTitleTable().pad(10.0f);
        
        root = getContentTable();
        buttons = getButtonTable();
        
        populate();
        
        updatePreviewAndOK();
        
        key(Keys.ESCAPE, false).key(Keys.ENTER, true);
        
        listeners = new Array<>();
    }

    @Override
    protected void result(Object object) {
        if ((Boolean) object) {
            data.writeFontToFile(main, target);

            for (DialogBitmapFontListener listener : listeners) {
                listener.fontAdded(target);
            }
        }
    }
    
    public void addListener(DialogBitmapFontListener listener) {
        listeners.add(listener);
    }

    private void populate() {
        root.pad(15.0f);
        
        Label label = new Label("Create a new Bitmap Font.", skin, "required");
        root.add(label);
        
        root.row();
        Image image = new Image(skin, "welcome-separator");
        image.setScaling(Scaling.stretch);
        root.add(image).growX().space(15.0f);
        
        root.row();
        var textField = new TextField(previewText, previewStyle);
        textField.setName("previewField");
        textField.setAlignment(Align.center);
        root.add(textField).growX();
        
        textField.addListener(main.getIbeamListener());
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                previewText = ((TextField) actor).getText();
            }
        });
        
        root.row();
        image = new Image(skin, "welcome-separator");
        image.setScaling(Scaling.stretch);
        root.add(image).growX().space(15.0f);
        
        root.row();
        Table bottom = new Table();
        ScrollPane scrollPane = new ScrollPane(bottom, skin);
        scrollPane.setName("scrollPane");
        scrollPane.setFadeScrollBars(false);
        scrollPane.setFlickScroll(false);
        root.add(scrollPane).padTop(10.0f).growX();
        
        bottom.defaults().space(5.0f);
        var table = new Table();
        bottom.add(table).growX().colspan(5).spaceBottom(15.0f);
        
        table.defaults().space(5.0f);
        label = new Label("Source TTF Path:", skin);
        table.add(label).right();
        
        textField = new TextField(data.file == null ? "" : data.file.path(), skin);
        textField.setName("sourceFileField");
        textField.setDisabled(true);
        table.add(textField).growX();
        
        var textButton = new TextButton("Browse...", skin);
        table.add(textButton).fillX();
        
        textField.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                event.getListenerActor().fire(new ChangeListener.ChangeEvent());
            }
        });
        
        var toolTip = new TextTooltip("Path to source TTF file to be read", main.getTooltipManager(), getSkin());
        textField.addListener(toolTip);
        textButton.addListener(toolTip);
        textField.addListener(main.getHandListener());
        textButton.addListener(main.getHandListener());
        var sourceChangeListener = new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Runnable runnable = () -> {
                    String defaultPath = main.getProjectData().getLastFontPath();

                    String[] filterPatterns = null;
                    if (!Utils.isMac()) {
                        filterPatterns = new String[]{"*.ttf"};
                    }

                    File file = main.getDesktopWorker().openDialog("Select TTF file...", defaultPath, filterPatterns, "True Type Font files");
                    if (file != null) {
                        data.file = new FileHandle(file);
                        
                        var textField = (TextField)DialogBitmapFont.this.findActor("sourceFileField");
                        textField.setText(data.file.path());
                        textField.setCursorPosition(textField.getText().length() - 1);
                        main.getProjectData().setLastFontPath(data.file.parent().path() + "/");
                        
                        if (target == null) {
                            target = data.file.sibling(data.file.nameWithoutExtension() + ".fnt");
                            textField = (TextField)DialogBitmapFont.this.findActor("targetFileField");
                            textField.setText(target.path());
                            textField.setCursorPosition(textField.getText().length() - 1);
                        }
                        
                        updatePreviewAndOK();
                    }
                };
                
                main.getDialogFactory().showDialogLoading(runnable);
            }
        };
        textField.addListener(sourceChangeListener);
        textButton.addListener(sourceChangeListener);
        
        table.row();
        table.defaults().space(5.0f);
        label = new Label("Target FNT Path:", skin);
        table.add(label).right();
        
        textField = new TextField(target == null ? "" : data.file.path(), skin);
        textField.setName("targetFileField");
        textField.setDisabled(true);
        table.add(textField).growX();
        
        textButton = new TextButton("Browse...", skin);
        table.add(textButton).fillX();
        
        textField.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                event.getListenerActor().fire(new ChangeListener.ChangeEvent());
            }
        });
        
        toolTip = new TextTooltip("Path to target FNT file to be saved", main.getTooltipManager(), getSkin());
        textField.addListener(toolTip);
        textButton.addListener(toolTip);
        textField.addListener(main.getHandListener());
        textButton.addListener(main.getHandListener());
        var targetChangeListener = new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Runnable runnable = () -> {
                    String defaultPath = main.getProjectData().getLastFontPath();

                    String[] filterPatterns = null;
                    if (!Utils.isMac()) {
                        filterPatterns = new String[]{"*.fnt"};
                    }

                    File file = main.getDesktopWorker().saveDialog("Select FNT file...", defaultPath, filterPatterns, "Bitmap Font files");
                    if (file != null) {
                        target = new FileHandle(file);
                        if (!target.extension().equalsIgnoreCase("fnt")) {
                            target = target.sibling(target.name() + ".fnt");
                        }
                        
                        var textField = (TextField)DialogBitmapFont.this.findActor("targetFileField");
                        textField.setText(target.path());
                        textField.setCursorPosition(textField.getText().length() - 1);
                        
                        main.getProjectData().setLastFontPath(target.parent().path() + "/");
                        
                        updatePreviewAndOK();
                    }
                };
                
                main.getDialogFactory().showDialogLoading(runnable);
            }
        };
        textField.addListener(targetChangeListener);
        textButton.addListener(targetChangeListener);
        
        table.row();
        
        table.defaults().space(5.0f);
        label = new Label("Characters:", skin);
        table.add(label).right();
        
        final var charactersTextField = new TextField(data.characters, skin);
        table.add(charactersTextField).growX();
        
        charactersTextField.addListener(main.getIbeamListener());
        toolTip = new TextTooltip("The characters the font should contain. Leave blank for defaults.", main.getTooltipManager(), getSkin());
        charactersTextField.addListener(toolTip);
        
        var characterSelectBox = new SelectBox<String>(skin);
        characterSelectBox.setItems("default", "0-9", "a-zA-Z", "a-zA-Z0-9", "custom");
        table.add(characterSelectBox).fillX();
        
        characterSelectBox.addListener(main.getHandListener());
        toolTip = new TextTooltip("Character preset list", main.getTooltipManager(), getSkin());
        characterSelectBox.addListener(toolTip);
        characterSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                switch (characterSelectBox.getSelected()) {
                    case "default":
                        charactersTextField.setText("");
                        break;
                    case "0-9":
                        charactersTextField.setText("0123456789");
                        break;
                    case "a-zA-Z":
                        charactersTextField.setText("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
                        break;
                    case "a-zA-Z0-9":
                        charactersTextField.setText("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
                        break;
                }
                
                data.characters = charactersTextField.getText();
                updatePreviewAndOK();
            }
        });
        
        charactersTextField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                data.characters = charactersTextField.getText();
                updatePreviewAndOK();
                
                if ("".equals(charactersTextField.getText())) {
                    characterSelectBox.setSelected("default");
                } else {
                    characterSelectBox.setSelected("custom");
                }
            }
        });
        
        bottom.row();
        label = new Label("Size:", skin);
        bottom.add(label).right();
        
        Spinner spinner = new Spinner(data.size, 1.0, true, Spinner.Orientation.HORIZONTAL, skin);
        bottom.add(spinner).left().minWidth(100.0f);
        
        toolTip = new TextTooltip("The size in pixels", main.getTooltipManager(), getSkin());
        spinner.addListener(toolTip);
        
        spinner.getButtonMinus().addListener(main.getHandListener());
        spinner.getButtonPlus().addListener(main.getHandListener());
        spinner.getTextField().addListener(main.getIbeamListener());
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Spinner spinner = (Spinner) actor;
                
                data.size = (int) spinner.getValue();
                updatePreviewAndOK();
            }
        });
        
        label = new Label("Mono:", skin);
        bottom.add(label).right();
        
        Button button = new Button(skin, "switch");
        button.setChecked(data.mono);
        bottom.add(button).left();
        
        toolTip = new TextTooltip("If on, font smoothing is disabled", main.getTooltipManager(), getSkin());
        button.addListener(toolTip);
        
        button.addListener(main.getHandListener());
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Button button = (Button) actor;
                
                data.mono = button.isChecked();
                updatePreviewAndOK();
            }
        });
        
        bottom.add().growX();
        
        bottom.row();
        label = new Label("Hinting:", skin);
        bottom.add(label).right();
        
        var selectBox = new SelectBox(skin);
        selectBox.setItems("None", "Slight", "Medium", "Full", "AutoSlight", "AutoMedium", "AutoFull");
        selectBox.setSelected(data.hinting);
        bottom.add(selectBox).left();
        
        toolTip = new TextTooltip("Strength of hinting", main.getTooltipManager(), getSkin());
        selectBox.addListener(toolTip);
        
        selectBox.addListener(main.getHandListener());
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                SelectBox<String> selectBox = (SelectBox) actor;
                
                data.hinting = selectBox.getSelected();
                updatePreviewAndOK();
            }
        });
        
        label = new Label("Color:", skin);
        bottom.add(label).right();
        
        textButton = new TextButton(data.color, skin);
        textButton.setName("colorTextButton");
        image = new Image(skin, "icon-colorwheel-over");
        textButton.add(image).space(10.0f);
        bottom.add(textButton).left();
        
        toolTip = new TextTooltip("Foreground color (Required)", main.getTooltipManager(), getSkin());
        textButton.addListener(toolTip);
        
        textButton.addListener(main.getHandListener());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                TextButton textButton = (TextButton) actor;
                
                main.getDialogFactory().showDialogColors(new StyleProperty(), (ColorData colorData) -> {
                    if (colorData != null) {
                        textButton.setText(colorData.getName());
                        textButton.setUserObject(colorData);
                        data.color = colorData.getName();
                    } else {
                        textButton.setText("");
                        textButton.setUserObject(null);
                        data.color = null;
                    }
                    
                    updateColors();
                    updatePreviewAndOK();
                });
            }
        });
        
        bottom.row();
        label = new Label("Gamma:", skin);
        bottom.add(label).right();
        
        spinner = new Spinner(Double.parseDouble(df.format(data.gamma)), 1.0, false, Spinner.Orientation.HORIZONTAL, skin);
        bottom.add(spinner).left().minWidth(100.0f);
        
        toolTip = new TextTooltip("Glyph gamma. Values > 1 reduce antialiasing.", main.getTooltipManager(), getSkin());
        spinner.addListener(toolTip);
        
        spinner.getButtonMinus().addListener(main.getHandListener());
        spinner.getButtonPlus().addListener(main.getHandListener());
        spinner.getTextField().addListener(main.getIbeamListener());
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Spinner spinner = (Spinner) actor;
                
                data.gamma = (float) spinner.getValue();
                updatePreviewAndOK();
            }
        });
        
        label = new Label("Render Count:", skin);
        bottom.add(label).right();
        
        spinner = new Spinner(data.renderCount, 1.0, true, Spinner.Orientation.HORIZONTAL, skin);
        bottom.add(spinner).left().minWidth(100.0f);
        
        toolTip = new TextTooltip("Number of times to render the glyph. Useful with a shadow or border, so it doesn't show through the glyph.", main.getTooltipManager(), getSkin());
        spinner.addListener(toolTip);
        
        spinner.getButtonMinus().addListener(main.getHandListener());
        spinner.getButtonPlus().addListener(main.getHandListener());
        spinner.getTextField().addListener(main.getIbeamListener());
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Spinner spinner = (Spinner) actor;
                
                data.renderCount = (int) spinner.getValue();
                updatePreviewAndOK();
            }
        });
        
        bottom.row();
        label = new Label("Border Width:", skin);
        bottom.add(label).right();
        
        spinner = new Spinner(data.borderWidth, 1.0, false, Spinner.Orientation.HORIZONTAL, skin);
        bottom.add(spinner).left().minWidth(100.0f);
        
        toolTip = new TextTooltip("Border width in pixels, 0 to disable", main.getTooltipManager(), getSkin());
        spinner.addListener(toolTip);
        
        spinner.getButtonMinus().addListener(main.getHandListener());
        spinner.getButtonPlus().addListener(main.getHandListener());
        spinner.getTextField().addListener(main.getIbeamListener());
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Spinner spinner = (Spinner) actor;
                
                data.borderWidth = (float) spinner.getValue();
                updatePreviewAndOK();
            }
        });
        
        label = new Label("Border Color:", skin);
        bottom.add(label).right();
        
        textButton = new TextButton(data.borderColor, skin);
        textButton.setName("borderColorTextButton");
        image = new Image(skin, "icon-colorwheel-over");
        textButton.add(image).space(10.0f);
        bottom.add(textButton).left();
        
        toolTip = new TextTooltip("Border color; Required if borderWidth > 0", main.getTooltipManager(), getSkin());
        textButton.addListener(toolTip);
        
        textButton.addListener(main.getHandListener());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                TextButton textButton = (TextButton) actor;
                
                main.getDialogFactory().showDialogColors(new StyleProperty(), (ColorData colorData) -> {
                    if (colorData != null) {
                        textButton.setText(colorData.getName());
                        textButton.setUserObject(colorData);
                        data.borderColor = colorData.getName();
                    } else {
                        textButton.setText("");
                        textButton.setUserObject(null);
                        data.borderColor = null;
                    }
                    updateColors();
                    updatePreviewAndOK();
                });
            }
        });
        
        bottom.row();
        label = new Label("Border Straight:", skin);
        bottom.add(label).right();
        
        button = new Button(skin, "switch");
        button.setChecked(data.borderStraight);
        bottom.add(button).left();
        
        toolTip = new TextTooltip("On for straight (mitered), off for rounded borders", main.getTooltipManager(), getSkin());
        button.addListener(toolTip);
        
        button.addListener(main.getHandListener());
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Button button = (Button) actor;
                
                data.borderStraight = button.isChecked();
                updatePreviewAndOK();
            }
        });
        
        label = new Label("Border Gamma:", skin);
        bottom.add(label).right();
        
        spinner = new Spinner(Double.parseDouble(df.format(data.borderGamma)), 1.0, false, Spinner.Orientation.HORIZONTAL, skin);
        bottom.add(spinner).left().minWidth(100.0f);
        
        toolTip = new TextTooltip("Values < 1 increase the border size.", main.getTooltipManager(), getSkin());
        spinner.addListener(toolTip);
        
        spinner.getButtonMinus().addListener(main.getHandListener());
        spinner.getButtonPlus().addListener(main.getHandListener());
        spinner.getTextField().addListener(main.getIbeamListener());
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Spinner spinner = (Spinner) actor;
                
                data.borderGamma = (float) spinner.getValue();
                updatePreviewAndOK();
            }
        });
        
        bottom.row();
        label = new Label("Shadow Offset X:", skin);
        bottom.add(label).right();
        
        spinner = new Spinner(data.shadowOffsetX, 1.0, true, Spinner.Orientation.HORIZONTAL, skin);
        bottom.add(spinner).left().minWidth(100.0f);
        
        toolTip = new TextTooltip("Offset of text shadow on X axis in pixels, 0 to disable", main.getTooltipManager(), getSkin());
        spinner.addListener(toolTip);
        
        spinner.getButtonMinus().addListener(main.getHandListener());
        spinner.getButtonPlus().addListener(main.getHandListener());
        spinner.getTextField().addListener(main.getIbeamListener());
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Spinner spinner = (Spinner) actor;
                
                data.shadowOffsetX = (int) spinner.getValue();
                updatePreviewAndOK();
            }
        });
        
        label = new Label("Shadow Offset Y:", skin);
        bottom.add(label).right();
        
        spinner = new Spinner(data.shadowOffsetY, 1.0, true, Spinner.Orientation.HORIZONTAL, skin);
        bottom.add(spinner).left().minWidth(100.0f);
        
        toolTip = new TextTooltip("Offset of text shadow on Y axis in pixels, 0 to disable", main.getTooltipManager(), getSkin());
        spinner.addListener(toolTip);
        
        spinner.getButtonMinus().addListener(main.getHandListener());
        spinner.getButtonPlus().addListener(main.getHandListener());
        spinner.getTextField().addListener(main.getIbeamListener());
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Spinner spinner = (Spinner) actor;
                
                data.shadowOffsetY = (int) spinner.getValue();
                updatePreviewAndOK();
            }
        });
        
        bottom.row();
        label = new Label("Shadow Color:", skin);
        bottom.add(label).right();
        
        textButton = new TextButton(data.shadowColor, skin);
        textButton.setName("shadowColorTextButton");
        image = new Image(skin, "icon-colorwheel-over");
        textButton.add(image).space(10.0f);
        bottom.add(textButton).left();
        
        toolTip = new TextTooltip("Shadow color; required if shadowOffset > 0.", main.getTooltipManager(), getSkin());
        textButton.addListener(toolTip);
        
        textButton.addListener(main.getHandListener());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                TextButton textButton = (TextButton) actor;
                
                main.getDialogFactory().showDialogColors(new StyleProperty(), (ColorData colorData) -> {
                    if (colorData != null) {
                        textButton.setText(colorData.getName());
                        textButton.setUserObject(colorData);
                        data.shadowColor = colorData.getName();
                    } else {
                        textButton.setText("");
                        textButton.setUserObject(null);
                        data.shadowColor = null;
                    }
                    updateColors();
                    updatePreviewAndOK();
                });
            }
        });
        
        label = new Label("Incremental:", skin);
        bottom.add(label).right();
        
        button = new Button(skin, "switch");
        button.setChecked(data.incremental);
        bottom.add(button).left();
        
        toolTip = new TextTooltip("When true, glyphs are rendered on the fly to the \n"
                + "font's glyph page textures as they are needed.", main.getTooltipManager(), getSkin());
        button.addListener(toolTip);
        
        button.addListener(main.getHandListener());
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Button button = (Button) actor;
                
                data.incremental = button.isChecked();
                updatePreviewAndOK();
            }
        });
        
        bottom.row();
        label = new Label("Space X:", skin);
        bottom.add(label).right();
        
        spinner = new Spinner(data.spaceX, 1.0, true, Spinner.Orientation.HORIZONTAL, skin);
        bottom.add(spinner).left().minWidth(100.0f);
        
        toolTip = new TextTooltip("Pixels to add to glyph spacing. Can be negative.", main.getTooltipManager(), getSkin());
        spinner.addListener(toolTip);
        
        spinner.getButtonMinus().addListener(main.getHandListener());
        spinner.getButtonPlus().addListener(main.getHandListener());
        spinner.getTextField().addListener(main.getIbeamListener());
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Spinner spinner = (Spinner) actor;
                
                data.spaceX = (int) spinner.getValue();
                updatePreviewAndOK();
            }
        });
        
        label = new Label("Space Y:", skin);
        bottom.add(label).right();
        
        spinner = new Spinner(data.spaceY, 1.0, true, Spinner.Orientation.HORIZONTAL, skin);
        bottom.add(spinner).left().minWidth(100.0f);
        
        toolTip = new TextTooltip("Pixels to add to glyph spacing. Can be negative.", main.getTooltipManager(), getSkin());
        spinner.addListener(toolTip);
        
        spinner.getButtonMinus().addListener(main.getHandListener());
        spinner.getButtonPlus().addListener(main.getHandListener());
        spinner.getTextField().addListener(main.getIbeamListener());
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Spinner spinner = (Spinner) actor;
                
                data.spaceY = (int) spinner.getValue();
                updatePreviewAndOK();
            }
        });
        
        bottom.row();
        label = new Label("Kerning:", skin);
        bottom.add(label).right();
        
        button = new Button(skin, "switch");
        button.setChecked(data.kerning);
        bottom.add(button).left();
        
        toolTip = new TextTooltip("Whether the font should include kerning", main.getTooltipManager(), getSkin());
        button.addListener(toolTip);
        
        button.addListener(main.getHandListener());
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Button button = (Button) actor;
                
                data.kerning = button.isChecked();
                updatePreviewAndOK();
            }
        });
        
        bottom.row();
        label = new Label("Flip:", skin);
        bottom.add(label).right();
        
        button = new Button(skin, "switch");
        button.setChecked(data.flip);
        bottom.add(button).left();
        
        toolTip = new TextTooltip("Whether to flip the font vertically", main.getTooltipManager(), getSkin());
        button.addListener(toolTip);
        
        button.addListener(main.getHandListener());
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Button button = (Button) actor;
                
                data.flip = button.isChecked();
                updatePreviewAndOK();
            }
        });
        
        label = new Label("GenMipMaps:", skin);
        bottom.add(label).right();
        
        button = new Button(skin, "switch");
        button.setChecked(data.genMipMaps);
        bottom.add(button).left();
        
        toolTip = new TextTooltip("Whether to generate mip maps for the resulting texture", main.getTooltipManager(), getSkin());
        button.addListener(toolTip);
        
        button.addListener(main.getHandListener());
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Button button = (Button) actor;
                
                data.genMipMaps = button.isChecked();
                updatePreviewAndOK();
            }
        });
        
        bottom.row();
        label = new Label("Min Filter:", skin);
        bottom.add(label).right();
        
        selectBox = new SelectBox(skin);
        selectBox.setItems("Nearest", "Linear", "MipMap", "MipMapNearestNearest", "MipMapLinearNearest", "MipMapNearestLinear", "MipMapLinearLinear");
        selectBox.setSelected(data.minFilter);
        bottom.add(selectBox).left();
        
        toolTip = new TextTooltip("Minification filter", main.getTooltipManager(), getSkin());
        selectBox.addListener(toolTip);
        
        selectBox.addListener(main.getHandListener());
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                SelectBox<String> selectBox = (SelectBox) actor;
                
                data.minFilter = selectBox.getSelected();
                updatePreviewAndOK();
            }
        });
        
        label = new Label("Mag Filter:", skin);
        bottom.add(label).right();
        
        selectBox = new SelectBox(skin);
        selectBox.setItems("Nearest", "Linear", "MipMap", "MipMapNearestNearest", "MipMapLinearNearest", "MipMapNearestLinear", "MipMapLinearLinear");
        selectBox.setSelected(data.magFilter);
        bottom.add(selectBox).left();
        
        toolTip = new TextTooltip("Magnification filter", main.getTooltipManager(), getSkin());
        selectBox.addListener(toolTip);
        
        selectBox.addListener(main.getHandListener());
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                SelectBox<String> selectBox = (SelectBox) actor;
                
                data.magFilter = selectBox.getSelected();
                updatePreviewAndOK();
            }
        });
        
        buttons.pad(10.0f);
        buttons.defaults().minWidth(75.0f).space(25.0f);
        textButton = new TextButton("OK", skin);
        textButton.setName("okButton");
        textButton.addListener(main.getHandListener());
        button(textButton, true);
        
        textButton = new TextButton("Cancel", skin);
        textButton.setName("cancelButton");
        textButton.addListener(main.getHandListener());
        button(textButton, false);
    }
    
    private void updateColors() {
        TextButton textButton = findActor("colorTextButton");
        ColorData colorData = (ColorData) textButton.getUserObject();
        if (colorData != null && main.getJsonData().getColors().contains(colorData, false)) {
            data.color = colorData.getName();
        } else {
            data.color = null;
        }
        textButton.setText(data.color);
        
        textButton = findActor("borderColorTextButton");
        colorData = (ColorData) textButton.getUserObject();
        if (colorData != null && main.getJsonData().getColors().contains(colorData, false)) {
            data.borderColor = colorData.getName();
        } else {
            data.borderColor = null;
        }
        textButton.setText(data.borderColor);
        
        textButton = findActor("shadowColorTextButton");
        colorData = (ColorData) textButton.getUserObject();
        if (colorData != null && main.getJsonData().getColors().contains(colorData, false)) {
            data.shadowColor = colorData.getName();
        } else {
            data.shadowColor = null;
        }
        textButton.setText(data.shadowColor);
    }
    
    private void updatePreviewAndOK() {
        boolean notValid = false;
        if (data.color == null) notValid = true;
        else if (data.file == null || !data.file.exists()) notValid = true;
        else if (target == null) notValid = true;
        else if (!MathUtils.isZero(data.borderWidth) && data.borderColor == null) notValid = true;
        else if ((data.shadowOffsetX != 0 || data.shadowOffsetY != 0) && data.shadowColor == null) notValid = true;
        
        if (notValid) {
            TextField textField = findActor("previewField");
            Cell cell = ((Table) textField.getParent()).getCell(textField);
            previewStyle.font = skin.get("free-type-preview", TextFieldStyle.class).font;
            textField = new TextField(previewText, previewStyle);
            textField.setName("previewField");
            textField.setAlignment(Align.center);
            cell.setActor(textField);
            
            textField.addListener(main.getIbeamListener());
            textField.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    previewText = ((TextField) actor).getText();
                }
            });
        } else {
            data.createBitmapFont(main);
            if (data.bitmapFont != null) {
                TextField textField = findActor("previewField");
                Cell cell = ((Table) textField.getParent()).getCell(textField);
                previewStyle.font = data.bitmapFont;
                textField = new TextField(previewText, previewStyle);
                textField.setName("previewField");
                textField.setAlignment(Align.center);
                cell.setActor(textField);

                textField.addListener(main.getIbeamListener());
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        previewText = ((TextField) actor).getText();
                    }
                });
            }
        }
        
        TextButton textButton = findActor("okButton");
        textButton.setDisabled(notValid);
    }
    
    public static interface DialogBitmapFontListener {
        public void fontAdded(FileHandle file);
    }
}
