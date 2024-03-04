/*******************************************************************************
 * MIT License
 * 
 * Copyright (c) 2024 Raymond Buckley
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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class TraversalTextField extends TextField {
    private Actor nextFocus;
    private Actor previousFocus;
    
    public TraversalTextField(String text, Skin skin) {
        super(text, skin);
    }
    
    public TraversalTextField(String text, Skin skin, String styleName) {
        super(text, skin, styleName);
    }
    
    public TraversalTextField(String text, TextFieldStyle style) {
        super(text, style);
    }

    public Actor getNextFocus() {
        return nextFocus;
    }

    public void setNextFocus(Actor nextFocus) {
        this.nextFocus = nextFocus;
    }

    public Actor getPreviousFocus() {
        return previousFocus;
    }

    public void setPreviousFocus(Actor previousFocus) {
        this.previousFocus = previousFocus;
    }

    @Override
    public void next(boolean up) {
        if (up) {
            if (previousFocus != null) {
                getStage().setKeyboardFocus(previousFocus);
            } else {
                super.next(up);
            }
        } else {
            if (nextFocus != null) {
                getStage().setKeyboardFocus(nextFocus);
            } else {
                super.next(up);
            }
        }
    }
}
