package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public class CellDeleteUndoable implements SceneComposerUndoable {
    @Override
    public void undo() {
    
    }
    
    @Override
    public void redo() {
    
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Delete Cell\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Delete Cell\"";
    }
}
