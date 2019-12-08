package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ListStyleUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimList list;
    private StyleData style;
    private StyleData previousStyle;
    private DialogSceneComposer dialog;
    
    public ListStyleUndoable(StyleData style) {
        this.style = style;
        dialog = DialogSceneComposer.dialog;
        list = (DialogSceneComposerModel.SimList) dialog.simActor;
        previousStyle = list.style;
    }
    
    @Override
    public void undo() {
        list.style = previousStyle;
        
        if (dialog.simActor != list) {
            dialog.simActor = list;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        list.style = style;
        
        if (dialog.simActor != list) {
            dialog.simActor = list;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"List style: " + style.name + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"List style: " + style.name + "\"";
    }
}
