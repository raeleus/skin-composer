package com.ray3k.skincomposer;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;

public abstract class StageResizeListener implements EventListener {
    @Override
    public boolean handle(Event event) {
        if (event instanceof StageResizeEvent) {
            StageResizeEvent sEvent = (StageResizeEvent) event;
            resized(sEvent.getWidth(), sEvent.getHeight());
        }
        
        return false;
    }
    
    public abstract void resized(int width, int height);
}
