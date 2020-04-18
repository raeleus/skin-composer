package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.data.DrawableData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ContainerBackgroundUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimContainer container;
    private DrawableData background;
    private DrawableData previousBackground;
    private DialogSceneComposer dialog;
    
    public ContainerBackgroundUndoable(DrawableData background) {
        dialog = DialogSceneComposer.dialog;
        container = (DialogSceneComposerModel.SimContainer) dialog.simActor;
        this.background = background;
        previousBackground = container.background;
    }
    
    @Override
    public void undo() {
        container.background = previousBackground;
        
        if (dialog.simActor != container) {
            dialog.simActor = container;
            dialog.populatePath();
        }
        dialog.populateProperties();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        container.background = background;
        
        if (dialog.simActor != container) {
            dialog.simActor = container;
            dialog.populatePath();
        }
        dialog.populateProperties();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Container Background: " + (background == null ? "null" : background.name) + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Container Background: " + (background == null ? "null" : background.name) + "\"";
    }
}
