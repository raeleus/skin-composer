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
        table.setBackground(Main.instance.getLoadingAnimation());
        t.add(table);
    }
}
