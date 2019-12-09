package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public class ScrollPaneDeleteUndoable extends ActorDeleteUndoable {
    @Override
    public String getRedoString() {
        return "Redo \"Delete ScrollPane\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Delete ScrollPane\"";
    }
}
