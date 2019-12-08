package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TextAreaTextUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTextArea textArea;
    private String text;
    private String previousText;
    private DialogSceneComposer dialog;
    
    public TextAreaTextUndoable(String text) {
        this.text = text;
        dialog = DialogSceneComposer.dialog;
        textArea = (DialogSceneComposerModel.SimTextArea) dialog.simActor;
        if (text != null && text.equals("")) {
            this.text = null;
        }
        previousText = textArea.text;
    }
    
    @Override
    public void undo() {
        textArea.text = previousText;
        
        if (dialog.simActor != textArea) {
            dialog.simActor = textArea;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        textArea.text = text;
        
        if (dialog.simActor != textArea) {
            dialog.simActor = textArea;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextArea text " + text + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextArea text " + text + "\"";
    }
}
