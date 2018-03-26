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
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
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
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Scaling;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.Spinner;
import com.ray3k.skincomposer.data.ColorData;
import com.ray3k.skincomposer.data.FreeTypeFontData;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.data.StyleProperty;
import com.ray3k.skincomposer.utils.Utils;
import java.io.File;
import java.text.DecimalFormat;

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
    private static final DecimalFormat df = new DecimalFormat("#.#");
    
    public DialogFreeTypeFont(Main main, FreeTypeFontData freeTypeFontData) {
        super(freeTypeFontData == null ? "Create new FreeType Font" : "Edit FreeType Font", main.getSkin(), "bg");
        
        if (freeTypeFontData == null) {
            mode = Mode.NEW;
            this.data = new FreeTypeFontData();
        } else {
            mode = Mode.EDIT;
            this.data = new FreeTypeFontData(freeTypeFontData);
        }
        
        this.main = main;
        skin = main.getSkin();
        
        getTitleTable().pad(10.0f);
        
        root = getContentTable();
        buttons = getButtonTable();
        
        populate();
        
        updateDisabledFields();
        
        key(Keys.ESCAPE, false).key(Keys.ENTER, true);
    }
    
    public DialogFreeTypeFont(Main main) {
        this(main, null);
    }

    @Override
    protected void result(Object object) {
        if ((Boolean) object) {
            Json json = new Json();
            json.setUsePrototypes(false);
            System.out.println(json.prettyPrint(data));
        }
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
        selectBox.setItems("Roboto", "Century", "Comic Sans");
        table.add(selectBox);
        
        selectBox.addListener(main.getHandListener());
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                SelectBox<String> selectBox = (SelectBox) actor;
                
                data.name = selectBox.getSelected();
            }
        });
        
        TextButton textButton = new TextButton("Open Preview Folder", skin);
        table.add(textButton);
        
        textButton.addListener(main.getHandListener());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                
            }
        });
        
        root.row();
        Image image = new Image(skin, "welcome-separator");
        image.setScaling(Scaling.stretch);
        root.add(image).growX().space(15.0f);
        
        root.row();
        textField = new TextField("Lorem ipsum dolor sit", skin, "free-type-preview");
        textField.setAlignment(Align.center);
        root.add(textField).growX();
        
        textField.addListener(main.getIbeamListener());
        
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
        label = new Label("Font Path:", skin);
        table.add(label);
        
        textField = new TextField(data.file == null ? "" : data.file.path(), skin);
        textField.setName("fileField");
        textField.setDisabled(true);
        table.add(textField).growX();
        
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
        image = new Image(skin, "icon-colorwheel-over");
        textButton.add(image).space(10.0f);
        bottom.add(textButton).left();
        
        textButton.addListener(main.getHandListener());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                TextButton textButton = (TextButton) actor;
                
                main.getDialogFactory().showDialogColors(new StyleProperty(), (ColorData colorData) -> {
                    if (colorData != null) {
                        textButton.setText(colorData.getName());
                        data.color = colorData.getName();
                        updateDisabledFields();
                    }
                });
            }
        });
        
        bottom.row();
        label = new Label("Gamma:", skin);
        bottom.add(label).right();
        
        spinner = new Spinner(Double.parseDouble(df.format(data.gamma)), 1.0, false, Spinner.Orientation.HORIZONTAL, skin);
        bottom.add(spinner).left().minWidth(100.0f);
        
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
        image = new Image(skin, "icon-colorwheel-over");
        textButton.add(image).space(10.0f);
        bottom.add(textButton).left();
        
        textButton.addListener(main.getHandListener());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                TextButton textButton = (TextButton) actor;
                
                main.getDialogFactory().showDialogColors(new StyleProperty(), (ColorData colorData) -> {
                    if (colorData != null) {
                        textButton.setText(colorData.getName());
                        data.borderColor = colorData.getName();
                        updateDisabledFields();
                    }
                });
            }
        });
        
        bottom.row();
        label = new Label("Border Straight:", skin);
        bottom.add(label).right();
        
        button = new Button(skin, "switch");
        button.setChecked(data.borderStraight);
        bottom.add(button).left();
        
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
        image = new Image(skin, "icon-colorwheel-over");
        textButton.add(image).space(10.0f);
        bottom.add(textButton).left();
        
        textButton.addListener(main.getHandListener());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                TextButton textButton = (TextButton) actor;
                
                main.getDialogFactory().showDialogColors(new StyleProperty(), (ColorData colorData) -> {
                    if (colorData != null) {
                        textButton.setText(colorData.getName());
                        data.shadowColor = colorData.getName();
                        updateDisabledFields();
                    }
                });
            }
        });
        
        label = new Label("Incremental:", skin);
        bottom.add(label).right();
        
        button = new Button(skin, "switch");
        button.setChecked(data.incremental);
        bottom.add(button).left();
        
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
    
    private void updateDisabledFields() {
        CheckBox checkBox = findActor("serializerCheckBox");
        SelectBox selectBox = findActor("previewSelectBox");
        
        selectBox.setDisabled(checkBox.isChecked());
        
        boolean notValid = false;
        if (!StyleData.validate(((TextField)findActor("fontName")).getText())) notValid = true;
        if (checkBox.isChecked()) {
            if (data.color == null) notValid = true;
            else if (data.file == null || !data.file.exists()) notValid = true;
            else if (!MathUtils.isZero(data.borderWidth) && data.borderColor == null) notValid = true;
            else if ((data.shadowOffsetX != 0 || data.shadowOffsetY != 0) && data.shadowColor == null) notValid = true;
        }
        
        TextButton textButton = findActor("okButton");
        textButton.setDisabled(notValid);
    }
}
