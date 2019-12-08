package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ScrollPaneScrollBarsVisibleUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimScrollPane scrollPane;
    private DialogSceneComposer dialog;
    private boolean scrollBarsVisible;
    private boolean previousScrollBarsVisible;
    
    public ScrollPaneScrollBarsVisibleUndoable(boolean scrollBarsVisible) {
        this.scrollBarsVisible = scrollBarsVisible;
        dialog = DialogSceneComposer.dialog;
        scrollPane = (DialogSceneComposerModel.SimScrollPane) dialog.simActor;
        previousScrollBarsVisible = scrollPane.scrollBarsVisible;
    }
    
    @Override
    public void undo() {
        scrollPane.scrollBarsVisible = previousScrollBarsVisible;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        scrollPane.scrollBarsVisible = scrollBarsVisible;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"ScrollPane scroll bars visible " + scrollBarsVisible + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ScrollPane scroll bars visible " + scrollBarsVisible + "\"";
    }
}
