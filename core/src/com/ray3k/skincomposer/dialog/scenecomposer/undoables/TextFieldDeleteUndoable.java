package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public class TextFieldDeleteUndoable extends ActorDeleteUndoable {
    @Override
    public String getRedoString() {
        return "Redo \"Delete TextField\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Delete TextField\"";
    }
}
