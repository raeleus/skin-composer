package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class CellUniformUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimCell cell;
    private DialogSceneComposer dialog;
    private boolean uniformX;
    private boolean uniformY;
    private boolean previousUniformX;
    private boolean previousUniformY;
    
    public CellUniformUndoable(boolean uniformX, boolean uniformY) {
        this.uniformX = uniformX;
        this.uniformY = uniformY;
        dialog = DialogSceneComposer.dialog;
        cell = (DialogSceneComposerModel.SimCell) dialog.simActor;
        
        previousUniformX = cell.uniformX;
        previousUniformY = cell.uniformY;
    }
    
    @Override
    public void undo() {
        cell.uniformX = previousUniformX;
        cell.uniformY = previousUniformY;
    
        if (dialog.simActor != cell) {
            dialog.simActor = cell;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        cell.uniformX = uniformX;
        cell.uniformY = uniformY;
    
        if (dialog.simActor != cell) {
            dialog.simActor = cell;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Cell Uniform\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Cell Uniform\"";
    }
}
