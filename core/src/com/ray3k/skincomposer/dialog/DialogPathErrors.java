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
package com.ray3k.skincomposer.dialog;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.data.DrawableData;
import com.ray3k.skincomposer.data.FontData;

public class DialogPathErrors extends Dialog {
    private Array<DrawableData> foundDrawables;
    
    public DialogPathErrors(Main main, Skin skin, String windowStyleName, Array<DrawableData> drawables, Array<FontData> fonts) {
        super("", skin, windowStyleName);
    
        foundDrawables = new Array<>();
        
        key(Keys.ENTER, true);
        key(Keys.ESCAPE, false);
        Table table = getContentTable();
        table.defaults().pad(10.0f);
        
        Label label = new Label("Path Errors", skin, "title");
        table.add(label);
        
        table.row();
        label = new Label("The following assets could not be found. Please resolve by clicking the associated button.", skin);
        label.setAlignment(Align.center);
        table.add(label).padBottom(0);
        
        table.row();
        Table left = new Table();
        Table middle = new Table();
        Table right = new Table();
        SplitPane rightSP = new SplitPane(middle, right, false, skin);
        SplitPane leftSP = new SplitPane(left, rightSP, false, skin);
        ScrollPane scrollPane = new ScrollPane(leftSP, skin);
        scrollPane.setFlickScroll(false);
        scrollPane.setFadeScrollBars(false);
        table.add(scrollPane).grow();
        
        label = new Label("Drawable Name", skin, "black");
        left.add(label);
        
        label = new Label("Path", skin, "black");
        middle.add(label);
        
        label = new Label("Found?", skin, "black");
        right.add(label);
        
        
        for (DrawableData drawable : drawables) {
            left.row();
            label = new Label(drawable.name, skin);
            left.add(label);
            
            middle.row();
            label = new Label(drawable.file.path(), skin);
            middle.add(label);
            
            TextButton textButton = new TextButton("browse...", skin);
            middle.add(textButton);
            
            right.row();
            if (foundDrawables.contains(drawable, true)) {
                label = new Label("YES", skin, "white");
                label.setColor(Color.GREEN);
                right.add(label);
            } else {
                label = new Label("NO", skin, "white");
                label.setColor(Color.RED);
                right.add(label);
            }
        }
        
        button("Apply");
        getButtonTable().getCells().first().getActor().addListener(main.getHandListener());
        
        button("Cancel");
        getButtonTable().getCells().get(1).getActor().addListener(main.getHandListener());
        
        getCell(getButtonTable()).padBottom(20.0f);
        
        table.setWidth(200);
    }

    @Override
    public boolean remove() {
        return super.remove();
    }
}
