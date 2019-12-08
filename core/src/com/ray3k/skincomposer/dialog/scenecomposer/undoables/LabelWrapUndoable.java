package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class LabelWrapUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimLabel label;
    private DialogSceneComposer dialog;
    private boolean wrap;
    private boolean previousWrap;
    
    public LabelWrapUndoable(boolean wrap) {
        this.wrap = wrap;
        dialog = DialogSceneComposer.dialog;
        label = (DialogSceneComposerModel.SimLabel) dialog.simActor;
        previousWrap = label.wrap;
    }
    
    @Override
    public void undo() {
        label.wrap = previousWrap;
        
        if (dialog.simActor != label) {
            dialog.simActor = label;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        label.wrap = wrap;
        
        if (dialog.simActor != label) {
            dialog.simActor = label;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Label wrap " + wrap + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Label wrap " + wrap + "\"";
    }
}
