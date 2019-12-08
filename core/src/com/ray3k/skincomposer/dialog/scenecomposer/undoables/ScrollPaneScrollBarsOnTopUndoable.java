package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ScrollPaneScrollBarsOnTopUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimScrollPane scrollPane;
    private DialogSceneComposer dialog;
    private boolean scrollBarsOnTop;
    private boolean previousScrollBarsOnTop;
    
    public ScrollPaneScrollBarsOnTopUndoable(boolean scrollBarsOnTop) {
        this.scrollBarsOnTop = scrollBarsOnTop;
        dialog = DialogSceneComposer.dialog;
        scrollPane = (DialogSceneComposerModel.SimScrollPane) dialog.simActor;
        previousScrollBarsOnTop = scrollPane.scrollBarsOnTop;
    }
    
    @Override
    public void undo() {
        scrollPane.scrollBarsOnTop = previousScrollBarsOnTop;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        scrollPane.scrollBarsOnTop = scrollBarsOnTop;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"ScrollPane scroll bars on top " + scrollBarsOnTop + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ScrollPane scroll bars on top " + scrollBarsOnTop + "\"";
    }
}
