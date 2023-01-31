package com.ray3k.skincomposer.dialog.scenecomposer;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.stripe.PopTableClickListener;
import static com.ray3k.skincomposer.Main.*;

public class StyleSelectorPopTable extends PopTableClickListener {
    private ScrollPane scrollFocus;
    private Cell previewCell;
    
    public StyleSelectorPopTable(Class clazz, String selectedStyle) {
        super(Main.skin);
        
        popTable.key(Keys.ESCAPE, popTable::hide);
        popTable.pad(15);
        var label = new Label(clazz.getSimpleName() + " Styles", skin, "scene-label-colored");
        popTable.add(label).colspan(2);
        
        popTable.row();
        var table = new Table();
        var scrollPane = new ScrollPane(table, skin, "scene");
        scrollFocus = scrollPane;
        scrollPane.setFlickScroll(false);
        scrollPane.setFadeScrollBars(false);
        popTable.add(scrollPane).grow().minSize(200);
        scrollPane.addListener(scrollFocusListener);
        
        var list = new List<String>(skin, "scene-dark");
        var listNames = new Array<String>();
        var styles = jsonData.getClassStyleMap().get(clazz);
        for (var style : styles) {
            listNames.add(style.name);
        }
        list.setItems(listNames);
        list.setSelected(selectedStyle);
        table.add(list);
        list.addListener(handListener);
    
        table = new Table();
        scrollPane = new ScrollPane(table, skin, "scene");
        scrollFocus = scrollPane;
        scrollPane.setFlickScroll(false);
        scrollPane.setFadeScrollBars(false);
        popTable.add(scrollPane).grow().minSize(200);
        scrollPane.addListener(scrollFocusListener);
        
        previewCell = table.add();
        
        popTable.row();
        var textButton = new TextButton("OK", skin, "scene-med");
        popTable.add(textButton).minWidth(100).colspan(2);
        textButton.addListener(handListener);
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
        
        previewCell.setActor(createStylePreviewWidget(clazz, styleData));
    
        list.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                var styleData = styles.get(list.getSelectedIndex());
                previewCell.setActor(createStylePreviewWidget(clazz, styleData));
                textButton.setDisabled(styleData.hasAllNullFields() || !styleData.hasMandatoryFields());
            }
        });
    }
    
    private Actor createStylePreviewWidget(Class clazz, StyleData styleData) {
        if (!styleData.hasMandatoryFields()) {
            var label = new Label("Style does not have all mandatory fields!", skin, "scene-label-colored");
            return label;
        } else if (styleData.hasAllNullFields()) {
            var label = new Label("Style has all null fields!", skin, "scene-label-colored");
            return label;
        } else if (clazz.equals(TextButton.class)) {
            var style = rootTable.createPreviewStyle(TextButton.TextButtonStyle.class, styleData);
            var textButton = new TextButton("Lorem Ipsum", style);
            textButton.addListener(handListener);
            return textButton;
        } else if (clazz.equals(Button.class)) {
            var style = rootTable.createPreviewStyle(Button.ButtonStyle.class, styleData);
            var button = new Button(style);
            button.addListener(handListener);
            return button;
        } else if (clazz.equals(CheckBox.class)) {
            var style = rootTable.createPreviewStyle(CheckBox.CheckBoxStyle.class, styleData);
            var checkBox = new CheckBox("Lorem Ipsum", style);
            checkBox.addListener(handListener);
            return checkBox;
        } else if (clazz.equals(ImageButton.class)) {
            var style = rootTable.createPreviewStyle(ImageButton.ImageButtonStyle.class, styleData);
            var imageButton = new ImageButton(style);
            imageButton.addListener(handListener);
            return imageButton;
        } else if (clazz.equals(ImageTextButton.class)) {
            var style = rootTable.createPreviewStyle(ImageTextButton.ImageTextButtonStyle.class, styleData);
            var imageTextButton = new ImageTextButton("Lorem Ipsum", style);
            imageTextButton.addListener(handListener);
            return imageTextButton;
        } else if (clazz.equals(Label.class)) {
            var style = rootTable.createPreviewStyle(Label.LabelStyle.class, styleData);
            var label = new Label("Lorem Ipsum", style);
            return label;
        } else if (clazz.equals(List.class)) {
            var style = rootTable.createPreviewStyle(List.ListStyle.class, styleData);
            var list = new List<String>(style);
            list.setItems("Lorem", "Ipsum", "Dolor", "Sit");
            list.addListener(handListener);
            return list;
        } else if (clazz.equals(ProgressBar.class)) {
            var style = rootTable.createPreviewStyle(ProgressBar.ProgressBarStyle.class, styleData);
            var progressBar = new ProgressBar(0, 10, 1,false, style);
            progressBar.setValue(5);
            return progressBar;
        } else if (clazz.equals(ScrollPane.class)) {
            var style = rootTable.createPreviewStyle(ScrollPane.ScrollPaneStyle.class, styleData);
            var scrollPane = new ScrollPane(new Table(), style);
            return scrollPane;
        } else if (clazz.equals(SelectBox.class)) {
            var style = rootTable.createPreviewStyle(SelectBox.SelectBoxStyle.class, styleData);
            var selectBox = new SelectBox<String>(style);
            selectBox.setItems("Lorem", "Ipsum", "Dolor", "Sit");
            selectBox.addListener(handListener);
            return selectBox;
        } else if (clazz.equals(Slider.class)) {
            var style = rootTable.createPreviewStyle(Slider.SliderStyle.class, styleData);
            var slider = new Slider(0, 10, 1, false, style);
            slider.setValue(5);
            slider.addListener(handListener);
            return slider;
        } else if (clazz.equals(SplitPane.class)) {
            var style = rootTable.createPreviewStyle(SplitPane.SplitPaneStyle.class, styleData);
            var table1 = new Table();
            table1.setTouchable(Touchable.enabled);
            var table2 = new Table();
            table2.setTouchable(Touchable.enabled);
            var splitPane = new SplitPane(table1, table2, false, style);
            splitPane.addListener(horizontalResizeArrowListener);
            return splitPane;
        } else if (clazz.equals(TextArea.class)) {
            var style = rootTable.createPreviewStyle(TextField.TextFieldStyle.class, styleData);
            var textArea = new TextArea("Lorem Ipsum", style);
            textArea.addListener(handListener);
            return textArea;
        } else if (clazz.equals(TextField.class)) {
            var style = rootTable.createPreviewStyle(TextField.TextFieldStyle.class, styleData);
            var textField = new TextField("Lorem Ipsum", style);
            textField.addListener(handListener);
            return textField;
        } else if (clazz.equals(TextTooltip.class)) {
            var style = rootTable.createPreviewStyle(TextTooltip.TextTooltipStyle.class, styleData);
            var label = new Label("hover over me", skin);
            label.addListener((Main.makeTooltip("Lorem Ipsum", style)));
            return label;
        } else if (clazz.equals(Touchpad.class)) {
            var style = rootTable.createPreviewStyle(Touchpad.TouchpadStyle.class, styleData);
            var touchPad = new Touchpad(0, style);
            return touchPad;
        } else if (clazz.equals(Tree.class)) {
            var style = rootTable.createPreviewStyle(Tree.TreeStyle.class, styleData);
            var tree = new Tree<>(style);
            var node = new Tree.Node<>(new Label("Lorem Ipsum", skin)) {
            
            };
            tree.add(node);
            var node2 = new Tree.Node<>(new Label("Dolor Sit", skin)) {
            
            };
            tree.add(node2);
            var node3 = new Tree.Node<>(new Label("Amet Consectetur", skin)) {
            
            };
            tree.add(node3);
            return tree;
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
