package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public class SelectBoxDeleteUndoable extends ActorDeleteUndoable {
    @Override
    public String getRedoString() {
        return "Redo \"Delete SelectBox\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Delete SelectBox\"";
    }
}
