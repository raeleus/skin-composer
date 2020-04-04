package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ScrollPaneOverScrollXUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimScrollPane scrollPane;
    private DialogSceneComposer dialog;
    private boolean overScrollX;
    private boolean previousOverScrollX;
    
    public ScrollPaneOverScrollXUndoable(boolean overScrollX) {
        this.overScrollX = overScrollX;
        dialog = DialogSceneComposer.dialog;
        scrollPane = (DialogSceneComposerModel.SimScrollPane) dialog.simActor;
        previousOverScrollX = scrollPane.overScrollX;
    }
    
    @Override
    public void undo() {
        scrollPane.overScrollX = previousOverScrollX;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        scrollPane.overScrollX = overScrollX;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"ScrollPane over scroll X " + overScrollX + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ScrollPane over scroll X " + overScrollX + "\"";
    }
}
