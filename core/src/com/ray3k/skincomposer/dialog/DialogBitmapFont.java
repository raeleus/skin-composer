/*
 * The MIT License
 *
 * Copyright (c) 2024 Raymond Buckley.
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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.*;
import com.ray3k.skincomposer.FilesDroppedListener;
import com.ray3k.skincomposer.LeadingTruncateLabel;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.SpineDrawable;
import com.ray3k.skincomposer.data.ColorData;
import com.ray3k.skincomposer.data.FreeTypeFontData;
import com.ray3k.skincomposer.data.StyleProperty;
import com.ray3k.skincomposer.utils.Utils;
import com.ray3k.stripe.PopColorPicker.PopColorPickerAdapter;
import com.ray3k.stripe.Spinner;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static com.ray3k.skincomposer.Main.*;

public class DialogBitmapFont extends Dialog {
    private Table root;
    private Table buttons;
    private FreeTypeFontData data;
    private FileHandle target;
    private static DecimalFormat df;
    private Array<DialogBitmapFontListener> listeners;
    private TextFieldStyle previewStyle;
    private SpriteDrawable previewCursor;
    private SpriteDrawable previewSelection;
    private String previewText;
    private FilesDroppedListener filesDroppedListener;
    private static enum ButtonType {
        GENERATE, SAVE_SETTINGS, LOAD_SETTINGS, CANCEL
    }
    private Json json;
    private Color previewBGcolor;
    private boolean automaticBgColor;
    private Table previewTable;
    private SpineDrawable arrowDrawable;
    private Image arrowImage;
    private static Vector2 temp = new Vector2();
    private Actor previousArrowTarget;

    public DialogBitmapFont() {
        super("Create new Bitmap Font", skin, "bg");
        arrowDrawable = new SpineDrawable(skeletonRenderer, arrowSkeletonData, arrowAnimationStateData);
        arrowDrawable.getAnimationState().setAnimation(0, "animation", true);
        arrowDrawable.setCrop(-10, -10, 20, 20);
        arrowImage = new Image(arrowDrawable);
        arrowImage.setTouchable(Touchable.disabled);
        addActor(arrowImage);
        arrowImage.pack();
        previewBGcolor = new Color(Color.BLACK);
        automaticBgColor = true;

        json = new Json(JsonWriter.OutputType.json);

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

        previewStyle = new TextFieldStyle(skin.get("free-type-preview", TextFieldStyle.class));
        previewCursor = (SpriteDrawable) ((TextureRegionDrawable) skin.getDrawable("white")).tint(Color.WHITE);
        previewStyle.cursor = previewCursor;
        previewSelection = (SpriteDrawable) ((TextureRegionDrawable) skin.getDrawable("white")).tint(Color.LIGHT_GRAY);
        previewStyle.selection = previewSelection;
        previewText = "Lorem ipsum dolor sit";

        getTitleTable().pad(10.0f);

        root = getContentTable();
        buttons = getButtonTable();

        populate();
        
        updatePreviewAndOK();

        key(Keys.ESCAPE, ButtonType.CANCEL);

        listeners = new Array<>();

        filesDroppedListener = (Array<FileHandle> files) -> {
            if (files.size > 0) {
                var extension = files.first().extension().toLowerCase(Locale.ROOT);
                if (extension.equals("ttf") || extension.equals("otf")) {
                    Runnable runnable = () -> {
                        Gdx.app.postRunnable(() -> {
                            loadTTFsource(files.first());
                        });
                    };

                    dialogFactory.showDialogLoading(runnable);
                } else if (extension.equals("scmp-font")) {
                    loadSettings(files.first());
                }
            }
        };

        desktopWorker.addFilesDroppedListener(filesDroppedListener);
    }
    
    @Override
    public Dialog show(Stage stage, Action action) {
        var result = super.show(stage, action);
        updatePreviewAndOK();
        return result;
    }
    
    @Override
    protected void result(Object object) {
        switch ((ButtonType) object) {
            case GENERATE:
                data.writeFontToFile(main, target);

                for (DialogBitmapFontListener listener : listeners) {
                    listener.fontAdded(target);
                }
                break;
            case SAVE_SETTINGS:
                saveSettings();
                break;
            case LOAD_SETTINGS:
                loadSettings();
                break;
            case CANCEL:
                break;
        }
    }
    
    @Override
    public void act(float delta) {
        super.act(delta);
        arrowDrawable.update(delta);
    }
    
    @Override
    public boolean remove() {
        desktopWorker.removeFilesDroppedListener(filesDroppedListener);
        return super.remove();
    }

    public void addListener(DialogBitmapFontListener listener) {
        listeners.add(listener);
    }

    private void populate() {
        root.pad(15.0f);

        Label label = new Label("Create a new Bitmap Font.", skin, "black");
        root.add(label);

        root.row();
        Image image = new Image(skin, "welcome-separator");
        image.setScaling(Scaling.stretch);
        root.add(image).growX().space(15.0f);

        root.row();
        previewTable = new Table();
        previewTable.setBackground(getSkin().getDrawable("white"));
        previewTable.setColor(Color.BLACK);
        root.add(previewTable).growX();
        
        var textField = new TextField(previewText, previewStyle);
        textField.setName("previewField");
        textField.setAlignment(Align.center);
        previewTable.add(textField).growX();

        textField.addListener(ibeamListener);
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                previewText = ((TextField) actor).getText();
            }
        });
        
        root.row();
        var imageButton = new ImageButton(getSkin(), "color");
        root.add(imageButton).expandX().right();
        imageButton.addListener(handListener);
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                dialogFactory.showDialogColorPicker(previewBGcolor, new PopColorPickerAdapter() {
                    @Override
                    public void picked(Color color) {
                        automaticBgColor = false;
                        previewBGcolor.set(color);
                        previewTable.setColor(color);
                    }
                });
            }
        });
        var toolTip = (Main.makeTooltip("Background color for preview text.", tooltipManager, getSkin()));
        imageButton.addListener(toolTip);

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
        label = new Label("Source Font Path:", skin);
        label.setName("source-label");
        table.add(label).right();

        var ltLabel = new LeadingTruncateLabel(data.file == null ? "" : data.file.path(), skin, "field");
        ltLabel.setName("sourceFileField");
        ltLabel.setEllipsis(true);
        table.add(ltLabel).growX().minWidth(0).prefWidth(0);
        ltLabel.addListener(handListener);
        ltLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                event.getListenerActor().fire(new ChangeListener.ChangeEvent());
            }
        });

        var textButton = new TextButton("Browse...", skin);
        table.add(textButton).fillX();

        toolTip = (Main.makeTooltip("Path to source Font file to be read", tooltipManager, getSkin()));
        ltLabel.addListener(toolTip);
        textButton.addListener(toolTip);
        textButton.addListener(handListener);
        var sourceChangeListener = new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Runnable runnable = () -> {
                    String defaultPath = projectData.getLastFontPath();

                    File file = desktopWorker.openDialog("Select Font file...", defaultPath, "ttf,otf", "Font Files (*.TTF;*.OTF)");
                    if (file != null) {
                        Gdx.app.postRunnable(() -> {
                            loadTTFsource(new FileHandle(file));
                        });
                    }
                };

                dialogFactory.showDialogLoading(runnable);
            }
        };
        ltLabel.addListener(sourceChangeListener);
        textButton.addListener(sourceChangeListener);

        table.row();
        table.defaults().space(5.0f);
        label = new Label("Target FNT Path:", skin);
        label.setName("target-label");
        label.setEllipsis(true);
        table.add(label).right();

        ltLabel = new LeadingTruncateLabel(target == null ? "" : data.file.path(), skin, "field");
        ltLabel.setName("targetFileField");
        ltLabel.setEllipsis(true);
        table.add(ltLabel).growX().minWidth(0).prefWidth(0);
        ltLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                event.getListenerActor().fire(new ChangeListener.ChangeEvent());
            }
        });

        textButton = new TextButton("Browse...", skin);
        table.add(textButton).fillX();


        toolTip = (Main.makeTooltip("Path to target FNT file to be saved", tooltipManager, getSkin()));
        ltLabel.addListener(toolTip);
        textButton.addListener(toolTip);
        ltLabel.addListener(handListener);
        textButton.addListener(handListener);
        var targetChangeListener = new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Runnable runnable = () -> {
                    String defaultPath = projectData.getLastFontPath();

                    File file = desktopWorker.saveDialog("Select FNT file...", defaultPath, "fnt", "Bitmap Font files");
                    if (file != null) {
                        Gdx.app.postRunnable(() -> {
                            target = new FileHandle(file);
                            if (!target.extension().equalsIgnoreCase("fnt")) {
                                target = target.sibling(target.name() + ".fnt");
                            }

                            var leadingTruncateLabel = (LeadingTruncateLabel) DialogBitmapFont.this.findActor("targetFileField");
                            leadingTruncateLabel.setText(target.path());

                            projectData.setLastFontPath(target.parent().path() + "/");

                            updatePreviewAndOK();
                        });
                    }
                };

                dialogFactory.showDialogLoading(runnable);
            }
        };
        ltLabel.addListener(targetChangeListener);
        textButton.addListener(targetChangeListener);

        table.row();

        table.defaults().space(5.0f);
        label = new Label("Characters:", skin);
        table.add(label).right();

        final var charactersTextField = new TextField(data.characters, skin);
        charactersTextField.setName("characters");
        table.add(charactersTextField).growX();

        charactersTextField.addListener(ibeamListener);
        toolTip = (Main.makeTooltip("The characters the font should contain. Leave blank for defaults.", tooltipManager, getSkin()));
        charactersTextField.addListener(toolTip);

        var characterSelectBox = new SelectBox<String>(skin);
        characterSelectBox.setName("characterSelectBox");
        characterSelectBox.setItems("default", "0-9", "a-zA-Z", "a-zA-Z0-9", "custom", "Load from file (UTF-8)...");
        table.add(characterSelectBox).fillX();

        characterSelectBox.addListener(handListener);
        characterSelectBox.getList().addListener(handListener);
        toolTip = (Main.makeTooltip("Character preset list", tooltipManager, getSkin()));
        characterSelectBox.addListener(toolTip);
        characterSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                switch (characterSelectBox.getSelected()) {
                    case "default":
                        charactersTextField.setText("");
                        charactersTextField.setMessageText("");
                        data.characters = "";
                        break;
                    case "0-9":
                        charactersTextField.setText("0123456789");
                        charactersTextField.setMessageText("");
                        data.characters = charactersTextField.getText();
                        break;
                    case "a-zA-Z":
                        charactersTextField.setText("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
                        charactersTextField.setMessageText("");
                        data.characters = charactersTextField.getText();
                        break;
                    case "a-zA-Z0-9":
                        charactersTextField.setText("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
                        charactersTextField.setMessageText("");
                        data.characters = charactersTextField.getText();
                        break;
                    case "Load from file (UTF-8)...":
                        charactersTextField.setText("");
                        charactersTextField.setMessageText("Characters loaded from text file...");
                        data.characters = "";
                        showCharacterDialog();
                        break;
                }
                
                data.characters = !data.characters.equals("") && !data.characters.contains("\u0000") ? "\u0000" + data.characters : data.characters;
                updatePreviewAndOK();
            }
        });

        charactersTextField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                data.characters = charactersTextField.getText();
                data.characters = !data.characters.equals("") && !data.characters.contains("\u0000") ? "\u0000" + data.characters : data.characters;
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
        spinner.setName("size");
        spinner.setMinimum(5);
        bottom.add(spinner).left().minWidth(100.0f);

        toolTip = (Main.makeTooltip("The size in pixels", tooltipManager, getSkin()));
        spinner.addListener(toolTip);

        spinner.getButtonMinus().addListener(handListener);
        spinner.getButtonPlus().addListener(handListener);
        spinner.getTextField().addListener(ibeamListener);
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
        button.setName("mono");
        button.setChecked(data.mono);
        bottom.add(button).left();

        toolTip = (Main.makeTooltip("If on, font smoothing is disabled", tooltipManager, getSkin()));
        button.addListener(toolTip);

        button.addListener(handListener);
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

        var selectBox = new SelectBox<String>(skin);
        selectBox.setName("hinting");
        selectBox.setItems("None", "Slight", "Medium", "Full", "AutoSlight", "AutoMedium", "AutoFull");
        selectBox.setSelected(data.hinting);
        bottom.add(selectBox).left();

        toolTip = (Main.makeTooltip("Strength of hinting", tooltipManager, getSkin()));
        selectBox.addListener(toolTip);

        selectBox.addListener(handListener);
        selectBox.getList().addListener(handListener);
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                SelectBox<String> selectBox = (SelectBox) actor;

                data.hinting = selectBox.getSelected();
                updatePreviewAndOK();
            }
        });

        label = new Label("Color:", skin);
        label.setName("color-label");
        bottom.add(label).right();

        textButton = new TextButton(data.color, skin);
        textButton.setName("colorTextButton");
        image = new Image(skin, "icon-colorwheel-over");
        textButton.add(image).space(10.0f);
        bottom.add(textButton).left();

        toolTip = (Main.makeTooltip("Foreground color (Required)", tooltipManager, getSkin()));
        textButton.addListener(toolTip);

        textButton.addListener(handListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                TextButton textButton = (TextButton) actor;

                dialogFactory.showDialogColors(new StyleProperty(), (colorData, pressedCancel) -> {
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
                }, null);
            }
        });

        bottom.row();
        label = new Label("Gamma:", skin);
        bottom.add(label).right();

        spinner = new Spinner(Double.parseDouble(df.format(data.gamma)), .1, false, Spinner.Orientation.HORIZONTAL, skin);
        spinner.setName("gamma");
        bottom.add(spinner).left().minWidth(100.0f);

        toolTip = (Main.makeTooltip("Glyph gamma. Values > 1 reduce antialiasing.", tooltipManager, getSkin()));
        spinner.addListener(toolTip);

        spinner.getButtonMinus().addListener(handListener);
        spinner.getButtonPlus().addListener(handListener);
        spinner.getTextField().addListener(ibeamListener);
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
        spinner.setName("renderCount");
        bottom.add(spinner).left().minWidth(100.0f);

        toolTip = (Main.makeTooltip("Number of times to render the glyph. Useful with a shadow or border, so it doesn't show through the glyph.", tooltipManager, getSkin()));
        spinner.addListener(toolTip);

        spinner.getButtonMinus().addListener(handListener);
        spinner.getButtonPlus().addListener(handListener);
        spinner.getTextField().addListener(ibeamListener);
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
        spinner.setName("borderWidth");
        bottom.add(spinner).left().minWidth(100.0f);

        toolTip = (Main.makeTooltip("Border width in pixels, 0 to disable", tooltipManager, getSkin()));
        spinner.addListener(toolTip);

        spinner.getButtonMinus().addListener(handListener);
        spinner.getButtonPlus().addListener(handListener);
        spinner.getTextField().addListener(ibeamListener);
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Spinner spinner = (Spinner) actor;

                data.borderWidth = (float) spinner.getValue();
                updatePreviewAndOK();
            }
        });

        label = new Label("Border Color:", skin);
        label.setName("border-color-label");
        bottom.add(label).right();

        textButton = new TextButton(data.borderColor, skin);
        textButton.setName("borderColorTextButton");
        image = new Image(skin, "icon-colorwheel-over");
        textButton.add(image).space(10.0f);
        bottom.add(textButton).left();

        toolTip = (Main.makeTooltip("Border color; Required if borderWidth > 0", tooltipManager, getSkin()));
        textButton.addListener(toolTip);

        textButton.addListener(handListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                TextButton textButton = (TextButton) actor;

                dialogFactory.showDialogColors(new StyleProperty(), (colorData, pressedCancel) -> {
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
                }, null);
            }
        });

        bottom.row();
        label = new Label("Border Straight:", skin);
        bottom.add(label).right();

        button = new Button(skin, "switch");
        button.setName("borderStraight");
        button.setChecked(data.borderStraight);
        bottom.add(button).left();

        toolTip = (Main.makeTooltip("On for straight (mitered), off for rounded borders", tooltipManager, getSkin()));
        button.addListener(toolTip);

        button.addListener(handListener);
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
        spinner.setName("borderGamma");
        bottom.add(spinner).left().minWidth(100.0f);

        toolTip = (Main.makeTooltip("Values < 1 increase the border size.", tooltipManager, getSkin()));
        spinner.addListener(toolTip);

        spinner.getButtonMinus().addListener(handListener);
        spinner.getButtonPlus().addListener(handListener);
        spinner.getTextField().addListener(ibeamListener);
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
        spinner.setName("shadowOffsetX");
        bottom.add(spinner).left().minWidth(100.0f);

        toolTip = (Main.makeTooltip("Offset of text shadow on X axis in pixels, 0 to disable", tooltipManager, getSkin()));
        spinner.addListener(toolTip);

        spinner.getButtonMinus().addListener(handListener);
        spinner.getButtonPlus().addListener(handListener);
        spinner.getTextField().addListener(ibeamListener);
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
        spinner.setName("shadowOffsetY");
        bottom.add(spinner).left().minWidth(100.0f);

        toolTip = (Main.makeTooltip("Offset of text shadow on Y axis in pixels, 0 to disable", tooltipManager, getSkin()));
        spinner.addListener(toolTip);

        spinner.getButtonMinus().addListener(handListener);
        spinner.getButtonPlus().addListener(handListener);
        spinner.getTextField().addListener(ibeamListener);
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
        label.setName("shadow-color-label");
        bottom.add(label).right();

        textButton = new TextButton(data.shadowColor, skin);
        textButton.setName("shadowColorTextButton");
        image = new Image(skin, "icon-colorwheel-over");
        textButton.add(image).space(10.0f);
        bottom.add(textButton).left();

        toolTip = (Main.makeTooltip("Shadow color; required if shadowOffset > 0.", tooltipManager, getSkin()));
        textButton.addListener(toolTip);

        textButton.addListener(handListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                TextButton textButton = (TextButton) actor;

                dialogFactory.showDialogColors(new StyleProperty(), (colorData, pressedCancel) -> {
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
                }, null);
            }
        });

        label = new Label("Incremental:", skin);
        bottom.add(label).right();

        button = new Button(skin, "switch");
        button.setName("incremental");
        button.setChecked(data.incremental);
        bottom.add(button).left();

        toolTip = new TextTooltip("When true, glyphs are rendered on the fly to the \n"
                + "font's glyph page textures as they are needed.", tooltipManager, getSkin());
        button.addListener(toolTip);

        button.addListener(handListener);
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
        spinner.setName("spaceX");
        bottom.add(spinner).left().minWidth(100.0f);

        toolTip = (Main.makeTooltip("Pixels to add to glyph spacing. Can be negative.", tooltipManager, getSkin()));
        spinner.addListener(toolTip);

        spinner.getButtonMinus().addListener(handListener);
        spinner.getButtonPlus().addListener(handListener);
        spinner.getTextField().addListener(ibeamListener);
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
        spinner.setName("spaceY");
        bottom.add(spinner).left().minWidth(100.0f);

        toolTip = (Main.makeTooltip("Pixels to add to glyph spacing. Can be negative.", tooltipManager, getSkin()));
        spinner.addListener(toolTip);

        spinner.getButtonMinus().addListener(handListener);
        spinner.getButtonPlus().addListener(handListener);
        spinner.getTextField().addListener(ibeamListener);
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
        button.setName("kerning");
        button.setChecked(data.kerning);
        bottom.add(button).left();

        toolTip = (Main.makeTooltip("Whether the font should include kerning", tooltipManager, getSkin()));
        button.addListener(toolTip);

        button.addListener(handListener);
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
        button.setName("flip");
        button.setChecked(data.flip);
        bottom.add(button).left();

        toolTip = (Main.makeTooltip("Whether to flip the font vertically", tooltipManager, getSkin()));
        button.addListener(toolTip);

        button.addListener(handListener);
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
        button.setName("genMipMaps");
        button.setChecked(data.genMipMaps);
        bottom.add(button).left();

        toolTip = (Main.makeTooltip("Whether to generate mip maps for the resulting texture", tooltipManager, getSkin()));
        button.addListener(toolTip);

        button.addListener(handListener);
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

        selectBox = new SelectBox<>(skin);
        selectBox.setName("minFilter");
        selectBox.setItems("Nearest", "Linear", "MipMap", "MipMapNearestNearest", "MipMapLinearNearest", "MipMapNearestLinear", "MipMapLinearLinear");
        selectBox.setSelected(data.minFilter);
        bottom.add(selectBox).left();

        toolTip = (Main.makeTooltip("Minification filter", tooltipManager, getSkin()));
        selectBox.addListener(toolTip);

        selectBox.addListener(handListener);
        selectBox.getList().addListener(handListener);
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

        selectBox = new SelectBox<>(skin);
        selectBox.setName("magFilter");
        selectBox.setItems("Nearest", "Linear", "MipMap", "MipMapNearestNearest", "MipMapLinearNearest", "MipMapNearestLinear", "MipMapLinearLinear");
        selectBox.setSelected(data.magFilter);
        bottom.add(selectBox).left();

        toolTip = (Main.makeTooltip("Magnification filter", tooltipManager, getSkin()));
        selectBox.addListener(toolTip);

        selectBox.addListener(handListener);
        selectBox.getList().addListener(handListener);
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
        textButton = new TextButton("Generate Font", skin);
        textButton.setName("okButton");
        textButton.addListener(handListener);
        button(textButton, ButtonType.GENERATE);

        textButton = new TextButton("Save Settings", skin);
        textButton.setName("saveButton");
        textButton.addListener(handListener);
        getButtonTable().add(textButton);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                result(ButtonType.SAVE_SETTINGS);
            }
        });

        textButton = new TextButton("Load Settings", skin);
        textButton.setName("loadButton");
        textButton.addListener(handListener);
        getButtonTable().add(textButton);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                result(ButtonType.LOAD_SETTINGS);
            }
        });

        textButton = new TextButton("Cancel", skin);
        textButton.setName("cancelButton");
        textButton.addListener(handListener);
        button(textButton, ButtonType.CANCEL);
        
        updateLabelHighlight(null, null);
    }

    private void loadTTFsource(FileHandle file) {
        data.file = file;

        LeadingTruncateLabel ltLabel =  DialogBitmapFont.this.findActor("sourceFileField");
        ltLabel.setText(data.file.path());
        projectData.setLastFontPath(data.file.parent().path() + "/");

        if (target == null) {
            var name = data.file.nameWithoutExtension();
            target = data.file.sibling(name + ".fnt");
            var counter = 1;
            while (target.exists()) {
                target = target.sibling(name + "(" + counter + ").fnt");
                counter++;
            }
            ltLabel = DialogBitmapFont.this.findActor("targetFileField");
            ltLabel.setText(this.target.path());
        }

        updatePreviewAndOK();
    }

    private void updateColors() {
        TextButton textButton = findActor("colorTextButton");
        ColorData colorData = (ColorData) textButton.getUserObject();
        if (colorData != null && jsonData.getColors().contains(colorData, false)) {
            data.color = colorData.getName();
            if (automaticBgColor) {
                previewBGcolor.set(Utils.blackOrWhiteBgColor(colorData.color));
                previewTable.setColor(previewBGcolor);
                previewCursor.getSprite().setColor(colorData.color);
                previewSelection.getSprite().setColor(previewBGcolor == Color.BLACK ? Color.LIGHT_GRAY : Color.DARK_GRAY);
            }
        } else {
            data.color = null;
            if (automaticBgColor) {
                previewBGcolor.set(Color.BLACK);
                previewTable.setColor(previewBGcolor);
                previewCursor.getSprite().setColor(Color.WHITE);
                previewSelection.getSprite().setColor(Color.LIGHT_GRAY);
            }
        }
        textButton.setText(data.color);

        textButton = findActor("borderColorTextButton");
        colorData = (ColorData) textButton.getUserObject();
        if (colorData != null && jsonData.getColors().contains(colorData, false)) {
            data.borderColor = colorData.getName();
        } else {
            data.borderColor = null;
        }
        textButton.setText(data.borderColor);

        textButton = findActor("shadowColorTextButton");
        colorData = (ColorData) textButton.getUserObject();
        if (colorData != null && jsonData.getColors().contains(colorData, false)) {
            data.shadowColor = colorData.getName();
        } else {
            data.shadowColor = null;
        }
        textButton.setText(data.shadowColor);
    }

    private void updatePreviewAndOK() {
        boolean notValid = false;
        if (data.color == null) {
            notValid = true;
            updateLabelHighlight("color-label", findActor("colorTextButton"));
        } else if (data.file == null || !data.file.exists()) {
            notValid = true;
            updateLabelHighlight("source-label", findActor("sourceFileField"));
        } else if (target == null) {
            notValid = true;
            updateLabelHighlight("target-label", findActor("targetFileField"));
        } else if (!MathUtils.isZero(data.borderWidth) && data.borderColor == null) {
            notValid = true;
            updateLabelHighlight("border-color-label", findActor("borderColorTextButton"));
        } else if ((data.shadowOffsetX != 0 || data.shadowOffsetY != 0) && data.shadowColor == null) {
            notValid = true;
            updateLabelHighlight("shadow-color-label", findActor("shadowColorTextButton"));
        } else {
            updateLabelHighlight(null, null);
        }

        if (notValid) {
            TextField textField = findActor("previewField");
            Cell cell = ((Table) textField.getParent()).getCell(textField);
            previewStyle.font = skin.get("free-type-preview", TextFieldStyle.class).font;
            textField = new TextField(previewText, previewStyle);
            textField.setName("previewField");
            textField.setAlignment(Align.center);
            cell.setActor(textField);

            textField.addListener(ibeamListener);
            textField.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    previewText = ((TextField) actor).getText();
                }
            });
        } else {
            data.createBitmapFont();
            if (data.bitmapFont != null) {
                TextField textField = findActor("previewField");
                Cell cell = ((Table) textField.getParent()).getCell(textField);
                previewStyle.font = data.bitmapFont;
                textField = new TextField(previewText, previewStyle);
                textField.setName("previewField");
                textField.setAlignment(Align.center);
                cell.setActor(textField);

                textField.addListener(ibeamListener);
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

    private void saveSettings() {
        Runnable runnable = () -> {
            String defaultPath = projectData.getLastFontPath();

            File file = desktopWorker.saveDialog("Save Bitmap Font settings...", defaultPath, "scmp-font", "Font Settings files");
            if (file != null) {
                Gdx.app.postRunnable(() -> {
                    var fileHandle = new FileHandle(file);

                    if (!fileHandle.extension().toLowerCase(Locale.ROOT).equals("scmp-font")) {
                        fileHandle = fileHandle.sibling(fileHandle.name() + ".scmp-font");
                    }

                    saveSettings(fileHandle);
                });
            }
        };

        dialogFactory.showDialogLoading(runnable);
    }

    private void saveSettings(FileHandle fileHandle) {
        var fontSettings = new FontSettings();
        fontSettings.characters = data.characters;
        fontSettings.size = ((Spinner) findActor("size")).getValueAsInt();
        fontSettings.mono = ((Button) findActor("mono")).isChecked();
        fontSettings.hinting = ((SelectBox<String>) findActor("hinting")).getSelected();
        
        if (((TextButton) findActor("colorTextButton")).getUserObject() != null) {
            fontSettings.color = ((ColorData) ((TextButton) findActor("colorTextButton")).getUserObject()).getName();
            fontSettings.colorValue = Utils.colorToInt(((ColorData) ((TextButton) findActor("colorTextButton")).getUserObject()).color);
        }
        
        fontSettings.gamma = (float) ((Spinner) findActor("gamma")).getValue();
        fontSettings.renderCount = ((Spinner) findActor("renderCount")).getValueAsInt();
        fontSettings.borderWidth = (float) ((Spinner) findActor("borderWidth")).getValue();
        
        if (((TextButton) findActor("borderColorTextButton")).getUserObject() != null) {
            fontSettings.borderColor = ((ColorData) ((TextButton) findActor("borderColorTextButton")).getUserObject()).getName();
            fontSettings.borderColorValue = Utils.colorToInt(((ColorData) ((TextButton) findActor("borderColorTextButton")).getUserObject()).color);
        }
        
        fontSettings.borderStraight = ((Button) findActor("borderStraight")).isChecked();
        fontSettings.borderGamma = (float) ((Spinner) findActor("borderGamma")).getValue();
        fontSettings.shadowOffsetX = ((Spinner) findActor("shadowOffsetX")).getValueAsInt();
        fontSettings.shadowOffsetY = ((Spinner) findActor("shadowOffsetY")).getValueAsInt();
        
        if (((TextButton) findActor("shadowColorTextButton")).getUserObject() != null) {
            fontSettings.shadowColor = ((ColorData) ((TextButton) findActor("shadowColorTextButton")).getUserObject()).getName();
            fontSettings.shadowColorValue = Utils.colorToInt(((ColorData) ((TextButton) findActor("shadowColorTextButton")).getUserObject()).color);
        }
        
        fontSettings.incremental = ((Button) findActor("incremental")).isChecked();
        fontSettings.spaceX = ((Spinner) findActor("spaceX")).getValueAsInt();
        fontSettings.spaceY = ((Spinner) findActor("spaceY")).getValueAsInt();
        fontSettings.kerning = ((Button) findActor("kerning")).isChecked();
        fontSettings.flip = ((Button) findActor("flip")).isChecked();
        fontSettings.genMipMaps = ((Button) findActor("genMipMaps")).isChecked();
        fontSettings.minFilter = ((SelectBox<String>) findActor("minFilter")).getSelected();
        fontSettings.magFilter = ((SelectBox<String>) findActor("magFilter")).getSelected();

        fileHandle.writeString(json.prettyPrint(fontSettings), false, "utf-8");
    }

    private static class FontSettings {

        String characters;
        int size;
        boolean mono;
        String hinting;
        String color;
        int colorValue; //use as backup if color is not found. Create a color with the name.
        float gamma;
        int renderCount;
        float borderWidth;
        String borderColor;
        int borderColorValue; //use as backup if borderColor is not found. Create a color with the name.
        boolean borderStraight;
        float borderGamma;
        int shadowOffsetX;
        int shadowOffsetY;
        String shadowColor;
        int shadowColorValue; //use as backup if shadowColor is not found. Create a color with the name.
        boolean incremental;
        int spaceX;
        int spaceY;
        boolean kerning;
        boolean flip;
        boolean genMipMaps;
        String minFilter;
        String magFilter;
    }

    private void loadSettings() {
        Runnable runnable = () -> {
            String defaultPath = projectData.getLastFontPath();

            File file = desktopWorker.openDialog("Select Bitmap Font settings...", defaultPath, "scmp-font", "Font Settings files");
            if (file != null) {
                Gdx.app.postRunnable(() -> {
                    loadSettings(new FileHandle(file));
                });
            }
        };

        dialogFactory.showDialogLoading(runnable);
    }
    
    private void loadSettings(FileHandle fileHandle) {
        var fontSettings = json.fromJson(FontSettings.class, fileHandle.readString("utf-8"));

        ((TextField) findActor("characters")).setText(fontSettings.characters);
        data.characters = fontSettings.characters;
        
        ((Spinner) findActor("size")).setValue(fontSettings.size);
        data.size = fontSettings.size;
        
        ((Button) findActor("mono")).setChecked(fontSettings.mono);
        data.mono = fontSettings.mono;
        
        ((SelectBox<String>) findActor("hinting")).setSelected(fontSettings.hinting);
        data.hinting = fontSettings.hinting;
        
        if (fontSettings.color != null) {
            var color = jsonData.getColorByName(fontSettings.color);
            if (color == null) {
                try {
                    color = new ColorData(fontSettings.color, new Color(fontSettings.colorValue));
                } catch (ColorData.NameFormatException ex) {
                    Gdx.app.error(getClass().getName(), "Error creating color.", ex);
                }
                jsonData.getColors().add(color);
            }
            ((TextButton) findActor("colorTextButton")).setUserObject(color);
            data.color = fontSettings.color;
        }
        
        ((Spinner) findActor("gamma")).setValue(new BigDecimal(df.format(fontSettings.gamma)));
        data.gamma = fontSettings.gamma;
        
        ((Spinner) findActor("renderCount")).setValue(fontSettings.renderCount);
        data.renderCount = fontSettings.renderCount;
        
        ((Spinner) findActor("borderWidth")).setValue(fontSettings.borderWidth);
        data.borderWidth = fontSettings.borderWidth;
        
        if (fontSettings.borderColor != null) {
            var color = jsonData.getColorByName(fontSettings.borderColor);
            if (color == null) {
                try {
                    color = new ColorData(fontSettings.borderColor, new Color(fontSettings.borderColorValue));
                } catch (ColorData.NameFormatException ex) {
                    Gdx.app.error(getClass().getName(), "Error creating color.", ex);
                }
                jsonData.getColors().add(color);
            }
            ((TextButton) findActor("borderColorTextButton")).setUserObject(color);
            data.borderColor = fontSettings.borderColor;
        }
        
        ((Button) findActor("borderStraight")).setChecked(fontSettings.borderStraight);
        data.borderStraight = fontSettings.borderStraight;
        
        ((Spinner) findActor("borderGamma")).setValue(new BigDecimal(df.format(fontSettings.gamma)));
        data.borderGamma = fontSettings.borderGamma;
        
        ((Spinner) findActor("shadowOffsetX")).setValue(fontSettings.shadowOffsetX);
        data.shadowOffsetX = fontSettings.shadowOffsetX;
        
        ((Spinner) findActor("shadowOffsetY")).setValue(fontSettings.shadowOffsetY);
        data.shadowOffsetY = fontSettings.shadowOffsetY;
        
        if (fontSettings.shadowColor != null) {
            var color = jsonData.getColorByName(fontSettings.shadowColor);
            if (color == null) {
                try {
                    color = new ColorData(fontSettings.shadowColor, new Color(fontSettings.shadowColorValue));
                } catch (ColorData.NameFormatException ex) {
                    Gdx.app.error(getClass().getName(), "Error creating color.", ex);
                }
                jsonData.getColors().add(color);
            }
            ((TextButton) findActor("shadowColorTextButton")).setUserObject(color);
            data.shadowColor = fontSettings.shadowColor;
        }
        
        ((Button) findActor("incremental")).setChecked(fontSettings.incremental);
        data.incremental = fontSettings.incremental;
        
        ((Spinner) findActor("spaceX")).setValue(fontSettings.spaceX);
        data.spaceX = fontSettings.spaceX;
        
        ((Spinner) findActor("spaceY")).setValue(fontSettings.spaceY);
        data.spaceY = fontSettings.spaceY;
        
        ((Button) findActor("kerning")).setChecked(fontSettings.kerning);
        data.kerning = fontSettings.kerning;
        
        ((Button) findActor("flip")).setChecked(fontSettings.flip);
        data.flip = fontSettings.flip;
        
        ((Button) findActor("genMipMaps")).setChecked(fontSettings.genMipMaps);
        data.genMipMaps = fontSettings.genMipMaps;
        
        ((SelectBox<String>) findActor("minFilter")).setSelected(fontSettings.minFilter);
        data.minFilter = fontSettings.minFilter;
        
        ((SelectBox<String>) findActor("magFilter")).setSelected(fontSettings.magFilter);
        data.magFilter = fontSettings.magFilter;

        updateColors();
        updatePreviewAndOK();
    }

    private void updateLabelHighlight(String requiredLabelName, Actor arrowTarget) {
        var normalStyle = skin.get(LabelStyle.class);
        var requiredStyle = skin.get("dialog-required", LabelStyle.class);
        var actors = new Array<Actor>();
        actors.addAll(getChildren());
        
        for (int i = 0; i < actors.size; i++) {
            var actor = actors.get(i);
            
            if (actor instanceof Group) {
                actors.addAll(((Group) actor).getChildren());
            }
            
            if (actor instanceof Label) {
                Label label = (Label) actor;
                
                if (label.getStyle().equals(requiredStyle)) {
                    label.setStyle(normalStyle);
                }
                
                if (requiredLabelName != null && label.getName() != null && label.getName().equals(requiredLabelName)) {
                    label.setStyle(requiredStyle);
                }
            }
        }
    
        if (arrowTarget == null) arrowImage.setVisible(false);
        else {
            if (previousArrowTarget != arrowTarget) arrowDrawable.getAnimationState().setAnimation(0, "animation", true);
            arrowImage.setVisible(true);
            temp.set(arrowTarget.getWidth() / 2, arrowTarget.getHeight() / 2);
            arrowTarget.localToActorCoordinates(this, temp);
            arrowDrawable.getSkeleton().findBone("left-arrow").setPosition(-arrowTarget.getWidth() / 2, 0);
            arrowDrawable.getSkeleton().findBone("right-arrow").setPosition(arrowTarget.getWidth() / 2, 0);
            arrowImage.setPosition(temp.x, temp.y, Align.center);
            previousArrowTarget = arrowTarget;
        }
    }
    
    private void showCharacterDialog() {
        Runnable runnable = () -> {
            String defaultPath = projectData.getLastFontPath();

            File file = desktopWorker.openDialog("Select character text file...", defaultPath, null, "All files");
            if (file != null) {
                var fileHandle = new FileHandle(file);
                String characters = fileHandle.readString("utf-8");
                Gdx.app.postRunnable(() -> {
                    data.characters = Utils.removeDuplicateCharacters(characters);
                    updatePreviewAndOK();
                });
            } else {
                Gdx.app.postRunnable(() -> {
                    SelectBox<String> selectBox = findActor("characterSelectBox");
                    selectBox.setSelected("default");
                });
            }
        };

        dialogFactory.showDialogLoading(runnable);
    }
}