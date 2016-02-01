package com.ray3k.skincomposer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Main extends ApplicationAdapter {
    private Stage stage;
    private static Skin skin;
    private OrderedMap<StyleData.ClassName, Array<StyleData>> classStyleMap;
    
    @Override
    public void create() {
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        
        Table rootTable = new Table(skin);
        rootTable.setFillParent(true);
        stage.addActor(rootTable);
        
        initializeClassStyleMap();
        
        Table table = new Table(skin);
        new PanelMenuBar(table, skin, stage);
        rootTable.add(table).growX();        
        rootTable.row();
        table = new Table(skin);
        table.setBackground("dim-orange");
        rootTable.add(table).height(2.0f).growX();
        
        rootTable.row();
        table = new Table(skin);
        rootTable.add(table).growX();
        PanelClassBar panelClassBar = new PanelClassBar(table, classStyleMap, skin, stage);
        rootTable.row();
        table = new Table(skin);
        table.setBackground("dim-orange");
        rootTable.add(table).height(2.0f).growX();
        
        rootTable.row();
        table = new Table(skin);
        PanelStyleProperties panelStyleProperties = new PanelStyleProperties(table, skin, stage);
        Table table2 = new Table(skin);
        new PanelPreview(table2, skin, stage);
        Table table3 = new Table(skin);
        new PanelPreviewProperties(table3, skin, stage);
        ScrollPane scrollPane = new ScrollPane(table, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setFlickScroll(false);
        SplitPane splitPaneRight = new SplitPane(table2, table3, true, skin);
        splitPaneRight.setSplitAmount(.75f);
        splitPaneRight.setMinSplitAmount(.05f);
        splitPaneRight.setMaxSplitAmount(.95f);
        SplitPane splitPane = new SplitPane(scrollPane, splitPaneRight, false, skin);
        splitPane.setSplitAmount(.35f);
        splitPane.setMinSplitAmount(.05f);
        splitPane.setMaxSplitAmount(.95f);
        rootTable.add(splitPane).grow();
        
        rootTable.row();
        table = new Table(skin);
        new PanelStatusBar(table, skin, stage);
        rootTable.add(table).growX();
        
        panelStyleProperties.populate(panelClassBar.getStyleSelectBox().getSelected());
        stage.setScrollFocus(scrollPane);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
    
    private void initializeClassStyleMap() {
        classStyleMap = new OrderedMap(StyleData.ClassName.values().length);
        for (StyleData.ClassName item : StyleData.ClassName.values()) {
            Array<StyleData> array = new Array<StyleData>();
            classStyleMap.put(item, array);
            if (item == StyleData.ClassName.Slider || item == StyleData.ClassName.ProgressBar || item == StyleData.ClassName.SplitPane) {
                StyleData data = new StyleData(item, "default-horizontal");
                array.add(data);
                data = new StyleData(item, "default-vertical");
                array.add(data);
            } else {
                StyleData data = new StyleData(item, "default");
                array.add(data);
            }
        }
    }
}
