package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public class NodeDeleteUndoable extends ActorDeleteUndoable {
    @Override
    public String getRedoString() {
        return "Redo \"Delete Node\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Delete Node\"";
    }
}
