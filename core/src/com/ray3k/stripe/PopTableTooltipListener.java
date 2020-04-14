package com.ray3k.stripe;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.utils.Align;
import com.ray3k.stripe.PopTable.PopTableStyle;
import com.ray3k.stripe.PopTable.TableHiddenListener;

public class PopTableTooltipListener extends InputListener {
    protected PopTable popTable;
    private int align;
    private final static Vector2 temp = new Vector2();
    
    public PopTableTooltipListener(int align, Skin skin) {
        this(align, skin.get(PopTableStyle.class));
    }
    
    public PopTableTooltipListener(int align, Skin skin, String style) {
        this(align, skin.get(style, PopTableStyle.class));
    }
    
    public PopTableTooltipListener(int align, PopTableStyle style) {
        popTable = new PopTable(style);
        popTable.setModal(false);
        popTable.setHideOnUnfocus(false);
        popTable.setTouchable(Touchable.disabled);
        this.align = align;
        popTable.addListener(new TableHiddenListener() {
            @Override
            public void tableShown(Event event) {
                com.ray3k.stripe.PopTableTooltipListener.this.tableShown(event);
            }
            
            @Override
            public void tableHidden(Event event) {
                com.ray3k.stripe.PopTableTooltipListener.this.tableHidden(event);
            }
        });
    }

    @Override
    public boolean mouseMoved(InputEvent event, float x, float y) {
        temp.set(x, y);
        event.getListenerActor().localToStageCoordinates(temp);
        switch (align) {
            case Align.bottomLeft:
            case Align.topLeft:
            case Align.left:
                temp.x -= popTable.getWidth();
                break;
            case Align.bottom:
            case Align.top:
            case Align.center:
                temp.x -= popTable.getWidth() / 2;
                break;
        }
        switch (align) {
            case Align.bottomLeft:
            case Align.bottomRight:
            case Align.bottom:
                temp.y -= popTable.getHeight();
                break;
            case Align.left:
            case Align.right:
            case Align.center:
                temp.y -= popTable.getHeight() / 2;
                break;
        }
        popTable.setPosition(temp.x, temp.y);
        return false;
    }

    @Override
    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        if (fromActor == null || !event.getListenerActor().isAscendantOf(fromActor)) {
            Stage stage = event.getListenerActor().getStage();
            Actor actor = event.getListenerActor();
            
            if (actor instanceof Disableable) {
                if (((Disableable) actor).isDisabled()) return;
            }

            temp.set(x, y);
            event.getListenerActor().localToStageCoordinates(temp);
            switch (align) {
                case Align.bottomLeft:
                case Align.topLeft:
                case Align.left:
                    temp.x -= popTable.getWidth();
                    break;
                case Align.bottom:
                case Align.top:
                case Align.center:
                    temp.x -= popTable.getWidth() / 2;
                    break;
            }
            switch (align) {
                case Align.bottomLeft:
                case Align.bottomRight:
                case Align.bottom:
                    temp.y -= popTable.getHeight();
                    break;
                case Align.left:
                case Align.right:
                case Align.center:
                    temp.y -= popTable.getHeight() / 2;
                    break;
            }
            popTable.setPosition(temp.x, temp.y);
            
            
            popTable.show(stage);
            popTable.moveToInsideStage();
        }
    }
    
    @Override
    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
        if (toActor == null || !event.getListenerActor().isAscendantOf(toActor)) {
            popTable.hide();
        }
    }
    
    public PopTable getPopTable() {
        return popTable;
    }
    
    /**
     * Override this method to be performed when the popTable is hidden or dismissed.
     */
    public void tableShown(Event event) {
    
    }
    
    /**
     * Override this method to be performed when the popTable is hidden or dismissed.
     */
    public void tableHidden(Event event) {
    
    }
}
