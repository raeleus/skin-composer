package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public class StackDeleteUndoable extends ActorDeleteUndoable {
    @Override
    public String getRedoString() {
        return "Redo \"Delete Stack\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Delete Stack\"";
    }
}
