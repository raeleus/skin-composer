package com.ray3k.skincomposer;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

public class PanelClassBar {
    public PanelClassBar(final Table table, final Skin skin, final Stage stage) {
        table.defaults().padTop(5.0f).padBottom(5.0f);
        table.add(new Label("Class:", skin, "white")).padLeft(2.0f).padRight(5.0f);
        table.setBackground("maroon");
        
        Array classItems = new Array(new Object[] {"Button", "Checkbox", "ImageButton", "ImageTextButton", "Label", "List", "ProgressBar", "ScrollPane", "SelectBox", "Slider", "SplitPane", "TextArea", "TextButton", "TextField", "TextTooltip", "Touchpad", "Tree", "Window"});
        SelectBox selectBox = new SelectBox(skin);
        selectBox.setItems(classItems);
        table.add(selectBox).padRight(30.0f);
        
        table.add(new Label("Style:", skin, "white")).padRight(5.0f);
        selectBox = new SelectBox(skin);
        selectBox.setItems(classItems);
        table.add(selectBox).padRight(10.0f);
        
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
}
