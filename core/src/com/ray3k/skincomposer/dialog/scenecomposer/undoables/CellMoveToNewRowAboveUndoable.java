package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class CellMoveToNewRowAboveUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimCell cell;
    private DialogSceneComposer dialog;
    private DialogSceneComposerModel.SimTable table;
    private int oldColumn;
    
    public CellMoveToNewRowAboveUndoable() {
        dialog = DialogSceneComposer.dialog;
        
        cell = (DialogSceneComposerModel.SimCell) dialog.simActor;
        table = (DialogSceneComposerModel.SimTable) cell.parent;
        oldColumn = cell.column;
    }
    
    @Override
    public void undo() {
        cell.column = oldColumn;
        table.cells.removeValue(cell, true);
        
        for (var currentCell : table.cells) {
            if (currentCell.row >= cell.row) {
                currentCell.row--;
            }
        }
    
        for (var currentCell : table.cells) {
            if (currentCell.row == cell.row && currentCell.column >= oldColumn) {
                currentCell.column++;
            }
        }
    
        table.cells.add(cell);
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
        cell.column = 0;
        table.cells.removeValue(cell, true);
        
        for (var currentCell : table.cells) {
            if (currentCell.row == cell.row && currentCell.column > oldColumn) {
                currentCell.column--;
            }
        }
        
        for (var currentCell : table.cells) {
            if (currentCell.row >= cell.row) {
                currentCell.row++;
            }
        }
    
        table.cells.add(cell);
        table.sort();
        
        if (dialog.simActor != cell) {
            dialog.simActor = cell;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Move Cell to New Row Above\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Move Cell to New Row Above\"";
    }
}
