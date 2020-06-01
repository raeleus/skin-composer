package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class CellDuplicateCellAboveUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimCell cell;
    private DialogSceneComposerModel.SimCell newCell;
    private DialogSceneComposer dialog;
    private DialogSceneComposerModel.SimTable table;
    
    public CellDuplicateCellAboveUndoable() {
        dialog = DialogSceneComposer.dialog;
        
        cell = (DialogSceneComposerModel.SimCell) dialog.simActor;
        table = (DialogSceneComposerModel.SimTable) cell.parent;
        
        newCell = cell.duplicate();
        newCell.column = Math.min(cell.column, table.getColumns(cell.row - 1));
        newCell.row = cell.row - 1;
        newCell.parent = table;
    }
    
    @Override
    public void undo() {
        for (var currentCell : table.cells) {
            if (currentCell.row == newCell.row && currentCell.column > newCell.column) {
                currentCell.column--;
            }
        }
        
        table.cells.removeValue(newCell, true);
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
            if (currentCell.row == newCell.row && currentCell.column >= newCell.column) {
                currentCell.column++;
            }
        }
        
        table.cells.add(newCell);
        table.sort();
        
        if (dialog.simActor != newCell) {
            dialog.simActor = newCell;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Duplicate Cell to Row Above\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Duplicate Cell to Row Above\"";
    }
}
