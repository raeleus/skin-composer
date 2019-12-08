package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.data.ColorData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class CheckBoxColorUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimCheckBox checkBox;
    private ColorData color;
    private ColorData previousColor;
    private DialogSceneComposer dialog;
    
    public CheckBoxColorUndoable(ColorData color) {
        this.color = color;
        dialog = DialogSceneComposer.dialog;
        checkBox = (DialogSceneComposerModel.SimCheckBox) dialog.simActor;
        previousColor = checkBox.color;
    }
    
    @Override
    public void undo() {
        checkBox.color = previousColor;
        
        if (dialog.simActor != checkBox) {
            dialog.simActor = checkBox;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        checkBox.color = color;
        
        if (dialog.simActor != checkBox) {
            dialog.simActor = checkBox;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"CheckBox Color: " + color.getName() + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"CheckBox Color: " + color.getName() + "\"";
    }
}
