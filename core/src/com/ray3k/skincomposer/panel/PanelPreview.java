package com.ray3k.skincomposer.panel;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class PanelPreview {
    private Table table;
    private Skin skin;
    private Stage stage;
    public static PanelPreview instance;
    public Table contentTable;
    
    public PanelPreview(Table table, Skin skin, Stage stage) {
        this.table = table;
        this.skin = skin;
        this.stage = stage;
        instance = this;
        
        table.clear();
        
        contentTable = new Table(skin);
        contentTable.setBackground("white");
        table.add(contentTable).center().grow();
        contentTable.defaults().pad(3.0f);
    }
}
