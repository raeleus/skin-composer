package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class RootAddTableUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTable table;
    private DialogSceneComposer dialog;
    private DialogSceneComposerModel model;
    private DialogSceneComposerModel.SimGroup group;
    private int columns;
    private int rows;
    
    public RootAddTableUndoable(int columns, int rows) {
        dialog = DialogSceneComposer.dialog;
        model = dialog.model;
        group = (DialogSceneComposerModel.SimGroup) dialog.simActor;
        
        table = new DialogSceneComposerModel.SimTable();
        table.parent = group;
        
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                var cell = new DialogSceneComposerModel.SimCell();
                cell.row = row;
                cell.column = column;
                cell.parent = table;
                table.cells.add(cell);
            }
        }
        
        this.columns = columns;
        this.rows = rows;
    }
    
    @Override
    public void undo() {
        model.root.children.removeValue(table, true);
        
        if (dialog.simActor != group) {
            dialog.simActor = group;
            dialog.populateProperties();
            dialog.populatePath();
        }
    }
    
    @Override
    public void redo() {
        model.root.children.add(table);
        
        if (dialog.simActor != table) {
            dialog.simActor = table;
            dialog.populateProperties();
            dialog.populatePath();
        }
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Add Table (" +  columns + "x" + rows + ")\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Add Table (" +  columns + "x" + rows + ")\"";
    }
}
