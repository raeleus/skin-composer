package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public class TableNameUndoable implements  SceneComposerUndoable {
    @Override
    public void undo() {
    
    }
    
    @Override
    public void redo() {
    
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Table Name\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Table Name\"";
    }
}
