package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public class TextButtonStyleUndoable implements SceneComposerUndoable {
    @Override
    public void undo() {
    
    }
    
    @Override
    public void redo() {
    
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextButton Style\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextButton Style\"";
    }
}
