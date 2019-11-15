package com.ray3k.skincomposer;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class ScrollFocusListener extends InputListener {
    private Stage stage;
    
    public ScrollFocusListener(Stage stage) {
        this.stage = stage;
    }
    
    @Override
    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        stage.setScrollFocus(event.getListenerActor());
    }
}
