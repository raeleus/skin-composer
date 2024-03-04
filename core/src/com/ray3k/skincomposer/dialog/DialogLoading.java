/*******************************************************************************
 * MIT License
 * 
 * Copyright (c) 2024 Raymond Buckley
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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.utils.Utils;
import static com.ray3k.skincomposer.Main.*;

public class DialogLoading extends Dialog {
    private Runnable runnable;
    private Main main;
    
    public DialogLoading(String title, Runnable runnable, Main main) {
        super(title, skin, !DialogSceneComposer.isShowing() ? "dialog" : "scene");
        this.main = main;
        this.runnable = runnable;
        setFillParent(true);
        populate();
    }

    @Override
    public Dialog show(Stage stage) {
        Dialog dialog = super.show(stage);
        RunnableAction runnableAction = new RunnableAction();
        runnableAction.setRunnable(() -> {
            if (Utils.isMac()) {
                if (runnable != null) {
                    runnable.run();
                }
                hide();
            } else {
                Thread thread = new Thread(() -> {
                    if (runnable != null) {
                        runnable.run();
                    }
                    Gdx.app.postRunnable(() -> {
                        hide();
                    });
                });
                thread.start();
            }
        });
        Action action = new SequenceAction(new DelayAction(.5f), runnableAction);
        addAction(action);
        
        return dialog;
    }
    
    public void populate() {
        Table t = getContentTable();
        Label label = new Label("Loading...", skin, !DialogSceneComposer.isShowing() ? "title" : "scene-title");
        label.setAlignment(Align.center);
        t.add(label);
        t.row();
        Table table = new Table(skin);
        table.setBackground(!DialogSceneComposer.isShowing() ? loadingAnimation : loadingAnimation2);
        t.add(table);
    }
}
