package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ScrollPaneScrollingDisabledXUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimScrollPane scrollPane;
    private DialogSceneComposer dialog;
    private boolean scrollingDisabledX;
    private boolean previousScrollingDisabledX;
    
    public ScrollPaneScrollingDisabledXUndoable(boolean scrollingDisabledX) {
        this.scrollingDisabledX = scrollingDisabledX;
        dialog = DialogSceneComposer.dialog;
        scrollPane = (DialogSceneComposerModel.SimScrollPane) dialog.simActor;
        previousScrollingDisabledX = scrollPane.scrollingDisabledX;
    }
    
    @Override
    public void undo() {
        scrollPane.scrollingDisabledX = previousScrollingDisabledX;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        scrollPane.scrollingDisabledX = scrollingDisabledX;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"ScrollPane scrolling disabled x " + scrollingDisabledX + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ScrollPane scrolling disabled x " + scrollingDisabledX + "\"";
    }
}
