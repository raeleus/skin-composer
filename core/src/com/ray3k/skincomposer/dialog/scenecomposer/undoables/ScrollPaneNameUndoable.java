package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ScrollPaneNameUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimScrollPane scrollPane;
    private String name;
    private String previousName;
    private DialogSceneComposer dialog;
    
    public ScrollPaneNameUndoable(String name) {
        this.name = name;
        dialog = DialogSceneComposer.dialog;
        scrollPane = (DialogSceneComposerModel.SimScrollPane) dialog.simActor;
        if (name != null && name.equals("")) {
            this.name = null;
        }
        previousName = scrollPane.name;
    }
    
    @Override
    public void undo() {
        scrollPane.name = previousName;
    
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        scrollPane.name = name;
    
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"ScrollPane name " + name + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ScrollPane name " + name + "\"";
    }
}
