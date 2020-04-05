package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimCell;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimTable;

public class TableSetCellsUndoable implements SceneComposerUndoable {
    private SimTable table;
    private DialogSceneComposer dialog;
    private DialogSceneComposerModel model;
    private int columns;
    private int rows;
    private Array<SimCell> cellsPrevious;
    private Array<SimCell> cells;
    
    public TableSetCellsUndoable(int columns, int rows) {
        dialog = DialogSceneComposer.dialog;
        model = dialog.model;
        table = (DialogSceneComposerModel.SimTable) dialog.simActor;
        cellsPrevious = table.cells;
        cells = new Array<>();
        
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                var cell = new DialogSceneComposerModel.SimCell();
                cell.row = row;
                cell.column = column;
                cell.parent = table;
                cells.add(cell);
            }
        }
        
        this.columns = columns;
        this.rows = rows;
    }
    
    @Override
    public void undo() {
        table.cells = cellsPrevious;
        
        if (dialog.simActor != table) {
            dialog.simActor = table;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        table.cells = cells;
        
        if (dialog.simActor != table) {
            dialog.simActor = table;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Set cells for Table (" +  columns + "x" + rows + ")\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Set cells for Table (" +  columns + "x" + rows + ")\"";
    }
}
