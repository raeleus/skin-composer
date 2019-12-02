package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ImageTextButtonTextUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimImageTextButton button;
    private String text;
    private String previousText;
    private DialogSceneComposer dialog;
    
    public ImageTextButtonTextUndoable(String text) {
        this.text = text;
        dialog = DialogSceneComposer.dialog;
        button = (DialogSceneComposerModel.SimImageTextButton) dialog.simActor;
        if (text != null && text.equals("")) {
            this.text = null;
        }
        previousText = button.text;
    }
    
    @Override
    public void undo() {
        button.text = previousText;
        
        if (dialog.simActor != button) {
            dialog.simActor = button;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        button.text = text;
        
        if (dialog.simActor != button) {
            dialog.simActor = button;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"ImageTextButton text " + text + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ImageTextButton text " + text + "\"";
    }
}
