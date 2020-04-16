package com.ray3k.skincomposer.dialog.scenecomposer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class FadeLabel extends Label {
    public static final float TIME = .75f;
    
    public FadeLabel(CharSequence text, Skin skin) {
        super(text, skin);
        addActions();
    }
    
    public FadeLabel(CharSequence text, Skin skin, String styleName) {
        super(text, skin, styleName);
        addActions();
    }
    
    public FadeLabel(CharSequence text, Skin skin, String fontName,
                     Color color) {
        super(text, skin, fontName, color);
        addActions();
    }
    
    public FadeLabel(CharSequence text, Skin skin, String fontName,
                     String colorName) {
        super(text, skin, fontName, colorName);
        addActions();
    }
    
    public FadeLabel(CharSequence text, LabelStyle style) {
        super(text, style);
        addActions();
    }
    
    private void addActions() {
        setTouchable(Touchable.disabled);
        addAction(Actions.parallel(
                Actions.sequence(Actions.fadeOut(TIME), Actions.removeActor()),
                Actions.moveBy(0, 20, TIME)));
    }
}
