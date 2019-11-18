package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public class CellPaddingSpacingUndoable implements SceneComposerUndoable {
    @Override
    public void undo() {
    
    }
    
    @Override
    public void redo() {
    
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Cell Padding/Spacing\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Cell Padding/Spacing\"";
    }
}
