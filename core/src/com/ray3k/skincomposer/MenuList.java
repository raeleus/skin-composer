package com.ray3k.skincomposer;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.RemoveActorAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;

public class MenuList<T> extends Table {
    private MenuListStyle style;
    private Array<T> items;

    public MenuList(Skin skin) {
        this(skin, "default");
    }

    public MenuList(Skin skin, String styleName) {
        this(skin.get(styleName, MenuListStyle.class));
        setSkin(skin);
    }

    public MenuList(MenuListStyle style) {
        setBackground(style.background);
        setWidth(100.0f);
        setHeight(100.0f);
        setTouchable(Touchable.enabled);
    }
    
    public void show(Vector2 screenPosition, Stage stage) {
        stage.addActor(this);
        setX(screenPosition.x);
        setY(screenPosition.y - getHeight());
        
        //fade in
        clearActions();
        getColor().a = 0;
        addAction(fadeIn(0.3f, Interpolation.fade));
    }
    
    public void hide() {
        //fade out and then remove
        clearActions();
        AlphaAction alphaAction = new AlphaAction();
        alphaAction.setAlpha(0.0f);
        alphaAction.setDuration(.3f);
        alphaAction.setInterpolation(Interpolation.fade);
        RemoveActorAction removeAction = new RemoveActorAction();
        removeAction.setActor(this);
        SequenceAction sequenceAction = new SequenceAction(alphaAction, removeAction);
        addAction(sequenceAction);
    }
    
    public void setStyle(MenuListStyle style) {
        if (style == null) {
            throw new NullPointerException("style cannot be null");
        }
        if (!(style instanceof MenuListStyle)) {
            throw new IllegalArgumentException("style must be a MenuListStyle.");
        }
        this.style = style;
    }
    
    public Array<T> getItems() {
        return items;
    }

    public void setItems(Array<T> items) {
        this.items = items;
    }
    
    public void setItems(T... newItems) {
        items.clear();
        items.addAll(newItems);
    }
    
    public void clearItems() {
        items.clear();
    }
    
    public MenuListStyle getStyle() {
        return style;
    }
    
    public static class MenuListStyle {
        public Drawable background;
        public TextButtonStyle textButtonStyle;
    }
}
