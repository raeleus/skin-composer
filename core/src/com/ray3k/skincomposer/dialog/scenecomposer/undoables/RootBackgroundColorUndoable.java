package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.data.ColorData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimRootGroup;

public class RootBackgroundColorUndoable implements SceneComposerUndoable {
    private DialogSceneComposer dialog;
    private DialogSceneComposerModel model;
    private SimRootGroup group;
    private ColorData color;
    private ColorData colorPrevious;
    
    public RootBackgroundColorUndoable(ColorData newColor) {
        dialog = DialogSceneComposer.dialog;
        model = dialog.model;
        group = (SimRootGroup) dialog.simActor;
        
        colorPrevious = model.root.backgroundColor;
        color = newColor;
    }
    
    @Override
    public void undo() {
        model.root.backgroundColor = colorPrevious;
        dialog.previewTable.setColor(colorPrevious.color);
    }
    
    @Override
    public void redo() {
        model.root.backgroundColor = color;
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
