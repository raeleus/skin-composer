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
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.RootTable;
import com.ray3k.skincomposer.Spinner;
import com.ray3k.skincomposer.Spinner.Orientation;
import com.ray3k.skincomposer.utils.Utils;

public class DialogSettings extends Dialog {
    private Integer maxUndos;
    private boolean resourcesRelative;
    private boolean allowingWelcome;
    private boolean allowingUpdates;
    private final Main main;

    public DialogSettings(String title, String windowStyleName, Main main) {
        super(title, main.getSkin(), windowStyleName);
        this.main = main;

        maxUndos = main.getProjectData().getMaxUndos();
        resourcesRelative = main.getProjectData().areResourcesRelative();
        allowingWelcome = main.getProjectData().isAllowingWelcome();
        allowingUpdates = main.getProjectData().isCheckingForUpdates();

        populate();
    }

    @Override
    protected void result(Object object) {
        super.result(object);

        if ((boolean) object) {
            main.getProjectData().setChangesSaved(false);
            main.getProjectData().setMaxUndos(maxUndos);
            main.getProjectData().setResourcesRelative(resourcesRelative);
            main.getProjectData().setAllowingWelcome(allowingWelcome);
            main.getProjectData().setCheckingForUpdates(allowingUpdates);
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
    public Dialog show(Stage stage, Action action) {
        fire(new DialogEvent(DialogEvent.Type.OPEN));
        return super.show(stage, action);
    }

    @Override
    public boolean remove() {
        fire(new DialogEvent(DialogEvent.Type.CLOSE));
        return super.remove();
    }

    public void populate() {
        getContentTable().pad(5);
        
        var t = getContentTable();

        t.defaults().space(15);
        var label = new Label("Settings", main.getSkin(), "title");
        t.add(label);
        
        t.row();
        var table = new Table();
        t.add(table);
        
        table.defaults().growX().space(5);
        var textButton = new TextButton("Open temp/log directory", main.getSkin());
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
        table.add(textButton);

        table.row();
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
        table.add(textButton);

        if (main.getProjectData().areChangesSaved() && main.getProjectData().getSaveFile().exists()) {
            table.row();
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
            table.add(textButton);
        }

        table.row();
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
        table.add(textButton);

        t.row();
        table = new Table();
        t.add(table);
        
        table.defaults().space(5);
        label = new Label("Max Number of Undos: ", main.getSkin());
        table.add(label);
        
        var spinner = new Spinner(main.getProjectData().getMaxUndos(), 1.0, true, Orientation.HORIZONTAL, getSkin());
        spinner.setMinimum(1.0);
        spinner.setMaximum(100.0);
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                maxUndos = (int) spinner.getValue();
            }
        });
        spinner.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusListener.FocusEvent event,
                    Actor actor, boolean focused) {
                maxUndos = (int) spinner.getValue();
            }

        });
        spinner.getTextField().addListener(main.getIbeamListener());
        spinner.getButtonMinus().addListener(main.getHandListener());
        spinner.getButtonPlus().addListener(main.getHandListener());
        table.add(spinner).minWidth(100.0f);
        
        t.row();
        table = new Table();
        t.add(table);
        
        table.defaults().expandX().left().space(5);
        var relativeCheckBox = new ImageTextButton("Keep resources relative?", getSkin(), "checkbox");
        relativeCheckBox.setChecked(resourcesRelative);
        relativeCheckBox.addListener(main.getHandListener());
        relativeCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                resourcesRelative = relativeCheckBox.isChecked();
            }
        });
        table.add(relativeCheckBox);
        
        table.row();
        var welcomeCheckBox = new ImageTextButton("Show welcome screen?", getSkin(), "checkbox");
        welcomeCheckBox.setChecked(allowingWelcome);
        welcomeCheckBox.addListener(main.getHandListener());
        welcomeCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                allowingWelcome = welcomeCheckBox.isChecked();
            }
        });
        table.add(welcomeCheckBox);
        
        table.row();
        var updatesCheckBox = new ImageTextButton("Check for updates?", getSkin(), "checkbox");
        updatesCheckBox.setChecked(allowingUpdates);
        updatesCheckBox.addListener(main.getHandListener());
        updatesCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                allowingUpdates = updatesCheckBox.isChecked();
            }
        });
        table.add(updatesCheckBox);

        getButtonTable().pad(5);
        
        getButtonTable().defaults().minWidth(75).space(5);
        textButton = new TextButton("OK", getSkin());
        textButton.addListener(main.getHandListener());
        button(textButton, true);

        textButton = new TextButton("CANCEL", getSkin());
        textButton.addListener(main.getHandListener());
        button(textButton, false);
        
        key(Keys.ENTER, true).key(Keys.ESCAPE, false);
    }
}
