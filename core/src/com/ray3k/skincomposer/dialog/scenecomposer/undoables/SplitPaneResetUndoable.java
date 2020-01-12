package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class SplitPaneResetUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimSplitPane splitPane;
    private DialogSceneComposer dialog;
    private String previousName;
    private StyleData previousStyle;
    private DialogSceneComposerModel.SimActor previousChildFirst;
    private DialogSceneComposerModel.SimActor oreviousChildSecond;
    private boolean previousVertical;
    private float previousSplit;
    private float previousSplitMin;
    private float previousSplitMax;
    
    public SplitPaneResetUndoable() {
        dialog = DialogSceneComposer.dialog;
        splitPane = (DialogSceneComposerModel.SimSplitPane) dialog.simActor;
        previousName = splitPane.name;
        previousStyle = splitPane.style;
        previousChildFirst = splitPane.childFirst;
        oreviousChildSecond = splitPane.childSecond;
        previousVertical = splitPane.vertical;
        previousSplit = splitPane.split;
        previousSplitMin = splitPane.splitMin;
        previousSplitMax = splitPane.splitMax;
    }
    
    @Override
    public void undo() {
        splitPane.name = previousName;
        splitPane.style = previousStyle;
        splitPane.childFirst = previousChildFirst;
        splitPane.childSecond = oreviousChildSecond;
        splitPane.vertical = previousVertical;
        splitPane.split = previousSplit;
        splitPane.splitMin = previousSplitMin;
        splitPane.splitMax = previousSplitMax;
        
        if (dialog.simActor != splitPane) {
            dialog.simActor = splitPane;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        splitPane.reset();
        
        if (dialog.simActor != splitPane) {
            dialog.simActor = splitPane;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Reset SplitPane\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Reset SplitPane\"";
    }
}
