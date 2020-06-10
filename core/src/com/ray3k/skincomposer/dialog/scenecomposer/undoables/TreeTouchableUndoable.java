package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimButton;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimTree;

public class TreeTouchableUndoable implements SceneComposerUndoable {
    private SimTree tree;
    private DialogSceneComposer dialog;
    private Touchable touchable;
    private Touchable previousTouchable;
    
    public TreeTouchableUndoable(Touchable touchable) {
        this.touchable = touchable;
        dialog = DialogSceneComposer.dialog;
        tree = (SimTree) dialog.simActor;
        previousTouchable = tree.touchable;
    }
    
    @Override
    public void undo() {
        tree.touchable = previousTouchable;
        
        if (dialog.simActor != tree) {
            dialog.simActor = tree;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        tree.touchable = touchable;
        
        if (dialog.simActor != tree) {
            dialog.simActor = tree;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Tree touchable " + touchable + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Tree touchable " + touchable + "\"";
    }
}
