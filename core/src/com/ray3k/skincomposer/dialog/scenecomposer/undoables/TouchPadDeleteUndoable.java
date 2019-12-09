package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public class TouchPadDeleteUndoable extends ActorDeleteUndoable {
    @Override
    public String getRedoString() {
        return "Redo \"Delete TouchPad\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Delete TouchPad\"";
    }
}
