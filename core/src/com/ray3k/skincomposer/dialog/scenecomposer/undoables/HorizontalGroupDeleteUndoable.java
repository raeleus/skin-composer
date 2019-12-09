package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public class HorizontalGroupDeleteUndoable extends ActorDeleteUndoable {
    @Override
    public String getRedoString() {
        return "Redo \"Delete HorizontalGroup\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Delete HorizontalGroup\"";
    }
}
