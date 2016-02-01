package com.ray3k.skincomposer;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.ray3k.skincomposer.StyleData.ClassName;

public class PanelClassBar {    
    
    private SelectBox<StyleData> styleSelectBox;
    
    public PanelClassBar(final Table table, final OrderedMap<StyleData.ClassName, Array<StyleData>> classStyleMap, final Skin skin, final Stage stage) {
        table.defaults().padTop(5.0f).padBottom(5.0f);
        table.add(new Label("Class:", skin, "white")).padLeft(2.0f).padRight(5.0f);
        table.setBackground("maroon");
        
        final SelectBox<ClassName> classSelectBox = new SelectBox<ClassName>(skin);
        classSelectBox.setItems(ClassName.values());
        table.add(classSelectBox).padRight(30.0f);
        
        table.add(new Label("Style:", skin, "white")).padRight(5.0f);
        styleSelectBox = new SelectBox<StyleData>(skin);
        styleSelectBox.setItems(classStyleMap.get(classSelectBox.getSelected()));
        table.add(styleSelectBox).padRight(10.0f).minWidth(200.0f);
        
        classSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                styleSelectBox.setItems(classStyleMap.get(classSelectBox.getSelected()));
            }
        });
        
        styleSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                PanelStyleProperties.actor.populate(styleSelectBox.getSelected());
            }
        });
        
        ImageButtonStyle style = new ImageButton.ImageButtonStyle(skin.get("menu-left", ImageButton.ImageButtonStyle.class));
        style.imageUp = skin.getDrawable("image-plus");
        style.imageDown = skin.getDrawable("image-plus-down");
        ImageButton imageButton = new ImageButton(style);
        table.add(imageButton);
        
        style = new ImageButton.ImageButtonStyle(skin.get("menu-center", ImageButton.ImageButtonStyle.class));
        style.imageUp = skin.getDrawable("image-duplicate");
        style.imageDown = skin.getDrawable("image-duplicate-down");
        imageButton = new ImageButton(style);
        table.add(imageButton);
        
        style = new ImageButton.ImageButtonStyle(skin.get("menu-right", ImageButton.ImageButtonStyle.class));
        style.imageUp = skin.getDrawable("image-delete");
        style.imageDown = skin.getDrawable("image-delete-down");
        imageButton = new ImageButton(style);
        table.add(imageButton);
        
        table.add().growX();
    }

    public SelectBox<StyleData> getStyleSelectBox() {
        return styleSelectBox;
    }
}
