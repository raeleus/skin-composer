package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class CellMoveCellLeftUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimCell cell;
    private DialogSceneComposerModel.SimCell otherCell;
    private DialogSceneComposer dialog;
    private DialogSceneComposerModel.SimTable table;
    
    public CellMoveCellLeftUndoable() {
        dialog = DialogSceneComposer.dialog;
        
        cell = (DialogSceneComposerModel.SimCell) dialog.simActor;
        table = (DialogSceneComposerModel.SimTable) cell.parent;
        
        otherCell = table.getCell(cell.column - 1, cell.row);
    }
    
    @Override
    public void undo() {
        cell.column++;
        otherCell.column--;
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
        cell.column--;
        otherCell.column++;
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
        return "Redo \"Move Cell Left\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Move Cell Left\"";
    }
}
