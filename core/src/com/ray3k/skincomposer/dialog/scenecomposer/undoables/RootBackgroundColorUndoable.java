package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.graphics.Color;
import com.ray3k.skincomposer.data.ColorData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class RootBackgroundColorUndoable implements SceneComposerUndoable {
    private DialogSceneComposer dialog;
    private DialogSceneComposerModel model;
    private DialogSceneComposerModel.SimGroup group;
    private ColorData color;
    private ColorData colorPrevious;
    
    public RootBackgroundColorUndoable(ColorData newColor) {
        dialog = DialogSceneComposer.dialog;
        model = dialog.model;
        group = (DialogSceneComposerModel.SimGroup) dialog.simActor;
        
        colorPrevious = model.backgroundColor;
        color = newColor;
    }
    
    @Override
    public void undo() {
        model.backgroundColor = colorPrevious;
        dialog.previewTable.setColor(colorPrevious.color);
    }
    
    @Override
    public void redo() {
        model.backgroundColor = color;
        dialog.previewTable.setColor(color.color);
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Set background color (" + color + ") \"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Set background color (" + color + ") \"";
    }
}
