package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.data.ColorData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ButtonColorUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimButton button;
    private ColorData color;
    private ColorData previousColor;
    private DialogSceneComposer dialog;
    
    public ButtonColorUndoable(ColorData color) {
        this.color = color;
        dialog = DialogSceneComposer.dialog;
        button = (DialogSceneComposerModel.SimButton) dialog.simActor;
        previousColor = button.color;
    }
    
    @Override
    public void undo() {
        button.color = previousColor;
        
        if (dialog.simActor != button) {
            dialog.simActor = button;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        button.color = color;
        
        if (dialog.simActor != button) {
            dialog.simActor = button;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Button Color: " + color.getName() + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Button Color: " + color.getName() + "\"";
    }
}
