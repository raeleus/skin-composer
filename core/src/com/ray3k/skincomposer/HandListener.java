package com.ray3k.skincomposer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class HandListener extends ClickListener {
    private static HandListener instance;
    
    public static HandListener get() {
        if (instance == null) {
            instance = new HandListener();
        }
        return instance;
    }
    
    private HandListener() {
        super();
    }
    
    @Override
    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
    }

    @Override
    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
    }
}
