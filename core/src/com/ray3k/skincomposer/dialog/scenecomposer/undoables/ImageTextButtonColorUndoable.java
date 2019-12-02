package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.data.ColorData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ImageTextButtonColorUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimImageTextButton button;
    private ColorData color;
    private ColorData previousColor;
    private DialogSceneComposer dialog;
    
    public ImageTextButtonColorUndoable(ColorData color) {
        this.color = color;
        dialog = DialogSceneComposer.dialog;
        button = (DialogSceneComposerModel.SimImageTextButton) dialog.simActor;
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
        return "Redo \"ImageTextButton Color: " + color.getName() + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ImageTextButton Color: " + color.getName() + "\"";
    }
}
