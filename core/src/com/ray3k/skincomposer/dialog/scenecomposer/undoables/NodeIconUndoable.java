package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.data.DrawableData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class NodeIconUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimNode node;
    private DrawableData icon;
    private DrawableData previousIcon;
    private DialogSceneComposer dialog;
    
    public NodeIconUndoable(DrawableData icon) {
        dialog = DialogSceneComposer.dialog;
        node = (DialogSceneComposerModel.SimNode) dialog.simActor;
        this.icon = icon;
        previousIcon = node.icon;
    }
    
    @Override
    public void undo() {
        node.icon = previousIcon;
        
        if (dialog.simActor != node) {
            dialog.simActor = node;
            dialog.populatePath();
        }
        dialog.populateProperties();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        node.icon = icon;
        
        if (dialog.simActor != node) {
            dialog.simActor = node;
            dialog.populatePath();
        }
        dialog.populateProperties();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Node icon: " + (icon == null ? "null" : icon.name) + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Node icon: " + (icon == null ? "null" : icon.name) + "\"";
    }
}
