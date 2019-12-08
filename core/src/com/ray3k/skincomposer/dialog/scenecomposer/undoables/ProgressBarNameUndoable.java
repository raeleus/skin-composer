package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ProgressBarNameUndoable implements  SceneComposerUndoable {
    private DialogSceneComposerModel.SimProgressBar progressBar;
    private String name;
    private String previousName;
    private DialogSceneComposer dialog;
    
    public ProgressBarNameUndoable(String name) {
        dialog = DialogSceneComposer.dialog;
        progressBar = (DialogSceneComposerModel.SimProgressBar) dialog.simActor;
        if (name != null && name.equals("")) {
            name = null;
        }
        this.name = name;
        previousName = progressBar.name;
    }
    
    @Override
    public void undo() {
        progressBar.name = previousName;
        
        if (dialog.simActor != progressBar) {
            dialog.simActor = progressBar;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        progressBar.name = name;
        
        if (dialog.simActor != progressBar) {
            dialog.simActor = progressBar;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"ProgressBar Name: " + name + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ProgressBar Name: " + name + "\"";
    }
}
