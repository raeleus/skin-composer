package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ContainerFillUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimContainer container;
    private DialogSceneComposer dialog;
    private boolean fillX;
    private boolean fillY;
    private boolean previousFillX;
    private boolean previousFillY;
    
    public ContainerFillUndoable(boolean fillX, boolean fillY) {
        this.fillX = fillX;
        this.fillY = fillY;
        dialog = DialogSceneComposer.dialog;
        container = (DialogSceneComposerModel.SimContainer) dialog.simActor;

        previousFillX = container.fillX;
        previousFillY = container.fillY;
    }
    
    @Override
    public void undo() {
        container.fillX = previousFillX;
        container.fillY = previousFillY;
        
        if (dialog.simActor != container) {
            dialog.simActor = container;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {

        container.fillX = fillX;
        container.fillY = fillY;
        
        if (dialog.simActor != container) {
            dialog.simActor = container;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Container fill\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Container fill\"";
    }
}
