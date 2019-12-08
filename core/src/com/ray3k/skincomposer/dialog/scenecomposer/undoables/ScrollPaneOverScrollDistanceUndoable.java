package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ScrollPaneOverScrollDistanceUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimScrollPane scrollPane;
    private DialogSceneComposer dialog;
    private float overScrollDistance;
    private float previousOverScrollDistance;
    
    public ScrollPaneOverScrollDistanceUndoable(float overScrollDistance) {
        this.overScrollDistance = overScrollDistance;
        dialog = DialogSceneComposer.dialog;
        scrollPane = (DialogSceneComposerModel.SimScrollPane) dialog.simActor;
        previousOverScrollDistance = scrollPane.overScrollDistance;
    }
    
    @Override
    public void undo() {
        scrollPane.overScrollDistance = previousOverScrollDistance;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        scrollPane.overScrollDistance = overScrollDistance;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"ScrollPane over scroll distance " + overScrollDistance + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ScrollPane over scroll distance " + overScrollDistance + "\"";
    }
}
