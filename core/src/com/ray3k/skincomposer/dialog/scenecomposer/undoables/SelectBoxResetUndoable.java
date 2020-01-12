package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class SelectBoxResetUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimSelectBox selectBox;
    private DialogSceneComposer dialog;
    private String previousName;
    private StyleData previousStyle;
    private boolean previousDisabled;
    private int previousMaxListCount;
    private Array<String> previousList;
    private int previousAlignment;
    private int previousSelected;
    private boolean previousScrollingDisabled;
    
    public SelectBoxResetUndoable() {
        dialog = DialogSceneComposer.dialog;
        selectBox = (DialogSceneComposerModel.SimSelectBox) dialog.simActor;
        previousName = selectBox.name;
        previousStyle = selectBox.style;
        previousDisabled = selectBox.disabled;
        previousMaxListCount = selectBox.maxListCount;
        previousList = new Array<>(selectBox.list);
        previousAlignment = selectBox.alignment;
        previousSelected = selectBox.selected;
        previousScrollingDisabled = selectBox.scrollingDisabled;
    }
    
    @Override
    public void undo() {
        selectBox.name = previousName;
        selectBox.style = previousStyle;
        selectBox.disabled = previousDisabled;
        selectBox.maxListCount = previousMaxListCount;
        selectBox.list.clear();
        selectBox.list.addAll(previousList);
        selectBox.alignment = previousAlignment;
        selectBox.selected = previousSelected;
        selectBox.scrollingDisabled = previousScrollingDisabled;
        
        if (dialog.simActor != selectBox) {
            dialog.simActor = selectBox;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        selectBox.reset();
        
        if (dialog.simActor != selectBox) {
            dialog.simActor = selectBox;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Reset SelectBox\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Reset SelectBox\"";
    }
}
