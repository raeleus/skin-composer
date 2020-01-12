package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ListResetUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimList list;
    private DialogSceneComposer dialog;
    private String previousName;
    private StyleData previousStyle;
    private Array<String> previousList;
    
    public ListResetUndoable() {
        dialog = DialogSceneComposer.dialog;
        list = (DialogSceneComposerModel.SimList) dialog.simActor;
        
        previousName = list.name;
        previousStyle = list.style;
        previousList = new Array<>(list.list);
    }
    
    @Override
    public void undo() {
        list.name = previousName;
        list.style = previousStyle;
        list.list.clear();
        list.list.addAll(previousList);
    
        if (dialog.simActor != list) {
            dialog.simActor = list;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        list.reset();
        
        if (dialog.simActor != list) {
            dialog.simActor = list;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Reset List\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Reset List\"";
    }
}
