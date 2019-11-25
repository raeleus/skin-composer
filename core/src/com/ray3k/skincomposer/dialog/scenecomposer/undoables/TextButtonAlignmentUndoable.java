package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.utils.Align;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TextButtonAlignmentUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTextButton textButton;
    private int alignment;
    private int previousAlignment;
    private DialogSceneComposer dialog;
    
    public TextButtonAlignmentUndoable(int alignment) {
        dialog = DialogSceneComposer.dialog;
    
        this.alignment = alignment;
        textButton = (DialogSceneComposerModel.SimTextButton) dialog.simActor;
        previousAlignment = textButton.alignment;
    }
    
    @Override
    public void undo() {
        textButton.alignment = previousAlignment;
    
        if (dialog.simActor != textButton) {
            dialog.simActor = textButton;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        textButton.alignment = alignment;
    
        if (dialog.simActor != textButton) {
            dialog.simActor = textButton;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextButton Alignment: " + Align.toString(alignment) + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextButton Alignment: " + Align.toString(alignment) + "\"";
    }
}
