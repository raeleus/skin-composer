package com.ray3k.skincomposer.dialog.scenecomposer;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.PopTable;
import com.ray3k.skincomposer.data.StyleData;

public class StyleSelectorPopTable extends PopTable.PopTableClickListener {
    private Main main;
    private Skin skin;
    private ScrollPane scrollFocus;
    private Cell previewCell;
    
    public StyleSelectorPopTable(Class clazz) {
        super(Main.main.getSkin());
        main = Main.main;
        skin = main.getSkin();
        
        var label = new Label(clazz.getSimpleName() + " Styles", skin, "scene-label-colored");
        popTable.add(label).colspan(2);
        
        popTable.row();
        var table = new Table();
        var scrollPane = new ScrollPane(table, skin, "scene");
        scrollFocus = scrollPane;
        scrollPane.setFlickScroll(false);
        scrollPane.setFadeScrollBars(false);
        popTable.add(scrollPane).grow().minSize(200);
        scrollPane.addListener(main.getScrollFocusListener());
        
        var list = new List<String>(skin, "scene-dark");
        var listNames = new Array<String>();
        var styles = main.getJsonData().getClassStyleMap().get(clazz);
        for (var style : styles) {
            listNames.add(style.name);
        }
        list.setItems(listNames);
        table.add(list);
        list.addListener(main.getHandListener());
    
        table = new Table();
        scrollPane = new ScrollPane(table, skin, "scene");
        scrollFocus = scrollPane;
        scrollPane.setFlickScroll(false);
        scrollPane.setFadeScrollBars(false);
        popTable.add(scrollPane).grow().minSize(200);
        scrollPane.addListener(main.getScrollFocusListener());
        
        previewCell = table.add();
        
        popTable.row();
        var textButton = new TextButton("OK", skin, "scene-med");
        popTable.add(textButton).minWidth(100).colspan(2);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                var styleData = styles.get(list.getSelectedIndex());
                accepted(styleData);
                popTable.hide();
            }
        });
        var styleData = styles.get(list.getSelectedIndex());
        textButton.setDisabled(styleData.hasAllNullFields() || !styleData.hasMandatoryFields());
        
        previewCell.setActor(createWidget(clazz, styleData));
    
        list.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                var styleData = styles.get(list.getSelectedIndex());
                previewCell.setActor(createWidget(clazz, styleData));
                textButton.setDisabled(styleData.hasAllNullFields() || !styleData.hasMandatoryFields());
            }
        });
    }
    
    private Actor createWidget(Class clazz, StyleData styleData) {
        if (!styleData.hasMandatoryFields()) {
            var label = new Label("Style does not have all mandatory fields!", skin, "scene-label-colored");
            return label;
        } else if (styleData.hasAllNullFields()) {
            var label = new Label("Style has all null fields!", skin, "scene-label-colored");
            return label;
        } if (clazz.equals(TextButton.class)) {
            var style = main.getRootTable().createPreviewStyle(TextButton.TextButtonStyle.class, styleData);
            var textButton = new TextButton("Lorem Ipsum", style);
            textButton.addListener(main.getHandListener());
            return textButton;
        }
        
        return null;
    }
    
    @Override
    public void clicked(InputEvent event, float x, float y) {
        super.clicked(event, x, y);
        var stage = event.getListenerActor().getStage();
        
        stage.setScrollFocus(scrollFocus);
    }
    
    public void accepted(StyleData styleData) {
    
    }
}
