package com.ray3k.skincomposer;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class RunListener extends ChangeListener {
    private Runnable runnable;
    
    private RunListener(Runnable runnable) {
        this.runnable = runnable;
    }
    
    @Override
    public void changed(ChangeEvent event, Actor actor) {
        runnable.run();
    }
    
    public static RunListener rl(Runnable runnable) {
        return new RunListener(runnable);
    }
}
