package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimRootGroup;

import static com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.rootActor;

public class MenuClearUndoable implements SceneComposerUndoable {
    private DialogSceneComposer dialog;
    private SimRootGroup group;
    private SimRootGroup previousGroup;
    
    public MenuClearUndoable() {
        dialog = DialogSceneComposer.dialog;
        previousGroup = rootActor;
        group = new SimRootGroup();
    }
    
    @Override
    public void undo() {
        rootActor = previousGroup;
    
        dialog.simActor = previousGroup;
        dialog.populateProperties();
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        rootActor = group;
        
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
