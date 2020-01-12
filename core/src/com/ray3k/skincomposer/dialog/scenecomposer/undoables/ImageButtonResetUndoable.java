package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.data.ColorData;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ImageButtonResetUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimImageButton button;
    private float previousPadLeft, previousPadRight, previousPadTop, previousPadBottom;
    private boolean previousChecked, previousDisabled;
    private String previousName;
    private ColorData previousColorData;
    private StyleData previousStyle;
    private String previousText;
    private DialogSceneComposer dialog;
    
    public ImageButtonResetUndoable() {
        dialog = DialogSceneComposer.dialog;
        button = (DialogSceneComposerModel.SimImageButton) dialog.simActor;
        previousName = button.name;
        previousPadLeft = button.padLeft;
        previousPadRight = button.padRight;
        previousPadTop = button.padTop;
        previousPadBottom = button.padBottom;
        previousColorData = button.color;
        previousChecked = button.checked;
        previousDisabled = button.disabled;
        previousStyle = button.style;
    }
    
    @Override
    public void undo() {
        button.padLeft = previousPadLeft;
        button.padRight = previousPadRight;
        button.padTop = previousPadTop;
        button.padBottom = previousPadBottom;
        button.name = previousName;
        button.color = previousColorData;
        button.checked = previousChecked;
        button.disabled = previousDisabled;
        button.style = previousStyle;
        
        if (dialog.simActor != button) {
            dialog.simActor = button;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        button.reset();
        
        if (dialog.simActor != button) {
            dialog.simActor = button;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Reset ImageButton\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Reset ImageButton\"";
    }
}
