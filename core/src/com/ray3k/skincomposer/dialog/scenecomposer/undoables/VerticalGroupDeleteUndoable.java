package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public class VerticalGroupDeleteUndoable extends ActorDeleteUndoable {
    @Override
    public String getRedoString() {
        return "Redo \"Delete VerticalGroup\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Delete VerticalGroup\"";
    }
}
