package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ScrollPaneSmoothScrollingUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimScrollPane scrollPane;
    private DialogSceneComposer dialog;
    private boolean smoothScrolling;
    private boolean previousSmoothScrolling;
    
    public ScrollPaneSmoothScrollingUndoable(boolean smoothScrolling) {
        this.smoothScrolling = smoothScrolling;
        dialog = DialogSceneComposer.dialog;
        scrollPane = (DialogSceneComposerModel.SimScrollPane) dialog.simActor;
        previousSmoothScrolling = scrollPane.smoothScrolling;
    }
    
    @Override
    public void undo() {
        scrollPane.smoothScrolling = previousSmoothScrolling;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        scrollPane.smoothScrolling = smoothScrolling;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"ScrollPane smooth scrolling " + smoothScrolling + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ScrollPane smooth scrolling " + smoothScrolling + "\"";
    }
}
