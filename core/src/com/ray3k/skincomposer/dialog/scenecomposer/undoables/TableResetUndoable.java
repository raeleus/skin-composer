package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.data.ColorData;
import com.ray3k.skincomposer.data.DrawableData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class TableResetUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimTable table;
    private float previousPadLeft, previousPadRight, previousPadTop, previousPadBottom;
    private String previousName;
    private ColorData previousColorData;
    private DrawableData previousBackground;
    private int previousAlignment;
    private DialogSceneComposer dialog;
    
    public TableResetUndoable() {
        dialog = DialogSceneComposer.dialog;
        table = (DialogSceneComposerModel.SimTable) dialog.simActor;
        previousName = table.name;
        previousPadLeft = table.padLeft;
        previousPadRight = table.padRight;
        previousPadTop = table.padTop;
        previousPadBottom = table.padBottom;
        previousColorData = table.color;
        previousBackground = table.background;
        previousAlignment = table.alignment;
    }
    
    @Override
    public void undo() {
        table.padLeft = previousPadLeft;
        table.padRight = previousPadRight;
        table.padTop = previousPadTop;
        table.padBottom = previousPadBottom;
        table.name = previousName;
        table.color = previousColorData;
        table.background = previousBackground;
        table.alignment = previousAlignment;
    
        if (dialog.simActor != table) {
            dialog.simActor = table;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        table.reset();
    
        if (dialog.simActor != table) {
            dialog.simActor = table;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Reset Table\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Reset Table\"";
    }
}
