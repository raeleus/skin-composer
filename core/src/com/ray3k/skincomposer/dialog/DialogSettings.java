/** *****************************************************************************
 * MIT License
 *
 * Copyright (c) 2018 Raymond Buckley
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
 ***************************************************************************** */
package com.ray3k.skincomposer.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.RootTable;
import com.ray3k.skincomposer.Spinner;
import com.ray3k.skincomposer.Spinner.Orientation;
import com.ray3k.skincomposer.utils.Utils;

public class DialogSettings extends Dialog {
    private Integer maxUndos;
    private boolean resourcesRelative;
    private boolean simpleNames;
    private boolean allowingWelcome;
    private boolean allowingUpdates;
    private final Main main;
    private ExportFormat exportFormat;
    
    public static enum ExportFormat {
        MINIMAL("Minimal", OutputType.minimal), JAVASCRIPT("JavaScript", JsonWriter.OutputType.javascript), JSON("JSON", JsonWriter.OutputType.json);
        
        private final String name;
        private final OutputType outputType;
        
        ExportFormat(String name, OutputType outputType) {
            this.name = name;
            this.outputType = outputType;
        }

        @Override
        public String toString() {
            return name;
        }

        public OutputType getOutputType() {
            return outputType;
        }
    }

    public DialogSettings(String title, String windowStyleName, Main main) {
        super(title, main.getSkin(), windowStyleName);
        this.main = main;

        maxUndos = main.getProjectData().getMaxUndos();
        resourcesRelative = main.getProjectData().areResourcesRelative();
        simpleNames = main.getProjectData().isUsingSimpleNames();
        allowingWelcome = main.getProjectData().isAllowingWelcome();
        allowingUpdates = main.getProjectData().isCheckingForUpdates();
        exportFormat = main.getProjectData().getExportFormat();
        setFillParent(true);

        populate();
    }

    @Override
    protected void result(Object object) {
        super.result(object);

        if ((boolean) object) {
            main.getProjectData().setChangesSaved(false);
            main.getProjectData().setMaxUndos(maxUndos);
            main.getProjectData().setResourcesRelative(resourcesRelative);
            main.getProjectData().setUsingSimpleNames(simpleNames);
            main.getProjectData().setAllowingWelcome(allowingWelcome);
            main.getProjectData().setCheckingForUpdates(allowingUpdates);
            main.getProjectData().setExportFormat(exportFormat);
            main.getUndoableManager().clearUndoables();
            
            if (allowingUpdates) {
                Main.checkForUpdates(main);
            } else {
                Main.newVersion = Main.VERSION;
                main.getRootTable().fire(new RootTable.RootTableEvent(RootTable.RootTableEnum.CHECK_FOR_UPDATES_COMPLETE));
            }
        }
    }

    @Override
    public boolean remove() {
        return super.remove();
    }

    public void populate() {
        Table t = getContentTable();

        getButtonTable().padBottom(15.0f);

        Label label = new Label("Settings", main.getSkin(), "title");
        t.add(label).colspan(2);

        t.row();
        TextButton textButton = new TextButton("Open temp/log directory", main.getSkin());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                try {
                    Utils.openFileExplorer(Main.appFolder.child("temp/"));
                } catch (Exception e) {
                    Gdx.app.error(getClass().getName(), "Error opening temp folder", e);
                    main.getDialogFactory().showDialogError("Folder Error...", "Error opening temp folder.\n\nOpen log?");
                }
            }
        });
        textButton.addListener(main.getHandListener());
        t.add(textButton).colspan(2).padTop(15.0f);

        t.row();
        textButton = new TextButton("Open preferences directory", main.getSkin());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                try {
                    Utils.openFileExplorer(Gdx.files.external(".prefs/"));
                } catch (Exception e) {
                    Gdx.app.error(getClass().getName(), "Error opening preferences folder", e);
                    main.getDialogFactory().showDialogError("Folder Error...", "Error opening preferences folder.\n\nOpen log?");
                }
            }
        });
        textButton.addListener(main.getHandListener());
        t.add(textButton).colspan(2);

        if (main.getProjectData().areChangesSaved() && main.getProjectData().getSaveFile().exists()) {
            t.row();
            textButton = new TextButton("Open project/import directory", main.getSkin());
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event,
                        Actor actor) {
                    try {
                        Utils.openFileExplorer(main.getProjectData().getSaveFile().sibling(main.getProjectData().getSaveFile().nameWithoutExtension() + "_data"));
                    } catch (Exception e) {
                        Gdx.app.error(getClass().getName(), "Error opening project folder", e);
                        main.getDialogFactory().showDialogError("Folder Error...", "Error opening project folder\n\nOpen log?");
                    }
                }
            });
            textButton.addListener(main.getHandListener());
            t.add(textButton).colspan(2);
        }

        t.row();
        textButton = new TextButton("Open texture packer settings file", main.getSkin());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                try {
                    Utils.openFileExplorer(Main.appFolder.child("texturepacker/defaults.json"));
                } catch (Exception e) {
                    Gdx.app.error(getClass().getName(), "Error opening defaults.json", e);
                    main.getDialogFactory().showDialogError("File Error...", "Error opening defaults.json\n\nOpen log?");
                }
            }
        });
        textButton.addListener(main.getHandListener());
        t.add(textButton).colspan(2);

        t.row();
        label = new Label("Max Number of Undos: ", main.getSkin());
        t.add(label).right().padTop(10.0f);
        Spinner spinner3 = new Spinner(main.getProjectData().getMaxUndos(), 1.0, true, Orientation.HORIZONTAL, getSkin());
        spinner3.setMinimum(1.0);
        spinner3.setMaximum(100.0);
        spinner3.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                maxUndos = (int) spinner3.getValue();
            }
        });
        spinner3.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusListener.FocusEvent event,
                    Actor actor, boolean focused) {
                maxUndos = (int) spinner3.getValue();
            }

        });
        spinner3.getTextField().addListener(main.getIbeamListener());
        spinner3.getButtonMinus().addListener(main.getHandListener());
        spinner3.getButtonPlus().addListener(main.getHandListener());
        t.add(spinner3).minWidth(150.0f).left().padTop(10.0f);
        
        t.row();
        final ImageTextButton checkBox = new ImageTextButton("Keep resources relative?", getSkin(), "checkbox");
        checkBox.setChecked(resourcesRelative);
        checkBox.addListener(main.getHandListener());
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                resourcesRelative = checkBox.isChecked();
            }
        });
        t.add(checkBox).padTop(10.0f).colspan(2);
        
        t.row();
        final ImageTextButton simpleNameCheckBox = new ImageTextButton("Export with simple names?", getSkin(), "checkbox");
        simpleNameCheckBox.setChecked(simpleNames);
        simpleNameCheckBox.addListener(main.getHandListener());
        simpleNameCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                simpleNames = simpleNameCheckBox.isChecked();
            }
        });
        t.add(simpleNameCheckBox).padTop(10.0f).colspan(2);
        
        t.row();
        final ImageTextButton welcomeCheckBox = new ImageTextButton("Show welcome screen?", getSkin(), "checkbox");
        welcomeCheckBox.setChecked(allowingWelcome);
        welcomeCheckBox.addListener(main.getHandListener());
        welcomeCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                allowingWelcome = welcomeCheckBox.isChecked();
            }
        });
        t.add(welcomeCheckBox).padTop(10.0f).colspan(2);
        
        t.row();
        final ImageTextButton updatesCheckBox = new ImageTextButton("Check for updates?", getSkin(), "checkbox");
        updatesCheckBox.setChecked(allowingUpdates);
        updatesCheckBox.addListener(main.getHandListener());
        updatesCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                allowingUpdates = updatesCheckBox.isChecked();
            }
        });
        t.add(updatesCheckBox).padTop(10.0f).colspan(2);
        
        t.row();
        label = new Label("Exported JSON Format:", getSkin());
        t.add(label).right().padTop(10.0f);
        
        final SelectBox<ExportFormat> exportFormatSelectBox = new SelectBox(getSkin());
        exportFormatSelectBox.setItems(ExportFormat.MINIMAL, ExportFormat.JAVASCRIPT, ExportFormat.JSON);
        exportFormatSelectBox.setSelected(exportFormat);
        exportFormatSelectBox.addListener(main.getHandListener());
        exportFormatSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                exportFormat = exportFormatSelectBox.getSelected();
            }
        });
        t.add(exportFormatSelectBox).left().padTop(10.0f);

        button("OK", true);
        button("Cancel", false);
        getButtonTable().getCells().first().getActor().addListener(main.getHandListener());
        getButtonTable().getCells().get(1).getActor().addListener(main.getHandListener());
        key(Keys.ESCAPE, false);
    }
}
