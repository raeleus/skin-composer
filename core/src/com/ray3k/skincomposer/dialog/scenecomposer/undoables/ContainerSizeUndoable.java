package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ContainerSizeUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimContainer container;
    private DialogSceneComposer dialog;
    private float minWidth;
    private float minHeight;
    private float maxWidth;
    private float maxHeight;
    private float preferredWidth;
    private float preferredHeight;
    private float previousMinWidth;
    private float previousMinHeight;
    private float previousMaxWidth;
    private float previousMaxHeight;
    private float previousPreferredWidth;
    private float previousPreferredHeight;
    
    public ContainerSizeUndoable(float minWidth, float minHeight, float maxWidth, float maxHeight, float preferredWidth, float preferredHeight) {
        this.minWidth = minWidth;
        this.minHeight = minHeight;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.preferredWidth = preferredWidth;
        this.preferredHeight = preferredHeight;
        
        dialog = DialogSceneComposer.dialog;
        container = (DialogSceneComposerModel.SimContainer) dialog.simActor;

        previousMinWidth = container.minWidth;
        previousMinHeight = container.minHeight;
        previousMaxWidth = container.maxWidth;
        previousMaxHeight = container.maxHeight;
        previousPreferredWidth = container.preferredWidth;
        previousPreferredHeight = container.preferredHeight;
    }
    
    @Override
    public void undo() {
        container.minWidth = previousMinWidth;
        container.minHeight = previousMinHeight;
        container.maxWidth = previousMaxWidth;
        container.maxHeight = previousMaxHeight;
        container.preferredWidth = previousPreferredWidth;
        container.preferredHeight = previousPreferredHeight;
        
        if (dialog.simActor != container) {
            dialog.simActor = container;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {

        container.minWidth = minWidth;
        container.minHeight = minHeight;
        container.maxWidth = maxWidth;
        container.maxHeight = maxHeight;
        container.preferredWidth = preferredWidth;
        container.preferredHeight = preferredHeight;
        
        if (dialog.simActor != container) {
            dialog.simActor = container;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Container size\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Container size\"";
    }
}
