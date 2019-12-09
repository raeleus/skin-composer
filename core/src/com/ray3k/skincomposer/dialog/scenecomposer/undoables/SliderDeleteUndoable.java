package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

public class SliderDeleteUndoable extends ActorDeleteUndoable {
    @Override
    public String getRedoString() {
        return "Redo \"Delete Slider\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Delete Slider\"";
    }
}
