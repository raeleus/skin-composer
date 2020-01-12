package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TreeResetUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTree tree;
    private DialogSceneComposer dialog;
    private String previousName;
    private StyleData previousStyle;
    private Array<DialogSceneComposerModel.SimNode> children;
    private float padLeft;
    private float padRight;
    private float iconSpaceLeft;
    private float iconSpaceRight;
    private float indentSpacing;
    private float ySpacing;
    
    public TreeResetUndoable() {
        dialog = DialogSceneComposer.dialog;
        tree = (DialogSceneComposerModel.SimTree) dialog.simActor;
        previousName = tree.name;
        previousStyle = tree.style;
        children = new Array<>(tree.children);
        padLeft = tree.padLeft;
        padRight = tree.padRight;
        iconSpaceLeft = tree.iconSpaceLeft;
        iconSpaceRight = tree.iconSpaceRight;
        indentSpacing = tree.indentSpacing;
        ySpacing = tree.ySpacing;
    }
    
    @Override
    public void undo() {
        tree.name = previousName;
        tree.style = previousStyle;
        tree.children.clear();
        tree.children.addAll(children);
        tree.padLeft = padLeft;
        tree.padRight = padRight;
        tree.iconSpaceLeft = iconSpaceLeft;
        tree.iconSpaceRight = iconSpaceRight;
        tree.indentSpacing = indentSpacing;
        tree.ySpacing = ySpacing;
        
        if (dialog.simActor != tree) {
            dialog.simActor = tree;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        tree.reset();
        
        if (dialog.simActor != tree) {
            dialog.simActor = tree;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Reset Tree\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Reset Tree\"";
    }
}
