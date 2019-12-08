package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ScrollPaneScrollingDisabledYUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimScrollPane scrollPane;
    private DialogSceneComposer dialog;
    private boolean scrollingDisabledY;
    private boolean previousScrollingDisabledY;
    
    public ScrollPaneScrollingDisabledYUndoable(boolean scrollingDisabledY) {
        this.scrollingDisabledY = scrollingDisabledY;
        dialog = DialogSceneComposer.dialog;
        scrollPane = (DialogSceneComposerModel.SimScrollPane) dialog.simActor;
        previousScrollingDisabledY = scrollPane.scrollingDisabledY;
    }
    
    @Override
    public void undo() {
        scrollPane.scrollingDisabledY = previousScrollingDisabledY;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        scrollPane.scrollingDisabledY = scrollingDisabledY;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"ScrollPane scrolling disabled y " + scrollingDisabledY + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ScrollPane scrolling disabled y " + scrollingDisabledY + "\"";
    }
}
