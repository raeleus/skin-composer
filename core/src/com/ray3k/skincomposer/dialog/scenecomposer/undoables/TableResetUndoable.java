package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ObjectMap;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.data.ColorData;
import com.ray3k.skincomposer.data.DrawableData;
import com.ray3k.skincomposer.dialog.DialogSceneComposer;

public class TableResetUndoable implements SceneComposerUndoable {
    private Table table;
    private float previousPaddingLeft, previousPaddingRight, previousPaddingTop, previousPaddingBottom;
    private String name;
    private String previousName;
    private ColorData colorData;
    private ColorData previousColorData;
    private ObjectMap<String, Object> objectMap;
    private DrawableData previousDrawableData;
    private int previousAlignment;
    
    public TableResetUndoable() {
        var dialog = DialogSceneComposer.dialog;
        table = (Table) dialog.selectedObject;
        previousName = table.getName();
        previousPaddingLeft = table.getPadLeft();
        previousPaddingRight = table.getPadRight();
        previousPaddingTop = table.getPadTop();
        previousPaddingBottom = table.getPadBottom();
        objectMap = (ObjectMap<String, Object>) table.getUserObject();
        previousColorData = (ColorData) objectMap.get("color");
        previousDrawableData = (DrawableData) objectMap.get("background");
        previousAlignment = table.getAlign();
    }
    
    @Override
    public void undo() {
        table.pad(previousPaddingTop, previousPaddingLeft, previousPaddingBottom, previousPaddingRight);
        table.setName(previousName);
        table.setColor(previousColorData == null ? Color.WHITE : previousColorData.color);
        objectMap.put("color", previousColorData);
        objectMap.put("background", previousDrawableData);
        table.setBackground(previousDrawableData == null ? null : Main.main.getAtlasData().getDrawablePairs().get(previousDrawableData));
        table.align(previousAlignment);
    }
    
    @Override
    public void redo() {
        table.reset();
        objectMap.clear();
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
