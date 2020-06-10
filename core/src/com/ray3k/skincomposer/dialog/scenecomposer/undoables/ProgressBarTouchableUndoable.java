package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimButton;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimProgressBar;

public class ProgressBarTouchableUndoable implements SceneComposerUndoable {
    private SimProgressBar progressBar;
    private DialogSceneComposer dialog;
    private Touchable touchable;
    private Touchable previousTouchable;
    
    public ProgressBarTouchableUndoable(Touchable touchable) {
        this.touchable = touchable;
        dialog = DialogSceneComposer.dialog;
        progressBar = (SimProgressBar) dialog.simActor;
        previousTouchable = progressBar.touchable;
    }
    
    @Override
    public void undo() {
        progressBar.touchable = previousTouchable;
        
        if (dialog.simActor != progressBar) {
            dialog.simActor = progressBar;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        progressBar.touchable = touchable;
        
        if (dialog.simActor != progressBar) {
            dialog.simActor = progressBar;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"ProgressBar touchable " + touchable + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ProgressBar touchable " + touchable + "\"";
    }
}
