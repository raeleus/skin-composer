package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public class CellAddRowAboveUndoable implements SceneComposerUndoable {
    @Override
    public void undo() {
    
    }
    
    @Override
    public void redo() {
    
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Add Row Above Cell\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Add Row Above Cell\"";
    }
}
