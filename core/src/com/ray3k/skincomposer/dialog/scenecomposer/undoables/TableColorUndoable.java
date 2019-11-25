package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.data.ColorData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TableColorUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTable table;
    private ColorData color;
    private ColorData previousColor;
    private DialogSceneComposer dialog;
    
    public TableColorUndoable(ColorData color) {
        this.color = color;
        dialog = DialogSceneComposer.dialog;
        table = (DialogSceneComposerModel.SimTable) dialog.simActor;
        previousColor = table.color;
    }
    
    @Override
    public void undo() {
        table.color = previousColor;
        
        if (dialog.simActor != table) {
            dialog.simActor = table;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        table.color = color;
        
        if (dialog.simActor != table) {
            dialog.simActor = table;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Table Color: " + (color == null ? "No Color" : color.getName()) + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Table Color: " + (color == null ? "No Color" : color.getName()) + "\"";
    }
}
