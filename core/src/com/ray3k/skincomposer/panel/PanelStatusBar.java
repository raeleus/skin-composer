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
package com.ray3k.skincomposer.panel;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.actions.VisibleAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class PanelStatusBar {
    public static PanelStatusBar instance;
    private Label statusLabel;
    public PanelStatusBar(final Table table, Skin skin, Stage stage) {
        instance = this;
        table.setBackground("dark-orange");
        statusLabel = new Label("", skin, "white");
        statusLabel.setEllipsis(true);
        statusLabel.setVisible(false);
        statusLabel.setColor(1.0f, 1.0f, 1.0f, 0.0f);
        ScrollPane container = new ScrollPane(statusLabel, skin, "no-bg") {
            @Override
            public void layout() {
                super.layout();
                statusLabel.setWidth(getWidth());
            }
            
        };
        table.add(container).expand().left();
        Label label = new Label("ver. 3    RAY3K.WORDPRESS.COM 2016", skin, "white");
        table.add(label).right().padRight(20.0f);
    }
    
    private void display(final String text) {
        SequenceAction sequenceAction = new SequenceAction();
        if (statusLabel.isVisible()) {
            statusLabel.clearActions();
            AlphaAction alphaAction = new AlphaAction();
            alphaAction.setAlpha(0.0f);
            alphaAction.setDuration(.25f);
            sequenceAction.addAction(alphaAction);
            RunnableAction runnableAction = new RunnableAction();
            runnableAction.setRunnable(() -> {
                statusLabel.setText(text);
            });
            sequenceAction.addAction(runnableAction);
            alphaAction = new AlphaAction();
            alphaAction.setAlpha(1.0f);
            alphaAction.setDuration(.25f);
            sequenceAction.addAction(alphaAction);
            DelayAction delayAction = new DelayAction();
            delayAction.setDuration(3.0f);
            sequenceAction.addAction(delayAction);
            alphaAction = new AlphaAction();
            alphaAction.setAlpha(0.0f);
            alphaAction.setDuration(1.5f);
            sequenceAction.addAction(alphaAction);
            VisibleAction visibleAction = new VisibleAction();
            visibleAction.setVisible(false);
            sequenceAction.addAction(visibleAction);
        } else {
            statusLabel.setText(text);
            statusLabel.clearActions();
            statusLabel.setVisible(true);
            AlphaAction alphaAction = new AlphaAction();
            alphaAction.setAlpha(1.0f);
            alphaAction.setDuration(.5f);
            sequenceAction.addAction(alphaAction);
            DelayAction delayAction = new DelayAction();
            delayAction.setDuration(3.0f);
            sequenceAction.addAction(delayAction);
            alphaAction = new AlphaAction();
            alphaAction.setAlpha(0.0f);
            alphaAction.setDuration(1.5f);
            sequenceAction.addAction(alphaAction);
            VisibleAction visibleAction = new VisibleAction();
            visibleAction.setVisible(false);
            sequenceAction.addAction(visibleAction);
        }
        statusLabel.addAction(sequenceAction);
    }
    
    public void message(String text) {
        statusLabel.setColor(new Color(1.0f, 1.0f, 1.0f, statusLabel.getColor().a));
        display(text);
    }
    
    public void error(String text) {
        statusLabel.setColor(new Color(1.0f, 0.0f, 0.0f, statusLabel.getColor().a));
        display(text);
    }
}
