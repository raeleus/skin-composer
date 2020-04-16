package com.ray3k.skincomposer.dialog.scenecomposer;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

public class EditWidget extends Button {
    private Actor followActor;
    private Cell cell;
    private static final Vector2 temp = new Vector2();
    
    public EditWidget(Skin skin) {
        super(skin);
    }
    
    public EditWidget(Skin skin, String styleName) {
        super(skin, styleName);
    }
    
    public EditWidget(ButtonStyle style) {
        super(style);
    }
    
    public Actor getFollowActor() {
        return followActor;
    }
    
    public void setFollowActor(Actor followActor) {
        this.followActor = followActor;
    }
    
    public Cell getCell() {
        return cell;
    }
    
    public void setCell(Cell cell) {
        this.cell = cell;
    }
    
    @Override
    public void layout() {
        super.layout();
        temp.set(0, 0);
        
        if (followActor != null) {
            followActor.localToStageCoordinates(temp);
            stageToLocalCoordinates(temp);
            setBounds(temp.x, temp.y, followActor.getWidth(), followActor.getHeight());
        } else if (cell != null) {
            var table = cell.getTable();
            temp.set(0, 0);
    
            var contentWidth = 0f;
            var contentHeight = 0f;
            for (int i = 0; i < table.getColumns(); i++) {
                contentWidth += table.getColumnWidth(i);
            }
            for (int i = 0; i < table.getRows(); i++) {
                contentHeight += table.getRowHeight(i);
            }
    
            if ((table.getAlign() & Align.right) != 0) {
                temp.add(table.getWidth() - contentWidth, 0);
            } else if ((table.getAlign() & Align.left) == 0) {
                temp.add((table.getWidth() - contentWidth) / 2, 0);
            }
    
            if ((table.getAlign() & Align.top) != 0) {
                temp.add(0, table.getHeight() - contentHeight);
            } else if ((table.getAlign() & Align.bottom) == 0) {
                temp.add(0, (table.getHeight() - contentHeight) / 2);
            }
            
            table.localToStageCoordinates(temp);
            stageToLocalCoordinates(temp);
            
            var width = 0f;
            var height = 0f;
            
            for (int i = 0; i < cell.getColumn(); i++) {
                width += table.getColumnWidth(i);
            }
            
            for (int i = table.getRows() - 1; i > cell.getRow(); i--) {
                height += table.getRowHeight(i);
            }

            setBounds(temp.x + width, temp.y + height, table.getColumnWidth(cell.getColumn()), table.getRowHeight(cell.getRow()));
        }
    }
}
