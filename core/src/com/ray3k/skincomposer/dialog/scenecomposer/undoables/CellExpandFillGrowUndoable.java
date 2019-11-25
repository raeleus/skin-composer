package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class CellExpandFillGrowUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimCell cell;
    private DialogSceneComposer dialog;
    private boolean expandX;
    private boolean expandY;
    private boolean fillX;
    private boolean fillY;
    private boolean growX;
    private boolean growY;
    private boolean previousExpandX;
    private boolean previousExpandY;
    private boolean previousFillX;
    private boolean previousFillY;
    private boolean previousGrowX;
    private boolean previousGrowY;
    
    public CellExpandFillGrowUndoable(boolean expandX, boolean expandY, boolean fillX, boolean fillY, boolean growX, boolean growY) {
        this.expandX = expandX;
        this.expandY = expandY;
        this.fillX = fillX;
        this.fillY = fillY;
        this.growX = growX;
        this.growY = growY;
        dialog = DialogSceneComposer.dialog;
        cell = (DialogSceneComposerModel.SimCell) dialog.simActor;
        
        previousExpandX = cell.expandX;
        previousExpandY = cell.expandY;
        previousFillX = cell.fillX;
        previousFillY = cell.fillY;
        previousGrowX = cell.growX;
        previousGrowY = cell.growY;
    }
    
    @Override
    public void undo() {
        cell.expandX = previousExpandX;
        cell.expandY = previousExpandY;
        cell.fillX = previousFillX;
        cell.fillY = previousFillY;
        cell.growX = previousGrowX;
        cell.growY = previousGrowY;
        
        if (dialog.simActor != cell) {
            dialog.simActor = cell;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        cell.expandX = expandX;
        cell.expandY = expandY;
        cell.fillX = fillX;
        cell.fillY = fillY;
        cell.growX = growX;
        cell.growY = growY;
        
        if (dialog.simActor != cell) {
            dialog.simActor = cell;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Cell Expand/Fill/Grow\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Cell Expand/Fill/Grow\"";
    }
}
