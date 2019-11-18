package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public class TextButtonCheckedUndoable implements SceneComposerUndoable {
    @Override
    public void undo() {
    
    }
    
    @Override
    public void redo() {
    
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextButton Checked\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextButton Unchecked\"";
    }
}
