package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TextFieldTextUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTextField textField;
    private String text;
    private String previousText;
    private DialogSceneComposer dialog;
    
    public TextFieldTextUndoable(String text) {
        this.text = text;
        dialog = DialogSceneComposer.dialog;
        textField = (DialogSceneComposerModel.SimTextField) dialog.simActor;
        if (text != null && text.equals("")) {
            this.text = null;
        }
        previousText = textField.text;
    }
    
    @Override
    public void undo() {
        textField.text = previousText;
        
        if (dialog.simActor != textField) {
            dialog.simActor = textField;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        textField.text = text;
        
        if (dialog.simActor != textField) {
            dialog.simActor = textField;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextField text " + text + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextField text " + text + "\"";
    }
}
