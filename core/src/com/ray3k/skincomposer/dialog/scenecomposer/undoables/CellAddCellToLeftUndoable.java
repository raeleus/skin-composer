package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class CellAddCellToLeftUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimCell cell;
    private DialogSceneComposerModel.SimCell newCell;
    private DialogSceneComposer dialog;
    private DialogSceneComposerModel.SimTable table;
    
    public CellAddCellToLeftUndoable() {
        dialog = DialogSceneComposer.dialog;
        
        cell = (DialogSceneComposerModel.SimCell) dialog.simActor;
        table = (DialogSceneComposerModel.SimTable) cell.parent;
        
        newCell = new DialogSceneComposerModel.SimCell();
        newCell.column = cell.column;
        newCell.row = cell.row;
        newCell.parent = table;
    }
    
    @Override
    public void undo() {
        table.cells.removeValue(newCell, true);
        
        for (var currentCell : table.cells) {
            if (currentCell.row == newCell.row && currentCell.column >= newCell.column) {
                currentCell.column--;
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
        return "Redo \"Add Cell to Left\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Add Cell to Left\"";
    }
}
