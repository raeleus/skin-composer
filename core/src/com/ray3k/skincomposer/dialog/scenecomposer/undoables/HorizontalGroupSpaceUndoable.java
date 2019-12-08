package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class HorizontalGroupSpaceUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimHorizontalGroup horizontalGroup;
    private DialogSceneComposer dialog;
    private float space;
    private float previousSpace;
    
    public HorizontalGroupSpaceUndoable(float space) {
        this.space = space;
        dialog = DialogSceneComposer.dialog;
        horizontalGroup = (DialogSceneComposerModel.SimHorizontalGroup) dialog.simActor;

        previousSpace = horizontalGroup.space;
    }
    
    @Override
    public void undo() {
        horizontalGroup.space = previousSpace;
        
        if (dialog.simActor != horizontalGroup) {
            dialog.simActor = horizontalGroup;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {

        horizontalGroup.space = space;
        
        if (dialog.simActor != horizontalGroup) {
            dialog.simActor = horizontalGroup;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"HorizontalGroup space " + space + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"HorizontalGroup space " + space + "\"";
    }
}
