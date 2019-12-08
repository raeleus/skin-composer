package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class VerticalGroupWrapSpaceUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimVerticalGroup verticalGroup;
    private DialogSceneComposer dialog;
    private float wrapSpace;
    private float previousWrapSpace;
    
    public VerticalGroupWrapSpaceUndoable(float wrapSpace) {
        this.wrapSpace = wrapSpace;
        dialog = DialogSceneComposer.dialog;
        verticalGroup = (DialogSceneComposerModel.SimVerticalGroup) dialog.simActor;

        previousWrapSpace = verticalGroup.wrapSpace;
    }
    
    @Override
    public void undo() {
        verticalGroup.wrapSpace = previousWrapSpace;
        
        if (dialog.simActor != verticalGroup) {
            dialog.simActor = verticalGroup;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {

        verticalGroup.wrapSpace = wrapSpace;
        
        if (dialog.simActor != verticalGroup) {
            dialog.simActor = verticalGroup;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"VerticalGroup wrapSpace " + wrapSpace + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"VerticalGroup wrapSpace " + wrapSpace + "\"";
    }
}
