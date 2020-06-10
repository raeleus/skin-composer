package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimButton;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimScrollPane;

public class ScrollPaneTouchableUndoable implements SceneComposerUndoable {
    private SimScrollPane scrollPane;
    private DialogSceneComposer dialog;
    private Touchable touchable;
    private Touchable previousTouchable;
    
    public ScrollPaneTouchableUndoable(Touchable touchable) {
        this.touchable = touchable;
        dialog = DialogSceneComposer.dialog;
        scrollPane = (SimScrollPane) dialog.simActor;
        previousTouchable = scrollPane.touchable;
    }
    
    @Override
    public void undo() {
        scrollPane.touchable = previousTouchable;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        scrollPane.touchable = touchable;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"ScrollPane touchable " + touchable + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ScrollPane touchable " + touchable + "\"";
    }
}
