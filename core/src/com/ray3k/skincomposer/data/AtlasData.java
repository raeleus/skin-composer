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
package com.ray3k.skincomposer.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ray3k.skincomposer.Main;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;

public class AtlasData implements Json.Serializable {
    public boolean atlasCurrent = false;
    private Array<DrawableData> drawables;
    private Main main;
    
    public AtlasData() {
        drawables = new Array<>();
    }

    public void setMain(Main main) {
        this.main = main;
    }
    
    public void clear() {
        drawables.clear();
        atlasCurrent = false;
    }

    public Array<DrawableData> getDrawables() {
        return drawables;
    }
    
    public DrawableData getDrawable(String name) {
        DrawableData returnValue = null;
        for (DrawableData data : drawables) {
            if (data.name.equals(name)) {
                returnValue = data;
                break;
            }
        }
        
        return returnValue;
    }
    
    public void readAtlas(FileHandle fileHandle) throws Exception {
        if (fileHandle.exists()) {
            FileHandle saveFile = main.getProjectData().getSaveFile();
            FileHandle targetDirectory;
            if (saveFile != null) {
                targetDirectory = saveFile.sibling(saveFile.nameWithoutExtension() + "_data/");
            } else {
                targetDirectory = Gdx.files.local("temp/" + main.getProjectData().getId() + "_data/");
            }
            
            targetDirectory.mkdirs();
            
            TextureAtlas atlas = new TextureAtlas(fileHandle);
            Array<AtlasRegion> regions = atlas.getRegions();
            
            for (AtlasRegion region : regions) {
                Texture texture = region.getTexture();
                if (!texture.getTextureData().isPrepared()) {
                    texture.getTextureData().prepare();
                }
                
                Pixmap pixmap = texture.getTextureData().consumePixmap();
                pixmap.setBlending(Pixmap.Blending.None);
                Pixmap savePixmap;
                String name;
                
                if (region.splits == null && region.pads == null) {
                    name = region.name + ".png";
                    savePixmap = new Pixmap(region.getRegionWidth(), region.getRegionHeight(), Pixmap.Format.RGBA8888);
                    for (int x = 0; x < region.getRegionWidth(); x++) {
                        for (int y = 0; y < region.getRegionHeight(); y++) {
                            int colorInt = pixmap.getPixel(region.getRegionX() + x, region.getRegionY() + y);
                            savePixmap.drawPixel(x, y, colorInt);
                        }
                    }
                } else {
                    name = region.name + ".9.png";
                    savePixmap = new Pixmap(region.getRegionWidth() + 2, region.getRegionHeight() + 2, pixmap.getFormat());
                    int x;
                    int y;
                    
                    //draw 9 patch lines
                    savePixmap.setColor(Color.BLACK);

                    if (region.splits != null) {
                        x = 0;
                        for (y = region.splits[2] + 1; y < savePixmap.getHeight() - region.splits[3] - 1; y++) {
                            savePixmap.drawPixel(x, y);
                        }
                        
                        y = 0;
                        for (x = region.splits[0] + 1; x < savePixmap.getWidth() - region.splits[1] - 1; x++) {
                            savePixmap.drawPixel(x, y);
                        }
                    }
                    
                    if (region.pads != null) {
                        x = savePixmap.getWidth() - 1;
                        for (y = region.pads[2] + 1; y < savePixmap.getHeight() - region.pads[3] - 1; y++) {
                            savePixmap.drawPixel(x, y);
                        }
                        
                        y = savePixmap.getHeight() - 1;
                        for (x = region.pads[0] + 1; x < savePixmap.getWidth() - region.pads[1] - 1; x++) {
                            savePixmap.drawPixel(x, y);
                        }
                    }

                    for (x = 0; x < region.getRegionWidth(); x++) {
                        for (y = 0; y < region.getRegionHeight(); y++) {
                            int colorInt = pixmap.getPixel(region.getRegionX() + x, region.getRegionY() + y);
                            savePixmap.drawPixel(x + 1, y + 1, colorInt);
                        }
                    }
                }
                FileHandle outputFile = targetDirectory.child(name);
                PixmapIO.writePNG(outputFile, savePixmap);
                DrawableData drawable = new DrawableData(outputFile);
                
                //delete drawables with the same name
                for (DrawableData originalData : new Array<>(main.getProjectData().getAtlasData().getDrawables())) {
                    if (originalData.name.equals(drawable.name)) {
                        main.getProjectData().getAtlasData().getDrawables().removeValue(originalData, true);
                    }
                }
                    
                drawables.add(drawable);
            }
            
            
        } else {
            throw new FileNotFoundException();
        }
    }
    
    public void writeAtlas() throws Exception {
        FileHandle targetFile = Gdx.files.local("temp/" + main.getProjectData().getId() + ".atlas");
        targetFile.parent().mkdirs();
        FileHandle[] oldFiles = targetFile.parent().list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String string) {
                return string.matches(targetFile.nameWithoutExtension() + "\\d*\\.(?i)png");
            }
        });
        for (FileHandle fileHandle : oldFiles) {
            fileHandle.delete();
        }
        targetFile.sibling(targetFile.nameWithoutExtension() + ".atlas").delete();
        
        Array<FileHandle> files = new Array<>();
        for (DrawableData drawable : drawables) {
            if (!drawable.customized && !files.contains(drawable.file, false)) {
                files.add(drawable.file);
            }
        }
        
        main.getDesktopWorker().texturePack(files, main.getProjectData().getSaveFile(), targetFile);
    }
    
    public Array<String> writeAtlas(FileHandle targetFile) throws Exception {
        Array<String> warnings = new Array<>();
        targetFile.parent().mkdirs();
        FileHandle[] oldFiles = targetFile.parent().list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String string) {
                return string.matches(targetFile.nameWithoutExtension() + "\\d*\\.(?i)png");
            }
        });
        for (FileHandle fileHandle : oldFiles) {
            fileHandle.delete();
        }
        targetFile.sibling(targetFile.nameWithoutExtension() + ".atlas").delete();
        
        Array<FileHandle> files = new Array<>();
        for (DrawableData drawable : drawables) {
            if (!drawable.customized) {
                if (!files.contains(drawable.file, false)) {
                    files.add(drawable.file);
                }

                if (!main.getProjectData().resourceExists(drawable.file)) {
                    warnings.add("[RED]ERROR:[] Drawable file [BLACK]" + drawable.file + "[] does not exist.");
                }
            }
        }
        
        main.getDesktopWorker().texturePack(files, main.getProjectData().getSaveFile(), targetFile);
        return warnings;
    }
    
    public TextureAtlas getAtlas() {
        TextureAtlas atlas = null;
        FileHandle atlasFile = Gdx.files.local("temp/" + main.getProjectData().getId() + ".atlas");
        if (atlasFile.exists()) {
            atlas = new TextureAtlas(atlasFile);
        }
        return atlas;
    }
    
    public void clearTempData() {
        FileHandle tempFolder = Gdx.files.local("temp/");
        tempFolder.deleteDirectory();
    }
    
    public void set(AtlasData atlasData) {
        drawables.clear();
        drawables.addAll(atlasData.drawables);
    }

    @Override
    public void write(Json json) {
        json.writeValue("atlasCurrent", atlasCurrent);
        json.writeValue("drawables", drawables, Array.class, DrawableData.class);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        atlasCurrent = json.readValue("atlasCurrent", Boolean.TYPE, jsonData);
        drawables = json.readValue("drawables", Array.class, DrawableData.class, jsonData);
    }
}