package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public class TextButtonAlignUndoable implements SceneComposerUndoable {
    @Override
    public void undo() {
    
    }
    
    @Override
    public void redo() {
    
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Text Button Alignment\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Text Button Alignment\"";
    }
}
