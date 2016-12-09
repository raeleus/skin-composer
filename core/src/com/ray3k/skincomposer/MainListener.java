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

import com.ray3k.skincomposer.RootTable.RootTableEnum;
import com.ray3k.skincomposer.RootTable.RootTableListener;

public class MainListener extends RootTableListener {
    private RootTable root;
    
    public MainListener(RootTable root) {
        this.root = root;
    }
    
    @Override
    public void rootEvent(RootTableEnum rootTableEnum) {
        switch (rootTableEnum) {
            case NEW:
                break;
            case OPEN:
                break;
            case RECENT_FILES:
                break;
            case SAVE:
                break;
            case SAVE_AS:
                break;
            case IMPORT:
                break;
            case EXPORT:
                break;
            case EXIT:
                break;
            case UNDO:
                break;
            case REDO:
                break;
            case SETTINGS:
                break;
            case COLORS:
                break;
            case FONTS:
                break;
            case DRAWABLES:
                break;
            case ABOUT:
                break;
            case CLASS_SELECTED:
                break;
            case NEW_CLASS:
                break;
        }
    }
}
