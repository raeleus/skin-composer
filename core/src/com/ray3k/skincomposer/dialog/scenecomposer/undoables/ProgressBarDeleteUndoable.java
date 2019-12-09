package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public class ProgressBarDeleteUndoable extends ActorDeleteUndoable {
    @Override
    public String getRedoString() {
        return "Redo \"Delete ProgressBar\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Delete ProgressBar\"";
    }
}
