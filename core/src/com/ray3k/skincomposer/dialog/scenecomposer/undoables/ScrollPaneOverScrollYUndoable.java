package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ScrollPaneOverScrollYUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimScrollPane scrollPane;
    private DialogSceneComposer dialog;
    private boolean overScrollY;
    private boolean previousOverScrollY;
    
    public ScrollPaneOverScrollYUndoable(boolean overScrollY) {
        this.overScrollY = overScrollY;
        dialog = DialogSceneComposer.dialog;
        scrollPane = (DialogSceneComposerModel.SimScrollPane) dialog.simActor;
        previousOverScrollY = scrollPane.overScrollY;
    }
    
    @Override
    public void undo() {
        scrollPane.overScrollY = previousOverScrollY;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        scrollPane.overScrollY = overScrollY;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"ScrollPane over scroll Y " + overScrollY + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ScrollPane over scroll Y " + overScrollY + "\"";
    }
}
