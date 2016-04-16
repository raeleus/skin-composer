package com.ray3k.skincomposer.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.Main;
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
            FileHandle targetDirectory = Gdx.files.local("imported/" + ProjectData.instance().getId() + "/");
            targetDirectory.mkdirs();
            targetDirectory.emptyDirectory();
            
            //to do: copy images from atlas to this folder
            String text = fileHandle.toString();
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