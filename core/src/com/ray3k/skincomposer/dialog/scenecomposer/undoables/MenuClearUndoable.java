package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class MenuClearUndoable implements SceneComposerUndoable {
    private DialogSceneComposer dialog;
    private DialogSceneComposerModel.SimGroup group;
    private DialogSceneComposerModel.SimGroup previousGroup;
    
    public MenuClearUndoable() {
        dialog = DialogSceneComposer.dialog;
        previousGroup = dialog.model.root;
        group = new DialogSceneComposerModel.SimGroup();
    }
    
    @Override
    public void undo() {
        dialog.model.root = previousGroup;
    
        dialog.simActor = previousGroup;
        dialog.populateProperties();
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        dialog.model.root = group;
        
        dialog.simActor = group;
        dialog.populateProperties();
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Clear Project\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Clear Project\"";
    }
}
