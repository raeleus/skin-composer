package com.ray3k.skincomposer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class IbeamListener extends ClickListener {
    private static IbeamListener instance;
    
    public static IbeamListener get() {
        if (instance == null) {
            instance = new IbeamListener();
        }
        return instance;
    }
    
    private IbeamListener() {
        super();
    }
    
    @Override
    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
        Gdx.graphics.setSystemCursor(SystemCursor.Arrow);
    }

    @Override
    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        Gdx.graphics.setSystemCursor(SystemCursor.Ibeam);
    }
}
