package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class HorizontalGroupNameUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimHorizontalGroup horizontalGroup;
    private String name;
    private String previousName;
    private DialogSceneComposer dialog;
    
    public HorizontalGroupNameUndoable(String name) {
        this.name = name;
        dialog = DialogSceneComposer.dialog;
        horizontalGroup = (DialogSceneComposerModel.SimHorizontalGroup) dialog.simActor;
        if (name != null && name.equals("")) {
            this.name = null;
        }
        previousName = horizontalGroup.name;
    }
    
    @Override
    public void undo() {
        horizontalGroup.name = previousName;
    
        if (dialog.simActor != horizontalGroup) {
            dialog.simActor = horizontalGroup;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        horizontalGroup.name = name;
    
        if (dialog.simActor != horizontalGroup) {
            dialog.simActor = horizontalGroup;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"HorizontalGroup name " + name + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"HorizontalGroup name " + name + "\"";
    }
}
