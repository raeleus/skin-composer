package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimButton;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimSplitPane;

public class SplitPaneTouchableUndoable implements SceneComposerUndoable {
    private SimSplitPane splitPane;
    private DialogSceneComposer dialog;
    private Touchable touchable;
    private Touchable previousTouchable;
    
    public SplitPaneTouchableUndoable(Touchable touchable) {
        this.touchable = touchable;
        dialog = DialogSceneComposer.dialog;
        splitPane = (SimSplitPane) dialog.simActor;
        previousTouchable = splitPane.touchable;
    }
    
    @Override
    public void undo() {
        splitPane.touchable = previousTouchable;
        
        if (dialog.simActor != splitPane) {
            dialog.simActor = splitPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        splitPane.touchable = touchable;
        
        if (dialog.simActor != splitPane) {
            dialog.simActor = splitPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"SplitPane touchable " + touchable + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"SplitPane touchable " + touchable + "\"";
    }
}
