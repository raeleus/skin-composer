/*******************************************************************************
 * MIT License
 * 
 * Copyright (c) 2016 Raymond Buckley
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
 ******************************************************************************/
package com.ray3k.skincomposer.dialog;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.ray3k.skincomposer.Main;

public class DialogLoading extends Dialog {
    private Skin skin;
    private Runnable runnable;
    
    public DialogLoading(String title, Skin skin, Runnable runnable) {
        super(title, skin, "dialog-panel");
        this.skin = skin;
        this.runnable = runnable;
        setFillParent(true);
        populate();
    }

    @Override
    public Dialog show(Stage stage) {
        Dialog dialog = super.show(stage);
        RunnableAction runnableAction = new RunnableAction();
        runnableAction.setRunnable(() -> {
            if (runnable != null) {
                runnable.run();
            }
            hide();
        });
        Action action = new SequenceAction(new DelayAction(.5f), runnableAction);
        addAction(action);
        
        return dialog;
    }
    
    public void populate() {
        Table t = getContentTable();
        Label label = new Label("Loading...", skin, "title");
        label.setAlignment(Align.center);
        t.add(label).growX();
        t.row();
        Table table = new Table(skin);
        table.setBackground(Main.instance().getLoadingAnimation());
        t.add(table);
    }
}
