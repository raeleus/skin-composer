package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public class SplitPaneDeleteUndoable extends ActorDeleteUndoable {
    @Override
    public String getRedoString() {
        return "Redo \"Delete SplitPane\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Delete SplitPane\"";
    }
}
