package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class CellResetUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimCell cell;
    private DialogSceneComposer dialog;
    private DialogSceneComposerModel.SimActor previousChild;
    private int previousAlignment;
    private boolean previousExpandX;
    private boolean previousExpandY;
    private boolean previousFillX;
    private boolean previousFillY;
    private boolean previousGrowX;
    private boolean previousGrowY;
    private float previousPadLeft;
    private float previousPadRight;
    private float previousPadTop;
    private float previousPadBottom;
    private float previousSpaceLeft;
    private float previousSpaceRight;
    private float previousSpaceTop;
    private float previousSpaceBottom;
    private float previousMinWidth;
    private float previousMinHeight;
    private float previousMaxWidth;
    private float previousMaxHeight;
    private float previousPreferredWidth;
    private float previousPreferredHeight;
    private boolean previousUniformX;
    private boolean previousUniformY;
    private int previousColSpan;
    private int column;
    private int row;
    
    public CellResetUndoable() {
        dialog = DialogSceneComposer.dialog;
        cell = (DialogSceneComposerModel.SimCell) dialog.simActor;
        
        previousChild = cell.child;
        previousAlignment = cell.alignment;
        previousExpandX = cell.expandX;
        previousExpandY = cell.expandY;
        previousFillX = cell.fillX;
        previousFillY = cell.fillY;
        previousGrowX = cell.growX;
        previousGrowY = cell.growY;
        previousPadLeft = cell.padLeft;
        previousPadRight = cell.padRight;
        previousPadTop = cell.padTop;
        previousPadBottom = cell.padBottom;
        previousSpaceLeft = cell.spaceLeft;
        previousSpaceRight = cell.spaceRight;
        previousSpaceTop = cell.spaceTop;
        previousSpaceBottom = cell.spaceBottom;
        previousMinWidth = cell.minWidth;
        previousMinHeight = cell.minHeight;
        previousMaxWidth = cell.maxWidth;
        previousMaxHeight = cell.maxHeight;
        previousPreferredWidth = cell.preferredWidth;
        previousPreferredHeight = cell.preferredHeight;
        previousUniformX = cell.uniformX;
        previousUniformY = cell.uniformY;
        previousColSpan = cell.colSpan;
        column = cell.column;
        row = cell.row;
    }
    
    @Override
    public void undo() {
        cell.child = previousChild;
        cell.alignment = previousAlignment;
        cell.expandY = previousExpandY;
        cell.expandX = previousExpandX;
        cell.fillX = previousFillX;
        cell.fillY = previousFillY;
        cell.growX = previousGrowX;
        cell.growY = previousGrowY;
        cell.padLeft = previousPadLeft;
        cell.padRight = previousPadRight;
        cell.padTop = previousPadTop;
        cell.padBottom = previousPadBottom;
        cell.spaceLeft = previousSpaceLeft;
        cell.spaceRight = previousSpaceRight;
        cell.spaceTop = previousSpaceTop;
        cell.spaceBottom = previousSpaceBottom;
        cell.minWidth = previousMinWidth;
        cell.minHeight = previousMinHeight;
        cell.maxWidth = previousMaxWidth;
        cell.maxHeight = previousMaxHeight;
        cell.preferredWidth = previousPreferredWidth;
        cell.preferredHeight = previousPreferredHeight;
        cell.uniformX = previousUniformX;
        cell.uniformY = previousUniformY;
        cell.colSpan = previousColSpan;
    
        if (dialog.simActor != cell) {
            dialog.simActor = cell;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        cell.reset();
        cell.column = column;
        cell.row = row;
        
        if (dialog.simActor != cell) {
            dialog.simActor = cell;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Reset Cell\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Reset Cell\"";
    }
}
