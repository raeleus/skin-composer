package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class SplitPaneSplitMaxUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimSplitPane splitPane;
    private DialogSceneComposer dialog;
    private float splitMax;
    private float previousSplitMax;
    
    public SplitPaneSplitMaxUndoable(float splitMax) {
        this.splitMax = splitMax;
        dialog = DialogSceneComposer.dialog;
        splitPane = (DialogSceneComposerModel.SimSplitPane) dialog.simActor;
        previousSplitMax = splitPane.splitMax;
    }
    
    @Override
    public void undo() {
        splitPane.splitMax = previousSplitMax;
        
        if (dialog.simActor != splitPane) {
            dialog.simActor = splitPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        splitPane.splitMax = splitMax;
        
        if (dialog.simActor != splitPane) {
            dialog.simActor = splitPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"SplitPane split maximum " + splitMax + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"SplitPane split maximum " + splitMax + "\"";
    }
}
