package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TextFieldSelectAllUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTextField textField;
    private DialogSceneComposer dialog;
    private boolean selectAll;
    private boolean previousSelectAll;
    
    public TextFieldSelectAllUndoable(boolean selectAll) {
        this.selectAll = selectAll;
        dialog = DialogSceneComposer.dialog;
        textField = (DialogSceneComposerModel.SimTextField) dialog.simActor;
        previousSelectAll = textField.selectAll;
    }
    
    @Override
    public void undo() {
        textField.selectAll = previousSelectAll;
        
        if (dialog.simActor != textField) {
            dialog.simActor = textField;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        textField.selectAll = selectAll;
        
        if (dialog.simActor != textField) {
            dialog.simActor = textField;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextField select all " + selectAll + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextField select all " + selectAll + "\"";
    }
}
