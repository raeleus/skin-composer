package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.data.ColorData;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class LabelResetUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimLabel label;
    private DialogSceneComposer dialog;
    private String previousName;
    private StyleData previousStyle;
    private String previousText;
    private int previousTextAlignment;
    private boolean previousEllipsis;
    private String previousEllipsisString;
    private boolean previousWrap;
    private ColorData previousColor;
    
    public LabelResetUndoable() {
        dialog = DialogSceneComposer.dialog;
        label = (DialogSceneComposerModel.SimLabel) dialog.simActor;
        
        previousName = label.name;
        previousStyle = label.style;
        previousText = label.text;
        previousTextAlignment = label.textAlignment;
        previousEllipsis = label.ellipsis;
        previousEllipsisString = label.ellipsisString;
        previousWrap = label.wrap;
        previousColor = label.color;
    }
    
    @Override
    public void undo() {
        label.name = previousName;
        label.style = previousStyle;
        label.text = previousText;
        label.textAlignment = previousTextAlignment;
        label.ellipsis = previousEllipsis;
        label.ellipsisString = previousEllipsisString;
        label.wrap = previousWrap;
        label.color = previousColor;
    
        if (dialog.simActor != label) {
            dialog.simActor = label;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        label.reset();
        
        if (dialog.simActor != label) {
            dialog.simActor = label;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Reset Label\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Reset Label\"";
    }
}
