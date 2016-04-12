package com.ray3k.skincomposer;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

public class BrowseField extends Table {
    private TextField textField;
    private Button button;
    private Label label;
    
    public BrowseField(String text, BrowseFieldStyle style) {
        
        Table table = new Table();
        if (text != null && !text.equals("")) {
            label = new Label(text, style.labelStyle);
            table.add(label);
            table.row();
        }
        
        textField = new TextField("", style.textFieldStyle);
        textField.setAlignment(Align.center);
        textField.setFocusTraversal(false);
        textField.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                fire(new ChangeListener.ChangeEvent());
            }
        });
        table.add(textField).minWidth(35.0f).prefWidth(35.0f).growX();
        add(table).growX();
        
        if (style.textButtonStyle != null) {
            button = new TextButton("...", style.textButtonStyle);
        } else {
            button = new ImageButton(style.imageButtonStyle);
        }
        
        add(button).padLeft(5.0f).bottom();
    }
    
    public BrowseField(BrowseFieldStyle style, Drawable drawable) {
        
    }

    public TextField getTextField() {
        return textField;
    }

    public Button getButton() {
        return button;
    }

    public Label getLabel() {
        return label;
    }
    
    static public class BrowseFieldStyle {
        public TextButtonStyle textButtonStyle;
        public ImageButtonStyle imageButtonStyle;
        public TextFieldStyle textFieldStyle;
        public LabelStyle labelStyle;

        public BrowseFieldStyle(TextButtonStyle textButtonStyle, TextFieldStyle textFieldStyle, LabelStyle lableStyle) {
            this.textButtonStyle = textButtonStyle;
            this.textFieldStyle = textFieldStyle;
            this.labelStyle = labelStyle;
            imageButtonStyle = null;
        }

        public BrowseFieldStyle(ImageButtonStyle imageButtonStyle, TextFieldStyle textFieldStyle, LabelStyle labelStyle) {
            textButtonStyle = null;
            this.textFieldStyle = textFieldStyle;
            this.imageButtonStyle = imageButtonStyle;
            this.labelStyle = labelStyle;
        }
        
        public BrowseFieldStyle(BrowseFieldStyle style) {
            textButtonStyle = style.textButtonStyle;
            textFieldStyle = style.textFieldStyle;
            imageButtonStyle = style.imageButtonStyle;
            labelStyle = style.labelStyle;
        }
    }
}
