package com.ray3k.skincomposer.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.TextureWorker;

public class DesktopLauncher implements TextureWorker {

    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setResizable(true);
        config.useVsync(true);
        config.setWindowedMode(800, 800);
        config.setTitle("Skin Composer - RAY3K.COM");
        Main main = new Main();
        main.setTextureWorker(new DesktopLauncher());
        Lwjgl3Application app = new Lwjgl3Application(main, config);
    }
    
    @Override
    public void TexturePack(Array<FileHandle> handles, FileHandle targetFile, int maxWidth, int maxHeight) {
        Settings settings = new TexturePacker.Settings();
        settings.maxWidth = maxWidth;
        settings.maxHeight = maxHeight;
        settings.duplicatePadding = true;
        settings.square = true;
        settings.filterMin = Texture.TextureFilter.Linear;
        settings.filterMag = Texture.TextureFilter.Linear;
        settings.fast = true;
        settings.useIndexes = false;
        settings.silent = true;
        settings.flattenPaths = true;
        TexturePacker p = new TexturePacker(settings);
        for (FileHandle handle : handles) {
            p.addImage(handle.file());
        }
        p.pack(targetFile.parent().file(), targetFile.nameWithoutExtension());
    }
}
