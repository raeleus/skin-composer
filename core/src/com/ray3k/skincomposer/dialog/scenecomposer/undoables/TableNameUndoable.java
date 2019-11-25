package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TableNameUndoable implements  SceneComposerUndoable {
    private DialogSceneComposerModel.SimTable table;
    private String name;
    private String previousName;
    private DialogSceneComposer dialog;
    
    public TableNameUndoable(String name) {
        dialog = DialogSceneComposer.dialog;
        table = (DialogSceneComposerModel.SimTable) dialog.simActor;
        if (name != null && name.equals("")) {
            name = null;
        }
        this.name = name;
        previousName = table.name;
    }
    
    @Override
    public void undo() {
        table.name = previousName;
        
        if (dialog.simActor != table) {
            dialog.simActor = table;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        table.name = name;
        
        if (dialog.simActor != table) {
            dialog.simActor = table;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
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
