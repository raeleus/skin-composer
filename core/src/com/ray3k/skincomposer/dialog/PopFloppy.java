package com.ray3k.skincomposer.dialog;

import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.ray3k.skincomposer.SpineDrawable;
import com.ray3k.stripe.PopTable;

import static com.ray3k.skincomposer.Main.*;
import static com.ray3k.skincomposer.utils.Utils.onChange;
import static com.ray3k.skincomposer.utils.Utils.onEnter;

public class PopFloppy extends PopTable {
    private SpineDrawable drawable;
    private String message;
    private String option;
    private String cancel;
    private PopTable bubblePop;
    
    public PopFloppy(String message, String option, String cancel) {
        drawable = new SpineDrawable(skeletonRenderer, floppySkeletonData, floppyAnimationStateData);
        drawable.getAnimationState().setAnimation(0, "show", false);
        drawable.getAnimationState().addAnimation(0, "raise-brows", true, 0);
        var image = new Image(drawable);
        add(image);
        this.message = message;
        this.option = option;
        this.cancel = cancel;
        
        bubblePop = new PopTable();
        bubblePop.setBackground(skin.getDrawable("floppy-text-10"));
        bubblePop.attachToActor(this, Align.right, Align.topRight, -25, -15);
        
        bubblePop.defaults().space(5);
        var label = new Label(message, skin, "black");
        label.setAlignment(Align.center);
        label.setWrap(true);
        bubblePop.add(label).growX();
        
        bubblePop.row();
        var textButton = new TextButton(option, skin, "floppy");
        bubblePop.add(textButton);
        onChange(textButton, () -> {
            hide();
            fire(new PopFloppyEvent(true));
        });
        onEnter(textButton, () -> drawable.getAnimationState().setAnimation(0, "happy", true));
        textButton.addListener(handListener);
    
        bubblePop.row();
        textButton = new TextButton(cancel, skin, "floppy");
        bubblePop.add(textButton);
        onChange(textButton, () -> {
            hide();
            fire(new PopFloppyEvent(false));
        });
        onEnter(textButton, () -> drawable.getAnimationState().setAnimation(0, "sad", true));
        textButton.addListener(handListener);
        
        bubblePop.setTouchable(Touchable.disabled);
        bubblePop.show(stage, Actions.sequence(Actions.alpha(0), Actions.delay(.75f), Actions.run(() -> {
            bubblePop.getParent().toFront();
        }), Actions.fadeIn(1.0f), Actions.run(() -> bubblePop.setTouchable(Touchable.enabled))));
        
        setHideOnUnfocus(true);
    }
    
    @Override
    public void show(Stage stage, Action action) {
        super.show(stage, action);
        setPosition(50, 50, Align.bottomLeft);
    }
    
    @Override
    public void act(float delta) {
        super.act(delta);
        drawable.update(delta);
    }
    
    @Override
    public void hide(Action action) {
        super.hide(action);
        bubblePop.hide();
    }
    
    public static class PopFloppyEvent extends Event {
        public boolean accepted;
    
        public PopFloppyEvent(boolean accepted) {
            this.accepted = accepted;
        }
    }
    
    public abstract static class PopFloppyEventListener implements EventListener {
        @Override
        public boolean handle(Event event) {
            if (event instanceof PopFloppyEvent) {
                if (((PopFloppyEvent) event).accepted) accepted();
                else cancelled();
                return true;
            }
            return false;
        }
    
        public abstract void accepted();
        public abstract void cancelled();
    }
}
