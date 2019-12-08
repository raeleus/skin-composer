package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ContainerAlignmentUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimContainer container;
    private DialogSceneComposer dialog;
    private int alignment;
    private int previousAlignment;
    
    public ContainerAlignmentUndoable(int alignment) {
        this.alignment = alignment;
        dialog = DialogSceneComposer.dialog;
        container = (DialogSceneComposerModel.SimContainer) dialog.simActor;
        
        previousAlignment = container.alignment;
    }
    
    @Override
    public void undo() {
        container.alignment = previousAlignment;
        
        if (dialog.simActor != container) {
            dialog.simActor = container;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        container.alignment = alignment;
        
        if (dialog.simActor != container) {
            dialog.simActor = container;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Container alignment\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Container alignment\"";
    }
}
