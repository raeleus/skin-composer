package com.ray3k.skincomposer.undo;

public interface Undoable {
    public void undo();
    public void redo();
    public String getUndoText();
}
