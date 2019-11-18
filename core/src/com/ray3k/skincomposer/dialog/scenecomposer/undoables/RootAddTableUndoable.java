package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ObjectMap;
import com.ray3k.skincomposer.dialog.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class RootAddTableUndoable implements SceneComposerUndoable {
    private Table table;
    private DialogSceneComposer dialog;
    private DialogSceneComposerModel model;
    private Object previousSelected;
    private int columns;
    private int rows;
    
    public RootAddTableUndoable(int columns, int rows) {
        dialog = DialogSceneComposer.dialog;
        model = dialog.model;
        
        table = new Table();
        table.setUserObject(new ObjectMap<String, Object>());
        
        for (int j = 0; j < rows; j++) {
            if (j > 0) table.row();
            for (int i = 0; i < columns; i++) {
                table.add();
            }
        }
        
        previousSelected = dialog.selectedObject;
        this.columns = columns;
        this.rows = rows;
    }
    
    @Override
    public void undo() {
        model.stage.getRoot().removeActor(table);
        dialog.selectedObject = previousSelected;
        dialog.populateProperties();
        dialog.populatePath();
    }
    
    @Override
    public void redo() {
        model.stage.addActor(table);
        dialog.selectedObject = table;
        dialog.populateProperties();
        dialog.populatePath();
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
