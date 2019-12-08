package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class SplitPaneNameUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimSplitPane splitPane;
    private String name;
    private String previousName;
    private DialogSceneComposer dialog;
    
    public SplitPaneNameUndoable(String name) {
        this.name = name;
        dialog = DialogSceneComposer.dialog;
        splitPane = (DialogSceneComposerModel.SimSplitPane) dialog.simActor;
        if (name != null && name.equals("")) {
            this.name = null;
        }
        previousName = splitPane.name;
    }
    
    @Override
    public void undo() {
        splitPane.name = previousName;
    
        if (dialog.simActor != splitPane) {
            dialog.simActor = splitPane;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        splitPane.name = name;
    
        if (dialog.simActor != splitPane) {
            dialog.simActor = splitPane;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"SplitPane name " + name + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"SplitPane name " + name + "\"";
    }
}
