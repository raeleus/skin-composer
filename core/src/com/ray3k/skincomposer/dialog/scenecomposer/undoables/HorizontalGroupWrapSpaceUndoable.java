package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class HorizontalGroupWrapSpaceUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimHorizontalGroup horizontalGroup;
    private DialogSceneComposer dialog;
    private float wrapSpace;
    private float previousWrapSpace;
    
    public HorizontalGroupWrapSpaceUndoable(float wrapSpace) {
        this.wrapSpace = wrapSpace;
        dialog = DialogSceneComposer.dialog;
        horizontalGroup = (DialogSceneComposerModel.SimHorizontalGroup) dialog.simActor;

        previousWrapSpace = horizontalGroup.wrapSpace;
    }
    
    @Override
    public void undo() {
        horizontalGroup.wrapSpace = previousWrapSpace;
        
        if (dialog.simActor != horizontalGroup) {
            dialog.simActor = horizontalGroup;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {

        horizontalGroup.wrapSpace = wrapSpace;
        
        if (dialog.simActor != horizontalGroup) {
            dialog.simActor = horizontalGroup;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"HorizontalGroup wrapSpace " + wrapSpace + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"HorizontalGroup wrapSpace " + wrapSpace + "\"";
    }
}
