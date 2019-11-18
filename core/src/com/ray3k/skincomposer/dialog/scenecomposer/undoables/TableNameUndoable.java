package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.ray3k.skincomposer.dialog.DialogSceneComposer;

public class TableNameUndoable implements  SceneComposerUndoable {
    private Table table;
    private String name;
    private String previousName;
    
    public TableNameUndoable(String name) {
        var dialog = DialogSceneComposer.dialog;
        table = (Table) dialog.selectedObject;
        this.name = name;
        previousName = table.getName();
    }
    
    @Override
    public void undo() {
        table.setName(previousName);
    }
    
    @Override
    public void redo() {
        table.setName(name);
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Table Name: " + name + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Table Name: " + name + "\"";
    }
}
