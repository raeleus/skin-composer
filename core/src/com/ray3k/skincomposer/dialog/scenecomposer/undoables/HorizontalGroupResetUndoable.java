package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class HorizontalGroupResetUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimHorizontalGroup horizontalGroup;
    private DialogSceneComposer dialog;
    private String previousName;
    private int previousAlignment;
    private boolean previousExpand;
    private boolean previousFill;
    private float previousPadLeft;
    private float previousPadRight;
    private float previousPadTop;
    private float previousPadBottom;
    private boolean previousReverse;
    private int previousRowAlignment;
    private float previousSpace;
    private boolean previousWrap;
    private float previousWrapSpace;
    private Array<DialogSceneComposerModel.SimActor> children;
    
    public HorizontalGroupResetUndoable() {
        dialog = DialogSceneComposer.dialog;
        horizontalGroup = (DialogSceneComposerModel.SimHorizontalGroup) dialog.simActor;
        
        previousName = horizontalGroup.name;
        previousAlignment = horizontalGroup.alignment;
        previousExpand = horizontalGroup.expand;
        previousFill = horizontalGroup.fill;
        previousPadLeft = horizontalGroup.padLeft;
        previousPadRight = horizontalGroup.padRight;
        previousPadTop = horizontalGroup.padTop;
        previousPadBottom = horizontalGroup.padBottom;
        previousReverse = horizontalGroup.reverse;
        previousRowAlignment = horizontalGroup.rowAlignment;
        previousSpace = horizontalGroup.space;
        previousWrap = horizontalGroup.wrap;
        previousWrapSpace = horizontalGroup.wrapSpace;
        children = new Array<>(horizontalGroup.children);
    }
    
    @Override
    public void undo() {
        horizontalGroup.name = previousName;
        horizontalGroup.alignment = previousAlignment;
        horizontalGroup.expand = previousExpand;
        horizontalGroup.fill = previousFill;
        horizontalGroup.padLeft = previousPadLeft;
        horizontalGroup.padRight = previousPadRight;
        horizontalGroup.padTop = previousPadTop;
        horizontalGroup.padBottom = previousPadBottom;
        horizontalGroup.reverse = previousReverse;
        horizontalGroup.rowAlignment = previousRowAlignment;
        horizontalGroup.space = previousSpace;
        horizontalGroup.wrap = previousWrap;
        horizontalGroup.wrapSpace = previousWrapSpace;
        horizontalGroup.children.clear();
        horizontalGroup.children.addAll(children);
    
        if (dialog.simActor != horizontalGroup) {
            dialog.simActor = horizontalGroup;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        horizontalGroup.reset();
        
        if (dialog.simActor != horizontalGroup) {
            dialog.simActor = horizontalGroup;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Reset HorizontalGroup\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Reset HorizontalGroup\"";
    }
}
