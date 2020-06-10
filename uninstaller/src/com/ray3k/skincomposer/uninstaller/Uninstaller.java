package com.ray3k.skincomposer.uninstaller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;

public class Uninstaller implements DesktopWorker {

    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(220, 180);
        config.setDecorated(false);
        config.setWindowIcon("icon/logo-16.png", "icon/logo-32.png", "icon/logo-48.png", "icon/logo.png");
        config.setTitle("Skin Composer Uninstaller");
        Core.desktopWorker = new Uninstaller();
        new Lwjgl3Application(new Core(), config);
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
}
