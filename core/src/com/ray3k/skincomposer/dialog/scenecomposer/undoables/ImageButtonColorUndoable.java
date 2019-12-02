package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.data.ColorData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ImageButtonColorUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimImageButton button;
    private ColorData color;
    private ColorData previousColor;
    private DialogSceneComposer dialog;
    
    public ImageButtonColorUndoable(ColorData color) {
        this.color = color;
        dialog = DialogSceneComposer.dialog;
        button = (DialogSceneComposerModel.SimImageButton) dialog.simActor;
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
        return "Redo \"ImageButton Color: " + color.getName() + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ImageButton Color: " + color.getName() + "\"";
    }
}
