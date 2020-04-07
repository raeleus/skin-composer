package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import static com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.rootActor;

public class RootClassUndoable implements SceneComposerUndoable {
    private String classString;
    private String previousClassString;
    
    public RootClassUndoable(String classString) {
        this.classString = classString;
        if (classString != null && classString.equals("")) {
            this.classString = null;
        }
        previousClassString = rootActor.classString;
    }
    
    @Override
    public void undo() {
        rootActor.classString = previousClassString;
    }
    
    @Override
    public void redo() {
        rootActor.classString = classString;
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Class name " + classString + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Class name " + classString + "\"";
    }
}
