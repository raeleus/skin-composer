package com.ray3k.skincomposer;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public interface TextureWorker {
    public void TexturePack(Array<FileHandle> handles, FileHandle targetFile, int MaxWidth, int MaxHeight);
}
