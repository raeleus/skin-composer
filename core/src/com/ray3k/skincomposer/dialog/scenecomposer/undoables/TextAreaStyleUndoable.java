package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TextAreaStyleUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTextArea textArea;
    private StyleData style;
    private StyleData previousStyle;
    private DialogSceneComposer dialog;
    
    public TextAreaStyleUndoable(StyleData style) {
        this.style = style;
        dialog = DialogSceneComposer.dialog;
        textArea = (DialogSceneComposerModel.SimTextArea) dialog.simActor;
        previousStyle = textArea.style;
    }
    
    @Override
    public void undo() {
        textArea.style = previousStyle;
        
        if (dialog.simActor != textArea) {
            dialog.simActor = textArea;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        textArea.style = style;
        
        if (dialog.simActor != textArea) {
            dialog.simActor = textArea;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextArea style: " + style.name + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextArea style: " + style.name + "\"";
    }
}
