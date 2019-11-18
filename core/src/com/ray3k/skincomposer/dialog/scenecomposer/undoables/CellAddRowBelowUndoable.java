package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public class CellAddRowBelowUndoable implements SceneComposerUndoable {
    @Override
    public void undo() {
    
    }
    
    @Override
    public void redo() {
    
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Add Row Below Cell\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Add Row Below Cell\"";
    }
}
