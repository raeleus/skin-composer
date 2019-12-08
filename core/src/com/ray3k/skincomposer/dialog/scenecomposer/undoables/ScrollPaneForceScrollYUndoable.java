package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ScrollPaneForceScrollYUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimScrollPane scrollPane;
    private DialogSceneComposer dialog;
    private boolean forceScrollY;
    private boolean previousForceScrollY;
    
    public ScrollPaneForceScrollYUndoable(boolean forceScrollY) {
        this.forceScrollY = forceScrollY;
        dialog = DialogSceneComposer.dialog;
        scrollPane = (DialogSceneComposerModel.SimScrollPane) dialog.simActor;
        previousForceScrollY = scrollPane.forceScrollY;
    }
    
    @Override
    public void undo() {
        scrollPane.forceScrollY = previousForceScrollY;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        scrollPane.forceScrollY = forceScrollY;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"ScrollPane force scroll Y " + forceScrollY + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ScrollPane force scroll Y " + forceScrollY + "\"";
    }
}
