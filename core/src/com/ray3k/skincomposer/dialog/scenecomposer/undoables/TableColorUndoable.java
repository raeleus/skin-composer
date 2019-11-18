package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public class TableColorUndoable implements SceneComposerUndoable {
    @Override
    public void undo() {
    
    }
    
    @Override
    public void redo() {
    
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Table Color\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Table Color\"";
    }
}
