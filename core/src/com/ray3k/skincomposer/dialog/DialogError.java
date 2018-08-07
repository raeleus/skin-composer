/*******************************************************************************
 * MIT License
 * 
 * Copyright (c) 2018 Raymond Buckley
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
package com.ray3k.skincomposer.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.utils.Utils;
import java.io.IOException;

public class DialogError extends Dialog {
    private Main main;
    
    public DialogError(String title, String message, Main main) {
        super(title, main.getSkin());
        this.main = main;
        getTitleTable().getCells().first().padLeft(5.0f);
        
        text(message);
        getContentTable().getCells().first().pad(10.0f);
        
        getButtonTable().defaults().padBottom(10.0f).minWidth(50.0f);
        button("OK", true).key(Keys.ENTER, true);
        button("Cancel", false).key(Keys.ESCAPE, false);
        getButtonTable().getCells().first().getActor().addListener(main.getHandListener());
        getButtonTable().getCells().get(1).getActor().addListener(main.getHandListener());
    }

    @Override
    protected void result(Object object) {
        super.result(object);
        
        if ((boolean) object) {
            try {
                Utils.openFileExplorer(Main.appFolder.child("temp/log.txt"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
}
