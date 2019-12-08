package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class VerticalGroupSpaceUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimVerticalGroup verticalGroup;
    private DialogSceneComposer dialog;
    private float space;
    private float previousSpace;
    
    public VerticalGroupSpaceUndoable(float space) {
        this.space = space;
        dialog = DialogSceneComposer.dialog;
        verticalGroup = (DialogSceneComposerModel.SimVerticalGroup) dialog.simActor;

        previousSpace = verticalGroup.space;
    }
    
    @Override
    public void undo() {
        verticalGroup.space = previousSpace;
        
        if (dialog.simActor != verticalGroup) {
            dialog.simActor = verticalGroup;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {

        verticalGroup.space = space;
        
        if (dialog.simActor != verticalGroup) {
            dialog.simActor = verticalGroup;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"VerticalGroup space " + space + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"VerticalGroup space " + space + "\"";
    }
}
