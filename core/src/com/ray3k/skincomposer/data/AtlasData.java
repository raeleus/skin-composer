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
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.utils.Utils;
import java.io.FileNotFoundException;

public class AtlasData {
    private static AtlasData instance;
    private Array<DrawableData> drawables;
    
    private AtlasData() {
        drawables = new Array<DrawableData>();
    }
    
    public static AtlasData getInstance() {
        if (instance == null) {
            instance = new AtlasData();
        }
        return instance;
    }
    
    public void clear() {
        drawables.clear();
    }
    
    public static void loadInstance(AtlasData instance) {
        AtlasData.instance = instance;
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
            FileHandle saveFile = ProjectData.instance().getSaveFile();
            FileHandle targetDirectory = saveFile.sibling(saveFile.nameWithoutExtension() + "_data");
            targetDirectory.mkdirs();
            
            TextureAtlas atlas = new TextureAtlas(fileHandle);
            Array<AtlasRegion> regions = atlas.getRegions();
            
            for (AtlasRegion region : regions) {
                Texture texture = region.getTexture();
                if (!texture.getTextureData().isPrepared()) {
                    texture.getTextureData().prepare();
                }
                Pixmap.setBlending(Pixmap.Blending.None);
                Pixmap pixmap = texture.getTextureData().consumePixmap();
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
                drawables.add(drawable);
            }
            
            
        } else {
            throw new FileNotFoundException();
        }
    }
    
    public void writeAtlas() throws Exception {
        FileHandle targetFile = Gdx.files.local("temp/" + ProjectData.instance().getId() + ".atlas");
        targetFile.parent().mkdirs();
        targetFile.parent().emptyDirectory();
        
        Array<FileHandle> files = new Array<FileHandle>();
        for (DrawableData drawable : drawables) {
            if (!files.contains(drawable.file, false)) {
                files.add(drawable.file);
            }
        }
        
        Main.instance.getDesktopWorker().texturePack(files, targetFile, ProjectData.instance().getMaxTextureWidth(), ProjectData.instance().getMaxTextureHeight());
    }
    
    public void writeAtlas(FileHandle targetFile) throws Exception {
        targetFile.parent().mkdirs();
        
        Array<FileHandle> files = new Array<FileHandle>();
        for (DrawableData drawable : drawables) {
            if (!files.contains(drawable.file, false)) {
                files.add(drawable.file);
            }
        }
        
        Main.instance.getDesktopWorker().texturePack(files, targetFile, ProjectData.instance().getMaxTextureWidth(), ProjectData.instance().getMaxTextureHeight());
    }
    
    public TextureAtlas getAtlas() {
        TextureAtlas atlas = null;
        FileHandle atlasFile = Gdx.files.local("temp/" + ProjectData.instance().getId() + ".atlas");
        if (atlasFile.exists()) {
            atlas = new TextureAtlas(atlasFile);
        }
        return atlas;
    }
    
    public void clearTempData() {
        FileHandle tempFolder = Gdx.files.local("temp/");
        tempFolder.deleteDirectory();
    }
}