package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public class CellAddWidgetUndoable implements SceneComposerUndoable {
    @Override
    public void undo() {
    
    }
    
    @Override
    public void redo() {
    
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Add Widget to Cell\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Add Widget to Cell\"";
    }
}
