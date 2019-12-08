package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class LabelNameUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimLabel label;
    private String name;
    private String previousName;
    private DialogSceneComposer dialog;
    
    public LabelNameUndoable(String name) {
        this.name = name;
        dialog = DialogSceneComposer.dialog;
        label = (DialogSceneComposerModel.SimLabel) dialog.simActor;
        if (name != null && name.equals("")) {
            this.name = null;
        }
        previousName = label.name;
    }
    
    @Override
    public void undo() {
        label.name = previousName;
    
        if (dialog.simActor != label) {
            dialog.simActor = label;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        label.name = name;
    
        if (dialog.simActor != label) {
            dialog.simActor = label;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Label name " + name + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Label name " + name + "\"";
    }
}
