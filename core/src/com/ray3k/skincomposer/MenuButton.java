package com.ray3k.skincomposer;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.ray3k.skincomposer.MenuList.MenuListStyle;

public class MenuButton extends TextButton {
    private MenuList menuList;
    private MenuButtonStyle style;
    private Vector2 menuListPosition;
    private HideListener hideListener;

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
        menuListPosition = new Vector2();
        
        addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                if (isChecked() && getStage() != null) {
                    localToStageCoordinates(menuListPosition.set(0.0f, 0.0f));
                    menuList.show(menuListPosition, getStage());
                    
                    hideListener = new HideListener((MenuButton) actor, getStage());
                    getStage().addListener(hideListener);
                } else {
                    menuList.hide();
                }
            }
        });
    }

    public MenuList getMenuList() {
        return menuList;
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

    public MenuButtonStyle getStyle() {
        return style;
    }
    
    public static class MenuButtonStyle extends TextButtonStyle {
        public MenuListStyle menuListStyle;
    }
    
    private static class HideListener extends InputListener {
        private final MenuButton menuButton;
        private Stage stage;

        public HideListener(MenuButton menuButton, Stage stage) {
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
}
