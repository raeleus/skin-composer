package com.ray3k.skincomposer.dialog.scenecomposer;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.SnapshotArray;
import com.ray3k.skincomposer.dialog.DialogSceneComposer;

public class DialogSceneComposerModel {
    private DialogSceneComposer dialog;
    private Stage stage;
    
    public DialogSceneComposerModel(DialogSceneComposer dialog) {
        this.dialog = dialog;
        stage = new Stage();
    }
    
    public void undo() {
    
    }
    
    public void redo() {
    
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
