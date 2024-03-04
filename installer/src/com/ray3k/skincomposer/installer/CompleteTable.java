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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import java.awt.Desktop;
import java.io.IOException;

/**
 *
 * @author Raymond
 */
public class CompleteTable extends Table {

    public CompleteTable(final Skin skin, final Stage stage) {
        pad(10.0f);
        defaults().space(6);
        
        Label label = new Label("Installation Complete", skin);
        add(label);
        
        row();
        var button = new TextButton("Open", skin);
        add(button).grow();
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                try {
                    FileHandle file = Gdx.files.absolute(Core.installationPath.path() + "\\" + Core.properties.get("run-path"));
                    Desktop.getDesktop().open(file.file());
                } catch (IOException e) {
                    Gdx.app.log(getClass().getName(), "Error launching file", e);
                }
                Gdx.app.exit();
            }
        });
        
        row();
        button = new TextButton("Exit", skin);
        add(button).height(30).growX();
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                CompleteTable.this.addAction(Actions.sequence(Actions.fadeOut(.5f), Actions.delay(.5f), new Action() {
                    @Override
                    public boolean act(float delta) {
                        Gdx.app.exit();
                        return true;
                    }
                }));
            }
        });
    }
    
}
