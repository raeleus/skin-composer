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
    
    public static ProjectData instance() {
        if (instance == null) {
            instance = new ProjectData();
        }
        return instance;
    }
    
    private ProjectData() {
        preferences = new ObjectMap<>();
        generalPref = Gdx.app.getPreferences("com.ray3k.skincomposer");
        jsonData = JsonData.getInstance();
        atlasData = AtlasData.getInstance();
    }
    
    public String getName() {
        return (String) preferences.get("name");
    }
    
    public void setName(String name) {
        preferences.put("name", name);
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
    
    public void save(FileHandle file) {
        saveFile = file;
        generalPref.putString("last-save-directory", file.parent().path());
        generalPref.flush();
        Json json = new Json(JsonWriter.OutputType.minimal);
        json.setUsePrototypes(false);
        file.writeString(json.prettyPrint(this), false);
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
        PanelPreviewProperties.instance.produceAtlas();
        PanelPreviewProperties.instance.render();
    }
    
    public void load() {
        load(saveFile);
    }
    
    public void clear() {
        preferences.clear();
        jsonData.clear();
        atlasData.clear();
        saveFile = null;
        PanelClassBar.instance.populate();
        PanelStyleProperties.instance.populate(PanelClassBar.instance.getStyleSelectBox().getSelected());
        PanelPreviewProperties.instance.produceAtlas();
        PanelPreviewProperties.instance.render();
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
