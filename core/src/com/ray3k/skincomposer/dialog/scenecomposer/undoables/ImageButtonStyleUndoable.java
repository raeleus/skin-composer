package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ImageButtonStyleUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimImageButton button;
    private StyleData style;
    private StyleData previousStyle;
    private DialogSceneComposer dialog;
    
    public ImageButtonStyleUndoable(StyleData style) {
        this.style = style;
        dialog = DialogSceneComposer.dialog;
        button = (DialogSceneComposerModel.SimImageButton) dialog.simActor;
        previousStyle = button.style;
    }
    
    @Override
    public void undo() {
        button.style = previousStyle;
        
        if (dialog.simActor != button) {
            dialog.simActor = button;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        button.style = style;
        
        if (dialog.simActor != button) {
            dialog.simActor = button;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"ImageButton style: " + style.name + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"ImageButton style: " + style.name + "\"";
    }
}
