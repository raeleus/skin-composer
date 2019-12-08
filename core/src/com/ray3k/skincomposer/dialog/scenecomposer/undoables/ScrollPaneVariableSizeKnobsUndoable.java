package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ScrollPaneVariableSizeKnobsUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimScrollPane scrollPane;
    private DialogSceneComposer dialog;
    private boolean variableSizeKnobs;
    private boolean previousVariableSizeKnobs;
    
    public ScrollPaneVariableSizeKnobsUndoable(boolean variableSizeKnobs) {
        this.variableSizeKnobs = variableSizeKnobs;
        dialog = DialogSceneComposer.dialog;
        scrollPane = (DialogSceneComposerModel.SimScrollPane) dialog.simActor;
        previousVariableSizeKnobs = scrollPane.variableSizeKnobs;
    }
    
    @Override
    public void undo() {
        scrollPane.variableSizeKnobs = previousVariableSizeKnobs;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        scrollPane.variableSizeKnobs = variableSizeKnobs;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"ScrollPane variable size knobs " + variableSizeKnobs + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ScrollPane variable size knobs " + variableSizeKnobs + "\"";
    }
}
