package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class SplitPaneStyleUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimSplitPane splitPane;
    private StyleData style;
    private StyleData previousStyle;
    private DialogSceneComposer dialog;
    
    public SplitPaneStyleUndoable(StyleData style) {
        this.style = style;
        dialog = DialogSceneComposer.dialog;
        splitPane = (DialogSceneComposerModel.SimSplitPane) dialog.simActor;
        previousStyle = splitPane.style;
    }
    
    @Override
    public void undo() {
        splitPane.style = previousStyle;
        
        if (dialog.simActor != splitPane) {
            dialog.simActor = splitPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        splitPane.style = style;
        
        if (dialog.simActor != splitPane) {
            dialog.simActor = splitPane;
            dialog.populateProperties();
            dialog.populatePath();
        }
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"SplitPane style: " + style.name + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"SplitPane style: " + style.name + "\"";
    }
}
