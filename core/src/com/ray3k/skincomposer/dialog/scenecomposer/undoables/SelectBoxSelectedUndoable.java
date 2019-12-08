package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class SelectBoxSelectedUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimSelectBox selectBox;
    private DialogSceneComposer dialog;
    private int selected;
    private int previousSelected;
    
    public SelectBoxSelectedUndoable(int selected) {
        this.selected = selected;
        dialog = DialogSceneComposer.dialog;
        selectBox = (DialogSceneComposerModel.SimSelectBox) dialog.simActor;
        previousSelected = selectBox.selected;
    }
    
    @Override
    public void undo() {
        selectBox.selected = previousSelected;
        
        if (dialog.simActor != selectBox) {
            dialog.simActor = selectBox;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        selectBox.selected = selected;
        
        if (dialog.simActor != selectBox) {
            dialog.simActor = selectBox;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"SelectBox selected " + selected + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"SelectBox selected " + selected + "\"";
    }
}
