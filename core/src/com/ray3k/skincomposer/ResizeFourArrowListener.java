/*
 * The MIT License
 *
 * Copyright 2018 Raymond Buckley.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.ray3k.skincomposer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;

/**
 *
 * @author Raymond
 */
public class ResizeFourArrowListener extends DragListener {
    private Cursor cursor;

    public ResizeFourArrowListener(Cursor cursor) {
        this.cursor = cursor;
    }
    
    @Override
    public void exit(InputEvent event, float x, float y, int pointer,
            Actor toActor) {
        if (event.getListenerActor().equals(event.getTarget()) && !Gdx.input.isButtonPressed(Buttons.LEFT)) {
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        }
    }

    @Override
    public void enter(InputEvent event, float x, float y, int pointer,
            Actor fromActor) {
        if (event.getListenerActor().equals(event.getTarget()) && !Gdx.input.isButtonPressed(Buttons.LEFT)) {
            Gdx.graphics.setCursor(cursor);
        }
    }

    @Override
    public void dragStop(InputEvent event, float x, float y, int pointer) {
        
    }

    @Override
    public void dragStart(InputEvent event, float x, float y,
            int pointer) {
        
    }
}
