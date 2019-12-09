package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public class ListDeleteUndoable extends ActorDeleteUndoable {
    @Override
    public String getRedoString() {
        return "Redo \"Delete List\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Delete List\"";
    }
}
