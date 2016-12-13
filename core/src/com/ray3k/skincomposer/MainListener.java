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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.ray3k.skincomposer.RootTable.RootTableListener;

public class MainListener extends RootTableListener {
    private RootTable root;
    private DialogFactory dialogFactory;
    
    public MainListener(RootTable root, DialogFactory dialogFactory) {
        this.root = root;
        this.dialogFactory = dialogFactory;
    }
    
    @Override
    public void rootEvent(RootTable.RootTableEvent event) {
        switch (event.rootTableEnum) {
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
                dialogFactory.showCloseDialog();
                break;
            case UNDO:
                break;
            case REDO:
                break;
            case SETTINGS:
                dialogFactory.showSettings();
                break;
            case COLORS:
                dialogFactory.showColors();
                break;
            case FONTS:
                dialogFactory.showFonts();
                break;
            case DRAWABLES:
                dialogFactory.showDrawables();
                break;
            case ABOUT:
                dialogFactory.showAbout();
                break;
            case CLASS_SELECTED:
                //set root table's style list
                //set root table's style properties
                //set scrollpanestyles, liststyles, and label styles
                break;
            case NEW_CLASS:
                //set root table's class list
                break;
            case DELETE_CLASS:
                break;
            case STYLE_SELECTED:
                //set root table's style properties
                //set scrollpanestyles, liststyles, and label styles
                break;
            case NEW_STYLE:
                break;
            case DUPLICATE_STYLE:
                break;
            case DELETE_STYLE:
                break;
            case RENAME_STYLE:
                break;
            case STYLE_PROPERTY:
                if (event.styleProperty.type == Drawable.class) {
                    //show drawable dialog
                } else if (event.styleProperty.type == Color.class) {
                    //show color dialog
                } else if (event.styleProperty.type == BitmapFont.class) {
                    //show fonts dialog
                } else if (event.styleProperty.type == Float.TYPE) {
                    //apply value
                } else if (event.styleProperty.type == ScrollPaneStyle.class) {
                    //apply value
                } else if (event.styleProperty.type == LabelStyle.class) {
                    //apply value
                } else if (event.styleProperty.type == ListStyle.class) {
                    //apply value
                } else if (event.styleProperty.type == CustomStyle.class) {
                    //show custom style dialog
                }
                break;
            case PREVIEW_PROPERTY:
                break;
        }
    }
}
