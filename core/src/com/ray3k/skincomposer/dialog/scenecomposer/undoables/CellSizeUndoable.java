package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class CellSizeUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimCell cell;
    private DialogSceneComposer dialog;
    private float minWidth;
    private float minHeight;
    private float maxWidth;
    private float maxHeight;
    private float preferredWidth;
    private float preferredHeight;
    private float previousMinWidth;
    private float previousMinHeight;
    private float previousMaxWidth;
    private float previousMaxHeight;
    private float previousPreferredWidth;
    private float previousPreferredHeight;
    
    public CellSizeUndoable(float minWidth, float minHeight, float maxWidth, float maxHeight, float preferredWidth, float preferredHeight) {
        this.minWidth = minWidth;
        this.minHeight = minHeight;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.preferredWidth = preferredWidth;
        this.preferredHeight = preferredHeight;
        dialog = DialogSceneComposer.dialog;
        cell = (DialogSceneComposerModel.SimCell) dialog.simActor;
        
        previousMinWidth = cell.minWidth;
        previousMinHeight = cell.minHeight;
        previousMaxWidth = cell.maxWidth;
        previousMaxHeight = cell.maxHeight;
        previousPreferredWidth = cell.preferredWidth;
        previousPreferredHeight = cell.preferredHeight;
    }
    
    @Override
    public void undo() {
        cell.minWidth = previousMinWidth;
        cell.minHeight = previousMinHeight;
        cell.maxWidth = previousMaxWidth;
        cell.maxHeight = previousMaxHeight;
        cell.preferredWidth = previousPreferredWidth;
        cell.preferredHeight = previousPreferredHeight;
        
        if (dialog.simActor != cell) {
            dialog.simActor = cell;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        cell.minWidth = minWidth;
        cell.minHeight = minHeight;
        cell.maxWidth = maxWidth;
        cell.maxHeight = maxHeight;
        cell.preferredWidth = preferredWidth;
        cell.preferredHeight = preferredHeight;
        
        if (dialog.simActor != cell) {
            dialog.simActor = cell;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Cell Size\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Cell Size\"";
    }
}
