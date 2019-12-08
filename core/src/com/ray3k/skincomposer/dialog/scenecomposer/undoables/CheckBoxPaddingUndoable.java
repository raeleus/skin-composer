package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class CheckBoxPaddingUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimCheckBox checkBox;
    private float padLeft, padRight, padTop, padBottom;
    private float previousPadLeft, previousPadRight, previousPadTop, previousPadBottom;
    private DialogSceneComposer dialog;
    
    public CheckBoxPaddingUndoable(float padLeft, float padRight, float padTop, float padBottom) {
        this.padLeft = padLeft;
        this.padRight = padRight;
        this.padTop = padTop;
        this.padBottom = padBottom;
        dialog = DialogSceneComposer.dialog;
        checkBox = (DialogSceneComposerModel.SimCheckBox) dialog.simActor;
        previousPadLeft = checkBox.padLeft;
        previousPadRight = checkBox.padRight;
        previousPadTop = checkBox.padTop;
        previousPadBottom = checkBox.padBottom;
    }
    
    @Override
    public void undo() {
        checkBox.padLeft = previousPadLeft;
        checkBox.padRight = previousPadRight;
        checkBox.padTop = previousPadTop;
        checkBox.padBottom = previousPadBottom;
        
        if (dialog.simActor != checkBox) {
            dialog.simActor = checkBox;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        checkBox.padLeft = padLeft;
        checkBox.padRight = padRight;
        checkBox.padTop = padTop;
        checkBox.padBottom = padBottom;
        
        if (dialog.simActor != checkBox) {
            dialog.simActor = checkBox;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"CheckBox Padding\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"CheckBox Padding\"";
    }
}
