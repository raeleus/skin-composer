package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ButtonPaddingUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimButton button;
    private float padLeft, padRight, padTop, padBottom;
    private float previousPadLeft, previousPadRight, previousPadTop, previousPadBottom;
    private DialogSceneComposer dialog;
    
    public ButtonPaddingUndoable(float padLeft, float padRight, float padTop, float padBottom) {
        this.padLeft = padLeft;
        this.padRight = padRight;
        this.padTop = padTop;
        this.padBottom = padBottom;
        dialog = DialogSceneComposer.dialog;
        button = (DialogSceneComposerModel.SimButton) dialog.simActor;
        previousPadLeft = button.padLeft;
        previousPadRight = button.padRight;
        previousPadTop = button.padTop;
        previousPadBottom = button.padBottom;
    }
    
    @Override
    public void undo() {
        button.padLeft = previousPadLeft;
        button.padRight = previousPadRight;
        button.padTop = previousPadTop;
        button.padBottom = previousPadBottom;
        
        if (dialog.simActor != button) {
            dialog.simActor = button;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        button.padLeft = padLeft;
        button.padRight = padRight;
        button.padTop = padTop;
        button.padBottom = padBottom;
        
        if (dialog.simActor != button) {
            dialog.simActor = button;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Button Padding\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Button Padding\"";
    }
}
