package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public interface SceneComposerUndoable {
    public void undo();
    public void redo();
    public String getRedoString();
    public String getUndoString();
}
