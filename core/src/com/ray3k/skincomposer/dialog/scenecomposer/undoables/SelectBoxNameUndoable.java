package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class SelectBoxNameUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimSelectBox selectBox;
    private String name;
    private String previousName;
    private DialogSceneComposer dialog;
    
    public SelectBoxNameUndoable(String name) {
        this.name = name;
        dialog = DialogSceneComposer.dialog;
        selectBox = (DialogSceneComposerModel.SimSelectBox) dialog.simActor;
        if (name != null && name.equals("")) {
            this.name = null;
        }
        previousName = selectBox.name;
    }
    
    @Override
    public void undo() {
        selectBox.name = previousName;
    
        if (dialog.simActor != selectBox) {
            dialog.simActor = selectBox;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        selectBox.name = name;
    
        if (dialog.simActor != selectBox) {
            dialog.simActor = selectBox;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"SelectBox name " + name + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"SelectBox name " + name + "\"";
    }
}
