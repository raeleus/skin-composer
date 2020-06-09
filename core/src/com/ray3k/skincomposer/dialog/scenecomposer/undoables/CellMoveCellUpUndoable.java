package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class CellMoveCellUpUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimCell cell;
    private DialogSceneComposer dialog;
    private DialogSceneComposerModel.SimTable table;
    private int column;
    private int oldColumn;
    private boolean moveRows;
    
    public CellMoveCellUpUndoable() {
        dialog = DialogSceneComposer.dialog;
        
        cell = (DialogSceneComposerModel.SimCell) dialog.simActor;
        table = (DialogSceneComposerModel.SimTable) cell.parent;
        oldColumn = cell.column;
        column = Math.min(table.getColumns(cell.row - 1), cell.column);
        moveRows = table.getColumns(cell.row) == 1;
    }
    
    @Override
    public void undo() {
        if (moveRows) {
            for (var currentCell : table.cells) {
                if (currentCell.row >= cell.row + 1) {
                    currentCell.row++;
                }
            }
        }
        
        for (var currentCell : table.cells) {
            if (currentCell.row == cell.row && currentCell.column > column) {
                currentCell.column--;
            }
        }
    
        for (var currentCell : table.cells) {
            if (currentCell.row == cell.row + 1 && currentCell.column >= oldColumn) {
                currentCell.column++;
            }
        }
        
        cell.row++;
        cell.column = oldColumn;
        
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
        for (var currentCell : table.cells) {
            if (currentCell.row == cell.row && currentCell.column > oldColumn) {
                currentCell.column--;
            }
        }
    
        for (var currentCell : table.cells) {
            if (currentCell.row == cell.row - 1 && currentCell.column >= column) {
                currentCell.column++;
            }
        }
        
        cell.row--;
        cell.column = column;
        
        if (moveRows) {
            for (var currentCell : table.cells) {
                if (currentCell.row >= cell.row + 1) {
                    currentCell.row--;
                }
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
    public String getRedoString() {
        return "Redo \"Move Cell Up\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Move Cell Up\"";
    }
}
