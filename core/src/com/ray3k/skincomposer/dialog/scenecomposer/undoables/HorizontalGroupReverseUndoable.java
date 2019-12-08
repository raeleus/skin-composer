package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class HorizontalGroupReverseUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimHorizontalGroup horizontalGroup;
    private DialogSceneComposer dialog;
    private boolean reverse;
    private boolean previousReverse;
    
    public HorizontalGroupReverseUndoable(boolean reverse) {
        this.reverse = reverse;
        dialog = DialogSceneComposer.dialog;
        horizontalGroup = (DialogSceneComposerModel.SimHorizontalGroup) dialog.simActor;
        previousReverse = horizontalGroup.reverse;
    }
    
    @Override
    public void undo() {
        horizontalGroup.reverse = previousReverse;
        
        if (dialog.simActor != horizontalGroup) {
            dialog.simActor = horizontalGroup;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        horizontalGroup.reverse = reverse;
        
        if (dialog.simActor != horizontalGroup) {
            dialog.simActor = horizontalGroup;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"HorizontalGroup reverse " + reverse + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"HorizontalGroup reverse " + reverse + "\"";
    }
}
