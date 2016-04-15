package com.ray3k.skincomposer;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public interface DesktopWorker {
    public void texturePack(Array<FileHandle> handles, FileHandle targetFile, int MaxWidth, int MaxHeight);
    public void sizeWindowToFit(int maxWidth, int maxHeight, int displayBorder, Graphics graphics);
    public void centerWindow(Graphics graphics);
    public void addFilesDroppedListener(FilesDroppedListener filesDroppedListener);
    public void removeFilesDroppedListener(FilesDroppedListener filesDroppedListener);
}
