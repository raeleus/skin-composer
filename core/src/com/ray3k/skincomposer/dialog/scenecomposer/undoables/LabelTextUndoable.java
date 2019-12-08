package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class LabelTextUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimLabel label;
    private String text;
    private String previousText;
    private DialogSceneComposer dialog;
    
    public LabelTextUndoable(String text) {
        this.text = text;
        dialog = DialogSceneComposer.dialog;
        label = (DialogSceneComposerModel.SimLabel) dialog.simActor;
        if (text != null && text.equals("")) {
            this.text = null;
        }
        previousText = label.text;
    }
    
    @Override
    public void undo() {
        label.text = previousText;
        
        if (dialog.simActor != label) {
            dialog.simActor = label;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        label.text = text;
        
        if (dialog.simActor != label) {
            dialog.simActor = label;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Label text " + text + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Label text " + text + "\"";
    }
}
