package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.data.ColorData;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class CheckBoxResetUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimCheckBox checkBox;
    private DialogSceneComposer dialog;
    private String previousName;
    private StyleData previousStyle;
    private boolean previousDisabled;
    private String previousText;
    private ColorData previousColorData;
    private float previousPadLeft;
    private float previousPadRight;
    private float previousPadTop;
    private float previousPadBottom;
    private boolean previousChecked;
    
    public CheckBoxResetUndoable() {
        dialog = DialogSceneComposer.dialog;
        checkBox = (DialogSceneComposerModel.SimCheckBox) dialog.simActor;
        
        previousName = checkBox.name;
        previousStyle = checkBox.style;
        previousDisabled = checkBox.disabled;
        previousText = checkBox.text;
        previousColorData = checkBox.color;
        previousPadLeft = checkBox.padLeft;
        previousPadRight = checkBox.padRight;
        previousPadTop = checkBox.padTop;
        previousPadBottom = checkBox.padBottom;
        previousChecked = checkBox.checked;
    }
    
    @Override
    public void undo() {
        checkBox.name = previousName;
        checkBox.style = previousStyle;
        checkBox.disabled = previousDisabled;
        checkBox.text = previousText;
        checkBox.color = previousColorData;
        checkBox.padLeft = previousPadLeft;
        checkBox.padRight = previousPadRight;
        checkBox.padTop = previousPadTop;
        checkBox.padBottom = previousPadBottom;
        checkBox.checked = previousChecked;
    
        if (dialog.simActor != checkBox) {
            dialog.simActor = checkBox;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        checkBox.reset();
        
        if (dialog.simActor != checkBox) {
            dialog.simActor = checkBox;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Reset CheckBox\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Reset CheckBox\"";
    }
}
