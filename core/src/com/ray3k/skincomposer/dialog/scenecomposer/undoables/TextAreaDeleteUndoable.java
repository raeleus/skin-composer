package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public class TextAreaDeleteUndoable extends ActorDeleteUndoable {
    @Override
    public String getRedoString() {
        return "Redo \"Delete TextArea\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Delete TextArea\"";
    }
}
