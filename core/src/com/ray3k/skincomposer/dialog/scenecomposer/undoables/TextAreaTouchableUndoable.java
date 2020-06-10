package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimButton;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimTextArea;

public class TextAreaTouchableUndoable implements SceneComposerUndoable {
    private SimTextArea textArea;
    private DialogSceneComposer dialog;
    private Touchable touchable;
    private Touchable previousTouchable;
    
    public TextAreaTouchableUndoable(Touchable touchable) {
        this.touchable = touchable;
        dialog = DialogSceneComposer.dialog;
        textArea = (SimTextArea) dialog.simActor;
        previousTouchable = textArea.touchable;
    }
    
    @Override
    public void undo() {
        textArea.touchable = previousTouchable;
        
        if (dialog.simActor != textArea) {
            dialog.simActor = textArea;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        textArea.touchable = touchable;
        
        if (dialog.simActor != textArea) {
            dialog.simActor = textArea;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextArea touchable " + touchable + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextArea touchable " + touchable + "\"";
    }
}
