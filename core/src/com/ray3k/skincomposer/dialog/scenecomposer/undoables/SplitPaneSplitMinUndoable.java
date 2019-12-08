package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class SplitPaneSplitMinUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimSplitPane splitPane;
    private DialogSceneComposer dialog;
    private float splitMin;
    private float previousSplitMin;
    
    public SplitPaneSplitMinUndoable(float splitMin) {
        this.splitMin = splitMin;
        dialog = DialogSceneComposer.dialog;
        splitPane = (DialogSceneComposerModel.SimSplitPane) dialog.simActor;
        previousSplitMin = splitPane.splitMin;
    }
    
    @Override
    public void undo() {
        splitPane.splitMin = previousSplitMin;
        
        if (dialog.simActor != splitPane) {
            dialog.simActor = splitPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        splitPane.splitMin = splitMin;
        
        if (dialog.simActor != splitPane) {
            dialog.simActor = splitPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"SplitPane split minimum " + splitMin + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"SplitPane split minimum " + splitMin + "\"";
    }
}
