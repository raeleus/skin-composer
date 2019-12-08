package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class VerticalGroupExpandUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimVerticalGroup verticalGroup;
    private DialogSceneComposer dialog;
    private boolean expand;
    private boolean previousExpand;
    
    public VerticalGroupExpandUndoable(boolean expand) {
        this.expand = expand;
        dialog = DialogSceneComposer.dialog;
        verticalGroup = (DialogSceneComposerModel.SimVerticalGroup) dialog.simActor;
        previousExpand = verticalGroup.expand;
    }
    
    @Override
    public void undo() {
        verticalGroup.expand = previousExpand;
        
        if (dialog.simActor != verticalGroup) {
            dialog.simActor = verticalGroup;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        verticalGroup.expand = expand;
        
        if (dialog.simActor != verticalGroup) {
            dialog.simActor = verticalGroup;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"VerticalGroup expand " + expand + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"VerticalGroup expand " + expand + "\"";
    }
}
