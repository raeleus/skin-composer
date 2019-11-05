package com.ray3k.skincomposer.dialog;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.ray3k.skincomposer.Main;

public class DialogSceneComposer extends Dialog {
    public DialogSceneComposer() {
        super("", Main.main.getSkin(), "scene");
        setFillParent(true);
    }
}
