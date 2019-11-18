package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.ray3k.skincomposer.dialog.DialogSceneComposer;

public class TablePaddingUndoable implements SceneComposerUndoable {
    private Table table;
    private float paddingLeft, paddingRight, paddingTop, paddingBottom;
    private float previousPaddingLeft, previousPaddingRight, previousPaddingTop, previousPaddingBottom;
    
    public TablePaddingUndoable(float paddingLeft, float paddingRight, float paddingTop, float paddingBottom) {
        this.paddingLeft = paddingLeft;
        this.paddingRight = paddingRight;
        this.paddingTop = paddingTop;
        this.paddingBottom = paddingBottom;
        var dialog = DialogSceneComposer.dialog;
        table = (Table) dialog.selectedActor;
        previousPaddingLeft = table.getPadLeft();
        previousPaddingRight = table.getPadRight();
        previousPaddingTop = table.getPadTop();
        previousPaddingBottom = table.getPadBottom();
    }
    
    @Override
    public void undo() {
        table.pad(previousPaddingTop, previousPaddingLeft, previousPaddingBottom, previousPaddingRight);
    }
    
    @Override
    public void redo() {
        table.pad(paddingTop, paddingLeft, paddingBottom, paddingRight);
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
