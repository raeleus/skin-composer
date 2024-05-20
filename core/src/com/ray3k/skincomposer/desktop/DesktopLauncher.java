/*
 * The MIT License
 *
 * Copyright 2024 Raymond Buckley.
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
package com.ray3k.skincomposer.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.backends.lwjgl3.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.tools.bmfont.BitmapFontWriter;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.ray3k.skincomposer.*;
import com.ray3k.skincomposer.utils.Utils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.nfd.NFDPathSet;
import org.lwjgl.util.nfd.NativeFileDialog;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ray3k.skincomposer.Main.desktopWorker;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.memAllocPointer;

/**
 *
 * @author Raymond
 */
public class DesktopLauncher implements DesktopWorker, Lwjgl3WindowListener {
    private Array<FilesDroppedListener> filesDroppedListeners;
    private CloseListener closeListener;

    //constans for showFileChooser mode param
    private static final int OPEN_MULTIPLE=1;
    private static final int OPEN=2;
    private static final int SAVE=3;
    //flag for use swing JFileChooser
    private static boolean useSwing;


    public DesktopLauncher() {
        filesDroppedListeners = new Array<>();
    }
    
    @Override
    public void texturePack(Array<FileHandle> handles, FileHandle localFile, FileHandle targetFile, FileHandle settingsFile) {
        var json = new Json();
        var settings = json.fromJson(TexturePacker.Settings.class, settingsFile);
        
        var p = new TexturePacker(settings);
        for (var handle : handles) {
            if (handle.exists()) {
                p.addImage(handle.file());
            } else {
                if (localFile != null) {
                    var localHandle = localFile.sibling(localFile.nameWithoutExtension() + "_data/" + handle.name());
                    if (localHandle.exists()) {
                        p.addImage(localHandle.file());
                    } else {
                        Gdx.app.error(getClass().getName(), "File does not exist error while creating texture atlas: " + handle.path());
                    }
                } else {
                    Gdx.app.error(getClass().getName(), "File does not exist error while creating texture atlas: " + handle.path());
                }
            }
        }
        p.pack(targetFile.parent().file(), targetFile.nameWithoutExtension());
    }

    @Override
    public void packFontImages(Array<FileHandle> files, FileHandle saveFile) {
        var settings = new TexturePacker.Settings();
        settings.pot = false;
        settings.duplicatePadding = true;
        settings.filterMin = Texture.TextureFilter.Linear;
        settings.filterMag = Texture.TextureFilter.Linear;
        settings.ignoreBlankImages = false;
        settings.useIndexes = false;
        settings.limitMemory = false;
        settings.maxWidth = 2048;
        settings.maxHeight = 2048;
        settings.flattenPaths = true;
        settings.silent = true;
        var texturePacker = new TexturePacker(settings);

        for (FileHandle file : files) {
            if (file.exists()) {
                texturePacker.addImage(file.file());
            }
        }

        texturePacker.pack(saveFile.parent().file(), saveFile.nameWithoutExtension());
    }
    
    @Override
    public void centerWindow(Graphics graphics) {
        var g = (Lwjgl3Graphics) graphics;
        var mode = g.getDisplayMode();
        var window = g.getWindow();
        var monitor = g.getMonitor();
        window.setPosition(monitor.virtualX + mode.width / 2 - g.getWidth() / 2, monitor.virtualY + mode.height / 2 - g.getHeight() / 2);
    }

    @Override
    public void sizeWindowToFit(int maxWidth, int maxHeight, int displayBorder, Graphics graphics) {
        var mode = graphics.getDisplayMode();
        
        int width = Math.min(mode.width - displayBorder * 2, maxWidth);
        int height = Math.min(mode.height - displayBorder * 2, maxHeight);
        
        graphics.setWindowedMode(width, height);
        
        centerWindow(graphics);
    }

    @Override
    public void iconified(boolean isIconified) {
        
    }

    @Override
    public void focusLost() {
        
    }

    @Override
    public void focusGained() {
        
    }

    @Override
    public boolean closeRequested() {
        if (closeListener != null) {
            return closeListener.closed();
        } else {
            return true;
        }
    }
    
    @Override
    public void addFilesDroppedListener(FilesDroppedListener filesDroppedListener) {
        filesDroppedListeners.add(filesDroppedListener);
    }
    
    @Override
    public void removeFilesDroppedListener(FilesDroppedListener filesDroppedListener) {
        filesDroppedListeners.removeValue(filesDroppedListener, false);
    }
    
    @Override
    public void filesDropped(String[] files) {
        var fileHandles = new Array<FileHandle>();
        for (var file : files) {
            var fileHandle = new FileHandle(file);
            fileHandles.add(fileHandle);
        }
        
        for (var listener : filesDroppedListeners) {
            listener.filesDropped(fileHandles);
        }
    }

    @Override
    public void setCloseListener(CloseListener closeListener) {
        this.closeListener = closeListener;
    }

    @Override
    public void attachLogListener() {
        Gdx.app.setApplicationLogger(new TextFileApplicationLogger());
    }

    @Override
    public void maximized(boolean arg0) {
    }

    @Override
    public void refreshRequested() {
    }

    @Override
    public List<File> openMultipleDialog(String title, String defaultPath, String filterPatterns, String filterDescription) {
        if (useSwing) {
            var result = showFileChooser(OPEN_MULTIPLE, title, defaultPath, filterPatterns, filterDescription);
            return result.size() > 0 ? result : null;
        }
    
        NFDPathSet outPaths =NFDPathSet.calloc();
    
        //fix file path characters
        if (Utils.isWindows()) {
            defaultPath = defaultPath.replace("/", "\\");
        } else {
            defaultPath = defaultPath.replace("\\", "/");
        }
    
        try {
            var status = NativeFileDialog.NFD_OpenDialogMultiple(filterPatterns, defaultPath, outPaths);
        
            if (status == NativeFileDialog.NFD_CANCEL) return null;
        
            if (status != NativeFileDialog.NFD_OKAY) {
                var result = showFileChooser(OPEN_MULTIPLE, title, defaultPath, filterPatterns, filterDescription);
                return result.size() > 0 ? result : null;
            }
        
            ArrayList<File> list = new ArrayList<>();
            long count = NativeFileDialog.NFD_PathSet_GetCount(outPaths);
            for (long i = 0; i < count; i++) {
                String path = NativeFileDialog.NFD_PathSet_GetPath(outPaths, i);
                list.add(new File(path));
            }
            NativeFileDialog.NFD_PathSet_Free(outPaths);
            return list;
        } catch (Exception e) {
            return showFileChooser(OPEN_MULTIPLE, title, defaultPath, filterPatterns, filterDescription);
        }
    }

    @Override
    public File openDialog(String title, String defaultPath, String filterPatterns, String filterDescription) {
        if (useSwing) {
            var result = showFileChooser(OPEN, title, defaultPath, filterPatterns, filterDescription);
            return result.size() > 0 ? result.get(0) : null;
        }
    
        PointerBuffer outPath = memAllocPointer(1);
        
        //fix file path characters
        if (Utils.isWindows()) {
            defaultPath = defaultPath.replace("/", "\\");
        } else {
            defaultPath = defaultPath.replace("\\", "/");
        }
    
        try {
            var status = NativeFileDialog.NFD_OpenDialog(filterPatterns, defaultPath, outPath);
    
            if (status == NativeFileDialog.NFD_CANCEL) return null;
    
            if (status != NativeFileDialog.NFD_OKAY) {
                var result = showFileChooser(OPEN, title, defaultPath, filterPatterns, filterDescription);
                return result.size() > 0 ? result.get(0) : null;
            }
    
            String result = outPath.getStringUTF8();
            NativeFileDialog.nNFD_Free(outPath.get(0));
            return new File(result);
        } catch (Exception e) {
            return showFileChooser(OPEN, title, defaultPath, filterPatterns, filterDescription).get(0);
        }
    }
    
    @Override
    public File saveDialog(String title, String defaultPath, String filterPatterns, String filterDescription) {
        if (useSwing) {
            var result = showFileChooser(SAVE, title, defaultPath, filterPatterns, filterDescription);
            return result.size() > 0 ? result.get(0) : null;
        }
    
        PointerBuffer outPath = memAllocPointer(1);
    
        //fix file path characters
        if (Utils.isWindows()) {
            defaultPath = defaultPath.replace("/", "\\");
        } else {
            defaultPath = defaultPath.replace("\\", "/");
        }
    
        try {
            var status = NativeFileDialog.NFD_SaveDialog(filterPatterns, defaultPath, outPath);
        
            if (status == NativeFileDialog.NFD_CANCEL) return null;
        
            if (status != NativeFileDialog.NFD_OKAY) {
                var result = showFileChooser(SAVE, title, defaultPath, filterPatterns, filterDescription);
                return result.size() > 0 ? result.get(0) : null;
            }
        
            String result = outPath.getStringUTF8();
            NativeFileDialog.nNFD_Free(outPath.get(0));
            return new File(result);
        } catch (Exception e) {
            return showFileChooser(SAVE, title, defaultPath, filterPatterns, filterDescription).get(0);
        }
    }

	/* Opens a swing JFileChooser
	 * returns:
	 * if multiple selection mode the list of Files selected
	 * if single selection mode a list with one File Object
	 * if nothing selected or dialog is cancelled:
	 *    an empty list
	*/
    private List<File> showFileChooser(int mode, String title, String defaultPath, String filterPatterns, String filterDescription) {

        JFrame frame= new JFrame();
        frame.setVisible(true);
        frame.toFront();
        frame.setAlwaysOnTop(true);
        frame.setVisible(false);
        JFileChooser fileChooser= new JFileChooser(defaultPath);
        fileChooser.setDialogTitle(title);

        //process filterPatterns
        if (filterPatterns != null) {
            if (filterDescription == null){
                filterDescription="null";
            }
            
            for (var filter : filterPatterns.split(";")) {
                FileFilter filefilter = new FileNameExtensionFilter(filterDescription, filter.split(","));
                fileChooser.addChoosableFileFilter(filefilter);
            }
            if (fileChooser.getChoosableFileFilters().length > 0) fileChooser.setFileFilter(fileChooser.getChoosableFileFilters()[0]);
        }


        int res;
        if (mode == OPEN_MULTIPLE){
            fileChooser.setMultiSelectionEnabled(true);
            res= fileChooser.showOpenDialog(frame);
        }
        else if (mode == OPEN ){
            fileChooser.setMultiSelectionEnabled(false);
            res= fileChooser.showOpenDialog(frame);
        }
        else{
            res= fileChooser.showSaveDialog(frame);
        }
        frame.dispose();

        var returnValue= new ArrayList<File>();
        
        if (res==JFileChooser.APPROVE_OPTION){
            if (fileChooser.isMultiSelectionEnabled())
                Collections.addAll(returnValue, fileChooser.getSelectedFiles());
            else
                returnValue.add(fileChooser.getSelectedFile());
        }

        return returnValue;
    }

    @Override
    public char getKeyName(int keyCode) {
        try {
            var output = org.lwjgl.glfw.GLFW.glfwGetKeyName(keyCode, 0);
            return (output == null) ? ' ' : output.toLowerCase().charAt(0);
        } catch (Exception e) {
            return ' ';
        }
    }

    @Override
    public void writeFont(FreeTypeFontGenerator.FreeTypeBitmapFontData data, Array<PixmapPacker.Page> pages, FileHandle target) {
        var info = new BitmapFontWriter.FontInfo();
        data.capHeight--;
        info.face = target.nameWithoutExtension();
        info.padding = new BitmapFontWriter.Padding(1, 1, 1, 1);

        var pixmapNames = BitmapFontWriter.writePixmaps(pages, target.parent(), target.nameWithoutExtension());
        int scaleW;
        int scaleH;
        
        if (pixmapNames.length > 1) {
            //all the images must have the same width and height
            var pngTarget = target.sibling(pixmapNames[0]);
            var pixmap = new Pixmap(pngTarget);
            scaleW = pixmap.getWidth();
            scaleH = pixmap.getHeight();
        } else {
            var pngTarget = target.sibling(pixmapNames[0]);

            //trim whitespace on the image.
            var pixmap = new Pixmap(pngTarget);
            var color = new Color();
            scaleH = pixmap.getHeight();
            boolean foundOpaquePixel = false;
            for (int y = pixmap.getHeight() - 1; y >= 0 && !foundOpaquePixel; y--) {
                for (int x = 0; x < pixmap.getWidth(); x++) {
                    color.set(pixmap.getPixel(x, y));
                    if (color.a > 0) {
                        //add padding to new height
                        scaleH = y + 2;
                        foundOpaquePixel = true;
                        break;
                    }
                }
            }

            foundOpaquePixel = false;
            scaleW = pixmap.getWidth();
            for (int x = pixmap.getWidth() - 1; x >= 0 && !foundOpaquePixel; x--) {
                for (int y = 0; y < pixmap.getHeight(); y++) {
                    color.set(pixmap.getPixel(x, y));
                    if (color.a > 0) {
                        //add padding to new height
                        scaleW = x + 2;
                        foundOpaquePixel = true;
                        break;
                    }
                }
            }
    
            var fixedPixmap = new Pixmap(scaleW, scaleH, Pixmap.Format.RGBA8888);
            fixedPixmap.setBlending(Pixmap.Blending.None);
            fixedPixmap.drawPixmap(pixmap, 0, 0);
            PixmapIO.writePNG(pngTarget, fixedPixmap);
            pixmap.dispose();
            fixedPixmap.dispose();
        }
        
        BitmapFontWriter.writeFont(data, pixmapNames, target, info, scaleW, scaleH);
    }

    @Override
    public void created(Lwjgl3Window lw) {
        
    }
    
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) {
            return;
        }
        
        //setting a flag for use JFileChooser if "-swingfd" arg is in commandline 
        useSwing=false;
        for (var arg: args){
            if (arg.equals("-swingfd")){
                useSwing=true;
                break;
            }
        }

        var config = new Lwjgl3ApplicationConfiguration();
        config.setResizable(true);
        config.useVsync(true);
        config.setWindowedMode(800, 800);
        config.setBackBufferConfig(8, 8, 8, 8, 16, 2, 10);
        var desktopLauncher = new DesktopLauncher();
        config.setWindowListener(desktopLauncher);
        config.setTitle("Skin Composer - New Project*");
        config.setWindowSizeLimits(675, 400, -1, -1);
        config.setWindowIcon("logo-16.png", "logo-32.png", "logo-48.png", "logo.png");
        var main = new Main(args);
        desktopWorker = desktopLauncher;
        
        try {
            new Lwjgl3Application(main, config);
        } catch (Exception e) {
            e.printStackTrace();
            
            try {
                var fw = new FileWriter(Gdx.files.external(".skincomposer/temp/java-stacktrace.txt").file(), true);
                var pw = new PrintWriter(fw);
                e.printStackTrace(pw);
                pw.close();
                fw.close();
                int choice = JOptionPane.showConfirmDialog(null, "Exception occurred. See error log?", "Skin Composer Exception!", JOptionPane.YES_NO_OPTION);
                if (choice == 0) {
                    Utils.openFileExplorer(Gdx.files.external(".skincomposer/temp/java-stacktrace.txt"));
                }
            } catch (Exception ex) {
            
            }
        }
    }
}
