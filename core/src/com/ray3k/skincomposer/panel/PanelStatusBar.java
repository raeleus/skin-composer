package com.ray3k.skincomposer.panel;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.actions.VisibleAction;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import static com.badlogic.gdx.scenes.scene2d.ui.Table.Debug.table;

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
        Label label = new Label("ver. 1    RAY3K.COM 2016", skin, "white");
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
            runnableAction.setRunnable(new Runnable() {
                @Override
                public void run() {
                    statusLabel.setText(text);
                }
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
