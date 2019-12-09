package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public class ContainerDeleteUndoable extends ActorDeleteUndoable {
    @Override
    public String getRedoString() {
        return "Redo \"Delete Container\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Delete Container\"";
    }
}
