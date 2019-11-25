package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TextButtonStyleUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTextButton textButton;
    private StyleData style;
    private StyleData previousStyle;
    private DialogSceneComposer dialog;
    
    public TextButtonStyleUndoable(StyleData style) {
        this.style = style;
        dialog = DialogSceneComposer.dialog;
        textButton = (DialogSceneComposerModel.SimTextButton) dialog.simActor;
        previousStyle = textButton.style;
    }
    
    @Override
    public void undo() {
        textButton.style = previousStyle;
        
        if (dialog.simActor != textButton) {
            dialog.simActor = textButton;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        textButton.style = style;
        
        if (dialog.simActor != textButton) {
            dialog.simActor = textButton;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextButton style: " + style.name + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextButton style: " + style.name + "\"";
    }
}
