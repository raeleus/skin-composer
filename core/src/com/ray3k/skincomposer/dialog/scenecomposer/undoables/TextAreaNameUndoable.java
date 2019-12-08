package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TextAreaNameUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTextArea textArea;
    private String name;
    private String previousName;
    private DialogSceneComposer dialog;
    
    public TextAreaNameUndoable(String name) {
        this.name = name;
        dialog = DialogSceneComposer.dialog;
        textArea = (DialogSceneComposerModel.SimTextArea) dialog.simActor;
        if (name != null && name.equals("")) {
            this.name = null;
        }
        previousName = textArea.name;
    }
    
    @Override
    public void undo() {
        textArea.name = previousName;
    
        if (dialog.simActor != textArea) {
            dialog.simActor = textArea;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        textArea.name = name;
    
        if (dialog.simActor != textArea) {
            dialog.simActor = textArea;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextArea name " + name + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextArea name " + name + "\"";
    }
}
