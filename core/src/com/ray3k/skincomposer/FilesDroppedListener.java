/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ray3k.skincomposer;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

/**
 *
 * @author raymond
 */
public interface FilesDroppedListener {
    public void filesDropped(Array<FileHandle> files);
}
