package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public class RootAddTableUndoable implements SceneComposerUndoable {
    @Override
    public void undo() {
    
    }
    
    @Override
    public void redo() {
    
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Add Table\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Add Table\"";
    }
}
