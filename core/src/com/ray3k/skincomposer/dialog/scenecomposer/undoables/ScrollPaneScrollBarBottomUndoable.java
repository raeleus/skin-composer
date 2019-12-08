package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ScrollPaneScrollBarBottomUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimScrollPane scrollPane;
    private DialogSceneComposer dialog;
    private boolean scrollBarBottom;
    private boolean previousScrollBarBottom;
    
    public ScrollPaneScrollBarBottomUndoable(boolean scrollBarBottom) {
        this.scrollBarBottom = scrollBarBottom;
        dialog = DialogSceneComposer.dialog;
        scrollPane = (DialogSceneComposerModel.SimScrollPane) dialog.simActor;
        previousScrollBarBottom = scrollPane.scrollBarBottom;
    }
    
    @Override
    public void undo() {
        scrollPane.scrollBarBottom = previousScrollBarBottom;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        scrollPane.scrollBarBottom = scrollBarBottom;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"ScrollPane scroll bar bottom " + scrollBarBottom + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ScrollPane scroll bar bottom " + scrollBarBottom + "\"";
    }
}
