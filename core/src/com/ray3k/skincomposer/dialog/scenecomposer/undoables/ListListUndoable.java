package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ListListUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimList list;
    private Array<String> textList;
    private Array<String> previousTextList;
    private DialogSceneComposer dialog;
    
    public ListListUndoable(Array<String> textList) {
        this.textList = new Array<>(textList);
        dialog = DialogSceneComposer.dialog;
        list = (DialogSceneComposerModel.SimList) dialog.simActor;
        if (textList != null && textList.equals("")) {
            this.textList = null;
        }
        previousTextList = new Array<>(list.list);
    }
    
    @Override
    public void undo() {
        list.list.clear();
        list.list.addAll(previousTextList);
    
        if (dialog.simActor != list) {
            dialog.simActor = list;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        list.list.clear();
        list.list.addAll(textList);
    
        if (dialog.simActor != list) {
            dialog.simActor = list;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"List values\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"List values\"";
    }
}
