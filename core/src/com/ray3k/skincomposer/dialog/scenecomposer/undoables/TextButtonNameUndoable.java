package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public class TextButtonNameUndoable implements SceneComposerUndoable {
    @Override
    public void undo() {
    
    }
    
    @Override
    public void redo() {
    
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextButton Name\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextButton Name\"";
    }
}
