package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public class TextButtonDisabledUndoable implements SceneComposerUndoable {
    @Override
    public void undo() {
    
    }
    
    @Override
    public void redo() {
    
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextButton Disabled\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextButton Disabled\"";
    }
}
