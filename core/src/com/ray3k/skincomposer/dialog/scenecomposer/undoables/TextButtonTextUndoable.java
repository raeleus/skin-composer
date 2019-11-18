package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public class TextButtonTextUndoable implements SceneComposerUndoable {
    @Override
    public void undo() {
    
    }
    
    @Override
    public void redo() {
    
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextButton Text\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextButton Text\"";
    }
}
