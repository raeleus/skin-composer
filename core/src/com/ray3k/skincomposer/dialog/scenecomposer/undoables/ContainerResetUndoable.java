package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.data.DrawableData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ContainerResetUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimContainer container;
    private DialogSceneComposer dialog;
    private String previousName;
    private int previousAlignment;
    private DrawableData previousBackground;
    private boolean previousFillX;
    private boolean previousFillY;
    private float previousMinWidth;
    private float previousMinHeight;
    private float previousMaxWidth;
    private float previousMaxHeight;
    private float previousPreferredWidth;
    private float previousPreferredHeight;
    private float previousPadLeft;
    private float previousPadRight;
    private float previousPadTop;
    private float previousPadBottom;
    private DialogSceneComposerModel.SimActor child;
    
    public ContainerResetUndoable() {
        dialog = DialogSceneComposer.dialog;
        container = (DialogSceneComposerModel.SimContainer) dialog.simActor;
        
        previousName = container.name;
        previousAlignment = container.alignment;
        previousBackground = container.background;
        previousFillX = container.fillX;
        previousFillY = container.fillY;
        previousMinWidth = container.minWidth;
        previousMinHeight = container.minHeight;
        previousMaxWidth = container.maxWidth;
        previousMaxHeight = container.maxHeight;
        previousPreferredWidth = container.preferredWidth;
        previousPreferredHeight = container.preferredHeight;
        previousPadLeft = container.padLeft;
        previousPadRight = container.padRight;
        previousPadTop = container.padTop;
        previousPadBottom = container.padBottom;
    }
    
    @Override
    public void undo() {
        container.name = previousName;
        container.alignment = previousAlignment;
        container.background = previousBackground;
        container.fillX = previousFillX;
        container.fillY = previousFillY;
        container.minWidth = previousMinWidth;
        container.minHeight = previousMinHeight;
        container.maxWidth = previousMaxWidth;
        container.maxHeight = previousMaxHeight;
        container.preferredWidth = previousPreferredWidth;
        container.preferredHeight = previousPreferredHeight;
        container.padLeft = previousPadLeft;
        container.padRight = previousPadRight;
        container.padTop = previousPadTop;
        container.padBottom = previousPadBottom;
    
        if (dialog.simActor != container) {
            dialog.simActor = container;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        container.reset();
        
        if (dialog.simActor != container) {
            dialog.simActor = container;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Reset Container\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Reset Container\"";
    }
}
