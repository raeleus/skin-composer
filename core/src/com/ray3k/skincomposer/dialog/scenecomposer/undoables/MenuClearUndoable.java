package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public class MenuClearUndoable implements SceneComposerUndoable {
    @Override
    public void undo() {
    
    }
    
    @Override
    public void redo() {
    
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Clear Project\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Clear Project\"";
    }
}
