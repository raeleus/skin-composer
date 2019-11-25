package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerEvents;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class CellSetWidgetUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimCell cell;
    private DialogSceneComposer dialog;
    private DialogSceneComposerModel.SimActor newWidget;
    private DialogSceneComposerModel.SimActor previousWidget;
    
    public CellSetWidgetUndoable(DialogSceneComposerEvents.WidgetType widgetType) {
        dialog = DialogSceneComposer.dialog;
        cell = (DialogSceneComposerModel.SimCell) dialog.simActor;
        
        previousWidget = cell.child;
        
        if (widgetType == DialogSceneComposerEvents.WidgetType.TEXT_BUTTON) {
            newWidget = new DialogSceneComposerModel.SimTextButton();
        }
        newWidget.parent = cell;
    }
    
    @Override
    public void undo() {
        cell.child = previousWidget;
        
        if (dialog.simActor != cell) {
            dialog.simActor = cell;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        cell.child = newWidget;
        
        if (dialog.simActor != cell.child) {
            dialog.simActor = cell.child;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Set Widget to Cell\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Set Widget to Cell\"";
    }
}
