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
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.ObjectMap;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.dialog.DialogSettings.ExportFormat;
import java.util.Iterator;

public class ProjectData implements Json.Serializable {
    private static Preferences generalPref;
    private ObjectMap<String, Object> preferences;
    private FileHandle saveFile;
    private boolean changesSaved;
    private boolean newProject;
    private static final int MAX_RECENT_FILES = 5;
    private Main main;
    private final JsonData jsonData;
    private final AtlasData atlasData;
    
    public ProjectData() {
        jsonData = new JsonData();
        atlasData = new AtlasData();
        
        changesSaved = false;
        newProject = true;
        preferences = new ObjectMap<>();
        generalPref = Gdx.app.getPreferences("com.ray3k.skincomposer");
        clear();
    }

    public void setMain(Main main) {
        this.main = main;
        atlasData.setMain(main);
        jsonData.setMain(main);
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
    
    public Array<RecentFile> getRecentFiles() {
        Array<RecentFile> returnValue = new Array<>();
        int maxIndex = Math.min(MAX_RECENT_FILES, generalPref.getInteger("recentFilesCount", 0));
        for (int i = 0; i < maxIndex; i++) {
            String path = generalPref.getString("recentFile" + i);
            FileHandle file = new FileHandle(path);
            RecentFile recentFile = new RecentFile();
            recentFile.fileHandle = file;
            recentFile.name = file.nameWithoutExtension();
            if (file.exists()) {
                returnValue.add(recentFile);
            }
        }
        
        return returnValue;
    }
    
    public static class RecentFile {
        private String name;
        private FileHandle fileHandle;

        public FileHandle getFileHandle() {
            return fileHandle;
        }
        
        @Override
        public String toString() {
            return name;
        }
    }
    
    public void putRecentFile(String filePath) {
        Array<RecentFile> recentFiles = getRecentFiles();
        Iterator<RecentFile> iter = recentFiles.iterator();
        while(iter.hasNext()) {
            RecentFile recentFile = iter.next();
            if (recentFile.fileHandle.toString().equals(filePath)) {
                iter.remove();
            }
        }
        RecentFile newFile = new RecentFile();
        newFile.fileHandle = new FileHandle(filePath);
        newFile.name = newFile.fileHandle.nameWithoutExtension();

        recentFiles.add(newFile);
        while (recentFiles.size > MAX_RECENT_FILES) {
            recentFiles.removeIndex(0);
        }
        
        int size = Math.min(MAX_RECENT_FILES, recentFiles.size);
        generalPref.putInteger("recentFilesCount", size);
        
        for (int i = 0; i < size; i++) {
            RecentFile recentFile = recentFiles.get(i);
            generalPref.putString("recentFile" + i, recentFile.fileHandle.toString());
        }
        generalPref.flush();
        
        main.getRootTable().setRecentFilesDisabled(false);
    }
    
    public void setMaxUndos(int maxUndos) {
        preferences.put("maxUndos", maxUndos);
    }
    
    public int getMaxUndos() {
        return (int) preferences.get("maxUndos", 30);
    }
    
    public void setAllowingWelcome(boolean allow) {
        generalPref.putBoolean("allowingWelcome", allow);
        generalPref.flush();
    }
    
    public boolean isAllowingWelcome() {
        return generalPref.getBoolean("allowingWelcome", true);
    }
    
    public void setCheckingForUpdates(boolean allow) {
        generalPref.putBoolean("checkForUpdates", allow);
        generalPref.flush();
    }
    
    public boolean isCheckingForUpdates() {
        return generalPref.getBoolean("checkForUpdates", true);
    }
    
    public void setExportFormat(ExportFormat exportFormat) {
        generalPref.putString("exportFormat", exportFormat.toString());
    }
    
    public ExportFormat getExportFormat() {
        ExportFormat returnValue = ExportFormat.MINIMAL;
        String name = generalPref.getString("exportFormat");
        
        if (name != null) {
            for (ExportFormat exportFormat : ExportFormat.values()) {
                if (exportFormat.toString().equals(name)) {
                    returnValue = exportFormat;
                    break;
                }
            }
        }
        
        return returnValue;
    }
    
    public FileHandle getSaveFile() {
        return saveFile;
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
    
    private void moveImportedFiles(FileHandle oldSave, FileHandle newSave) {
        FileHandle tempImportFolder = Main.appFolder.child("temp/" + getId() + "_data/");
        FileHandle localImportFolder;
        if (oldSave != null) {
            localImportFolder = oldSave.sibling(oldSave.nameWithoutExtension() + "_data/");
        } else {
            localImportFolder = null;
        }
        FileHandle targetFolder = newSave.sibling(newSave.nameWithoutExtension() + "_data/");
        
        for (DrawableData drawableData : atlasData.getDrawables()) {
            if (drawableData.file != null && drawableData.file.exists()) {
                targetFolder.mkdirs();
                //drawable files in the temp folder
                if (drawableData.file.parent().equals(tempImportFolder)) {
                    drawableData.file.moveTo(targetFolder);
                    drawableData.file = targetFolder.child(drawableData.file.name());
                }
                //drawable files in the folder next to the old save
                else if (localImportFolder != null && !localImportFolder.equals(targetFolder) && drawableData.file.parent().equals(localImportFolder)) {
                    drawableData.file.copyTo(targetFolder);
                    drawableData.file = targetFolder.child(drawableData.file.name());
                }
            }
        }
        
        for (FontData fontData : jsonData.getFonts()) {
            if (fontData.file.exists()) {
                targetFolder.mkdirs();
                
                //font files in the temp folder
                if (fontData.file.parent().equals(tempImportFolder)) {
                    fontData.file.moveTo(targetFolder);
                    fontData.file = targetFolder.child(fontData.file.name());
                }
                //font files in the data folder next to the old save
                else if (localImportFolder != null && !localImportFolder.equals(targetFolder) && fontData.file.parent().equals(localImportFolder)) {
                    fontData.file.copyTo(targetFolder);
                    fontData.file = targetFolder.child(fontData.file.name());
                }
            }
        }
        
        for (FreeTypeFontData fontData : jsonData.getFreeTypeFonts()) {
            if (fontData.file != null && fontData.file.exists()) {
                targetFolder.mkdirs();
                
                //font files in the temp folder
                if (fontData.file.parent().equals(tempImportFolder)) {
                    fontData.file.moveTo(targetFolder);
                    fontData.file = targetFolder.child(fontData.file.name());
                }
                //font files in the data folder next to the old save
                else if (localImportFolder != null && !localImportFolder.equals(targetFolder) && fontData.file.parent().equals(localImportFolder)) {
                    fontData.file.copyTo(targetFolder);
                    fontData.file = targetFolder.child(fontData.file.name());
                }
            }
        }
    }
    
    public void makeResourcesRelative(FileHandle saveFile) {
        FileHandle targetFolder = saveFile.sibling(saveFile.nameWithoutExtension() + "_data/");
        
        for (DrawableData drawableData : main.getAtlasData().getDrawables()) {
            if (drawableData.file.exists() && !targetFolder.equals(drawableData.file.parent())) {
                targetFolder.mkdirs();
                drawableData.file.copyTo(targetFolder);
                drawableData.file = targetFolder.child(drawableData.file.name());
            }
        }
        
        for (FontData fontData : main.getJsonData().getFonts()) {
            if (fontData.file.exists() && !targetFolder.equals(fontData.file.parent())) {
                fontData.file.copyTo(targetFolder);
                fontData.file = targetFolder.child(fontData.file.name());
            }
        }
        
        for (FreeTypeFontData fontData : main.getJsonData().getFreeTypeFonts()) {
            if (fontData.file != null && fontData.file.exists() && !targetFolder.equals(fontData.file.parent())) {
                fontData.file.copyTo(targetFolder);
                fontData.file = targetFolder.child(fontData.file.name());
            }
        }
    }
    
    public void makeResourcesRelative() {
        makeResourcesRelative(saveFile);
    }
    
    public void save(FileHandle file) {
        moveImportedFiles(saveFile, file);
        
        if (main.getProjectData().areResourcesRelative()) {
            makeResourcesRelative(file);
        }
        
        saveFile = file;
        putRecentFile(file.path());
        Json json = new Json(JsonWriter.OutputType.minimal);
        json.setUsePrototypes(false);
        file.writeString(json.prettyPrint(this), false, "UTF8");
        setChangesSaved(true);
    }
    
    public void save() {
        save(saveFile);
    }
    
    public void load(FileHandle file) {
        Json json = new Json(JsonWriter.OutputType.minimal);
        ProjectData instance = json.fromJson(ProjectData.class, file.reader("UTF8"));
        newProject = instance.newProject;
        jsonData.set(instance.jsonData);
        for (FreeTypeFontData font : jsonData.getFreeTypeFonts()) {
            font.createBitmapFont(main);
        }
        atlasData.set(instance.atlasData);
        preferences.clear();
        preferences.putAll(instance.preferences);
        
        //set main for custom classes, styles, and properties
        for (CustomClass customClass : jsonData.getCustomClasses()) {
            customClass.setMain(main);
        }
        
        saveFile = file;
        putRecentFile(file.path());
        setLastOpenSavePath(file.parent().path() + "/");
        atlasData.atlasCurrent = false;
        
        correctFilePaths();
        
        if (verifyDrawablePaths().size == 0 && verifyFontPaths().size == 0) {
            main.getRootTable().produceAtlas();
            main.getRootTable().populate();
        }
        setChangesSaved(true);
    }
    
    /**
     * Checks every drawable path for existence. Errors are reported as a list
     * of DrawableDatas.
     * @return A list of all DrawableDatas that must have their paths resolved.
     * Returns an empty list if there are no errors.
     */
    public Array<DrawableData> verifyDrawablePaths() {
        Array<DrawableData> errors = new Array<>();
        
        if (!areResourcesRelative()) {
            for (DrawableData drawable : atlasData.getDrawables()) {
                if (!drawable.customized && (drawable.file == null || !drawable.file.exists())) {
                    errors.add(drawable);
                }
            }
        } else {
            FileHandle targetFolder = saveFile.sibling(saveFile.nameWithoutExtension() + "_data/");
            
            for (DrawableData drawable : atlasData.getDrawables()) {
                if (!drawable.customized) {
                    if (drawable.file == null) {
                        errors.add(drawable);
                    } else {
                        FileHandle localFile = targetFolder.child(drawable.file.name());
                        if (!localFile.exists()) {
                            errors.add(drawable);
                        }
                    }
                }
            }
        }
        return errors;
    }
    
    public Array<FontData> verifyFontPaths() {
        Array<FontData> errors = new Array<>();
        
        if (!areResourcesRelative()) {
            for (FontData font : jsonData.getFonts()) {
                if (font.file == null || !font.file.exists()) {
                    errors.add(font);
                }
            }
        } else {
            FileHandle targetFolder = saveFile.sibling(saveFile.nameWithoutExtension() + "_data/");
            
            for (FontData font : jsonData.getFonts()) {
                if (font.file == null) {
                    errors.add(font);
                } else {
                    FileHandle localFile = targetFolder.child(font.file.name());
                    if (!localFile.exists()) {
                        errors.add(font);
                    }
                }
            }
        }
        return errors;
    }
    
    private void correctFilePaths() {
        FileHandle targetFolder = saveFile.sibling(saveFile.nameWithoutExtension() + "_data/");
        
        boolean resourcesRelative = main.getProjectData().areResourcesRelative();
        
        if (targetFolder.exists()) {
            for (DrawableData drawableData : atlasData.getDrawables()) {
                if (resourcesRelative || drawableData.file != null && !drawableData.file.exists()) {
                    FileHandle newFile = targetFolder.child(drawableData.file.name());
                    if (newFile.exists()) {
                        drawableData.file = newFile;
                    }
                }
            }
            
            for (FontData fontData : jsonData.getFonts()) {
                if (resourcesRelative || !fontData.file.exists()) {
                    FileHandle newFile = targetFolder.child(fontData.file.name());
                    if (newFile.exists()) {
                        fontData.file = newFile;
                    }
                }
            }
        }
    }
    
    public void load() {
        load(saveFile);
    }
    
    public void clear() {
        preferences.clear();

        randomizeId();
        setMaxUndos(30);
        setResourcesRelative(false);
        
        jsonData.clear();
        atlasData.clear();
        saveFile = null;
        if (main != null) {
            main.getRootTable().produceAtlas();
            main.getRootTable().populate();
        }
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
    public void read(Json json, JsonValue jsonValue) {
        preferences = json.readValue("preferences", ObjectMap.class, jsonValue);
        jsonData.set(json.readValue("jsonData", JsonData.class, jsonValue));
        atlasData.set(json.readValue("atlasData", AtlasData.class, jsonValue));
        if (!jsonValue.get("saveFile").isNull()) {
            saveFile = new FileHandle(jsonValue.getString("saveFile"));
        }
    }

    public JsonData getJsonData() {
        return jsonData;
    }

    public AtlasData getAtlasData() {
        return atlasData;
    }

    public String getLastOpenSavePath() {

        return (String) generalPref.getString("last-open-save-path",
                generalPref.getString("last-path",
                        Gdx.files.getLocalStoragePath()));
    }

    public void setLastOpenSavePath(String openSavePath) {
        generalPref.putString("last-open-save-path", openSavePath);
        generalPref.flush();

        setLastPath(openSavePath);
    }

    public String getLastImportExportPath() {
        return (String) generalPref.getString("last-import-export-path",
                generalPref.getString("last-path",
                        Gdx.files.getLocalStoragePath()));
    }

    public void setLastImportExportPath(String importExportPath) {
        generalPref.putString("last-import-export-path", importExportPath);
        generalPref.flush();

        setLastPath(importExportPath);
    }

    public String getLastFontPath() {
        return (String) generalPref.getString("last-font-path",
                generalPref.getString("last-path",
                        Gdx.files.getLocalStoragePath()));
    }

    public void setLastFontPath(String fontPath) {
        generalPref.putString("last-font-path", fontPath);
        generalPref.flush();

        setLastPath(fontPath);
    }

    public String getLastDrawablePath() {
        return (String) generalPref.getString("last-drawable-path",
                generalPref.getString("last-path",
                        Gdx.files.getLocalStoragePath()));
    }

    public void setLastDrawablePath(String drawablePath) {
        generalPref.putString("last-drawable-path", drawablePath);
        generalPref.flush();

        setLastPath(drawablePath);
    }

    public String getLastPath() {
        return (String) generalPref.getString("last-path",
                Gdx.files.getLocalStoragePath());
    }
    
    public void setLastPath(String lastPath) {
        generalPref.putString("last-path", lastPath);
        generalPref.flush();
    }
    
    public boolean areResourcesRelative() {
        return (boolean) preferences.get("resources-relative", false);
    }
    
    public void setResourcesRelative(boolean resourcesRelative) {
        preferences.put("resources-relative", resourcesRelative);
    }
    
    /**
     * Returns true if file exists and depending on the state of relative resources
     * and save file state.
     * @param file
     * @return 
     */
    public boolean resourceExists(FileHandle file) {
        FileHandle targetDirectory = (saveFile != null) ? saveFile.sibling(saveFile.nameWithoutExtension() + "_data/") : Main.appFolder.child("temp/" + getId() + "_data/");
        if (!areResourcesRelative()) {
            if (!file.exists() && !targetDirectory.child(file.name()).exists()) {
                return false;
            }
        } else {
            if (targetDirectory == null || !targetDirectory.child(file.name()).exists()) {
                return false;
            }
        }
        return true;
    }
}
