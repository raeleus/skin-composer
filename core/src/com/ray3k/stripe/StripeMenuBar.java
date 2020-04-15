package com.ray3k.stripe;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.IntSet.IntSetIterator;
import com.ray3k.stripe.PopTable.PopTableStyle;

public class StripeMenuBar extends Table implements StripeMenu {
    private final TextButtonStyle itemStyle;
    private final TextButtonStyle submenuStyle;
    private final PopTableStyle popTableStyle;
    private final LabelStyle shortcutLabelStyle;
    private final Array<StripeMenuValue> stripeMenuValues = new Array<>();
    private final Stage stage;
    private final WidgetGroup modalGroup;
    private boolean menuActivated;
    private final Array<KeyboardShortcutListener> keyboardShortcutListeners = new Array<>();
    private StripeMenuBarStyle style;
    private static final Vector2 temp = new Vector2();
    
    public StripeMenuBar(Stage stage, Skin skin) {
        this(stage, skin, "default");
    }
    
    public StripeMenuBar(Stage stage, Skin skin, String style) {
        this(stage, skin.get(style, StripeMenuBarStyle.class));
    }
    
    public StripeMenuBar(Stage stage, StripeMenuBarStyle style) {
        this.style = style;
        
        itemStyle = new TextButtonStyle();
        itemStyle.up = style.itemUp;
        itemStyle.down = style.itemDown;
        itemStyle.over = style.itemOver;
        itemStyle.checked = style.itemOpen;
        itemStyle.disabled = style.itemDisabled;
        itemStyle.font = style.itemFont;
        itemStyle.fontColor = style.itemFontColor;
        itemStyle.overFontColor = style.itemOverFontColor;
        itemStyle.downFontColor = style.itemDownFontColor;
        itemStyle.checkedFontColor = style.itemOpenFontColor;
        itemStyle.disabledFontColor = style.itemDisabledFontColor;
        
        submenuStyle = new TextButtonStyle();
        submenuStyle.up = style.submenuUp == null ? style.itemUp : style.submenuUp;
        submenuStyle.down = style.submenuDown == null ? style.itemDown : style.submenuDown;
        submenuStyle.over = style.submenuOver == null ? style.itemOver : style.submenuOver;
        submenuStyle.checked = style.submenuOpen;
        submenuStyle.disabled = style.submenuDisabled;
        submenuStyle.font = style.itemFont;
        submenuStyle.fontColor = style.itemFontColor;
        submenuStyle.overFontColor = style.itemOverFontColor;
        submenuStyle.downFontColor = style.itemDownFontColor;
        submenuStyle.checkedFontColor = style.itemOpenFontColor;
        submenuStyle.disabledFontColor = style.itemDisabledFontColor;
        
        popTableStyle = new PopTableStyle();
        popTableStyle.background = style.submenuBackground;
        
        shortcutLabelStyle = new LabelStyle();
        shortcutLabelStyle.font = style.shortcutFont == null ? style.itemFont : style.shortcutFont;
        setBackground(style.menuBar);
        
        this.stage = stage;
        align(Align.left);
    
        modalGroup = new WidgetGroup() {
            @Override
            public Actor hit(float x, float y, boolean touchable) {
                temp.set(x,y);
                localToStageCoordinates(temp);
                StripeMenuBar.this.stageToLocalCoordinates(temp);
                Actor actor = StripeMenuBar.this.hit(temp.x, temp.y, true);
                return actor == null ? this : actor;
            }
        };
        modalGroup.setFillParent(true);
        modalGroup.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hideEverything();
            }
        });
    }
    
    private static void hideRecursive(Array<StripeMenuValue> items) {
        for (StripeMenuValue item : items) {
            hideRecursive(item.stripeMenuValues);
            item.hide();
        }
    }
    
    private void hideEverything() {
        hideRecursive(stripeMenuValues);
        modalGroup.remove();
        menuActivated = false;
    
        for (StripeMenuValue value : stripeMenuValues) {
            value.getParentButton().setChecked(false);
        }
    }
    
    @Override
    public StripeMenu menu(String name, EventListener... listeners) {
        StripeMenuValue returnValue = createMenu(name, this, stripeMenuValues, Align.bottomLeft, Align.bottomRight, true, listeners);
        add(returnValue.textButton);
        return returnValue;
    }
    
    @Override
    public StripeMenu item(String name, EventListener... listeners) {
        return item(name, null, listeners);
    }
    
    @Override
    public StripeMenu item(String name, KeyboardShortcut keyboardShortcut, EventListener... listeners) {
        add(createItem(name, Align.center, stripeMenuValues, keyboardShortcut, listeners));
        return this;
    }
    
    @Override
    public StripeMenu parent() {
        return null;
    }
    
    @Override
    public TextButton getParentButton() {
        return null;
    }
    
    public class StripeMenuValue extends PopTable implements StripeMenu {
        private StripeMenu parent;
        private TextButton textButton;
        private final Array<StripeMenuValue> stripeMenuValues = new Array<>();
    
        public StripeMenuValue(StripeMenu parent) {
            super(popTableStyle);
            StripeMenuValue.this.parent = parent;
            align(Align.top);
        }
    
        @Override
        public StripeMenu menu(String name, EventListener... listeners) {
            StripeMenu returnValue = createMenu(name, this, stripeMenuValues, Align.topRight, Align.bottomRight, false, listeners);
            add(returnValue.getParentButton());
            row();
            return returnValue;
        }
    
        @Override
        public StripeMenu item(String name, EventListener... listeners) {
            return item(name, null, listeners);
        }
    
        @Override
        public StripeMenu item(String name, KeyboardShortcut keyboardShortcut, EventListener... listeners) {
            add(createItem(name, Align.left, stripeMenuValues, keyboardShortcut, listeners));
            row();
            return this;
        }
        
        @Override
        public StripeMenu parent() {
            return parent;
        }
    
        @Override
        public TextButton getParentButton() {
            return textButton;
        }
    
        @Override
        public TextButton findButton(String name) {
            for (Actor actor : getChildren()) {
                if (actor instanceof TextButton) {
                    TextButton textButton = (TextButton) actor;
                    if (textButton.getText().toString().equals(name)) {
                        return textButton;
                    }
                }
            }
            return null;
        }
    
        @Override
        public Cell findCell(String name) {
            for (Actor actor : getChildren()) {
                if (actor instanceof TextButton) {
                    TextButton textButton = (TextButton) actor;
                    if (textButton.getText().toString().equals(name)) {
                        return getCell(textButton);
                    }
                }
            }
            return null;
        }
    
        @Override
        public StripeMenu findMenu(String name) {
            for (StripeMenuValue value : stripeMenuValues) {
                if (value.textButton.getText().toString().equals(name)) return value;
            }
            return null;
        }
    }
    
    private StripeMenuValue createMenu(String name, StripeMenu parent, Array<StripeMenuValue> stripeMenuValues, int edge, int align, boolean modal, EventListener... listeners) {
        TextButton textButton = new TextButton(name, itemStyle);
        textButton.setProgrammaticChangeEvents(false);
        textButton.getLabel().setAlignment(Align.left);
    
        for (EventListener listener : listeners) {
            textButton.addListener(listener);
        }
    
        StripeMenuValue menu = new StripeMenuValue(parent);
        menu.defaults().growX();
        menu.attachToActor(textButton, edge, align);
        menu.textButton = textButton;
        stripeMenuValues.add(menu);
        
        ItemHoverListener listener = new ItemHoverListener(menu, stripeMenuValues);
        listener.modal = modal;
        listener.clickMode = modal;
        textButton.addListener(listener);
        
        return menu;
    }
    
    private TextButton createItem(String name, int textAlign, Array<StripeMenuValue> stripeMenuValues, KeyboardShortcut keyboardShortcut, EventListener... listeners) {
        TextButton textButton = new TextButton(name, itemStyle);
        textButton.setProgrammaticChangeEvents(false);
        textButton.getLabel().setAlignment(textAlign);
    
        for (EventListener listener : listeners) {
            textButton.addListener(listener);
        }

        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                textButton.setChecked(false);
                fire(new MenuBarEvent(textButton, textButton.getText().toString()));
            }
        });
        
        textButton.addListener(new ItemHoverListener(null, stripeMenuValues));
        
        if (keyboardShortcut != null) {
            Label label = new Label(keyboardShortcut.name, shortcutLabelStyle) {
                @Override
                public void draw(Batch batch, float parentAlpha) {
                    if (textButton.isDisabled()) {
                        shortcutLabelStyle.fontColor = style.shortcutDisabledFontColor == null ? style.itemDisabledFontColor : style.shortcutDisabledFontColor;
                    } else if (textButton.isPressed()) {
                        shortcutLabelStyle.fontColor = style.shortcutDownFontColor == null ? style.itemDownFontColor : style.shortcutDownFontColor;
                    } else if (textButton.isChecked()) {
                        shortcutLabelStyle.fontColor = style.shortcutOpenFontColor == null ? style.itemOpenFontColor : style.shortcutOpenFontColor;
                    } else if (textButton.isOver()) {
                        shortcutLabelStyle.fontColor = style.shortcutOverFontColor == null ? style.itemOverFontColor : style.shortcutOverFontColor;
                    } else {
                        shortcutLabelStyle.fontColor = style.shortcutFontColor == null ? style.itemFontColor : style.shortcutFontColor;
                    }
                    super.draw(batch, parentAlpha);

                }
            };
            textButton.add(label).space(style.shortcutSpace);

            KeyboardShortcutListener keyboardShortcutListener = new KeyboardShortcutListener(keyboardShortcut, textButton);
            stage.addListener(keyboardShortcutListener);
            keyboardShortcutListeners.add(keyboardShortcutListener);
        }
        return textButton;
    }
    
    public StripeMenuBarStyle getStyle() {
        return style;
    }
    
    @Override
    public TextButton findButton(String name) {
        for (Actor actor : getChildren()) {
            if (actor instanceof TextButton) {
                TextButton textButton = (TextButton) actor;
                if (textButton.getText().toString().equals(name)) {
                    return textButton;
                }
            }
        }
        return null;
    }
    
    @Override
    public Cell findCell(String name) {
        for (Actor actor : getChildren()) {
            if (actor instanceof TextButton) {
                TextButton textButton = (TextButton) actor;
                if (textButton.getText().toString().equals(name)) {
                    return getCell(textButton);
                }
            }
        }
        return null;
    }
    
    @Override
    public StripeMenu findMenu(String name) {
        for (StripeMenuValue value : stripeMenuValues) {
            if (value.textButton.getText().toString().equals(name)) return value;
        }
        return null;
    }
    
    public static class KeyboardShortcut {
        public String name;
        public int key;
        public final IntSet modifiers = new IntSet();
        
        public KeyboardShortcut(String name, int key, int... modifiers) {
            this.name = name;
            this.key = key;
            this.modifiers.addAll(modifiers,0, modifiers.length);
        }
    }
    
    public static class MenuBarEvent extends Event {
        public TextButton textButton;
        public String name;
    
        public MenuBarEvent(TextButton textButton, String name) {
            this.textButton = textButton;
            this.name = name;
        }
    }
    
    public static abstract class MenuBarListener implements EventListener {
        @Override
        public boolean handle(Event event) {
            if (event instanceof MenuBarEvent) {
                MenuBarEvent menuBarEvent = (MenuBarEvent) event;
                itemClicked(menuBarEvent.name, menuBarEvent.textButton, menuBarEvent);
                return true;
            }
            return false;
        }
        
        public abstract void itemClicked(String name, TextButton textButton, MenuBarEvent event);
    }
    
    private class KeyboardShortcutListener extends InputListener {
        private KeyboardShortcut keyboardShortcut;
        private TextButton textButton;
    
        public KeyboardShortcutListener(KeyboardShortcut keyboardShortcut, TextButton textButton) {
            this.keyboardShortcut = keyboardShortcut;
            this.textButton = textButton;
        }
    
        @Override
        public boolean keyDown(InputEvent event, int keycode) {
            if (getParent() != null && !textButton.isDisabled() && keycode == keyboardShortcut.key) {
                IntSetIterator iter = keyboardShortcut.modifiers.iterator();
                while (iter.hasNext) {
                    int modifier = iter.next();
                    if (!Gdx.input.isKeyPressed(modifier)) return false;
                }
                textButton.fire(new ChangeEvent());
                fire(new MenuBarEvent(textButton, textButton.getText().toString()));
                hideRecursive(stripeMenuValues);
                return true;
            }
            return false;
        }
    }
    
    private class ItemHoverListener extends ClickListener {
        private final Array<StripeMenuValue> stripeMenuValues;
        private final StripeMenuValue stripeMenuValue;
        private boolean modal;
        private boolean clickMode;
    
        public ItemHoverListener(StripeMenuValue stripeMenuValue, Array<StripeMenuValue> stripeMenuValues) {
            this.stripeMenuValue = stripeMenuValue;
            this.stripeMenuValues = stripeMenuValues;
        }
    
        @Override
        public void clicked(InputEvent event, float x, float y) {
            if (stripeMenuValue == null) {
                hideEverything();
            }
            
            if (clickMode) {
                if (!menuActivated) {
                    menuActivated = true;
    
                    hide();
    
                    show();
                } else {
                    hideEverything();
                }
            }
        }
        
        @Override
        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
            super.enter(event, x, y, pointer, fromActor);
            
            if (menuActivated) {
                hide();
    
                show();
            }
        }
        
        private void hide() {
            for (StripeMenuValue stripeMenuValue : stripeMenuValues) {
                if (stripeMenuValue != this.stripeMenuValue) {
                    hideRecursive(stripeMenuValue.stripeMenuValues);
                    stripeMenuValue.hide();
                    stripeMenuValue.setTouchable(Touchable.disabled);
                    if (stripeMenuValue.textButton != null) stripeMenuValue.textButton.setChecked(false);
                }
            }
        }
        
        private void show() {
            if (stripeMenuValue != null && stripeMenuValue.isHidden()) {
                if (modal) stage.addActor(modalGroup);
                stripeMenuValue.show(stage);
                stripeMenuValue.attachToActor();
                stripeMenuValue.setTouchable(Touchable.enabled);
                if (stripeMenuValue.textButton != null) stripeMenuValue.textButton.setChecked(true);
            }
        }
    }
    
    public static class StripeMenuBarStyle {
        public BitmapFont itemFont;
        /*Optional*/
        public Drawable menuBar, submenuBackground, itemUp, itemOver, itemDown, itemOpen, itemDisabled, submenuUp, submenuOver, submenuDown, submenuOpen, submenuDisabled;
        public BitmapFont shortcutFont;
        public Color itemFontColor, itemOverFontColor, itemDownFontColor, itemOpenFontColor, itemDisabledFontColor, shortcutFontColor, shortcutOverFontColor, shortcutDownFontColor, shortcutOpenFontColor, shortcutDisabledFontColor;
        public float shortcutSpace;
        
        public StripeMenuBarStyle() {
        
        }
        
        public StripeMenuBarStyle(StripeMenuBarStyle style) {
            itemFont = style.itemFont;
            menuBar = style.menuBar;
            submenuBackground = style.submenuBackground;
            itemUp = style.itemUp;
            itemOver = style.itemOver;
            itemDown = style.itemDown;
            itemOpen = style.itemOpen;
            itemDisabled = style.itemDisabled;
            submenuUp = style.submenuUp;
            submenuOver = style.submenuOver;
            submenuDown = style.submenuDown;
            submenuOpen = style.submenuOpen;
            submenuDisabled = style.submenuDisabled;
            shortcutFont = style.shortcutFont;
            itemFontColor = style.itemFontColor;
            itemOverFontColor = style.itemOverFontColor;
            itemDownFontColor = style.itemDownFontColor;
            itemOpenFontColor = style.itemOpenFontColor;
            itemDisabledFontColor = style.itemDisabledFontColor;
            shortcutOverFontColor = style.shortcutOverFontColor;
            shortcutDownFontColor = style.shortcutDownFontColor;
            shortcutOpenFontColor = style.shortcutOpenFontColor;
            shortcutDisabledFontColor = style.shortcutDisabledFontColor;
            shortcutFontColor = style.shortcutFontColor;
            shortcutSpace = style.shortcutSpace;
        }
    }
}