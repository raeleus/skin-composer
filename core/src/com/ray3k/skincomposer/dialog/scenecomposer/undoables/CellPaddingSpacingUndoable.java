package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class CellPaddingSpacingUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimCell cell;
    private DialogSceneComposer dialog;
    private float padLeft;
    private float padRight;
    private float padTop;
    private float padBottom;
    private float spaceLeft;
    private float spaceRight;
    private float spaceTop;
    private float spaceBottom;
    private float previousPadLeft;
    private float previousPadRight;
    private float previousPadTop;
    private float previousPadBottom;
    private float previousSpaceLeft;
    private float previousSpaceRight;
    private float previousSpaceTop;
    private float previousSpaceBottom;
    
    public CellPaddingSpacingUndoable(float padLeft, float padRight, float padTop, float padBottom, float spaceLeft, float spaceRight, float spaceTop, float spaceBottom) {
        this.padLeft = padLeft;
        this.padRight = padRight;
        this.padTop = padTop;
        this.padBottom = padBottom;
        this.spaceLeft = spaceLeft;
        this.spaceRight = spaceRight;
        this.spaceTop = spaceTop;
        this.spaceBottom = spaceBottom;
        dialog = DialogSceneComposer.dialog;
        cell = (DialogSceneComposerModel.SimCell) dialog.simActor;
        
        previousPadLeft = cell.padLeft;
        previousPadRight = cell.padRight;
        previousPadTop = cell.padTop;
        previousPadBottom = cell.padBottom;
        previousSpaceLeft = cell.spaceLeft;
        previousSpaceRight = cell.spaceRight;
        previousSpaceTop = cell.spaceTop;
        previousSpaceBottom = cell.spaceBottom;
    }
    
    @Override
    public void undo() {
        cell.padLeft = previousPadLeft;
        cell.padRight = previousPadRight;
        cell.padTop = previousPadTop;
        cell.padBottom = previousPadBottom;
        cell.spaceLeft = previousSpaceLeft;
        cell.spaceRight = previousSpaceRight;
        cell.spaceTop = previousSpaceTop;
        cell.spaceBottom = previousSpaceBottom;
        
        if (dialog.simActor != cell) {
            dialog.simActor = cell;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        cell.padLeft = padLeft;
        cell.padRight = padRight;
        cell.padTop = padTop;
        cell.padBottom = padBottom;
        cell.spaceLeft = spaceLeft;
        cell.spaceRight = spaceRight;
        cell.spaceTop = spaceTop;
        cell.spaceBottom = spaceBottom;
        
        if (dialog.simActor != cell) {
            dialog.simActor = cell;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Cell Padding/Spacing\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Cell Padding/Spacing\"";
    }
}
