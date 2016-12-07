package com.ray3k.skincomposer;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.RemoveActorAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

public class MenuList<T> extends Table {
    private MenuListStyle style;
    private Array<T> items;
    private T selectedItem;
    private int selectedIndex;

    public MenuList(Skin skin) {
        this(skin, "default");
    }

    public MenuList(Skin skin, String styleName) {
        this(skin.get(styleName, MenuListStyle.class));
        setSkin(skin);
    }

    public MenuList(MenuListStyle style) {
        this.style = style;
        items = new Array<>();
        
        setBackground(style.background);
        setTouchable(Touchable.enabled);
        
        updateContents();
        selectedIndex = -1;
    }
    
    public void show(Vector2 screenPosition, Stage stage) {
        stage.addActor(this);
        setX(screenPosition.x);
        setY(screenPosition.y - getHeight());
        
        //fade in
        clearActions();
        getColor().a = 0;
        addAction(fadeIn(0.3f, Interpolation.fade));
        
        selectedItem = null;
        selectedIndex = -1;
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

    public void setItems(Array<T> newItems) {
        if (newItems == null) throw new IllegalArgumentException("newItems cannot be null.");
        this.items.clear();
        this.items.addAll(newItems);
        
        updateContents();
    }
    
    public void setItems(T... newItems) {
        if (newItems == null) throw new IllegalArgumentException("newItems cannot be null.");
        items.clear();
        items.addAll(newItems);
        
        updateContents();
    }
    
    public void clearItems() {
        items.clear();
        
        updateContents();
    }
    
    private void updateContents() {
        clearChildren();
        
        int index = 0;
        
        for (T item : items) {
            TextButton textButton = new TextButton(item.toString(), style.textButtonStyle);
            textButton.getLabel().setAlignment(Align.left);
            if (getCells().size > 0) {
                row();
            }
            add(textButton);
            
            int i = index++;
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    selectedIndex = i;
                    selectedItem = item;
                    fire(new MenuListEvent());
                }
            });
        }
        
        validate();
        
        float width = style.background.getLeftWidth() + style.background.getRightWidth();
        for (int i = 0; i < getColumns(); i++) {
            
            width += getColumnWidth(i);
        }
        
        float height = style.background.getLeftWidth() + style.background.getRightWidth();
        for (int i = 0; i < getRows(); i++) {
            
            height += getRowHeight(i);
        }
        
        for (Cell cell : getCells()) {
            cell.growX();
        }
        
        setSize(width, height);
    }

    public T getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(T selectedItem) {
        this.selectedItem = selectedItem;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }
    
    public MenuListStyle getStyle() {
        return style;
    }
    
    public static class MenuListStyle {
        public Drawable background;
        public TextButtonStyle textButtonStyle;
    }
    
    public static class MenuListEvent extends Event {
    }
    
    public static abstract class MenuListListener implements EventListener {

        @Override
        public boolean handle(Event event) {
            if (event instanceof MenuListEvent) {
                menuClicked();
                return true;
            } else {
                return false;
            }
        }
        
        public abstract void menuClicked();
    }
}
