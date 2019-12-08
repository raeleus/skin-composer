package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class SelectBoxMaxListCountUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimSelectBox selectBox;
    private DialogSceneComposer dialog;
    private int maxListCount;
    private int previousMaxListCount;
    
    public SelectBoxMaxListCountUndoable(int maxListCount) {
        this.maxListCount = maxListCount;
        dialog = DialogSceneComposer.dialog;
        selectBox = (DialogSceneComposerModel.SimSelectBox) dialog.simActor;
        previousMaxListCount = selectBox.maxListCount;
    }
    
    @Override
    public void undo() {
        selectBox.maxListCount = previousMaxListCount;
        
        if (dialog.simActor != selectBox) {
            dialog.simActor = selectBox;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        selectBox.maxListCount = maxListCount;
        
        if (dialog.simActor != selectBox) {
            dialog.simActor = selectBox;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"SelectBox max list count " + maxListCount + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"SelectBox max list count " + maxListCount + "\"";
    }
}
