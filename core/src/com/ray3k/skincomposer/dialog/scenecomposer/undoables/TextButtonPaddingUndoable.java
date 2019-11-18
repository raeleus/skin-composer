package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public class TextButtonPaddingUndoable implements SceneComposerUndoable {
    @Override
    public void undo() {
    
    }
    
    @Override
    public void redo() {
    
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextButton Padding\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextButton Padding\"";
    }
}
