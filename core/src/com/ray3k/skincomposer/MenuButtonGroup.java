package com.ray3k.skincomposer;

import com.badlogic.gdx.utils.Array;

public class MenuButtonGroup<T extends MenuButton> {
    private final Array<T> buttons;
    private T selected;

    public MenuButtonGroup() {
        buttons = new Array<>();
    }
    
    public void add(T button) {
        if (button == null) throw new IllegalArgumentException("button cannot be null.");
        buttons.add(button);
        button.setMenuButtonGroup(this);
    }
    
    public Array<T> getButtons() {
        return buttons;
    }
    
    public void check(T button) {
        if (selected == null || !selected.equals(button)) {
            if (selected != null) selected.setChecked(false);
            button.setChecked(true);
            selected = button;
        }
    }
    
    public void uncheckAll() {
        for (MenuButton button : buttons) {
            if (button.isChecked()) button.setChecked(false);
        }
        
        selected = null;
    }
    
    public T getSelected() {
        return selected;
    }
}
