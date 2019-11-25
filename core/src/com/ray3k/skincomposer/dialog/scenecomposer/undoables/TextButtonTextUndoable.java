package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TextButtonTextUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTextButton textButton;
    private String text;
    private String previousText;
    private DialogSceneComposer dialog;
    
    public TextButtonTextUndoable(String text) {
        this.text = text;
        dialog = DialogSceneComposer.dialog;
        textButton = (DialogSceneComposerModel.SimTextButton) dialog.simActor;
        if (text != null && text.equals("")) {
            this.text = null;
        }
        previousText = textButton.text;
    }
    
    @Override
    public void undo() {
        textButton.text = previousText;
        
        if (dialog.simActor != textButton) {
            dialog.simActor = textButton;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        textButton.text = text;
        
        if (dialog.simActor != textButton) {
            dialog.simActor = textButton;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextButton text " + text + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextButton text " + text + "\"";
    }
}
