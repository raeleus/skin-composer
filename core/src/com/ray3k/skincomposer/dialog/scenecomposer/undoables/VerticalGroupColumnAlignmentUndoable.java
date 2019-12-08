package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class VerticalGroupColumnAlignmentUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimVerticalGroup verticalGroup;
    private DialogSceneComposer dialog;
    private int columnAlignment;
    private int previousColumnAlignment;
    
    public VerticalGroupColumnAlignmentUndoable(int columnAlignment) {
        this.columnAlignment = columnAlignment;
        dialog = DialogSceneComposer.dialog;
        verticalGroup = (DialogSceneComposerModel.SimVerticalGroup) dialog.simActor;
        
        previousColumnAlignment = verticalGroup.columnAlignment;
    }
    
    @Override
    public void undo() {
        verticalGroup.columnAlignment = previousColumnAlignment;
        
        if (dialog.simActor != verticalGroup) {
            dialog.simActor = verticalGroup;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        verticalGroup.columnAlignment = columnAlignment;
        
        if (dialog.simActor != verticalGroup) {
            dialog.simActor = verticalGroup;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"VerticalGroup column alignment\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"VerticalGroup column alignment\"";
    }
}
