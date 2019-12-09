package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public class LabelDeleteUndoable extends ActorDeleteUndoable {
    @Override
    public String getRedoString() {
        return "Redo \"Delete Label\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Delete Label\"";
    }
}
