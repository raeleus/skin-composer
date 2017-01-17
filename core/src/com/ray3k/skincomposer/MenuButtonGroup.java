/*******************************************************************************
 * MIT License
 * 
 * Copyright (c) 2017 Raymond Buckley
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
