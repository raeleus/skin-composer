package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ScrollPaneOverScrollSpeedMaxUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimScrollPane scrollPane;
    private DialogSceneComposer dialog;
    private float overScrollSpeedMax;
    private float previousOverScrollSpeedMax;
    
    public ScrollPaneOverScrollSpeedMaxUndoable(float overScrollSpeedMax) {
        this.overScrollSpeedMax = overScrollSpeedMax;
        dialog = DialogSceneComposer.dialog;
        scrollPane = (DialogSceneComposerModel.SimScrollPane) dialog.simActor;
        previousOverScrollSpeedMax = scrollPane.overScrollSpeedMax;
    }
    
    @Override
    public void undo() {
        scrollPane.overScrollSpeedMax = previousOverScrollSpeedMax;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        scrollPane.overScrollSpeedMax = overScrollSpeedMax;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"ScrollPane over scroll speed maximum " + overScrollSpeedMax + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ScrollPane over scroll speed maximum " + overScrollSpeedMax + "\"";
    }
}
