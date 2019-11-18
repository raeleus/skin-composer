package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public class TableResetUndoable implements SceneComposerUndoable {
    @Override
    public void undo() {
    
    }
    
    @Override
    public void redo() {
    
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Reset Table\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Reset Table\"";
    }
}
