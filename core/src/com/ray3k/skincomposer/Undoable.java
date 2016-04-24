package com.ray3k.skincomposer;

public interface Undoable {
    public void undo();
    public void redo();
    public String getUndoText();
}
