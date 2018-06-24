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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.Spinner;
import com.ray3k.skincomposer.data.ColorData;
import com.ray3k.skincomposer.data.FontData;
import com.ray3k.skincomposer.data.FreeTypeFontData;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.data.StyleProperty;
import com.ray3k.skincomposer.utils.Utils;
import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class DialogFreeTypeFont extends Dialog {
    private Main main;
    private Skin skin;
    private Table root;
    private Table buttons;
    private Mode mode;
    public static enum Mode {
         NEW, EDIT
    }
    private FreeTypeFontData data;
    private FreeTypeFontData originalData;
    private static DecimalFormat df;
    private Array<DialogFreeTypeFontListener> listeners;
    private TextFieldStyle previewStyle;
    private String previewText;
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
    
    public DialogFreeTypeFont(Main main, FreeTypeFontData freeTypeFontData) {
        super(freeTypeFontData == null ? "Create new FreeType Font" : "Edit FreeType Font", main.getSkin(), "bg");
        
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);
        df = new DecimalFormat("#.#", decimalFormatSymbols);
        
        if (freeTypeFontData == null) {
            mode = Mode.NEW;
            this.data = new FreeTypeFontData();
            
            FileHandle previewFontsPath = Gdx.files.local("preview fonts");
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
        
        this.main = main;
        skin = main.getSkin();
        
        previewStyle = new TextFieldStyle(skin.get("free-type-preview", TextFieldStyle.class));
        previewText = "Lorem ipsum dolor sit";
        
        getTitleTable().pad(10.0f);
        
        root = getContentTable();
        buttons = getButtonTable();
        
        populate();
        
        updateDisabledFields();
        
        key(Keys.ESCAPE, false).key(Keys.ENTER, true);
        
        listeners = new Array<>();
    }
    
    public DialogFreeTypeFont(Main main) {
        this(main, null);
    }

    @Override
    protected void result(Object object) {
        if ((Boolean) object) {
            if (mode == Mode.EDIT) {
                if (!originalData.name.equals(data.name)) {
                    for (Array<StyleData> styleDatas : main.getJsonData().getClassStyleMap().values()) {
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
                main.getJsonData().getFreeTypeFonts().removeValue(originalData, false);
                
                main.getUndoableManager().clearUndoables();

                main.getProjectData().setChangesSaved(false);
            }
            
            data.createBitmapFont(main);
            main.getJsonData().getFreeTypeFonts().add(data);

            for (DialogFreeTypeFontListener listener : listeners) {
                listener.fontAdded(data);
            }
            
            if (mode == Mode.EDIT) {
                main.getRootTable().refreshStyleProperties(true);
            }
        }
    }
    
    public void addListener(DialogFreeTypeFontListener listener) {
        listeners.add(listener);
    }

    private void populate() {
        root.pad(15.0f);
        
        Label label = new Label(mode == Mode.NEW ? "Create a new FreeType Font placeholder." : "Edit FreeType Font placeholder.", skin, "required");
        root.add(label);
        
        root.row();
        Table table = new Table();
        root.add(table).growX();
        
        table.defaults().space(5.0f);
        label = new Label("Font Name:", skin);
        table.add(label);
        
        TextField textField = new TextField(data.name, skin);
        textField.setName("fontName");
        table.add(textField);
        
        TextTooltip toolTip = new TextTooltip("The name used in skin JSON", main.getTooltipManager(), getSkin());
        textField.addListener(toolTip);
        
        textField.addListener(main.getIbeamListener());
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
        
        FileHandle previewFontsPath = Gdx.files.local("preview fonts");
        if (previewFontsPath.exists()) {
            Array<FileHandle> files = new Array<>(previewFontsPath.list("ttf"));
            
            for (FileHandle file : files) {
                previewFontNames.add(file.nameWithoutExtension());
            }
        }
        
        selectBox.setItems(previewFontNames);
        
        selectBox.setSelected(data.previewTTF);
        table.add(selectBox);
        
        toolTip = new TextTooltip("The TTF font for preview use in Skin Composer only", main.getTooltipManager(), getSkin());
        selectBox.addListener(toolTip);
        
        selectBox.addListener(main.getHandListener());
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
        
        toolTip = new TextTooltip("Add new preview fonts here", main.getTooltipManager(), getSkin());
        textButton.addListener(toolTip);
        
        textButton.addListener(main.getHandListener());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                try {
                    Utils.openFileExplorer(Gdx.files.local("preview fonts/"));
                } catch (Exception e) {
                    
                }
            }
        });
        
        root.row();
        Image image = new Image(skin, "welcome-separator");
        image.setScaling(Scaling.stretch);
        root.add(image).growX().space(15.0f);
        
        root.row();
        textField = new TextField(previewText, previewStyle);
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
        table = new Table();
        root.add(table);
        
        table.defaults().space(5.0f);
        CheckBox checkBox = new CheckBox("Use custom serializer and integrate TTF in Skin JSON", skin);
        checkBox.setName("serializerCheckBox");
        checkBox.setChecked(data.useCustomSerializer);
        table.add(checkBox);
        
        checkBox.addListener(main.getHandListener());
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
            }
        });
        
        textButton = new TextButton("More Info...", skin);
        table.add(textButton);
        
        textButton.addListener(main.getHandListener());
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
        root.add(scrollPane).padTop(10.0f);
        
        if (!data.useCustomSerializer) {
            scrollPane.setColor(1, 1, 1, .25f);
            scrollPane.setTouchable(Touchable.disabled);
        } else {
            selectBox.setColor(1, 1, 1, .25f);
            selectBox.setTouchable(Touchable.disabled);
        }
        
        bottom.defaults().space(5.0f);
        table = new Table();
        bottom.add(table).growX().colspan(4).spaceBottom(15.0f);
        
        table.defaults().space(5.0f);
        label = new Label("TTF Path:", skin);
        table.add(label);
        
        textField = new TextField(data.file == null ? "" : data.file.path(), skin);
        textField.setName("fileField");
        textField.setDisabled(true);
        table.add(textField).growX();
        
        toolTip = new TextTooltip("Path to TTF font to be distributed with skin", main.getTooltipManager(), getSkin());
        textField.addListener(toolTip);
        
        textButton = new TextButton("Browse...", skin);
        table.add(textButton);
        
        textButton.addListener(main.getHandListener());
        textButton.addListener(new ChangeListener() {
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
                        FileHandle fileHandle = new FileHandle(file);
                        data.file = fileHandle;
                        updateDisabledFields();
                        ((TextField)DialogFreeTypeFont.this.findActor("fileField")).setText(fileHandle.path());
                        main.getProjectData().setLastFontPath(fileHandle.parent().path() + "/");
                    }
                };
                
                main.getDialogFactory().showDialogLoading(runnable);
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
                updateDisabledFields();
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
                updateDisabledFields();
            }
        });
        
        bottom.row();
        label = new Label("Hinting:", skin);
        bottom.add(label).right();
        
        selectBox = new SelectBox(skin);
        selectBox.setItems("None", "Slight", "Medium", "Full", "AutoSlight", "AutoMedium", "AutoFull");
        selectBox.setSelected(data.hinting);
        bottom.add(selectBox).fillX().left();
        
        toolTip = new TextTooltip("Strength of hinting", main.getTooltipManager(), getSkin());
        selectBox.addListener(toolTip);
        
        selectBox.addListener(main.getHandListener());
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                SelectBox<String> selectBox = (SelectBox) actor;
                
                data.hinting = selectBox.getSelected();
                updateDisabledFields();
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
                    updateDisabledFields();
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
                updateDisabledFields();
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
                updateDisabledFields();
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
                updateDisabledFields();
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
                    updateDisabledFields();
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
                updateDisabledFields();
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
                updateDisabledFields();
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
                updateDisabledFields();
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
                updateDisabledFields();
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
                    updateDisabledFields();
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
                updateDisabledFields();
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
                updateDisabledFields();
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
                updateDisabledFields();
            }
        });
        
        bottom.row();
        
        label = new Label("Characters:", skin);
        bottom.add(label).right();
        
        textField = new TextField(data.characters, skin);
        bottom.add(textField).left().growX();
        
        toolTip = new TextTooltip("The characters the font should contain. Leave blank for defaults.", main.getTooltipManager(), getSkin());
        textField.addListener(toolTip);
        
        textField.addListener(main.getIbeamListener());
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                TextField textField = (TextField) actor;
                
                data.characters = textField.getText();
                updateDisabledFields();
            }
        });
        
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
                updateDisabledFields();
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
                updateDisabledFields();
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
                updateDisabledFields();
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
                updateDisabledFields();
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
                updateDisabledFields();
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
    
    private void updateDisabledFields() {
        CheckBox checkBox = findActor("serializerCheckBox");
        SelectBox selectBox = findActor("previewSelectBox");
        
        selectBox.setDisabled(checkBox.isChecked());
        
        boolean notValid = false;
        if (checkBox.isChecked()) {
            if (data.color == null) notValid = true;
            else if (data.file == null || !data.file.exists()) notValid = true;
            else if (!MathUtils.isZero(data.borderWidth) && data.borderColor == null) notValid = true;
            else if ((data.shadowOffsetX != 0 || data.shadowOffsetY != 0) && data.shadowColor == null) notValid = true;
        }
        
        if (notValid) {
            TextField textField = findActor("previewField");
            Cell cell = ((Table) textField.getParent()).getCell(textField);
            previewStyle.font = skin.get("free-type-preview", TextFieldStyle.class).font;
            textField = new TextField(previewText, previewStyle);
            textField.setName("previewField");
            textField.setAlignment(Align.center);
            cell.setActor(textField);
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
            }
        }
        
        if (!StyleData.validate(((TextField)findActor("fontName")).getText())) notValid = true;
        
        for (FontData font : main.getJsonData().getFonts()) {
            if (font.getName().equals(data.name)) {
                notValid = true;
                break;
            }
        }

        for (FreeTypeFontData font : main.getJsonData().getFreeTypeFonts()) {
            if (font.name.equals(data.name) && (mode == Mode.NEW || !font.name.equals(originalData.name))) {
                notValid = true;
                break;
            }
        }
        
        TextButton textButton = findActor("okButton");
        textButton.setDisabled(notValid);
    }
    
    private void showMoreInfoDialog() {
        Dialog dialog = new Dialog("Custom serializer for FreeType Fonts", skin, "bg");
        dialog.setFillParent(true);
        
        dialog.getTitleLabel().setAlignment(Align.center);
        dialog.getContentTable().pad(15.0f);
        dialog.getButtonTable().pad(15.0f);
        
        Label label = new Label("Integrating TTF files and specifying FreeType within a Skin JSON requires a custom serializer. This is done by overriding getJsonLoader(). See the example below:", skin);
        label.setWrap(true);
        dialog.getContentTable().add(label).growX();
        
        dialog.getContentTable().row();
        Image image = new Image(skin, "code-sample");
        image.setScaling(Scaling.fit);
        dialog.getContentTable().add(image);
        
        dialog.getContentTable().row();
        TextButton textButton = new TextButton("Copy sample code to clipboard", skin);
        dialog.getContentTable().add(textButton);
        
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Gdx.app.getClipboard().setContents(SERIALIZER_TEXT);
            }
        });

        dialog.key(Keys.ENTER, true).key(Keys.ESCAPE, false);
        dialog.getButtonTable().defaults().minWidth(100.0f);
        dialog.button("OK", true);
        dialog.show(main.getStage());
    }
    
    public static interface DialogFreeTypeFontListener {
        public void fontAdded(FreeTypeFontData font);
    }
}
