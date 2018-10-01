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
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;

/**
 *
 * @author Raymond
 */
public class ResizeArrowListener extends DragListener {
    private boolean draggingCursor;
    private boolean vertical;

    public ResizeArrowListener(boolean vertical) {
        this.draggingCursor = false;
        this.vertical = vertical;
    }
    
    @Override
    public void exit(InputEvent event, float x, float y, int pointer,
            Actor toActor) {
        if (!draggingCursor && event.getListenerActor().equals(event.getTarget())) {
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        }
    }

    @Override
    public void enter(InputEvent event, float x, float y, int pointer,
            Actor fromActor) {
        if (!draggingCursor && event.getListenerActor().equals(event.getTarget())) {
            if (vertical) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.VerticalResize);
            } else {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.HorizontalResize);
            }
        }
    }

    @Override
    public void dragStop(InputEvent event, float x, float y, int pointer) {
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        draggingCursor = false;
    }

    @Override
    public void dragStart(InputEvent event, float x, float y,
            int pointer) {
        if (!draggingCursor && event.getListenerActor().equals(event.getTarget())) {
            if (vertical) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.VerticalResize);
            } else {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.HorizontalResize);
            }
            draggingCursor = true;
        }
    }
}
