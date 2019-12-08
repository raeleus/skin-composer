package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class LabelStyleUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimLabel label;
    private StyleData style;
    private StyleData previousStyle;
    private DialogSceneComposer dialog;
    
    public LabelStyleUndoable(StyleData style) {
        this.style = style;
        dialog = DialogSceneComposer.dialog;
        label = (DialogSceneComposerModel.SimLabel) dialog.simActor;
        previousStyle = label.style;
    }
    
    @Override
    public void undo() {
        label.style = previousStyle;
        
        if (dialog.simActor != label) {
            dialog.simActor = label;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        label.style = style;
        
        if (dialog.simActor != label) {
            dialog.simActor = label;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Label style: " + style.name + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Label style: " + style.name + "\"";
    }
}
