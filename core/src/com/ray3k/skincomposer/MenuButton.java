/*******************************************************************************
 * MIT License
 * 
 * Copyright (c) 2016 Raymond Buckley
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package com.ray3k.skincomposer;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.MenuList.MenuListStyle;

public class MenuButton<T> extends TextButton {
    private MenuList<T> menuList;
    private MenuButtonStyle style;
    private Vector2 menuListPosition;
    private StageHideListener hideListener;
    private MenuButtonGroup menuButtonGroup;
    

    public MenuButton(String text, Skin skin) {
        this(text, skin, "default");
    }

    public MenuButton(String text, Skin skin, String styleName) {
        this(text, skin.get(styleName, MenuButtonStyle.class));
        setSkin(skin);
    }

    public MenuButton(String text, MenuButtonStyle style) {
        super(text, style);
        setStyle(style);
        
        menuList = new MenuList(style.menuListStyle);
        
        menuList.addCaptureListener(new MenuList.MenuListListener() {
            @Override
            public void menuClicked() {
                fire(new MenuButtonEvent());
                setChecked(false);
            }
        });
        
        menuListPosition = new Vector2();
        
        addListener(new MbGroupInputListener(this));
        
        addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                if (isChecked() && getStage() != null) {
                    localToStageCoordinates(menuListPosition.set(0.0f, 0.0f));
                    menuList.show(menuListPosition, getStage());
                    
                    if (menuButtonGroup != null) {
                        menuButtonGroup.check((MenuButton) actor);
                    }

                    hideListener = new StageHideListener((MenuButton) actor, getStage());
                    getStage().addListener(hideListener);
                } else {
                    menuList.hide();
                    menuButtonGroup.uncheckAll();
                }
            }
        });
    }

    public MenuList getMenuList() {
        return menuList;
    }

    public MenuButtonGroup getMenuButtonGroup() {
        return menuButtonGroup;
    }

    public void setMenuButtonGroup(MenuButtonGroup menuButtonGroup) {
        this.menuButtonGroup = menuButtonGroup;
    }

    public Array<T> getItems() {
        return menuList.getItems();
    }

    public void setItems(Array<T> newItems) {
        if (newItems == null) throw new IllegalArgumentException("newItems cannot be null.");
        menuList.setItems(newItems);
    }
    
    public void setItems(T... newItems) {
        if (newItems == null) throw new IllegalArgumentException("newItems cannot be null.");
        menuList.setItems(newItems);
    }
    
    public void clearItems() {
        menuList.clearItems();
    }
    
    public Array<String> getShortcuts() {
        return menuList.getShortcuts();
    }

    public void setShortcuts(Array<String> shortcuts) {
        if (shortcuts == null) throw new IllegalArgumentException("shortcuts cannot be null.");
        menuList.setShortcuts(shortcuts);
    }
    
    public void setShortcuts(String... shortcuts) {
        if (shortcuts == null) throw new IllegalArgumentException("shortcuts cannot be null.");
        menuList.setShortcuts(shortcuts);
    }
    
    public void clearShortcuts() {
        menuList.clearItems();
    }
    
    public Array<TextButton> getButtons() {
        return menuList.getButtons();
    }
    
    public int getSelectedIndex() {
        return menuList.getSelectedIndex();
    }
    
    public T getSelectedItem() {
        return menuList.getSelectedItem();
    }
    
    @Override
    public void setStyle(ButtonStyle style) {
        if (style == null) {
            throw new NullPointerException("style cannot be null");
        }
        if (!(style instanceof MenuButtonStyle)) {
            throw new IllegalArgumentException("style must be a MenuButtonStyle.");
        }
        super.setStyle(style);
        this.style = (MenuButtonStyle) style;
        if (menuList != null) {
            menuList.setStyle(this.style.menuListStyle);
        }
    }

    @Override
    public MenuButtonStyle getStyle() {
        return style;
    }
    
    public static class MenuButtonStyle extends TextButtonStyle {
        public MenuListStyle menuListStyle;
    }
    
    private static class MbGroupInputListener extends InputListener {
        final private MenuButton menuButton;

        public MbGroupInputListener(MenuButton menuButton) {
            this.menuButton = menuButton;
        }
        
        @Override
        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
            if (menuButton.menuButtonGroup != null 
                    && menuButton.menuButtonGroup.getSelected() != null) {
                menuButton.menuButtonGroup.check(menuButton);
            }
        }
        
    }
    
    private static class StageHideListener extends InputListener {
        private final MenuButton menuButton;
        private final Stage stage;

        public StageHideListener(MenuButton menuButton, Stage stage) {
            this.menuButton = menuButton;
            this.stage = stage;
        }
        
        @Override
        public boolean keyDown(InputEvent event, int keycode) {
            if (keycode == Input.Keys.ESCAPE) {
                menuButton.setChecked(false);
                stage.removeListener(this);
            }
            return false;
        }

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer,
                int button) {
            Actor target = event.getTarget();
            if (menuButton.isAscendantOf(target) || menuButton.getMenuList().isAscendantOf(target)) {
                return false;
            } else {
                menuButton.setChecked(false);
                stage.removeListener(this);
                return false;
            }
        }
    }
    
    public static class MenuButtonEvent extends Event {
    }
    
    public static abstract class MenuButtonListener implements EventListener {

        @Override
        public boolean handle(Event event) {
            if (event instanceof MenuButtonEvent) {
                menuClicked();
                return true;
            } else {
                return false;
            }
        }
        
        public abstract void menuClicked();
    }
}
