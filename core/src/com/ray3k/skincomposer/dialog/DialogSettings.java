package com.ray3k.skincomposer.dialog;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Align;
import com.ray3k.skincomposer.Spinner;
import com.ray3k.skincomposer.Spinner.SpinnerStyle;
import com.ray3k.skincomposer.data.ProjectData;

public class DialogSettings extends Dialog {
    private Skin skin;
    private SpinnerStyle spinnerStyle;
    private String name;
    private Integer textureWidth;
    private Integer textureHeight;

    public DialogSettings(String title, Skin skin, String windowStyleName) {
        super(title, skin, windowStyleName);
        this.skin = skin;
        
        spinnerStyle = new Spinner.SpinnerStyle(skin.get("spinner-minus", Button.ButtonStyle.class), skin.get("spinner-plus", Button.ButtonStyle.class), skin.get("spinner", TextField.TextFieldStyle.class));
        
        name = ProjectData.instance().getName();
        textureWidth = ProjectData.instance().getMaxTextureWidth();
        textureHeight = ProjectData.instance().getMaxTextureHeight();
        
        populate();
    }

    @Override
    protected void result(Object object) {
        super.result(object);
        
        if ((boolean) object) {
            ProjectData.instance().setName(name);
            ProjectData.instance().setMaxTextureDimensions(textureWidth, textureHeight);
        }
    }

    public void populate() {
        Table t = getContentTable();
        
        Label label = new Label("Settings", skin, "title");
        label.setAlignment(Align.center);
        t.add(label).growX().colspan(2);
        
        t.row();
        label = new Label("Project Name: ", skin);
        t.add(label).right();
        TextField textField = new TextField(ProjectData.instance().getName(), skin);
        textField.setTextFieldListener((TextField textField1, char c) -> {
            name = textField.getText();
        });
        t.add(textField).growX();
        
        t.row();
        label = new Label("Max Texture Width: ", skin);
        t.add(label).right();
        Spinner spinner = new Spinner(ProjectData.instance().getMaxTextureWidth(), 1.0, true, spinnerStyle);
        spinner.setMinimum(256.0);
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                textureWidth = (int) spinner.getValue();
            }
        });
        spinner.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusListener.FocusEvent event, Actor actor, boolean focused) {
                textureWidth = (int) spinner.getValue();
            }
            
        });
        t.add(spinner).growX();
        
        t.row();
        label = new Label("Max Texture Height: ", skin);
        t.add(label).right();
        Spinner spinner2 = new Spinner(ProjectData.instance().getMaxTextureHeight(), 1.0, true, spinnerStyle);
        spinner2.setMinimum(256.0);
        spinner2.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                textureWidth = (int) spinner2.getValue();
            }
        });
        spinner2.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusListener.FocusEvent event, Actor actor, boolean focused) {
                textureWidth = (int) spinner2.getValue();
            }
            
        });
        t.add(spinner2).growX();
        
        button("OK", true);
        button ("Cancel", false);
        key(Keys.ESCAPE, false);
    }
}
