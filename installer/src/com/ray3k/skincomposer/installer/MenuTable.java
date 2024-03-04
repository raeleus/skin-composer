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

import com.badlogic.gdx.*;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

/**
 *
 * @author Raymond
 */
public class MenuTable extends Table {

    public MenuTable(final Skin skin, final Stage stage) {
        pad(8.0f);
        
        var label = new Label(Core.properties.get("product-name"), skin, "small");
        label.setTouchable(Touchable.disabled);
        label.setAlignment(Align.top);
        add(label).expandX().left().top().height(0);
        
        var textButton = new TextButton("?", skin, "small");
        add(textButton).right();
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Core.transition(MenuTable.this, new InfoTable(skin, stage));
            }
        });
        
        row();
        var bottom = new Table();
        bottom.defaults().space(6).growX();
        add(bottom).grow().colspan(2).space(6);
        
        textButton = new TextButton("Install", skin);
        bottom.add(textButton).growY();
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Core.transition(MenuTable.this, new LocationTable(skin, stage));
            }
        });
        
        bottom.row();
        textButton = new TextButton("Quit", skin);
        bottom.add(textButton).height(30);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                MenuTable.this.addAction(Actions.sequence(Actions.fadeOut(.5f), Actions.delay(.5f), new Action() {
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
