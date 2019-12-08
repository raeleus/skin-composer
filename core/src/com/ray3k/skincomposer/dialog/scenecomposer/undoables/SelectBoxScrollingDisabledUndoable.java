package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class SelectBoxScrollingDisabledUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimSelectBox selectBox;
    private DialogSceneComposer dialog;
    private boolean scrollingDisabled;
    private boolean previousScrollingDisabled;
    
    public SelectBoxScrollingDisabledUndoable(boolean scrollingDisabled) {
        this.scrollingDisabled = scrollingDisabled;
        dialog = DialogSceneComposer.dialog;
        selectBox = (DialogSceneComposerModel.SimSelectBox) dialog.simActor;
        previousScrollingDisabled = selectBox.scrollingDisabled;
    }
    
    @Override
    public void undo() {
        selectBox.scrollingDisabled = previousScrollingDisabled;
        
        if (dialog.simActor != selectBox) {
            dialog.simActor = selectBox;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        selectBox.scrollingDisabled = scrollingDisabled;
        
        if (dialog.simActor != selectBox) {
            dialog.simActor = selectBox;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"SelectBox scrolling disabled " + scrollingDisabled + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"SelectBox scrolling disabled " + scrollingDisabled + "\"";
    }
}
