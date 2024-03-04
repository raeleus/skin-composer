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
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
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
import com.ray3k.skincomposer.data.*;
import com.ray3k.skincomposer.utils.Utils;
import com.ray3k.stripe.PopColorPicker.PopColorPickerAdapter;
import com.ray3k.stripe.Spinner;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static com.ray3k.skincomposer.Main.*;

public class DialogFreeTypeFont extends Dialog {
    private Table root;
    private Table buttons;
    private Mode mode;
    public enum Mode {
         NEW, EDIT
    }
    private FreeTypeFontData data;
    private FreeTypeFontData originalData;
    private static DecimalFormat df;
    private Array<DialogFreeTypeFontListener> listeners;
    private TextFieldStyle previewStyle;
    private SpriteDrawable previewCursor;
    private SpriteDrawable previewSelection;
    private String previewText;
    private boolean automaticBgColor;
    private static final String SERIALIZER_TEXT = "skin = new Skin(Gdx.files.internal(\"skin-name.json\")) {\n" +
"            //Override json loader to process FreeType fonts from skin JSON\n" +
"            @Override\n" +
"            protected Json getJsonLoader(final FileHandle skinFile) {\n" +
"                Json json = super.getJsonLoader(skinFile);\n" +
"                final Skin skin = this;\n" +
"\n" +
"                json.setSerializer(FreeTypeFontGenerator.class, new Json.ReadOnlySerializer<FreeTypeFontGenerator>() {\n" +
"                    @Override\n" +
"                    public FreeTypeFontGenerator read(Json json,\n" +
"                            JsonValue jsonData, Class type) {\n" +
"                        String path = json.readValue(\"font\", String.class, jsonData);\n" +
"                        jsonData.remove(\"font\");\n" +
"\n" +
"                        Hinting hinting = Hinting.valueOf(json.readValue(\"hinting\", \n" +
"                                String.class, \"AutoMedium\", jsonData));\n" +
"                        jsonData.remove(\"hinting\");\n" +
"\n" +
"                        TextureFilter minFilter = TextureFilter.valueOf(\n" +
"                                json.readValue(\"minFilter\", String.class, \"Nearest\", jsonData));\n" +
"                        jsonData.remove(\"minFilter\");\n" +
"\n" +
"                        TextureFilter magFilter = TextureFilter.valueOf(\n" +
"                                json.readValue(\"magFilter\", String.class, \"Nearest\", jsonData));\n" +
"                        jsonData.remove(\"magFilter\");\n" +
"\n" +
"                        FreeTypeFontParameter parameter = json.readValue(FreeTypeFontParameter.class, jsonData);\n" +
"                        parameter.hinting = hinting;\n" +
"                        parameter.minFilter = minFilter;\n" +
"                        parameter.magFilter = magFilter;\n" +
"                        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(skinFile.parent().child(path));\n" +
"                        BitmapFont font = generator.generateFont(parameter);\n" +
"                        skin.add(jsonData.name, font);\n" +
"                        if (parameter.incremental) {\n" +
"                            generator.dispose();\n" +
"                            return null;\n" +
"                        } else {\n" +
"                            return generator;\n" +
"                        }\n" +
"                    }\n" +
"                });\n" +
"\n" +
"                return json;\n" +
"            }\n" +
"        };";
    private static enum ButtonType {
        GENERATE, SAVE_SETTINGS, LOAD_SETTINGS, CANCEL
    }
    private Json json;
    private Color previewBGcolor;
    private FilesDroppedListener filesDroppedListener;
    private SpineDrawable arrowDrawable;
    private Image arrowImage;
    private static Vector2 temp = new Vector2();
    private Actor previousArrowTarget;
    
    public DialogFreeTypeFont(FreeTypeFontData freeTypeFontData) {
        super(freeTypeFontData == null ? "Create new FreeType Font" : "Edit FreeType Font", skin, "bg");
        arrowDrawable = new SpineDrawable(skeletonRenderer, arrowSkeletonData, arrowAnimationStateData);
        arrowDrawable.getAnimationState().setAnimation(0, "animation", true);
        arrowDrawable.setCrop(-10, -10, 20, 20);
        arrowImage = new Image(arrowDrawable);
        arrowImage.setTouchable(Touchable.disabled);
        addActor(arrowImage);
        arrowImage.pack();
        previewBGcolor = new Color(Color.WHITE);
        automaticBgColor = true;
        
        json = new Json(JsonWriter.OutputType.json);
        
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);
        df = new DecimalFormat("#.#", decimalFormatSymbols);
        
        if (freeTypeFontData == null) {
            mode = Mode.NEW;
            this.data = new FreeTypeFontData();
            
            FileHandle previewFontsPath = Main.appFolder.child("preview fonts");
            if (previewFontsPath.exists()) {
                FileHandle[] files = previewFontsPath.list("ttf");
                if (files.length > 0) {
                    data.previewTTF = files[0].nameWithoutExtension();
                }
            }
        } else {
            mode = Mode.EDIT;
            this.data = new FreeTypeFontData(freeTypeFontData);
            originalData = freeTypeFontData;
        }
        
        if (data.color != null) previewBGcolor.set(Utils.blackOrWhiteBgColor(jsonData.getColorByName(data.color).color));
        previewStyle = new TextFieldStyle(skin.get("free-type-preview", TextFieldStyle.class));
        previewCursor = (SpriteDrawable) ((TextureRegionDrawable) skin.getDrawable("white")).tint(Color.BLACK);
        previewStyle.cursor = previewCursor;
        previewSelection = (SpriteDrawable) ((TextureRegionDrawable) skin.getDrawable("white")).tint(Color.DARK_GRAY);
        previewStyle.selection = previewSelection;
        previewText = "Lorem ipsum dolor sit";
        
        getTitleTable().pad(10.0f);
        
        root = getContentTable();
        buttons = getButtonTable();
        
        populate();
        
        updateDisabledFields();
        
        key(Keys.ESCAPE, ButtonType.CANCEL);
        
        listeners = new Array<>();
        
        filesDroppedListener = (Array<FileHandle> files) -> {
            if (files.size > 0) {
                CheckBox checkBox = findActor("serializerCheckBox");
                
                var extension = files.first().extension().toLowerCase(Locale.ROOT);
                if (extension.equals("ttf") || extension.equals("otf")) {
                    checkBox.setChecked(true);
                    loadTTF(files.first());
                } else if (extension.equals("scmp-font")) {
                    checkBox.setChecked(true);
                    loadSettings(files.first());
                }
            }
        };
        
        desktopWorker.addFilesDroppedListener(filesDroppedListener);
    }
    
    @Override
    public Dialog show(Stage stage, Action action) {
        var result = super.show(stage, action);
        updateDisabledFields();
        return result;
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
    
    public DialogFreeTypeFont() {
        this(null);
    }

    @Override
    protected void result(Object object) {
        switch ((ButtonType) object) {
            case GENERATE:
                if (mode == Mode.EDIT) {
                    if (!originalData.name.equals(data.name)) {
                        for (Array<StyleData> styleDatas : jsonData.getClassStyleMap().values()) {
                            for (StyleData styleData : styleDatas) {
                                for (StyleProperty property : styleData.properties.values()) {
                                    if (property != null && property.type.equals(BitmapFont.class) && property.value != null && property.value.equals(originalData.name)) {
                                        property.value = data.name;
                                    }
                                }
                            }
                        }
                    }

                    originalData.bitmapFont.dispose();
                    jsonData.getFreeTypeFonts().removeValue(originalData, false);

                    undoableManager.clearUndoables();

                    projectData.setChangesSaved(false);
                }

                data.createBitmapFont();
                jsonData.getFreeTypeFonts().add(data);

                for (DialogFreeTypeFontListener listener : listeners) {
                    listener.fontAdded(data);
                }

                if (mode == Mode.EDIT) {
                    rootTable.refreshStyleProperties(true);
                }
                break;
            case SAVE_SETTINGS:
                saveSettings();
                break;
            case LOAD_SETTINGS:
                loadSettings();
                break;
            case CANCEL:
                for (var listener : listeners) {
                    listener.cancelled();
                }
                break;
        }
    }
    
    public void addListener(DialogFreeTypeFontListener listener) {
        listeners.add(listener);
    }

    private void populate() {
        root.pad(15.0f);
        
        Label label = new Label(mode == Mode.NEW ? "Create a new FreeType Font placeholder." : "Edit FreeType Font placeholder.", skin, "black");
        root.add(label);
        
        root.row();
        Table table = new Table();
        root.add(table).growX();
        
        table.defaults().space(5.0f);
        label = new Label("Font Name:", skin);
        label.setName("name-label");
        table.add(label);
        
        TextField textField = new TextField(data.name, skin);
        textField.setName("fontName");
        table.add(textField);
        
        TextTooltip toolTip = (Main.makeTooltip("The name used in skin JSON", tooltipManager, getSkin()));
        textField.addListener(toolTip);
        
        textField.addListener(ibeamListener);
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                TextField textField = (TextField) actor;
                
                data.name = textField.getText();
                
                updateDisabledFields();
            }
        });
        
        label = new Label("Preview TTF:", skin);
        table.add(label).spaceLeft(15.0f);
        
        SelectBox<String> selectBox = new SelectBox(skin);
        selectBox.setName("previewSelectBox");
        
        Array<String> previewFontNames = new Array<>();
        
        FileHandle previewFontsPath = Main.appFolder.child("preview fonts");
        if (previewFontsPath.exists()) {
            Array<FileHandle> files = new Array<>(previewFontsPath.list("ttf"));
            
            for (FileHandle file : files) {
                previewFontNames.add(file.nameWithoutExtension());
            }
        }
        
        selectBox.setItems(previewFontNames);
        
        selectBox.setSelected(data.previewTTF);
        table.add(selectBox);
        
        toolTip = (Main.makeTooltip("The TTF font for preview use in Skin Composer only", tooltipManager, getSkin()));
        selectBox.addListener(toolTip);
        
        selectBox.addListener(handListener);
        selectBox.getList().addListener(handListener);
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                SelectBox<String> selectBox = (SelectBox) actor;
                
                data.previewTTF = selectBox.getSelected();
                updateDisabledFields();
            }
        });
        
        TextButton textButton = new TextButton("Open Preview Folder", skin);
        table.add(textButton);
        
        toolTip = (Main.makeTooltip("Add new preview fonts here", tooltipManager, getSkin()));
        textButton.addListener(toolTip);
        
        textButton.addListener(handListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                try {
                    Utils.openFileExplorer(Main.appFolder.child("preview fonts/"));
                } catch (IOException e) {
                    Gdx.app.error(getClass().getName(), "Error while selecting preview font.", e);
                }
            }
        });
        
        root.row();
        Image image = new Image(skin, "welcome-separator");
        image.setScaling(Scaling.stretch);
        root.add(image).growX().space(15.0f);
        
        root.row();
        final var previewTable = new Table();
        previewTable.setBackground(getSkin().getDrawable("white"));
        previewTable.setColor(previewBGcolor);
        root.add(previewTable).growX();
        
        textField = new TextField(previewText, previewStyle);
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
                        previewBGcolor.set(color);
                        previewTable.setColor(color);
                        automaticBgColor = false;
                    }
                });
            }
        });
        toolTip = (Main.makeTooltip("Background color for preview text.", tooltipManager, getSkin()));
        imageButton.addListener(toolTip);
        
        root.row();
        image = new Image(skin, "welcome-separator");
        image.setScaling(Scaling.stretch);
        root.add(image).growX().space(15.0f);
        
        root.row();
        table = new Table();
        root.add(table);
        
        table.defaults().space(5.0f);
        CheckBox checkBox = new CheckBox("Use custom serializer and integrate FreeType in Skin JSON", skin);
        checkBox.setName("serializerCheckBox");
        checkBox.setChecked(data.useCustomSerializer);
        table.add(checkBox);
        
        checkBox.addListener(handListener);
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                CheckBox checkBox = (CheckBox) actor;
                
                ScrollPane scrollPane = findActor("scrollPane");
                SelectBox selectBox = findActor("previewSelectBox");
                if (checkBox.isChecked()) {
                    scrollPane.addAction(Actions.sequence(Actions.fadeIn(.25f), Actions.touchable(Touchable.enabled)));
                    selectBox.addAction(Actions.sequence(Actions.alpha(.25f, .25f), Actions.touchable(Touchable.disabled)));
                } else {
                    scrollPane.addAction(Actions.sequence(Actions.alpha(.25f, .25f), Actions.touchable(Touchable.disabled)));
                    selectBox.addAction(Actions.sequence(Actions.fadeIn(.25f), Actions.touchable(Touchable.enabled)));
                }
                
                data.useCustomSerializer = checkBox.isChecked();
                
                updateDisabledFields();
    
                if (automaticBgColor) {
                    var color = jsonData.getColorByName(data.name);
                    previewBGcolor.set(Utils.blackOrWhiteBgColor(color == null? Color.WHITE : color.color));
                    previewTable.setColor(previewBGcolor);
                }
            }
        });
        
        textButton = new TextButton("More Info...", skin);
        table.add(textButton);
        
        textButton.addListener(handListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                showMoreInfoDialog();
            }
        });
        
        root.row();
        Table bottom = new Table();
        ScrollPane scrollPane = new ScrollPane(bottom, skin);
        scrollPane.setName("scrollPane");
        scrollPane.setFadeScrollBars(false);
        scrollPane.setFlickScroll(false);
        root.add(scrollPane).padTop(10.0f).grow();
        
        if (!data.useCustomSerializer) {
            scrollPane.setColor(1, 1, 1, .25f);
            scrollPane.setTouchable(Touchable.disabled);
        } else {
            selectBox.setColor(1, 1, 1, .25f);
            selectBox.setTouchable(Touchable.disabled);
        }
        
        bottom.defaults().space(5.0f);
        table = new Table();
        bottom.add(table).growX().colspan(5).spaceBottom(15.0f);
        
        table.defaults().space(5.0f);
        label = new Label("Font Path:", skin);
        label.setName("source-label");
        table.add(label).right();
        
        var ltLabel = new LeadingTruncateLabel(data.file == null ? "" : data.file.path(), skin, "field");
        ltLabel.setName("fileField");
        ltLabel.setEllipsis(true);
        table.add(ltLabel).growX().minWidth(0).prefWidth(0);
    
        ltLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                var ltLabel = findActor("fileField");
                ltLabel.fire(new ChangeListener.ChangeEvent());
            }
        });
        
        textButton = new TextButton("Browse...", skin);
        table.add(textButton).fillX();
        
        toolTip = (Main.makeTooltip("Path to font to be distributed with skin", tooltipManager, getSkin()));
        ltLabel.addListener(toolTip);
        textButton.addListener(toolTip);
        ltLabel.addListener(handListener);
        textButton.addListener(handListener);
        var changeListener = new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Runnable runnable = () -> {
                    String defaultPath = projectData.getLastFontPath();

                    File file = desktopWorker.openDialog("Select Font file...", defaultPath, "ttf,otf", "Font Files (*.TTF;*.OTF)");
                    if (file != null) {
                        Gdx.app.postRunnable(() -> {
                            FileHandle fileHandle = new FileHandle(file);
                            loadTTF(fileHandle);
                        });
                    }
                };
                
                dialogFactory.showDialogLoading(runnable);
            }
        };
        textButton.addListener(changeListener);
        ltLabel.addListener(changeListener);
        
        table.row();
        
        label = new Label("Characters:", skin);
        table.add(label).right();
        
        textField = new TextField(data.characters, skin);
        textField.setName("characters");
        table.add(textField).growX();
        
        toolTip = (Main.makeTooltip("The characters the font should contain. Leave blank for defaults.", tooltipManager, getSkin()));
        textField.addListener(toolTip);
        
        textField.addListener(ibeamListener);
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                TextField textField = (TextField) actor;
                
                data.characters = textField.getText();
                data.characters = !data.characters.equals("") && !data.characters.contains("\u0000") ? "\u0000" + data.characters : data.characters;
                updateDisabledFields();
                
                var selectBox = (SelectBox<String>) findActor("character-select-box");
                selectBox.setSelected("custom");
            }
        });
        
        selectBox = new SelectBox<String>(skin);
        selectBox.setName("character-select-box");
        selectBox.setItems("default", "0-9", "a-zA-Z", "a-zA-Z0-9", "custom", "Load from file (UTF-8)...");
        table.add(selectBox);
        if (!data.characters.equals("")) {
            selectBox.setSelected("custom");
        }
        
        selectBox.addListener(handListener);
        selectBox.getList().addListener(handListener);
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                var selectBox = (SelectBox<String>) findActor("character-select-box");
                var textField = (TextField) findActor("characters");
                
                switch (selectBox.getSelected()) {
                    case "default":
                        textField.setText("");
                        textField.setMessageText("");
                        data.characters = "";
                        break;
                    case "0-9":
                        textField.setText("0123456789");
                        textField.setMessageText("");
                        data.characters = textField.getText();
                        break;
                    case "a-zA-Z":
                        textField.setText("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
                        textField.setMessageText("");
                        data.characters = textField.getText();
                        break;
                    case "a-zA-Z0-9":
                        textField.setText("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
                        textField.setMessageText("");
                        data.characters = textField.getText();
                        break;
                    case "Load from file (UTF-8)...":
                        textField.setText("");
                        textField.setMessageText("Characters loaded from text file...");
                        data.characters = "";
                        showCharacterDialog();
                        break;
                }
                
                data.characters = !data.characters.equals("") && !data.characters.contains("\u0000") ? "\u0000" + data.characters : data.characters;
                updateDisabledFields();
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
                updateDisabledFields();
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
                updateDisabledFields();
            }
        });
        
        bottom.add().growX();
        
        bottom.row();
        label = new Label("Hinting:", skin);
        bottom.add(label).right();
        
        selectBox = new SelectBox<>(skin);
        selectBox.setName("hinting");
        selectBox.setItems("None", "Slight", "Medium", "Full", "AutoSlight", "AutoMedium", "AutoFull");
        selectBox.setSelected(data.hinting);
        bottom.add(selectBox).fillX().left();
        
        toolTip = (Main.makeTooltip("Strength of hinting", tooltipManager, getSkin()));
        selectBox.addListener(toolTip);
        
        selectBox.addListener(handListener);
        selectBox.getList().addListener(handListener);
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                SelectBox<String> selectBox = (SelectBox) actor;
                
                data.hinting = selectBox.getSelected();
                updateDisabledFields();
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
                        if (automaticBgColor) {
                            previewBGcolor.set(Utils.blackOrWhiteBgColor(colorData.color));
                            previewCursor.getSprite().setColor(colorData.color);
                            previewSelection.getSprite().setColor(previewBGcolor == Color.BLACK ? Color.LIGHT_GRAY : Color.DARK_GRAY);
                            previewTable.setColor(previewBGcolor);
                        }
                    } else {
                        textButton.setText("");
                        textButton.setUserObject(null);
                        data.color = null;
                    }
                    
                    updateColors();
                    updateDisabledFields();
                }, null);
            }
        });
        if (data.color != null) {
            textButton.setUserObject(jsonData.getColorByName(data.color));
        }
        
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
                updateDisabledFields();
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
                updateDisabledFields();
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
                updateDisabledFields();
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
                    updateDisabledFields();
                }, null);
            }
        });
        if (data.borderColor != null) {
            textButton.setUserObject(jsonData.getColorByName(data.borderColor));
        }
        
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
                updateDisabledFields();
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
                updateDisabledFields();
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
                updateDisabledFields();
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
                updateDisabledFields();
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
                    updateDisabledFields();
                }, null);
            }
        });
        if (data.shadowColor != null) {
            textButton.setUserObject(jsonData.getColorByName(data.shadowColor));
        }
        
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
                updateDisabledFields();
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
                updateDisabledFields();
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
                updateDisabledFields();
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
                updateDisabledFields();
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
                updateDisabledFields();
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
                updateDisabledFields();
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
                updateDisabledFields();
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
                updateDisabledFields();
            }
        });
        
        bottom.row();
        bottom.add().growY();
        
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
    }
    
    private void updateColors() {
        TextButton textButton = findActor("colorTextButton");
        ColorData colorData = (ColorData) textButton.getUserObject();
        if (colorData != null && jsonData.getColors().contains(colorData, false)) {
            data.color = colorData.getName();
        } else {
            data.color = null;
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
    
    private void updateDisabledFields() {
        CheckBox checkBox = findActor("serializerCheckBox");
        SelectBox selectBox = findActor("previewSelectBox");
        
        selectBox.setDisabled(checkBox.isChecked());
        
        boolean notValid = false;
        if (checkBox.isChecked()) {
            if (data.color == null) {
                notValid = true;
                updateLabelHighlight("color-label", findActor("colorTextButton"));
            }
            else if (data.file == null || !data.file.exists()) {
                notValid = true;
                updateLabelHighlight("source-label", findActor("fileField"));
            }
            else if (!MathUtils.isZero(data.borderWidth) && data.borderColor == null) {
                notValid = true;
                updateLabelHighlight("border-color-label", findActor("borderColorTextButton"));
            }
            else if ((data.shadowOffsetX != 0 || data.shadowOffsetY != 0) && data.shadowColor == null) {
                notValid = true;
                updateLabelHighlight("shadow-color-label", findActor("shadowColorTextButton"));
            }
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
        
        if (!StyleData.validate(((TextField)findActor("fontName")).getText())) {
            notValid = true;
            updateLabelHighlight("name-label", findActor("fontName"));
        }
        
        for (FontData font : jsonData.getFonts()) {
            if (font.getName().equals(data.name)) {
                notValid = true;
                updateLabelHighlight("name-label", findActor("fontName"));
                break;
            }
        }

        for (FreeTypeFontData font : jsonData.getFreeTypeFonts()) {
            if (font.name.equals(data.name) && (mode == Mode.NEW || !font.name.equals(originalData.name))) {
                notValid = true;
                updateLabelHighlight("name-label", findActor("fontName"));
                break;
            }
        }
        
        if (!notValid) {
            updateLabelHighlight(null, null);
        }
        
        TextButton textButton = findActor("okButton");
        textButton.setDisabled(notValid);
    }
    
    private void showMoreInfoDialog() {
        Dialog dialog = new Dialog("Custom serializer for FreeType Fonts", skin, "bg");
        
        dialog.getTitleLabel().setAlignment(Align.center);
        dialog.getContentTable().pad(15.0f);
        dialog.getButtonTable().pad(15.0f);
        
        Label label = new Label("Integrating TTF files and specifying FreeType within a Skin JSON requires a custom serializer. This is done by overriding getJsonLoader().", skin);
        label.setWrap(true);
        dialog.getContentTable().add(label).growX();
        
        dialog.getContentTable().row();
        TextButton textButton = new TextButton("Copy custom serializer to clipboard", skin);
        dialog.getContentTable().add(textButton);
        textButton.addListener(handListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Gdx.app.getClipboard().setContents(SERIALIZER_TEXT);
            }
        });
    
        dialog.getContentTable().row();
        label = new Label("Alternatively, you may use the FreeTypeSkin class from the Stripe library which automatically does this for you.", skin);
        label.setWrap(true);
        dialog.getContentTable().add(label).growX().padTop(50);
    
        dialog.getContentTable().row();
        var table = new Table();
        dialog.getContentTable().add(table);
    
        table.defaults().space(30).uniformX().fillX();
        textButton = new TextButton("How to install Stripe", skin);
        table.add(textButton);
        textButton.addListener(handListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Gdx.net.openURI("https://github.com/raeleus/stripe#how-to-include-in-your-project");
            }
        });
        
        textButton = new TextButton("Custom Serializer wiki", skin);
        table.add(textButton);
        textButton.addListener(handListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Gdx.net.openURI("https://github.com/raeleus/skin-composer/wiki/Creating-FreeType-Fonts#using-a-custom-serializer");
            }
        });
        

        dialog.key(Keys.ENTER, true).key(Keys.ESCAPE, false).key(Keys.NUMPAD_ENTER, true);
        dialog.getButtonTable().defaults().minWidth(100.0f);
        textButton = new TextButton("OK", skin);
        dialog.button(textButton, true);
        textButton.addListener(handListener);
        dialog.show(stage);
    }
    
    private void loadTTF(FileHandle fileHandle) {
        data.file = fileHandle;
        updateDisabledFields();
        ((LeadingTruncateLabel) DialogFreeTypeFont.this.findActor("fileField")).setText(fileHandle.path());
        projectData.setLastFontPath(fileHandle.parent().path() + "/");
    }
    
    public static interface DialogFreeTypeFontListener {
        public void fontAdded(FreeTypeFontData font);
        public void cancelled();
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
                    jsonData.getColors().add(color);
                } catch (ColorData.NameFormatException ex) {
                    Gdx.app.error(getClass().getName(), "Error creating color.", ex);
                }
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
        
        ((Spinner) findActor("borderGamma")).setValue(new BigDecimal(df.format(fontSettings.borderGamma)));
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
        updateDisabledFields();
    }
    
    private void updateLabelHighlight(String requiredLabelName, Actor arrowTarget) {
        var normalStyle = skin.get(Label.LabelStyle.class);
        var requiredStyle = skin.get("dialog-required", Label.LabelStyle.class);
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
                    updateDisabledFields();
                });
            } else {
                Gdx.app.postRunnable(() -> {
                    SelectBox<String> selectBox = findActor("character-select-box");
                    selectBox.setSelected("default");
                });
            }
        };

        dialogFactory.showDialogLoading(runnable);
    }
}