package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TablePaddingUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTable table;
    private float padLeft, padRight, padTop, padBottom;
    private float previousPadLeft, previousPadRight, previousPadTop, previousPadBottom;
    private DialogSceneComposer dialog;
    
    public TablePaddingUndoable(float padLeft, float padRight, float padTop, float padBottom) {
        this.padLeft = padLeft;
        this.padRight = padRight;
        this.padTop = padTop;
        this.padBottom = padBottom;
        dialog = DialogSceneComposer.dialog;
        table = (DialogSceneComposerModel.SimTable) dialog.simActor;
        previousPadLeft = table.padLeft;
        previousPadRight = table.padRight;
        previousPadTop = table.padTop;
        previousPadBottom = table.padBottom;
    }
    
    @Override
    public void undo() {
        table.padLeft = previousPadLeft;
        table.padRight = previousPadRight;
        table.padTop = previousPadTop;
        table.padBottom = previousPadBottom;
        
        if (dialog.simActor != table) {
            dialog.simActor = table;
            dialog.populateProperties();
            dialog.populatePath();
        }
    }
    
    @Override
    public void redo() {
        table.padLeft = padLeft;
        table.padRight = padRight;
        table.padTop = padTop;
        table.padBottom = padBottom;
        
        if (dialog.simActor != table) {
            dialog.simActor = table;
            dialog.populateProperties();
            dialog.populatePath();
        }
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Table Padding\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Table Padding\"";
    }
}
