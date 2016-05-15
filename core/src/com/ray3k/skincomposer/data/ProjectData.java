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
package com.ray3k.skincomposer.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.ObjectMap;
import com.ray3k.skincomposer.panel.PanelClassBar;
import com.ray3k.skincomposer.panel.PanelPreviewProperties;
import com.ray3k.skincomposer.panel.PanelStyleProperties;

public class ProjectData implements Json.Serializable{
    private static ProjectData instance;
    private static Preferences generalPref;
    private ObjectMap<String, Object> preferences;
    private JsonData jsonData;
    private AtlasData atlasData;
    private FileHandle saveFile;
    private boolean changesSaved;
    private boolean newProject;
    
    public static ProjectData instance() {
        if (instance == null) {
            instance = new ProjectData();
        }
        return instance;
    }
    
    private ProjectData() {
        changesSaved = false;
        newProject = true;
        preferences = new ObjectMap<>();
        generalPref = Gdx.app.getPreferences("com.ray3k.skincomposer");
        jsonData = JsonData.getInstance();
        atlasData = AtlasData.getInstance();
    }
    
    public int getId() {
        return (int) preferences.get("id");
    }
    
    public void setId(int id) {
        preferences.put("id", id);
    }
    
    public void randomizeId() {
        int id = MathUtils.random(100000000, 999999999);
        setId(id);
    }
    
    public void setLastDirectory(String lastDirectory) {
        preferences.put("last-directory", lastDirectory);
        generalPref.putString("last-directory", lastDirectory);
        generalPref.flush();
    }
    
    public String getLastDirectory() {
        return (String) preferences.get("last-directory", generalPref.getString("last-directory", null));
    }
    
    public void setMaxTextureDimensions(int width, int height) {
        preferences.put("texture-max-width", width);
        preferences.put("texture-max-height", height);
    }
    
    public int getMaxTextureWidth() {
        return (int) preferences.get("texture-max-width", 1024);
    }
    
    public int getMaxTextureHeight() {
        return (int) preferences.get("texture-max-height", 1024);
    }
    
    public void setMaxUndos(int maxUndos) {
        preferences.put("maxUndos", maxUndos);
    }
    
    public int getMaxUndos() {
        return (int) preferences.get("maxUndos", 30);
    }
    
    public FileHandle getSaveFile() {
        return saveFile;
    }
    
    public String getBestSaveDirectory() {
        if (saveFile != null) {
            return saveFile.parent().path();
        } else if (generalPref.contains("last-save-directory")) {
            return generalPref.getString("last-save-directory");
        } else {
            return getLastDirectory();
        }
    }

    public boolean areChangesSaved() {
        return changesSaved;
    }

    public void setChangesSaved(boolean changesSaved) {
        this.changesSaved = changesSaved;
        newProject = false;
        String title = "Skin Composer";
        if (saveFile != null && saveFile.exists()) {
            title += " - " + saveFile.nameWithoutExtension();
            if (!changesSaved) {
                title += "*";
            }
        } else {
            title += " - New Project*";
        }
        Gdx.graphics.setTitle(title);
    }

    public boolean isNewProject() {
        return newProject;
    }
    
    public void save(FileHandle file) {
        saveFile = file;
        generalPref.putString("last-save-directory", file.parent().path());
        generalPref.flush();
        Json json = new Json(JsonWriter.OutputType.minimal);
        json.setUsePrototypes(false);
        file.writeString(json.prettyPrint(this), false);
        setChangesSaved(true);
    }
    
    public void save() {
        save(saveFile);
    }
    
    public void load(FileHandle file) {
        Json json = new Json(JsonWriter.OutputType.minimal);
        instance = json.fromJson(ProjectData.class, file);
        instance.saveFile = file;
        generalPref.putString("last-save-directory", file.parent().path());
        generalPref.flush();
        PanelClassBar.instance.populate();
        PanelStyleProperties.instance.populate(PanelClassBar.instance.getStyleSelectBox().getSelected());
        AtlasData.getInstance().atlasCurrent = false;
        PanelPreviewProperties.instance.produceAtlas();
        PanelPreviewProperties.instance.render();
        instance.setChangesSaved(true);
    }
    
    public void load() {
        load(saveFile);
    }
    
    public void clear() {
        preferences.clear();

        randomizeId();
        setMaxTextureDimensions(1024, 1024);
        setMaxUndos(30);
        
        jsonData.clear();
        atlasData.clear();
        saveFile = null;
        PanelClassBar.instance.populate();
        PanelStyleProperties.instance.populate(PanelClassBar.instance.getStyleSelectBox().getSelected());
        PanelPreviewProperties.instance.produceAtlas();
        PanelPreviewProperties.instance.render();
        setChangesSaved(false);
        newProject = true;
    }

    @Override
    public void write(Json json) {
        json.writeValue("atlasData", atlasData);
        json.writeValue("jsonData", jsonData);
        json.writeValue("preferences", preferences);
        if (saveFile != null) {
            json.writeValue("saveFile", saveFile.path());
        } else {
            json.writeValue("saveFile", (String) null);
        }
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        preferences = json.readValue("preferences", ObjectMap.class, jsonData);
        JsonData.loadInstance(json.readValue("jsonData", JsonData.class, jsonData));
        this.jsonData = JsonData.getInstance();
        AtlasData.loadInstance(json.readValue("atlasData", AtlasData.class, jsonData));
        this.atlasData = AtlasData.getInstance();
        if (!jsonData.get("saveFile").isNull()) {
            saveFile = new FileHandle(jsonData.getString("saveFile"));
        }
    }
}
