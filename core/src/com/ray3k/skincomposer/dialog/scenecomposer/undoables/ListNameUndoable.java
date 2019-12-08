package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ListNameUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimList list;
    private String name;
    private String previousName;
    private DialogSceneComposer dialog;
    
    public ListNameUndoable(String name) {
        this.name = name;
        dialog = DialogSceneComposer.dialog;
        list = (DialogSceneComposerModel.SimList) dialog.simActor;
        if (name != null && name.equals("")) {
            this.name = null;
        }
        previousName = list.name;
    }
    
    @Override
    public void undo() {
        list.name = previousName;
    
        if (dialog.simActor != list) {
            dialog.simActor = list;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        list.name = name;
    
        if (dialog.simActor != list) {
            dialog.simActor = list;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"List name " + name + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"List name " + name + "\"";
    }
}
