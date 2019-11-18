package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public class CellAddCellToRightUndoable implements SceneComposerUndoable {
    @Override
    public void undo() {
    
    }
    
    @Override
    public void redo() {
    
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Add Cell to Right\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Add Cell to Right\"";
    }
}
