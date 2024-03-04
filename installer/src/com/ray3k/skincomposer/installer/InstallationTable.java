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
package com.ray3k.skincomposer.installer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.esotericsoftware.spine.AnimationState;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.zip.ZipInputStream;
import javax.swing.JOptionPane;
import mslinks.ShellLink;

/**
 *
 * @author Raymond
 */
public class InstallationTable extends Table {
    private long totalBytes, counter;
    private boolean continueInstall;
    private boolean pauseInstall;
    private GraphicWidget graphic;

    public InstallationTable(final Skin skin, final Stage stage) {
        super(skin);
        continueInstall = true;
        pauseInstall = false;
        
        var stack = new Stack();
        add(stack).grow();
        
        graphic = new GraphicWidget(skin);
        stack.add(graphic);
        graphic.setMode(GraphicWidget.Mode.START);
        
        var table = new Table();
        stack.add(table);
        
        table.pad(10.0f);
        
        final var button = new TextButton("X", skin, "small");
        table.add(button).top().right().expand();
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                if (continueInstall) {
                    showQuitDialog();
                }
            }
        });
        
        table = new Table();
        stack.add(table);
        
        var label = new Label("INSTALLING", skin, "small");
        label.setColor(1, 1, 1, 0);
        label.setAlignment(Align.center);
        label.setWrap(true);
        table.add(label).growX();
        
        final var thread = new Thread(() -> {
            try {
                final var installationFiles = new Array<FileHandle>();
                final var runtimeFiles = new Array<FileHandle>();
                final var deleteOnQuitFiles = new Array<File>();
                totalBytes = 0;
                var src = getClass().getProtectionDomain().getCodeSource();
                var jar = src.getLocation();
                var zip = new ZipInputStream(jar.openStream());
                
                while (true) {
                    var e = zip.getNextEntry();
                    if (e == null) {
                        break;
                    }
                    final java.lang.String name1 = e.getName();
                    if (name1.matches("installation\\/.+")) {
                        com.badlogic.gdx.files.FileHandle fileHandle = Gdx.files.internal(name1);
                        installationFiles.add(fileHandle);
                        //internal files don't report if it's a directory, remove parent directories
                        installationFiles.removeValue(fileHandle.parent(), false);
                    }
                }
                
                //iterate through runtime directory files. Exclude directories and exe's
                var runtimeDirectory = new FileHandle(Paths.get(jar.toURI()).toString()).parent().parent();
                for (FileHandle file : runtimeDirectory.list((File pathname) -> !pathname.isDirectory() && !pathname.getName().toLowerCase().endsWith(".exe"))) {
                    runtimeFiles.add(file);
                }
                
                runtimeFiles.addAll(findFilesRecursively(runtimeDirectory.child("runtime"), new Array<>()));
                var allFiles = new Array<FileHandle>(installationFiles);
                allFiles.addAll(runtimeFiles);
                for (FileHandle file : allFiles) {
                    totalBytes += file.length();
                }
                
                //read each file by byte and update the progress bar
                byte[] byteValue = new byte[1024];
                counter = 0;
                
                for (final FileHandle file : allFiles) {
                    //The user chose to quit, delete all installed files and stop
                    if (!continueInstall) {
                        //gather all parent directories of deleted files in a list that doesn't allow duplicates
                        var deleteDirectoriesSet = new ObjectSet<String>();
                        for (File deleteFile : deleteOnQuitFiles) {
                            //only include subdirectories of the installation path
                            if (isChildOf(deleteFile.getParentFile(), Core.installationPath.file())) {
                                deleteDirectoriesSet.add(deleteFile.getParent());
                            }
                            
                            //delete the installed file
                            deleteFile.delete();
                        }
                        
                        //include all paths up to the installation path
                        for (String path : new ObjectSet<String>(deleteDirectoriesSet)) {
                            FileHandle parent1 = Gdx.files.absolute(path);
                            parent1 = parent1.parent();
                            while (isChildOf(parent1, Core.installationPath)) {
                                deleteDirectoriesSet.add(parent1.path());
                                parent1 = parent1.parent();
                            }
                        }
                        
                        //sort the directories so that inner most paths are deleted first
                        Array<String> deleteDirectories = new Array<>();
                        for (String path : deleteDirectoriesSet) {
                            deleteDirectories.add(path);
                        }
                        deleteDirectories.sort();
                        deleteDirectories.reverse();
                        
                        //delete the directories
                        for (String path : deleteDirectories) {
                            Gdx.files.absolute(path).delete();
                        }
                        break;
                    }
                    //read the source file from the jar or java runtime
                    InputStream inputStream = file.read();
                    final File targetFile;
                    if (installationFiles.contains(file, false)) {
                        targetFile = new File(file.path().replaceFirst("^installation", Core.installationPath.path()));
                    } else {
                        targetFile = new File(file.path().replace(runtimeDirectory.path(), Core.installationPath.path()));
                    }
                    
                    //create folders
                    targetFile.getParentFile().mkdirs();
                    
                    //write file
                    FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
                    deleteOnQuitFiles.add(targetFile);
                    int bytesRead = 0;
                    while ((bytesRead = inputStream.read(byteValue)) != -1 && continueInstall) {
                        counter += bytesRead;
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                graphic.setTimeScale((float) counter / totalBytes);
                            }
                        });
                        fileOutputStream.write(byteValue, 0, bytesRead);
                        
                        //Sleep while the user pauses the installation
                        while (pauseInstall) {
                            Thread.sleep(200);
                        }
                    }
                    fileOutputStream.close();
                }
                
                //update the buttons
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        button.setVisible(false);
                        button.setTouchable(Touchable.disabled);
                    }
                });
                
                //Create a desktop shortcut
                if (Core.installationCreateDesktopIcon && continueInstall) {
                    String desktopPath = WindowsRegistry.readRegistry(
                            "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders", "Desktop");
                    desktopPath = desktopPath.replace("\\", "/");
                    ShellLink sl = ShellLink.createLink(Core.installationPath.path() + "/" + Core.properties.get("shortcut-target"));
                    File file = new File(desktopPath + "/" + Core.properties.get("product-name") + ".lnk");
                    sl.saveTo(file.getPath());
                    deleteOnQuitFiles.add(file);
                }
                
                //Create a start menu shortcut
                if (Core.installationCreateStartIcon && continueInstall) {
                    String startPath = WindowsRegistry.readRegistry(
                            "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders", "Start Menu");
                    startPath = startPath.replace("\\", "/");
                    ShellLink sl = ShellLink.createLink(Core.installationPath.path() + "/" + Core.properties.get("shortcut-target"));
                    File file = new File(startPath + "/" + Core.properties.get("product-name") + ".lnk");
                    sl.saveTo(file.getPath());
                    deleteOnQuitFiles.add(file);
                }
                
                //write uninstaller list file
                if (continueInstall) {
                    FileHandle uninstallLog = Gdx.files.absolute(Core.installationPath.path() + "/uninstall");
                    deleteOnQuitFiles.add(uninstallLog.file());
                    
                    String uninstallLines = "";
                    Iterator<File> iter = deleteOnQuitFiles.iterator();
                    while (iter.hasNext()) {
                        File file = iter.next();
                        uninstallLines += file.getPath();
                        
                        if (iter.hasNext()) {
                            uninstallLines += "\n";
                        }
                    }
                    
                    uninstallLog.writeString(uninstallLines, false);
                }
                
                //write registry entries for file association
                final var longFileExt = Core.properties.get("product-name").replace(' ', '_')
                        + "." + Core.properties.get("file-association-extension")
                        + "." + Core.properties.get("file-association-version");
                var process = Runtime.getRuntime().exec("cmd /c REG ADD HKCU\\Software\\Classes\\"
                        + longFileExt
                        + "\\shell\\open\\command /t REG_SZ /d \"\"\"\""
                        + Core.installationPath.path().replace('/', '\\') + "\\"
                        + Core.properties.get("shortcut-target") + "\"\"\" \"\"\"%1\"\"\"\" /f");
                process.waitFor();
                
                process = Runtime.getRuntime().exec("cmd /c REG ADD HKCU\\Software\\Classes\\"
                        + longFileExt
                        + "\\DefaultIcon /t REG_SZ /d \"\"\"\""
                        + Core.installationPath.path().replace('/', '\\') + "\\"
                        + Core.properties.get("file-association-icon") + "\"\"\"\" /f");
                process.waitFor();
                
                process = Runtime.getRuntime().exec("cmd /c REG ADD HKCU\\Software\\Classes\\."
                        + Core.properties.get("file-association-extension")
                        + " /t REG_SZ /d \"" + longFileExt + "\" /f");
                process.waitFor();
                
                //write registry entries for uninstaller
                process = Runtime.getRuntime().exec("cmd /c REG ADD HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\"
                        + Core.properties.get("product-name").replace(' ', '_')
                        + " /v DisplayName /t REG_SZ /d \"" + Core.properties.get("product-name") + "\" /f");
                process.waitFor();
                
                process = Runtime.getRuntime().exec("cmd /c REG ADD HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\"
                        + Core.properties.get("product-name").replace(' ', '_')
                        + " /v UninstallString /t REG_SZ /d \""
                        + Core.installationPath.path().replace('/', '\\') + "\\"
                        + Core.properties.get("installation-uninstall-path") + "\" /f");
                process.waitFor();
                
                process = Runtime.getRuntime().exec("cmd /c REG ADD HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\"
                        + Core.properties.get("product-name").replace(' ', '_')
                        + " /v URLInfoAbout /t REG_SZ /d \"" + Core.properties.get("installation-url-about") + "\" /f");
                process.waitFor();
                
                process = Runtime.getRuntime().exec("cmd /c REG ADD HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\"
                        + Core.properties.get("product-name").replace(' ', '_')
                        + " /v DisplayVersion /t REG_SZ /d \""
                        + Core.properties.get("installation-display-version") + "\" /f");
                process.waitFor();
                
                process = Runtime.getRuntime().exec("cmd /c REG ADD HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\"
                        + Core.properties.get("product-name").replace(' ', '_')
                        + " /v DisplayIcon /t REG_SZ /d \""
                        + Core.installationPath.path().replace('/', '\\') + "\\"
                        + Core.properties.get("installation-ico") + "\" /f");
                process.waitFor();
                
                process = Runtime.getRuntime().exec("cmd /c REG ADD HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\"
                        + Core.properties.get("product-name").replace(' ', '_')
                        + " /v InstallLocation /t REG_SZ /d \""
                        + Core.installationPath.path().replace('/', '\\') + "\" /f");
                process.waitFor();
                
                process = Runtime.getRuntime().exec("cmd /c REG ADD HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\"
                        + Core.properties.get("product-name").replace(' ', '_')
                        + " /v Publisher /t REG_SZ /d \""
                        + Core.properties.get("publisher") + "\" /f");
                process.waitFor();
                
                process = Runtime.getRuntime().exec("cmd /c REG ADD HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\"
                        + Core.properties.get("product-name").replace(' ', '_')
                        + " /v NoModify /t REG_DWORD /d \""
                        + (Core.properties.get("installation-no-modify").equals("true") ? "1" : "0") + "\" /f");
                process.waitFor();
                
                process = Runtime.getRuntime().exec("cmd /c REG ADD HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\"
                        + Core.properties.get("product-name").replace(' ', '_')
                        + " /v NoRepair /t REG_DWORD /d \""
                        + (Core.properties.get("installation-no-repair").equals("true") ? "1" : "0") + "\" /f");
                process.waitFor();
                
                //transition to complete screen
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        label.addAction(Actions.sequence(Actions.fadeOut(.5f), new Action() {
                            @Override
                            public boolean act(float delta) {
                                label.setText("COMPLETE");
                                return true;
                            }
                        }, Actions.fadeIn(.5f), Actions.delay(2.0f), Actions.fadeOut(.5f), new Action() {
                            @Override
                            public boolean act(float delta) {
                                graphic.setMode(GraphicWidget.Mode.COMPLETE);
                                return true;
                            }
                        }));
                    }
                });
            } catch (final IOException e) {
                JOptionPane.showMessageDialog(null, "IO Exception " + e.getMessage());
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        label.setText("IO Exception " + e.getMessage());
                        label.setColor(1, 1, 1, 1);
                    }
                });
            } catch (final InterruptedException e) {
                JOptionPane.showMessageDialog(null, "IO Exception " + e.getMessage());
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        label.setText("InterruptedException " + e.getMessage());
                        label.setColor(1, 1, 1, 1);
                    }
                });
            }catch (final URISyntaxException e) {
                JOptionPane.showMessageDialog(null, "IO Exception " + e.getMessage());
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        label.setText("URISyntaxException " + e.getMessage());
                        label.setColor(1, 1, 1, 1);
                    }
                });
            }
        });
        
        graphic.getSpineDrawable().getAnimationState().addListener(new AnimationState.AnimationStateAdapter() {
            @Override
            public void complete(AnimationState.TrackEntry entry) {
                if (graphic.getMode() == GraphicWidget.Mode.START) {
                    label.addAction(Actions.fadeIn(.5f));
                    graphic.setMode(GraphicWidget.Mode.INSTALLING);
                    thread.start();
                    
                } else if (graphic.getMode() == GraphicWidget.Mode.COMPLETE) {
                    Core.transition(InstallationTable.this, new CompleteTable(getSkin(), getStage()), 0.0f, .5f);
                }
            }
        });
    }
    
    private void showQuitDialog() {
        pauseInstall = true;
        var dialog = new Dialog("", getSkin()) {
            @Override
            protected void result(Object object) {
                pauseInstall = false;
                if ((Boolean) object) {
                    Core.transition(InstallationTable.this, new MenuTable(getSkin(), getStage()), 0.0f, .5f);
                    continueInstall = false;
                }
            }
        };
        
        dialog.getContentTable().pad(10.0f);
        dialog.getButtonTable().pad(10.0f);
        dialog.text("Quit installation?");
        dialog.button("Quit", true).button("Install", false);
        dialog.key(Keys.ENTER, true).key(Keys.ESCAPE, false);
        dialog.show(getStage());
    }

    private Array<FileHandle> findFilesRecursively(FileHandle begin, Array<FileHandle> handles) {
        var newHandles = begin.list();
        for (var f : newHandles) {
            if (f.isDirectory()) {
                findFilesRecursively(f, handles);
            } else {
                handles.add(f);
            }
        }
        
        return handles;
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
