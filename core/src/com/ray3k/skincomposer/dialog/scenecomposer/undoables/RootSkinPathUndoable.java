package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import static com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.rootActor;

public class RootSkinPathUndoable implements SceneComposerUndoable {
    private String skinPath;
    private String previousSkinPath;
    
    public RootSkinPathUndoable(String skinPath) {
        this.skinPath = skinPath;
        if (skinPath != null && skinPath.equals("")) {
            this.skinPath = null;
        }
        previousSkinPath = rootActor.skinPath;
    }
    
    @Override
    public void undo() {
        rootActor.skinPath = previousSkinPath;
    }
    
    @Override
    public void redo() {
        rootActor.skinPath = skinPath;
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Skin path " + skinPath + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Skin path " + skinPath + "\"";
    }
}
