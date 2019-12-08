package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ScrollPaneScrollBarTouchUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimScrollPane scrollPane;
    private DialogSceneComposer dialog;
    private boolean scrollBarTouch;
    private boolean previousScrollBarTouch;
    
    public ScrollPaneScrollBarTouchUndoable(boolean scrollBarTouch) {
        this.scrollBarTouch = scrollBarTouch;
        dialog = DialogSceneComposer.dialog;
        scrollPane = (DialogSceneComposerModel.SimScrollPane) dialog.simActor;
        previousScrollBarTouch = scrollPane.scrollBarTouch;
    }
    
    @Override
    public void undo() {
        scrollPane.scrollBarTouch = previousScrollBarTouch;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        scrollPane.scrollBarTouch = scrollBarTouch;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"ScrollPane scroll bar touch " + scrollBarTouch + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ScrollPane scroll bar touch " + scrollBarTouch + "\"";
    }
}
