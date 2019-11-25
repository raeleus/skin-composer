package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.utils.Align;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TableAlignmentUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTable table;
    private int alignment;
    private int previousAlignment;
    private DialogSceneComposer dialog;
    
    public TableAlignmentUndoable(int alignment) {
        dialog = DialogSceneComposer.dialog;
        
        this.alignment = alignment;
        table = (DialogSceneComposerModel.SimTable) dialog.simActor;
        previousAlignment = table.alignment;
    }
    
    @Override
    public void undo() {
        table.alignment = previousAlignment;
        
        if (dialog.simActor != table) {
            dialog.simActor = table;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        table.alignment = alignment;
        
        if (dialog.simActor != table) {
            dialog.simActor = table;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Table Alignment: " + Align.toString(alignment) + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Table Alignment: " + Align.toString(alignment) + "\"";
    }
}
