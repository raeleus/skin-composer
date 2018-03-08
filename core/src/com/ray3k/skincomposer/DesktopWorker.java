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
package com.ray3k.skincomposer;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import java.io.File;
import java.util.List;

public interface DesktopWorker {
    public void texturePack(Array<FileHandle> handles, FileHandle localFile, FileHandle targetFile);
    public void sizeWindowToFit(int maxWidth, int maxHeight, int displayBorder, Graphics graphics);
    public void centerWindow(Graphics graphics);
    public void addFilesDroppedListener(FilesDroppedListener filesDroppedListener);
    public void removeFilesDroppedListener(FilesDroppedListener filesDroppedListener);
    public void setCloseListener(CloseListener closeListener);
    public void attachLogListener();
    public List<File> openMultipleDialog(String title, String defaultPath, String[] filterPatterns, String filterDescription);
    public File openDialog(String title, String defaultPath, String[] filterPatterns, String filterDescription);
    public File saveDialog(String title, String defaultPath, String[] filterPatterns, String filterDescription);
    public void closeSplashScreen();
    public char getKeyName(int keyCode);
}
