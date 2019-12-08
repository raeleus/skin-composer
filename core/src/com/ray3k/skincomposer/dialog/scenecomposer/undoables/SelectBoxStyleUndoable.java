package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class SelectBoxStyleUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimSelectBox selectBox;
    private StyleData style;
    private StyleData previousStyle;
    private DialogSceneComposer dialog;
    
    public SelectBoxStyleUndoable(StyleData style) {
        this.style = style;
        dialog = DialogSceneComposer.dialog;
        selectBox = (DialogSceneComposerModel.SimSelectBox) dialog.simActor;
        previousStyle = selectBox.style;
    }
    
    @Override
    public void undo() {
        selectBox.style = previousStyle;
        
        if (dialog.simActor != selectBox) {
            dialog.simActor = selectBox;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        selectBox.style = style;
        
        if (dialog.simActor != selectBox) {
            dialog.simActor = selectBox;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"SelectBox style: " + style.name + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"SelectBox style: " + style.name + "\"";
    }
}
