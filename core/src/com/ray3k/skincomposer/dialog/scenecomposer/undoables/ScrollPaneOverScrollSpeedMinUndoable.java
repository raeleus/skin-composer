package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ScrollPaneOverScrollSpeedMinUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimScrollPane scrollPane;
    private DialogSceneComposer dialog;
    private float overScrollSpeedMin;
    private float previousOverScrollSpeedMin;
    
    public ScrollPaneOverScrollSpeedMinUndoable(float overScrollSpeedMin) {
        this.overScrollSpeedMin = overScrollSpeedMin;
        dialog = DialogSceneComposer.dialog;
        scrollPane = (DialogSceneComposerModel.SimScrollPane) dialog.simActor;
        previousOverScrollSpeedMin = scrollPane.overScrollSpeedMin;
    }
    
    @Override
    public void undo() {
        scrollPane.overScrollSpeedMin = previousOverScrollSpeedMin;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        scrollPane.overScrollSpeedMin = overScrollSpeedMin;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"ScrollPane over scroll speed minimum " + overScrollSpeedMin + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ScrollPane over scroll speed minimum " + overScrollSpeedMin + "\"";
    }
}
