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
package com.ray3k.skincomposer.data;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.ray3k.skincomposer.dialog.DialogTVG;
import com.ray3k.skincomposer.dialog.DialogTVG.TvgData;
import com.ray3k.skincomposer.dialog.tenpatch.DialogTenPatch;
import com.ray3k.skincomposer.utils.Utils;

public class DrawableData {
    public enum DrawableType {
        TINTED("Tinted", "button-colorwheel"), TINTED_FROM_COLOR_DATA("Tinted", "button-swatches"),
        TILED("Tiled", "button-tiles"), TENPATCH("TenPatch", "button-tenpatch"),
        TEXTURE("Texture", "icon-texture"), NINE_PATCH("NinePatch", "icon-ninepatch"),
        CUSTOM("Custom", "icon-custom"), FONT("Font", "icon-font"), PIXEL("Pixel", "icon-pixel"),
        TVG("TinyVG", "icon-tvg");
        
        public String formattedName;
        public String iconName;
        DrawableType (String formattedName, String iconName) {
            this.formattedName = formattedName;
            this.iconName = iconName;
        }
    
        @Override
        public String toString() {
            return formattedName;
        }
    }
    
    public FileHandle file;
    public Color bgColor;
    public Color tint;
    public String tintName;
    public String name;
    public boolean tiled;
    public float minWidth;
    public float minHeight;
    public boolean customized;
    public DialogTenPatch.TenPatchData tenPatchData;
    public DialogTVG.TvgData tvgData;
    public DrawableType type;
    public boolean hidden;

    public DrawableData(FileHandle file) {
        this.file = file;
        if (Utils.isBitmap(file.name())) {
            Color temp = Utils.averageEdgeColor(file);
            if (Utils.brightness(temp) > .5f) {
                bgColor = Color.BLACK;
            } else {
                bgColor = Color.WHITE;
            }
        } else {
            bgColor = Color.WHITE;
        }
        this.name = proper(file.name());
        customized = false;
        minWidth = -1;
        minHeight = -1;
    }
    
    public DrawableData(String customName) {
        name = customName;
        customized = true;
        bgColor = Color.WHITE;
        tiled = false;
        minWidth = -1;
        minHeight = -1;
    }
    
    public DrawableData() {
        minWidth = -1;
        minHeight = -1;
        bgColor = Color.WHITE;
    }
    
    public DrawableData(DrawableData drawableData) {
        set(drawableData);
    }
    
    public void set(DrawableData drawableData) {
        this.file = drawableData.file;
        this.bgColor = drawableData.bgColor;
        this.tint = drawableData.tint;
        this.tintName = drawableData.tintName;
        this.name = drawableData.name;
        this.tiled = drawableData.tiled;
        this.minWidth = drawableData.minWidth;
        this.minHeight = drawableData.minHeight;
        this.customized = drawableData.customized;
        if (drawableData.tenPatchData != null) {
            if (tenPatchData == null) tenPatchData = new DialogTenPatch.TenPatchData();
            this.tenPatchData.set(drawableData.tenPatchData);
        }
        if (drawableData.tvgData != null) {
            if (tvgData == null) tvgData = new TvgData();
            this.tvgData.set(drawableData.tvgData);
        }
        this.type = drawableData.type;
        this.hidden = drawableData.hidden;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DrawableData) {
            DrawableData dd = (DrawableData) obj;
            
            return name.equals(dd.name) && 
                    customized == dd.customized && 
                    (file == null && dd.file == null || file != null && file.equals(dd.file)) &&
                    (tint == null && dd.tint == null || tint != null && tint.equals(dd.tint)) &&
                    (tintName == null && dd.tintName == null || tintName != null && tintName.equals(dd.tintName)) &&
                    (tenPatchData == null && dd.tenPatchData == null || tenPatchData != null && tenPatchData.equals(dd.tenPatchData));
        }
        return false;
    }
    
    public static String proper(String name) {
        return name.replaceFirst("(\\.9)?\\.[a-zA-Z0-9]*$", "");
    }
    
    public static boolean validate(String name) {
        return name != null && !name.matches("^\\d.*|^-.*|.*\\s.*|.*[^a-zA-Z\\d\\s-_ñáéíóúüÑÁÉÍÓÚÜ].*|^$");
    }
}