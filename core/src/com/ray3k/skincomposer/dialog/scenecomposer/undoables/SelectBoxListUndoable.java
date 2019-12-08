package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class SelectBoxListUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimSelectBox selectBox;
    private Array<String> textList;
    private Array<String> previousTextList;
    private DialogSceneComposer dialog;
    
    public SelectBoxListUndoable(Array<String> textList) {
        this.textList = new Array<>(textList);
        dialog = DialogSceneComposer.dialog;
        selectBox = (DialogSceneComposerModel.SimSelectBox) dialog.simActor;
        if (textList != null && textList.equals("")) {
            this.textList = null;
        }
        previousTextList = new Array<>(selectBox.list);
    }
    
    @Override
    public void undo() {
        selectBox.list.clear();
        selectBox.list.addAll(previousTextList);
    
        if (dialog.simActor != selectBox) {
            dialog.simActor = selectBox;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        selectBox.list.clear();
        selectBox.list.addAll(textList);
    
        if (dialog.simActor != selectBox) {
            dialog.simActor = selectBox;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"SelectBox values\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"SelectBox values\"";
    }
}
