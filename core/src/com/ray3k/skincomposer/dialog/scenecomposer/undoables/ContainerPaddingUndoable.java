package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ContainerPaddingUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimContainer container;
    private DialogSceneComposer dialog;
    private float padLeft;
    private float padRight;
    private float padTop;
    private float padBottom;
    private float previousPadLeft;
    private float previousPadRight;
    private float previousPadTop;
    private float previousPadBottom;
    
    public ContainerPaddingUndoable(float padLeft, float padRight, float padTop, float padBottom) {
        this.padLeft = padLeft;
        this.padRight = padRight;
        this.padTop = padTop;
        this.padBottom = padBottom;
        dialog = DialogSceneComposer.dialog;
        container = (DialogSceneComposerModel.SimContainer) dialog.simActor;
        
        previousPadLeft = container.padLeft;
        previousPadRight = container.padRight;
        previousPadTop = container.padTop;
        previousPadBottom = container.padBottom;
    }
    
    @Override
    public void undo() {
        container.padLeft = previousPadLeft;
        container.padRight = previousPadRight;
        container.padTop = previousPadTop;
        container.padBottom = previousPadBottom;
        
        if (dialog.simActor != container) {
            dialog.simActor = container;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        container.padLeft = padLeft;
        container.padRight = padRight;
        container.padTop = padTop;
        container.padBottom = padBottom;
        
        if (dialog.simActor != container) {
            dialog.simActor = container;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Container Padding\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Container Padding\"";
    }
}
