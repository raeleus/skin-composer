package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TextButtonPaddingUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTextButton textButton;
    private float padLeft, padRight, padTop, padBottom;
    private float previousPadLeft, previousPadRight, previousPadTop, previousPadBottom;
    private DialogSceneComposer dialog;
    
    public TextButtonPaddingUndoable(float padLeft, float padRight, float padTop, float padBottom) {
        this.padLeft = padLeft;
        this.padRight = padRight;
        this.padTop = padTop;
        this.padBottom = padBottom;
        dialog = DialogSceneComposer.dialog;
        textButton = (DialogSceneComposerModel.SimTextButton) dialog.simActor;
        previousPadLeft = textButton.padLeft;
        previousPadRight = textButton.padRight;
        previousPadTop = textButton.padTop;
        previousPadBottom = textButton.padBottom;
    }
    
    @Override
    public void undo() {
        textButton.padLeft = previousPadLeft;
        textButton.padRight = previousPadRight;
        textButton.padTop = previousPadTop;
        textButton.padBottom = previousPadBottom;
        
        if (dialog.simActor != textButton) {
            dialog.simActor = textButton;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        textButton.padLeft = padLeft;
        textButton.padRight = padRight;
        textButton.padTop = padTop;
        textButton.padBottom = padBottom;
        
        if (dialog.simActor != textButton) {
            dialog.simActor = textButton;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextButton Padding\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextButton Padding\"";
    }
}
