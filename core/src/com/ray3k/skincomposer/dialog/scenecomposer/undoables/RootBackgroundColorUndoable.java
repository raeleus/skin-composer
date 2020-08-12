package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.graphics.Color;
import com.ray3k.skincomposer.data.ColorData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimRootGroup;

import static com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.rootActor;

public class RootBackgroundColorUndoable implements SceneComposerUndoable {
    private DialogSceneComposer dialog;
    private ColorData color;
    private ColorData colorPrevious;
    
    public RootBackgroundColorUndoable(ColorData newColor) {
        dialog = DialogSceneComposer.dialog;
        
        colorPrevious = rootActor.backgroundColor;
        color = newColor;
    }
    
    @Override
    public void undo() {
        rootActor.backgroundColor = colorPrevious;
        dialog.previewTable.setColor(colorPrevious == null ? Color.WHITE : colorPrevious.color);
    }
    
    @Override
    public void redo() {
        rootActor.backgroundColor = color;
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
