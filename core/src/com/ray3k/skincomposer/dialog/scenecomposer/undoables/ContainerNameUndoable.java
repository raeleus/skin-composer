package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ContainerNameUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimContainer container;
    private String name;
    private String previousName;
    private DialogSceneComposer dialog;
    
    public ContainerNameUndoable(String name) {
        this.name = name;
        dialog = DialogSceneComposer.dialog;
        container = (DialogSceneComposerModel.SimContainer) dialog.simActor;
        if (name != null && name.equals("")) {
            this.name = null;
        }
        previousName = container.name;
    }
    
    @Override
    public void undo() {
        container.name = previousName;
    
        if (dialog.simActor != container) {
            dialog.simActor = container;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        container.name = name;
    
        if (dialog.simActor != container) {
            dialog.simActor = container;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Container name " + name + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Container name " + name + "\"";
    }
}
