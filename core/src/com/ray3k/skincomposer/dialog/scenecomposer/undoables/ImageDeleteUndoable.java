package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public class ImageDeleteUndoable extends ActorDeleteUndoable {
    @Override
    public String getRedoString() {
        return "Redo \"Delete Image\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Delete Image\"";
    }
}
