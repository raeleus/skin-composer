package com.ray3k.skincomposer.dialog.scenecomposer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimActor;

import static com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer.dialog;
import static com.ray3k.skincomposer.Main.*;

public class EditWidget extends Button {
    private Actor followActor;
    private Cell cell;
    private static final Vector2 temp = new Vector2();
    private SimActor simActor;
    
    public EditWidget(Skin skin) {
        super(skin);
        addSimListener();
    }
    
    public EditWidget(Skin skin, String styleName) {
        super(skin, styleName);
        addSimListener();
    }
    
    public EditWidget(ButtonStyle style) {
        super(style);
        addSimListener();
    }
    
    private void addSimListener() {
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (simActor != null) {
                    dialog.simActor = simActor;
                    dialog.populateProperties();
                    dialog.model.updatePreview();
                    dialog.populatePath();
                    
                    var label = new FadeLabel(simActor.toString(), skin, "scene-edit-tip");
                    stage.addActor(label);
                    label.setPosition(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
                }
            }
        });
        addListener(handListener);
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
    
    public SimActor getSimActor() {
        return simActor;
    }
    
    public void setSimActorTarget(SimActor simActor) {
        this.simActor = simActor;
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
            temp.set(table.getPadLeft(), table.getPadBottom());
    
            var contentWidth = 0f;
            var contentHeight = 0f;
            for (int i = 0; i < table.getColumns(); i++) {
                contentWidth += table.getColumnWidth(i);
            }
            for (int i = 0; i < table.getRows(); i++) {
                contentHeight += table.getRowHeight(i);
            }
    
            if ((table.getAlign() & Align.right) != 0) {
                temp.add(table.getWidth() - contentWidth - table.getPadLeft() - table.getPadRight(), 0);
            } else if ((table.getAlign() & Align.left) == 0) {
                temp.add((table.getWidth() - contentWidth - table.getPadLeft() - table.getPadRight()) / 2, 0);
            }
    
            if ((table.getAlign() & Align.top) != 0) {
                temp.add(0, table.getHeight() - contentHeight - table.getPadBottom()  - table.getPadTop());
            } else if ((table.getAlign() & Align.bottom) == 0) {
                temp.add(0, (table.getHeight() - contentHeight - table.getPadBottom() - table.getPadTop()) / 2);
            }
            
            table.localToStageCoordinates(temp);
            stageToLocalCoordinates(temp);
            
            var xpos = 0f;
            var ypos = 0f;
            
            for (int i = 0; i < cell.getColumn(); i++) {
                xpos += table.getColumnWidth(i);
            }
            
            for (int i = table.getRows() - 1; i > cell.getRow(); i--) {
                ypos += table.getRowHeight(i);
            }

            var cellWidth = 0;
            for (int i = cell.getColumn(); i < cell.getColumn() + cell.getColspan(); i++) {
                cellWidth += table.getColumnWidth(i);
            }
            
            setBounds(temp.x + xpos, temp.y + ypos, cellWidth, table.getRowHeight(cell.getRow()));
        }
    }
}
