package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public class CheckBoxDeleteUndoable extends ActorDeleteUndoable {
    @Override
    public String getRedoString() {
        return "Redo \"Delete CheckBox\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Delete CheckBox\"";
    }
}
