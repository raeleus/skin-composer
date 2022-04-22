package com.ray3k.skincomposer.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationState.AnimationStateAdapter;
import com.esotericsoftware.spine.AnimationState.TrackEntry;
import com.ray3k.skincomposer.SpineDrawable;
import com.ray3k.stripe.PopTable;

import static com.ray3k.skincomposer.Main.*;

public class PopRevertUIscale extends PopTable {
    private SpineDrawable drawable;
    
    public PopRevertUIscale() {
        drawable = new SpineDrawable(skeletonRenderer, uiScaleSkeletonData, uiScaleAnimationStateData);
        drawable.getAnimationState().setAnimation(0, "animation", false);
        var image = new Image(drawable);
        add(image);
        setModal(true);
        
        drawable.getAnimationState().addListener(new AnimationStateAdapter() {
            @Override
            public void complete(TrackEntry entry) {
                hide();
                fire(new PopRevertEvent(false));
            }
        });
    }
    
    @Override
    public void show(Stage stage, Action action) {
        super.show(stage, action);
    }
    
    @Override
    public void act(float delta) {
        super.act(delta);
        drawable.update(delta);
        if (Gdx.input.isButtonJustPressed(Buttons.LEFT)) {
            hide();
            fire(new PopRevertEvent(true));
        }
    }
    
    public static class PopRevertEvent extends Event {
        public boolean accepted;
    
        public PopRevertEvent(boolean accepted) {
            this.accepted = accepted;
        }
    }
    
    public abstract static class PopRevertEventListener implements EventListener {
        @Override
        public boolean handle(Event event) {
            if (event instanceof PopRevertEvent) {
                if (((PopRevertEvent) event).accepted) accepted();
                else reverted();
                return true;
            }
            return false;
        }
    
        public abstract void accepted();
        public abstract void reverted();
    }
}
