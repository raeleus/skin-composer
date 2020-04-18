package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.data.DrawableData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TableBackgroundUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTable table;
    private DrawableData background;
    private DrawableData previousBackground;
    private DialogSceneComposer dialog;
    
    public TableBackgroundUndoable(DrawableData background) {
        dialog = DialogSceneComposer.dialog;
        table = (DialogSceneComposerModel.SimTable) dialog.simActor;
        this.background = background;
        previousBackground = table.background;
    }
    
    @Override
    public void undo() {
        table.background = previousBackground;
        
        if (dialog.simActor != table) {
            dialog.simActor = table;
            dialog.populatePath();
        }
        dialog.populateProperties();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        table.background = background;
        
        if (dialog.simActor != table) {
            dialog.simActor = table;
            dialog.populatePath();
        }
        dialog.populateProperties();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Table Background: " + (background == null ? "null" : background.name) + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Table Background: " + (background == null ? "null" : background.name) + "\"";
    }
}
