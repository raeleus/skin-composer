package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimButton;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimTextField;

public class TextFieldTouchableUndoable implements SceneComposerUndoable {
    private SimTextField textField;
    private DialogSceneComposer dialog;
    private Touchable touchable;
    private Touchable previousTouchable;
    
    public TextFieldTouchableUndoable(Touchable touchable) {
        this.touchable = touchable;
        dialog = DialogSceneComposer.dialog;
        textField = (SimTextField) dialog.simActor;
        previousTouchable = textField.touchable;
    }
    
    @Override
    public void undo() {
        textField.touchable = previousTouchable;
        
        if (dialog.simActor != textField) {
            dialog.simActor = textField;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        textField.touchable = touchable;
        
        if (dialog.simActor != textField) {
            dialog.simActor = textField;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"TextField touchable " + touchable + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"TextField touchable " + touchable + "\"";
    }
}
