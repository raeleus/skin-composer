package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public class CellResetUndoable implements SceneComposerUndoable {
    @Override
    public void undo() {
    
    }
    
    @Override
    public void redo() {
    
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Reset Cell\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Reset Cell\"";
    }
}
