package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class VerticalGroupNameUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimVerticalGroup verticalGroup;
    private String name;
    private String previousName;
    private DialogSceneComposer dialog;
    
    public VerticalGroupNameUndoable(String name) {
        this.name = name;
        dialog = DialogSceneComposer.dialog;
        verticalGroup = (DialogSceneComposerModel.SimVerticalGroup) dialog.simActor;
        if (name != null && name.equals("")) {
            this.name = null;
        }
        previousName = verticalGroup.name;
    }
    
    @Override
    public void undo() {
        verticalGroup.name = previousName;
    
        if (dialog.simActor != verticalGroup) {
            dialog.simActor = verticalGroup;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        verticalGroup.name = name;
    
        if (dialog.simActor != verticalGroup) {
            dialog.simActor = verticalGroup;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"VerticalGroup name " + name + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"VerticalGroup name " + name + "\"";
    }
}
