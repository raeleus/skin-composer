/*
 * The MIT License
 *
 * Copyright (c) 2024 Raymond Buckley.
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
package com.ray3k.skincomposer.uninstaller;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.PropertiesUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

public class Core extends ApplicationAdapter {
    private Skin skin;
    private Stage stage;
    private static Table root;
    public static DesktopWorker desktopWorker;
    private int dragStartX, dragStartY;
    private int windowStartX, windowStartY;
    private ObjectMap<String, String> properties;
    
    @Override
    public void create() {
        //read properties file
        properties = new ObjectMap<>();
        try {
            PropertiesUtils.load(properties, Gdx.files.internal("values.properties").reader());
        } catch (IOException e) {
            Gdx.app.error(getClass().getName(), "Error reading installer properties file.", e);
        }
        
        //create user interface
        skin = new Skin(Gdx.files.internal("ui/skin-composer-installer-ui.json"));
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        
        root = new Table();
        root.pad(10.0f);
        root.setFillParent(true);
        root.setTouchable(Touchable.enabled);
        root.setName("root");
        stage.addActor(root);
        
        //Add drag listener to move window
        stage.addListener(new DragListener() {
            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                if (event.getTarget() == root) {
                    desktopWorker.dragWindow(windowStartX + (int) x - dragStartX, windowStartY + dragStartY - (int) y);
                    windowStartX = desktopWorker.getWindowX();
                    windowStartY = desktopWorker.getWindowY();
                }
            }

            @Override
            public void dragStart(InputEvent event, float x, float y, int pointer) {
                dragStartX = (int) x;
                dragStartY = (int) y;
                windowStartX = desktopWorker.getWindowX();
                windowStartY = desktopWorker.getWindowY();
            }
        });
        
        root.defaults().space(6);
        var label = new Label(properties.get("product-name"), skin, "small");
        label.setTouchable(Touchable.disabled);
        root.add(label).expandX().left();
        
        root.row();
        var button = new TextButton("Uninstall", skin);
        root.add(button).grow();
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                performUninstall();
            }
        });
        
        root.row();
        button = new TextButton("Quit", skin);
        root.add(button).height(30).growX();
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(26 /255.0f, 26 /255.0f, 26 /255.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
    
    private void performUninstall() {
        //get app installation path
        var src = getClass().getProtectionDomain().getCodeSource();
        var jar = src.getLocation();
        try {
            var installationPath = new FileHandle(Paths.get(jar.toURI()).toString()).parent().parent();
            var uninstallFile = installationPath.child("uninstall");
            //Add delay to batch file so Java can exit completely
            var batchLines = "@echo Uninstalling. Please wait...\n@cd \\\n@ping localhost -n 6 > nul\n";

            if (uninstallFile.exists()) {
                //erase uninstaller registry values
                try {
                    var process = Runtime.getRuntime().exec("cmd /c REG DELETE HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\"
                            + properties.get("product-name").replace(' ', '_') + " /f");
                    process.waitFor();

                    //erase file association values
                    final var longFileExt = properties.get("product-name").replace(' ', '_')
                            + "." + properties.get("file-association-extension")
                            + "." + properties.get("file-association-version");
                    process = Runtime.getRuntime().exec("cmd /c REG DELETE HKCU\\Software\\Classes\\" + longFileExt + " /f");
                    process.waitFor();

                    process = Runtime.getRuntime().exec("cmd /c REG DELETE HKCU\\Software\\Classes\\." + properties.get("file-association-extension") + " /f");
                    process.waitFor();
                } catch (InterruptedException e) {
                    Gdx.app.error(getClass().getName(), "Failed to uninstall registry values.", e);
                }

                //gather all files to be uninstalled, leaving behind user files
                var lines = uninstallFile.readString().split("\n");
                var directoriesSet = new ObjectSet<String>();
                for (var line : lines) {
                    var file = new FileHandle(line);
                    batchLines += "del \"" + file.path().replace('/', '\\') + "\"\n";

                    //only include subdirectories of the installation path
                    if (isChildOf(file.parent(), installationPath)) {
                        directoriesSet.add(file.parent().path());
                    }
                }

                //include all paths up to the installation path
                for (var path : new ObjectSet<String>(directoriesSet)) {
                    var parent = Gdx.files.absolute(path);
                    parent = parent.parent();
                    while (isChildOf(parent, installationPath)) {
                        directoriesSet.add(parent.path());
                        parent = parent.parent();
                    }
                }

                //sort the directories so that inner most paths are deleted first
                var deleteDirectories = new Array<String>();
                for (var path : directoriesSet) {
                    deleteDirectories.add(path);
                }
                deleteDirectories.sort();
                deleteDirectories.reverse();

                //delete the directories
                for (var path : deleteDirectories) {
                    batchLines += "rmdir \"" + path.replace('/', '\\') + "\"\n";
                }

                //Add closing message to batch file
                batchLines += "@cls\n@echo Uninstallation Complete.\n@ping localhost -n 4 > nul";

                //generate batch file and execute
                var batchFile = Gdx.files.absolute(System.getenv("TEMP") + "/Installer Example/uninst.bat");
                batchFile.writeString(batchLines, false);
                Desktop.getDesktop().open(batchFile.file());

                Gdx.app.exit();
            }
        } catch (IOException | URISyntaxException e) {
            Gdx.app.error(getClass().getName(), "Failed to get runtime directory", e);
        }
    }
    
    private boolean isChildOf(FileHandle child, FileHandle parent) {
        return isChildOf(child.path(), parent.path());
    }
    
    private boolean isChildOf(File child, File parent) {
        return isChildOf(child.getPath(), parent.getPath());
    }
    
    private boolean isChildOf(String childPath, String parentPath) {
        childPath = childPath.replace('\\', '/');
        parentPath = parentPath.replace('\\', '/');
        
        return childPath.startsWith(parentPath);
    }
}
