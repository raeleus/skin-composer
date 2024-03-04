package com.ray3k.skincomposer.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.ray3k.skincomposer.Main;
import com.ray3k.stripe.PopTable;

import static com.ray3k.skincomposer.Main.handListener;
import static com.ray3k.skincomposer.Main.skin;

public class PopAbout extends PopTable {
    public PopAbout() {
        super(skin, "dialog");
        
        setKeepCenteredInWindow(true);
        setModal(true);
        setHideOnUnfocus(true);
        
        defaults().pad(10.0f);
    
        var label = new Label("About", skin, "title");
        add(label);
    
        row();
        label = new Label("Skin Composer is developed by Raeleus for the libGDX community.\nVersion " + Main.VERSION + "\nCopyright © Raymond \"Raeleus\" Buckley 2024", skin);
        label.setAlignment(Align.center);
        add(label).padBottom(0);
        row();
        var button = new TextButton("github.com/raeleus/skin-composer", skin, "link");
        button.addListener(handListener);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Gdx.net.openURI("https://github.com/raeleus/skin-composer");
            }
        });
        add(button).padTop(0);
    
        row();
        var textButton = new TextButton("Close", skin);
        add(textButton).padBottom(20);
        textButton.addListener(handListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hide();
            }
        });
    
        setWidth(200);
        
        key(Keys.ESCAPE,() -> hide());
        key(Keys.ENTER,() -> hide());
        key(Keys.NUMPAD_ENTER,() -> hide());
    }
    
    @Override
    public void show(Stage stage) {
        fire(new DialogEvent(DialogEvent.Type.OPEN));
        super.show(stage);
    }
    
    @Override
    public boolean remove() {
        fire(new DialogEvent(DialogEvent.Type.CLOSE));
        return super.remove();
    }
}
