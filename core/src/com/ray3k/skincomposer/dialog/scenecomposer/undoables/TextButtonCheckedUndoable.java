package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TextButtonCheckedUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTextButton textButton;
    private DialogSceneComposer dialog;
    private boolean checked;
    private boolean previousChecked;
    
    public TextButtonCheckedUndoable(boolean checked) {
        this.checked = checked;
        dialog = DialogSceneComposer.dialog;
        textButton = (DialogSceneComposerModel.SimTextButton) dialog.simActor;
        previousChecked = textButton.checked;
    }
    
    @Override
    public void undo() {
        textButton.checked = previousChecked;
        
        if (dialog.simActor != textButton) {
            dialog.simActor = textButton;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        textButton.checked = checked;
        
        if (dialog.simActor != textButton) {
            dialog.simActor = textButton;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextButton checked " + checked + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextButton checked " + checked + "\"";
    }
}
