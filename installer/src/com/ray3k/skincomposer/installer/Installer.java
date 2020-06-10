package com.ray3k.skincomposer.installer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.files.FileHandle;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

public class Installer implements DesktopWorker {

    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setDecorated(false);
        config.setWindowedMode(220, 180);
        config.setTitle("Skin Composer Installer");
        config.setWindowIcon("icon/logo-16.png", "icon/logo-32.png", "icon/logo-48.png", "icon/logo.png");
        Core core = new Core();
        Core.desktopWorker = new Installer();
        new Lwjgl3Application(core, config);
    }
    
    @Override
    public void dragWindow(int x, int y) {
        Lwjgl3Window window = ((Lwjgl3Graphics) Gdx.graphics).getWindow();
        window.setPosition(x, y);
    }

    @Override
    public int getWindowX() {
        return ((Lwjgl3Graphics) Gdx.graphics).getWindow().getPositionX();
    }

    @Override
    public int getWindowY() {
        return ((Lwjgl3Graphics) Gdx.graphics).getWindow().getPositionY();
    }
    
    @Override
    public FileHandle selectFolder(String title, FileHandle defaultPath) {
        //fix file path characters
        String path = defaultPath.path().replace("/", "\\");
        
        String result = TinyFileDialogs.tinyfd_selectFolderDialog(title, path);
        
        return result == null ? null : Gdx.files.absolute(result);
    }
}
