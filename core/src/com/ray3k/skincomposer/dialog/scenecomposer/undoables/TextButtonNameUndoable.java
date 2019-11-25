package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TextButtonNameUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTextButton textButton;
    private String name;
    private String previousName;
    private DialogSceneComposer dialog;
    
    public TextButtonNameUndoable(String name) {
        this.name = name;
        dialog = DialogSceneComposer.dialog;
        textButton = (DialogSceneComposerModel.SimTextButton) dialog.simActor;
        if (name != null && name.equals("")) {
            this.name = null;
        }
        previousName = textButton.name;
    }
    
    @Override
    public void undo() {
        textButton.name = previousName;
    
        if (dialog.simActor != textButton) {
            dialog.simActor = textButton;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        textButton.name = name;
    
        if (dialog.simActor != textButton) {
            dialog.simActor = textButton;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextButton name " + name + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextButton name " + name + "\"";
    }
}
