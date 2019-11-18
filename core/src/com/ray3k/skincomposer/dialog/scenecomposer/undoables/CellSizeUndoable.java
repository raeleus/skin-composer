package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public class CellSizeUndoable implements SceneComposerUndoable {
    @Override
    public void undo() {
    
    }
    
    @Override
    public void redo() {
    
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Cell Size\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Cell Size\"";
    }
}
