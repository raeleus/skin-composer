package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimRootGroup;

import static com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.rootActor;

public class RootAddTableUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTable table;
    private DialogSceneComposer dialog;
    private DialogSceneComposerModel model;
    private SimRootGroup group;
    private int columns;
    private int rows;
    
    public RootAddTableUndoable(int columns, int rows) {
        dialog = DialogSceneComposer.dialog;
        model = dialog.model;
        group = (SimRootGroup) dialog.simActor;
        
        table = new DialogSceneComposerModel.SimTable();
        table.parent = group;
        table.fillParent = true;
        
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
        rootActor.children.removeValue(table, true);
        
        if (dialog.simActor != group) {
            dialog.simActor = group;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        rootActor.children.add(table);
        
        if (dialog.simActor != table) {
            dialog.simActor = table;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
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
