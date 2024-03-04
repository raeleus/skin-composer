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
package com.ray3k.skincomposer.installer;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 *
 * @author Raymond
 */
public class LocationTable extends Table {
    private FileHandle path;

    public LocationTable(final Skin skin, final Stage stage) {
        super(skin);
        path = Core.installationPath;
        
        pad(10.0f);
        defaults().space(6);
        
        var label = new Label("Install to", skin);
        label.setTouchable(Touchable.disabled);
        add(label).expandX().left();
        
        row();        
        var pathLabel = new Label(Core.installationPath.path(), skin, "path");
        pathLabel.setEllipsis("...");
        pathLabel.setEllipsis(true);
        add(pathLabel).width(200);
        
        row();
        var button = new Button(skin, "browse");
        add(button).right();
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                FileHandle file = Core.desktopWorker.selectFolder("Installation Directory...", path);
                if (file != null) {
                    path = file;
                    pathLabel.setText(path.path());
                }
            }
        });
        
        row();
        var iconCheckBox = new ImageTextButton("Desktop Shortcut", skin, "checkbox");
        iconCheckBox.setChecked(Core.installationCreateDesktopIcon);
        add(iconCheckBox).left();
        iconCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Core.installationCreateDesktopIcon = iconCheckBox.isChecked();
            }
        });
        
        row();
        var startMenuCheckBox = new ImageTextButton("Start Menu Shortcut", skin, "checkbox");
        startMenuCheckBox.setChecked(Core.installationCreateStartIcon);
        add(startMenuCheckBox).left();
        startMenuCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Core.installationCreateStartIcon = startMenuCheckBox.isChecked();
            }
        });
        
        row();
        var table = new Table();
        table.defaults().space(10);
        add(table).expandX().right();
        
        button = new TextButton("Cancel", skin);
        table.add(button);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Core.transition(LocationTable.this, new MenuTable(skin, stage));
            }
        });
        
        button = new TextButton("Install", skin);
        table.add(button);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                if (Core.installationPath.exists() && Core.installationPath.list().length > 0) {
                    showOverwriteDialog();
                } else {
                    Core.installationPath = path;
                    Core.transition(LocationTable.this, new InstallationTable(skin, stage), .5f, 0.0f);
                }
            }
        });
    }
    
    public void showOverwriteDialog() {
        var dialog = new Dialog("", getSkin()) {
            @Override
            protected void result(Object object) {
                if ((Boolean) object) {
                    Core.installationPath = path;
                    Core.transition(LocationTable.this, new InstallationTable(getSkin(), getStage()), .5f, 0.0f);
                }
            }
        };
        
        dialog.getContentTable().pad(10.0f);
        dialog.getButtonTable().pad(10.0f);
        dialog.text("Overwrite files?");
        dialog.button("Install", true).button("Cancel", false);
        dialog.key(Input.Keys.ENTER, true).key(Input.Keys.ESCAPE, false);
        dialog.show(getStage());
    }
}
