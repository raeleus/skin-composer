package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class CellDeleteUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimCell cell;
    private DialogSceneComposerModel.SimTable table;
    private DialogSceneComposer dialog;
    private boolean verticalAdjust;
    private Array<DialogSceneComposerModel.SimCell> verticalAdjustCells;
    private Array<DialogSceneComposerModel.SimCell> horizontalAdjustCells;
    
    public CellDeleteUndoable() {
        dialog = DialogSceneComposer.dialog;
        cell = (DialogSceneComposerModel.SimCell) dialog.simActor;
        table = (DialogSceneComposerModel.SimTable) cell.parent;
    
        verticalAdjust = true;
        horizontalAdjustCells = new Array<>();
        verticalAdjustCells = new Array<>();
        for (var currentCell : table.cells) {
            if (currentCell != cell) {
                if (verticalAdjust && currentCell.row > cell.row) {
                    verticalAdjustCells.add(currentCell);
                } else if (currentCell.row == cell.row) {
                    verticalAdjust = false;
                    if (currentCell.column > cell.column) horizontalAdjustCells.add(currentCell);
                }
            }
        }
    }
    
    @Override
    public void undo() {
        table.cells.add(cell);
    
        if (verticalAdjust) {
            for (var currentCell : verticalAdjustCells) {
                currentCell.row++;
            }
        } else {
            for (var currentCell : horizontalAdjustCells) {
                currentCell.column++;
            }
        }
        table.sort();
        
        if (dialog.simActor != cell) {
            dialog.simActor = cell;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        table.cells.removeValue(cell, true);
    
        if (verticalAdjust) {
            for (var currentCell : verticalAdjustCells) {
                currentCell.row--;
            }
        }  else {
            for (var currentCell : horizontalAdjustCells) {
                currentCell.column--;
            }
        }
        table.sort();
        
        if (dialog.simActor != table) {
            dialog.simActor = table;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Delete Cell\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Delete Cell\"";
    }
}
