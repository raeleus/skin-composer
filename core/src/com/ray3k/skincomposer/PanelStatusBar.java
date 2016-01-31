package com.ray3k.skincomposer;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class PanelStatusBar {
    public PanelStatusBar(Table table, Skin skin, Stage stage) {
        table.setBackground("dark-orange");
        Label label = new Label("ver. 1    RAY3K.COM 2016", skin, "white");
        table.add(label).expand().right().padRight(20.0f);
    }
}
