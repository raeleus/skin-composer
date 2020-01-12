package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.data.ColorData;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TextButtonResetUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTextButton textButton;
    private float previousPadLeft, previousPadRight, previousPadTop, previousPadBottom;
    private boolean previousChecked, previousDisabled;
    private String previousName;
    private ColorData previousColorData;
    private StyleData previousStyle;
    private String previousText;
    private DialogSceneComposer dialog;
    
    public TextButtonResetUndoable() {
        dialog = DialogSceneComposer.dialog;
        textButton = (DialogSceneComposerModel.SimTextButton) dialog.simActor;
        previousName = textButton.name;
        previousPadLeft = textButton.padLeft;
        previousPadRight = textButton.padRight;
        previousPadTop = textButton.padTop;
        previousPadBottom = textButton.padBottom;
        previousColorData = textButton.color;
        previousChecked = textButton.checked;
        previousDisabled = textButton.disabled;
        previousStyle = textButton.style;
        previousText = textButton.text;
    }
    
    @Override
    public void undo() {
        textButton.padLeft = previousPadLeft;
        textButton.padRight = previousPadRight;
        textButton.padTop = previousPadTop;
        textButton.padBottom = previousPadBottom;
        textButton.name = previousName;
        textButton.color = previousColorData;
        textButton.checked = previousChecked;
        textButton.disabled = previousDisabled;
        textButton.style = previousStyle;
        textButton.text = previousText;
        
        if (dialog.simActor != textButton) {
            dialog.simActor = textButton;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        textButton.reset();
        
        if (dialog.simActor != textButton) {
            dialog.simActor = textButton;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Reset TextButton\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Reset TextButton\"";
    }
}
