package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TextFieldStyleUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTextField textField;
    private StyleData style;
    private StyleData previousStyle;
    private DialogSceneComposer dialog;
    
    public TextFieldStyleUndoable(StyleData style) {
        this.style = style;
        dialog = DialogSceneComposer.dialog;
        textField = (DialogSceneComposerModel.SimTextField) dialog.simActor;
        previousStyle = textField.style;
    }
    
    @Override
    public void undo() {
        textField.style = previousStyle;
        
        if (dialog.simActor != textField) {
            dialog.simActor = textField;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        textField.style = style;
        
        if (dialog.simActor != textField) {
            dialog.simActor = textField;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextField style: " + style.name + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextField style: " + style.name + "\"";
    }
}
