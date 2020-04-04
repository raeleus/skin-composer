package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class VerticalGroupFillUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimVerticalGroup verticalGroup;
    private DialogSceneComposer dialog;
    private boolean fill;
    private boolean previousFill;
    
    public VerticalGroupFillUndoable(boolean fill) {
        this.fill = fill;
        dialog = DialogSceneComposer.dialog;
        verticalGroup = (DialogSceneComposerModel.SimVerticalGroup) dialog.simActor;

        previousFill = verticalGroup.fill;
    }
    
    @Override
    public void undo() {
        verticalGroup.fill = previousFill;
        
        if (dialog.simActor != verticalGroup) {
            dialog.simActor = verticalGroup;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {

        verticalGroup.fill = fill;
        
        if (dialog.simActor != verticalGroup) {
            dialog.simActor = verticalGroup;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"VerticalGroup fill " + fill + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"VerticalGroup fill " + fill + "\"";
    }
}
