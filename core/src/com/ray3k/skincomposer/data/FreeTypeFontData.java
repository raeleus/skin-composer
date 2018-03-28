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

package com.ray3k.skincomposer.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.Hinting;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ray3k.skincomposer.Main;

public class FreeTypeFontData implements Json.Serializable {
    public static final String DEFAULT_CHARS = "\\u0000ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890\\\"!`?'.,;:()[]{}<>|/@\\\\^$€-%+=#_&~*\\u0080\\u0081\\u0082\\u0083\\u0084\\u0085\\u0086\\u0087\\u0088\\u0089\\u008A\\u008B\\u008C\\u008D\\u008E\\u008F\\u0090\\u0091\\u0092\\u0093\\u0094\\u0095\\u0096\\u0097\\u0098\\u0099\\u009A\\u009B\\u009C\\u009D\\u009E\\u009F\\u00A0\\u00A1\\u00A2\\u00A3\\u00A4\\u00A5\\u00A6\\u00A7\\u00A8\\u00A9\\u00AA\\u00AB\\u00AC\\u00AD\\u00AE\\u00AF\\u00B0\\u00B1\\u00B2\\u00B3\\u00B4\\u00B5\\u00B6\\u00B7\\u00B8\\u00B9\\u00BA\\u00BB\\u00BC\\u00BD\\u00BE\\u00BF\\u00C0\\u00C1\\u00C2\\u00C3\\u00C4\\u00C5\\u00C6\\u00C7\\u00C8\\u00C9\\u00CA\\u00CB\\u00CC\\u00CD\\u00CE\\u00CF\\u00D0\\u00D1\\u00D2\\u00D3\\u00D4\\u00D5\\u00D6\\u00D7\\u00D8\\u00D9\\u00DA\\u00DB\\u00DC\\u00DD\\u00DE\\u00DF\\u00E0\\u00E1\\u00E2\\u00E3\\u00E4\\u00E5\\u00E6\\u00E7\\u00E8\\u00E9\\u00EA\\u00EB\\u00EC\\u00ED\\u00EE\\u00EF\\u00F0\\u00F1\\u00F2\\u00F3\\u00F4\\u00F5\\u00F6\\u00F7\\u00F8\\u00F9\\u00FA\\u00FB\\u00FC\\u00FD\\u00FE\\u00FF";
    public String name;
    public FileHandle file;
    public String previewTTF;
    public boolean useCustomSerializer;
    public int size = 16;
    public boolean mono;
    public String hinting = "AutoMedium";
    public String color;
    public float gamma = 1.8f;
    public int renderCount = 2;
    public float borderWidth = 0;
    public String borderColor;
    public boolean borderStraight = false;
    public float borderGamma = 1.8f;
    public int shadowOffsetX = 0;
    public int shadowOffsetY = 0;
    public String shadowColor;
    public int spaceX, spaceY;
    public String characters = "";
    public boolean kerning = true;
    public boolean flip = false;
    public boolean genMipMaps = false;
    public String minFilter = "Nearest";
    public String magFilter = "Nearest";
    public boolean incremental;
    public BitmapFont bitmapFont;
    
    public FreeTypeFontData() {
        
    }
    
    public FreeTypeFontData(FreeTypeFontData original) {
        name = original.name;
        file = original.file != null ? new FileHandle(original.file.path()) : null;
        previewTTF = original.previewTTF;
        useCustomSerializer = original.useCustomSerializer;
        size = original.size;
        mono = original.mono;
        hinting = original.hinting;
        color = original.color;
        gamma = original.gamma;
        renderCount = original.renderCount;
        borderWidth = original.borderWidth;
        borderColor = original.borderColor;
        borderStraight = original.borderStraight;
        borderGamma = original.borderGamma;
        shadowOffsetX = original.shadowOffsetX;
        shadowOffsetY = original.shadowOffsetY;
        shadowColor = original.shadowColor;
        spaceX = original.spaceX;
        spaceY = original.spaceY;
        characters = original.characters;
        kerning = original.kerning;
        flip = original.flip;
        genMipMaps = original.genMipMaps;
        minFilter = original.minFilter;
        magFilter = original.magFilter;
        incremental = original.incremental;
    }
    
    public void createBitmapFont(Main main) {
        if (bitmapFont != null) {
            bitmapFont.dispose();
            bitmapFont = null;
        }
        
        if (!useCustomSerializer) {
            if (previewTTF == null) return;
            FreeTypeFontParameter parameter = new FreeTypeFontParameter();
            parameter.color = Color.BLACK;
            
            FileHandle previewFontPath = Gdx.files.local("preview fonts/" + previewTTF + ".ttf");
            if (previewFontPath.exists()) {
                FreeTypeFontGenerator generator = new FreeTypeFontGenerator(previewFontPath);
                bitmapFont = generator.generateFont(parameter);
                generator.dispose();
            }
        } else {
            if (file == null) return;
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(file);
            FreeTypeFontParameter parameter = new FreeTypeFontParameter();

            if (borderColor != null) for (ColorData colorData : main.getJsonData().getColors()) {
                if (colorData.getName().equals(borderColor)) {
                    parameter.borderColor = colorData.color;
                    break;
                }
            }
            parameter.borderGamma = borderGamma;
            parameter.borderStraight = borderStraight;
            parameter.borderWidth = borderWidth;
            parameter.characters = characters.equals("") ? FreeTypeFontGenerator.DEFAULT_CHARS : characters;
            if (color != null) for (ColorData colorData : main.getJsonData().getColors()) {
                if (colorData.getName().equals(color)) {
                    parameter.color = colorData.color;
                    break;
                }
            }
            parameter.flip = flip;
            parameter.gamma = gamma;
            parameter.genMipMaps = genMipMaps;
            parameter.hinting = hinting == null ? Hinting.AutoMedium : Hinting.valueOf(hinting);
            parameter.incremental = false;
            parameter.kerning = kerning;
            parameter.magFilter = magFilter == null ? TextureFilter.Nearest : TextureFilter.valueOf(magFilter);
            parameter.minFilter = minFilter == null ? TextureFilter.Nearest : TextureFilter.valueOf(minFilter);
            parameter.mono = mono;
            parameter.renderCount = renderCount;
            if (shadowColor != null) for (ColorData colorData : main.getJsonData().getColors()) {
                if (colorData.getName().equals(shadowColor)) {
                    parameter.shadowColor = colorData.color;
                    break;
                }
            }
            parameter.shadowOffsetX = shadowOffsetX;
            parameter.shadowOffsetY = shadowOffsetY;
            parameter.size = size;
            parameter.spaceX = spaceX;
            parameter.spaceY = spaceY;

            bitmapFont = generator.generateFont(parameter);
            generator.dispose();
        }
    }

    @Override
    public void write(Json json) {
        json.writeValue("name", name);
        if (file != null) json.writeValue("file", file.path());
        json.writeValue("previewTTF", previewTTF);
        json.writeValue("useCustomSerializer", useCustomSerializer);
        json.writeValue("size", size);
        json.writeValue("mono", mono);
        json.writeValue("hinting", hinting);
        json.writeValue("color", color);
        json.writeValue("gamma", gamma);
        json.writeValue("renderCount", renderCount);
        json.writeValue("borderWidth", borderWidth);
        json.writeValue("borderColor", borderColor);
        json.writeValue("borderStraight", borderStraight);
        json.writeValue("borderGamma", borderGamma);
        json.writeValue("shadowOffsetX", shadowOffsetX);
        json.writeValue("shadowOffsetY", shadowOffsetY);
        json.writeValue("shadowColor", shadowColor);
        json.writeValue("spaceX", spaceX);
        json.writeValue("spaceY", spaceY);
        json.writeValue("characters", characters);
        json.writeValue("kerning", kerning);
        json.writeValue("flip", flip);
        json.writeValue("genMipMaps", genMipMaps);
        json.writeValue("minFilter", minFilter);
        json.writeValue("magFilter", magFilter);
        json.writeValue("incremental", incremental);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        name = jsonData.getString("name");
        file = jsonData.has("file") ? Gdx.files.absolute(jsonData.getString("file")) : null;
        previewTTF = jsonData.getString("previewTTF");
        useCustomSerializer = jsonData.getBoolean("useCustomSerialized", false);
        size = jsonData.getInt("size", 16);
        mono = jsonData.getBoolean("mono");
        hinting = jsonData.getString("hinting", "AutoMedium");
        color = jsonData.getString("color");
        gamma = jsonData.getFloat("gamma", 1.8f);
        renderCount = jsonData.getInt("renderCount", 2);
        borderWidth = jsonData.getFloat("borderWidth", 0);
        borderColor = jsonData.getString("borderColor");
        borderStraight = jsonData.getBoolean("borderStraight", false);
        borderGamma = jsonData.getFloat("borderGamma", 1.8f);
        shadowOffsetX = jsonData.getInt("shadowOffsetX", 0);
        shadowOffsetY = jsonData.getInt("shadowOffsetY", 0);
        shadowColor = jsonData.getString("shadowColor");
        spaceX = jsonData.getInt("spaceX");
        spaceY = jsonData.getInt("spaceY");
        characters = jsonData.getString("characters", "");
        kerning = jsonData.getBoolean("kerning", true);
        flip = jsonData.getBoolean("flip", false);
        genMipMaps = jsonData.getBoolean("genMipMaps", false);
        minFilter = jsonData.getString("minFilter", "Nearest");
        magFilter = jsonData.getString("magFilter", "Nearest");
        incremental = jsonData.getBoolean("incremental");
    }
}
