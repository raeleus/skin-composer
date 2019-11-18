package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public class CellAddCellToLeftUndoable implements SceneComposerUndoable {
    @Override
    public void undo() {
    
    }
    
    @Override
    public void redo() {
    
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Add Cell to Left\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Add Cell to Left\"";
    }
}
