package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ObjectMap;
import com.ray3k.skincomposer.data.ColorData;
import com.ray3k.skincomposer.dialog.DialogSceneComposer;

public class TableColorUndoable implements SceneComposerUndoable {
    private Table table;
    private ColorData colorData;
    private ColorData previousColorData;
    private ObjectMap<String, Object> objectMap;
    
    public TableColorUndoable(ColorData colorData) {
        this.colorData = colorData;
        var dialog = DialogSceneComposer.dialog;
        table = (Table) dialog.selectedActor;
        objectMap = (ObjectMap<String, Object>) table.getUserObject();
        previousColorData = (ColorData) objectMap.get("color");
    }
    
    @Override
    public void undo() {
        table.setColor(previousColorData == null ? Color.WHITE : previousColorData.color);
        objectMap.put("color", previousColorData);
    }
    
    @Override
    public void redo() {
        table.setColor(colorData.color);
        objectMap.put("color", colorData);
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Table Color: " + colorData.getName() + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Table Color: " + colorData.getName() + "\"";
    }
}
