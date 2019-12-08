package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ScrollPaneFlickScrollUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimScrollPane scrollPane;
    private DialogSceneComposer dialog;
    private boolean flickScroll;
    private boolean previousFlickScroll;
    
    public ScrollPaneFlickScrollUndoable(boolean flickScroll) {
        this.flickScroll = flickScroll;
        dialog = DialogSceneComposer.dialog;
        scrollPane = (DialogSceneComposerModel.SimScrollPane) dialog.simActor;
        previousFlickScroll = scrollPane.flickScroll;
    }
    
    @Override
    public void undo() {
        scrollPane.flickScroll = previousFlickScroll;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        scrollPane.flickScroll = flickScroll;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"ScrollPane flick scroll " + flickScroll + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ScrollPane flick scroll " + flickScroll + "\"";
    }
}
