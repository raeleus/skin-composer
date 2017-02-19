package com.ray3k.skincomposer;

import com.badlogic.gdx.scenes.scene2d.Event;

public class StageResizeEvent extends Event {
    private int width;
    private int height;
    
    public StageResizeEvent(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
