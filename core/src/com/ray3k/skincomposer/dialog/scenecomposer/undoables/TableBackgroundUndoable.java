package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ObjectMap;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.data.DrawableData;
import com.ray3k.skincomposer.dialog.DialogSceneComposer;

public class TableBackgroundUndoable implements SceneComposerUndoable {
    private Table table;
    private DrawableData drawableData;
    private DrawableData previousDrawableData;
    ObjectMap<String, Object> objectMap;
    
    public TableBackgroundUndoable(DrawableData drawableData) {
        var dialog = DialogSceneComposer.dialog;
        table = (Table) dialog.selectedObject;
        this.drawableData = drawableData;
        objectMap = (ObjectMap<String, Object>) table.getUserObject();
        previousDrawableData = (DrawableData) objectMap.get("background");
    }
    
    @Override
    public void undo() {
        objectMap.put("background", previousDrawableData);
        table.setBackground(Main.main.getAtlasData().getDrawablePairs().get(previousDrawableData));
    }
    
    @Override
    public void redo() {
        objectMap.put("background", drawableData);
        table.setBackground(Main.main.getAtlasData().getDrawablePairs().get(drawableData));
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Table Background: " + drawableData.name + "\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Table Background: " + drawableData.name + "\"";
    }
}
