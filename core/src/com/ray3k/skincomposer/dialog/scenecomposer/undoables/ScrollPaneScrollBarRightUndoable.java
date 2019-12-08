package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ScrollPaneScrollBarRightUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimScrollPane scrollPane;
    private DialogSceneComposer dialog;
    private boolean scrollBarRight;
    private boolean previousScrollBarRight;
    
    public ScrollPaneScrollBarRightUndoable(boolean scrollBarRight) {
        this.scrollBarRight = scrollBarRight;
        dialog = DialogSceneComposer.dialog;
        scrollPane = (DialogSceneComposerModel.SimScrollPane) dialog.simActor;
        previousScrollBarRight = scrollPane.scrollBarRight;
    }
    
    @Override
    public void undo() {
        scrollPane.scrollBarRight = previousScrollBarRight;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        scrollPane.scrollBarRight = scrollBarRight;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"ScrollPane scroll bar right " + scrollBarRight + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ScrollPane scroll bar right " + scrollBarRight + "\"";
    }
}
