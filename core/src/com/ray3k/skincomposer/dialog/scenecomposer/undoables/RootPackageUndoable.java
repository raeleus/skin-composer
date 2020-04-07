package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import static com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.rootActor;

public class RootPackageUndoable implements SceneComposerUndoable {
    private String packageString;
    private String previousPackageString;
    
    public RootPackageUndoable(String packageString) {
        this.packageString = packageString;
        if (packageString != null && packageString.equals("")) {
            this.packageString = null;
        }
        previousPackageString = rootActor.packageString;
    }
    
    @Override
    public void undo() {
        rootActor.packageString = previousPackageString;
    }
    
    @Override
    public void redo() {
        rootActor.packageString = packageString;
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Package name " + packageString + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Package name " + packageString + "\"";
    }
}
