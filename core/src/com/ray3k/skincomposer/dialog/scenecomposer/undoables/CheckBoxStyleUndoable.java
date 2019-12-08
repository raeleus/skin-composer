package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class CheckBoxStyleUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimCheckBox checkBox;
    private StyleData style;
    private StyleData previousStyle;
    private DialogSceneComposer dialog;
    
    public CheckBoxStyleUndoable(StyleData style) {
        this.style = style;
        dialog = DialogSceneComposer.dialog;
        checkBox = (DialogSceneComposerModel.SimCheckBox) dialog.simActor;
        previousStyle = checkBox.style;
    }
    
    @Override
    public void undo() {
        checkBox.style = previousStyle;
        
        if (dialog.simActor != checkBox) {
            dialog.simActor = checkBox;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        checkBox.style = style;
        
        if (dialog.simActor != checkBox) {
            dialog.simActor = checkBox;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"CheckBox style: " + style.name + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"CheckBox style: " + style.name + "\"";
    }
}
