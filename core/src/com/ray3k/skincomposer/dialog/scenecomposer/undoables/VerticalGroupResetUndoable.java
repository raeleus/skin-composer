package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class VerticalGroupResetUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimVerticalGroup verticalGroup;
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
    private int previousColumnAlignment;
    private float previousSpace;
    private boolean previousWrap;
    private float previousWrapSpace;
    private Array<DialogSceneComposerModel.SimActor> children;
    
    public VerticalGroupResetUndoable() {
        dialog = DialogSceneComposer.dialog;
        verticalGroup = (DialogSceneComposerModel.SimVerticalGroup) dialog.simActor;
        
        previousName = verticalGroup.name;
        previousAlignment = verticalGroup.alignment;
        previousExpand = verticalGroup.expand;
        previousFill = verticalGroup.fill;
        previousPadLeft = verticalGroup.padLeft;
        previousPadRight = verticalGroup.padRight;
        previousPadTop = verticalGroup.padTop;
        previousPadBottom = verticalGroup.padBottom;
        previousReverse = verticalGroup.reverse;
        previousColumnAlignment = verticalGroup.columnAlignment;
        previousSpace = verticalGroup.space;
        previousWrap = verticalGroup.wrap;
        previousWrapSpace = verticalGroup.wrapSpace;
        children = new Array<>(verticalGroup.children);
    }
    
    @Override
    public void undo() {
        verticalGroup.name = previousName;
        verticalGroup.alignment = previousAlignment;
        verticalGroup.expand = previousExpand;
        verticalGroup.fill = previousFill;
        verticalGroup.padLeft = previousPadLeft;
        verticalGroup.padRight = previousPadRight;
        verticalGroup.padTop = previousPadTop;
        verticalGroup.padBottom = previousPadBottom;
        verticalGroup.reverse = previousReverse;
        verticalGroup.columnAlignment = previousColumnAlignment;
        verticalGroup.space = previousSpace;
        verticalGroup.wrap = previousWrap;
        verticalGroup.wrapSpace = previousWrapSpace;
        verticalGroup.children.clear();
        verticalGroup.children.addAll(children);
    
        if (dialog.simActor != verticalGroup) {
            dialog.simActor = verticalGroup;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        verticalGroup.reset();
        
        if (dialog.simActor != verticalGroup) {
            dialog.simActor = verticalGroup;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Reset VerticalGroup\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Reset VerticalGroup\"";
    }
}
