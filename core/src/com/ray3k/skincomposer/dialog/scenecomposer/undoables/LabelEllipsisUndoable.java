package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class LabelEllipsisUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimLabel label;
    private boolean ellipsis;
    private boolean previousEllipsis;
    private String ellipsisString;
    private String previousEllipsisString;
    private DialogSceneComposer dialog;
    
    public LabelEllipsisUndoable(boolean ellipsis, String ellipsisString) {
        this.ellipsis = ellipsis;
        this.ellipsisString = ellipsisString;
        dialog = DialogSceneComposer.dialog;
        label = (DialogSceneComposerModel.SimLabel) dialog.simActor;
        if (ellipsisString != null && ellipsisString.equals("")) {
            this.ellipsisString = null;
        }
        previousEllipsis = label.ellipsis;
        previousEllipsisString = label.ellipsisString;
    }
    
    @Override
    public void undo() {
        label.ellipsis = previousEllipsis;
        label.ellipsisString = previousEllipsisString;
        
        if (dialog.simActor != label) {
            dialog.simActor = label;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        label.ellipsis = ellipsis;
        label.ellipsisString = ellipsisString;
        
        if (dialog.simActor != label) {
            dialog.simActor = label;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Label ellipsis " + (ellipsis ? ellipsisString : "Off") + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Label ellipsis " + (ellipsis ? ellipsisString : "Off") + "\"";
    }
}
