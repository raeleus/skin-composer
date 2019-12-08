package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ScrollPaneForceScrollXUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimScrollPane scrollPane;
    private DialogSceneComposer dialog;
    private boolean forceScrollX;
    private boolean previousForceScrollX;
    
    public ScrollPaneForceScrollXUndoable(boolean forceScrollX) {
        this.forceScrollX = forceScrollX;
        dialog = DialogSceneComposer.dialog;
        scrollPane = (DialogSceneComposerModel.SimScrollPane) dialog.simActor;
        previousForceScrollX = scrollPane.forceScrollX;
    }
    
    @Override
    public void undo() {
        scrollPane.forceScrollX = previousForceScrollX;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        scrollPane.forceScrollX = forceScrollX;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"ScrollPane force scroll X " + forceScrollX + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ScrollPane force scroll X " + forceScrollX + "\"";
    }
}
