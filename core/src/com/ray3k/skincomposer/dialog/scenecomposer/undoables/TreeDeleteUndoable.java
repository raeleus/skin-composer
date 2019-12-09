package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public class TreeDeleteUndoable extends ActorDeleteUndoable {
    @Override
    public String getRedoString() {
        return "Redo \"Delete Tree\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Delete Tree\"";
    }
}
