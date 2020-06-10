package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimButton;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimTable;

public class TableTouchableUndoable implements SceneComposerUndoable {
    private SimTable table;
    private DialogSceneComposer dialog;
    private Touchable touchable;
    private Touchable previousTouchable;
    
    public TableTouchableUndoable(Touchable touchable) {
        this.touchable = touchable;
        dialog = DialogSceneComposer.dialog;
        table = (SimTable) dialog.simActor;
        previousTouchable = table.touchable;
    }
    
    @Override
    public void undo() {
        table.touchable = previousTouchable;
        
        if (dialog.simActor != table) {
            dialog.simActor = table;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        table.touchable = touchable;
        
        if (dialog.simActor != table) {
            dialog.simActor = table;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Table touchable " + touchable + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Table touchable " + touchable + "\"";
    }
}