package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.data.ColorData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class LabelColorUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimLabel label;
    private ColorData color;
    private ColorData previousColor;
    private DialogSceneComposer dialog;
    
    public LabelColorUndoable(ColorData color) {
        this.color = color;
        dialog = DialogSceneComposer.dialog;
        label = (DialogSceneComposerModel.SimLabel) dialog.simActor;
        previousColor = label.color;
    }
    
    @Override
    public void undo() {
        label.color = previousColor;
        
        if (dialog.simActor != label) {
            dialog.simActor = label;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        label.color = color;
        
        if (dialog.simActor != label) {
            dialog.simActor = label;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Label Color: " + color.getName() + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Label Color: " + color.getName() + "\"";
    }
}
