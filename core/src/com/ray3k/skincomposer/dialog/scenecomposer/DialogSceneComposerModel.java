package com.ray3k.skincomposer.dialog.scenecomposer;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;
import com.ray3k.skincomposer.dialog.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.undoables.SceneComposerUndoable;

public class DialogSceneComposerModel {
    private DialogSceneComposer dialog;
    private Stage stage;
    public Array<SceneComposerUndoable> undoables;
    public Array<SceneComposerUndoable> redoables;
    
    public DialogSceneComposerModel(DialogSceneComposer dialog) {
        this.dialog = dialog;
        stage = new Stage();
        undoables = new Array<>();
        redoables = new Array<>();
    }
    
    public void undo() {
        if (undoables.size > 0) {
            var undoable = undoables.pop();
            redoables.add(undoable);
            
            undoable.undo();
        }
    }
    
    public void redo() {
        if (redoables.size > 0) {
            var undoable = redoables.pop();
            undoables.add(undoable);
    
            undoable.redo();
        }
    }
    
    public void clear() {
        stage.clear();
    }
    
    public void addBaseTable() {
    
    }
    
    /**
     * Returns the children of the root group.
     * @return
     */
    public SnapshotArray<Actor> getChildren() {
        return stage.getRoot().getChildren();
    }
}
