package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimSplitPane;

public class SplitPaneVisibleUndoable implements SceneComposerUndoable {
    private SimSplitPane splitPane;
    private DialogSceneComposer dialog;
    private boolean visible;
    private boolean previousVisible;
    
    public SplitPaneVisibleUndoable(boolean visible) {
        this.visible = visible;
        dialog = DialogSceneComposer.dialog;
        splitPane = (SimSplitPane) dialog.simActor;
        previousVisible = splitPane.visible;
    }
    
    @Override
    public void undo() {
        splitPane.visible = previousVisible;
        
        if (dialog.simActor != splitPane) {
            dialog.simActor = splitPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        splitPane.visible = visible;
        
        if (dialog.simActor != splitPane) {
            dialog.simActor = splitPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"SplitPane visible " + visible + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"SplitPane visible " + visible + "\"";
    }
}
