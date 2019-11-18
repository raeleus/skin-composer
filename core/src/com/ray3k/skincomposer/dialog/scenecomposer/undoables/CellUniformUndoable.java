package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public class CellUniformUndoable implements SceneComposerUndoable {
    @Override
    public void undo() {
    
    }
    
    @Override
    public void redo() {
    
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Cell Uniform\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Cell Uniform\"";
    }
}
