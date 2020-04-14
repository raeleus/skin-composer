package com.ray3k.stripe;

import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.ray3k.stripe.PopTable.PopTableStyle;
import com.ray3k.stripe.PopTable.TableHiddenListener;

public class PopTableHoverListener extends InputListener {
    protected PopTable popTable;
    public boolean hideOnExit;
    private int align;
    private int edge;
    
    public PopTableHoverListener(int edge, int align, Skin skin) {
        this(edge, align, skin.get(PopTableStyle.class));
    }
    
    public PopTableHoverListener(int edge, int align, Skin skin, String style) {
        this(edge, align, skin.get(style, PopTableStyle.class));
    }
    
    public PopTableHoverListener(int edge, int align, PopTableStyle style) {
        hideOnExit = true;
        popTable = new PopTable(style);
        popTable.setModal(false);
        popTable.setHideOnUnfocus(true);
        popTable.setTouchable(Touchable.disabled);
        this.edge = edge;
        this.align = align;
        popTable.addListener(new TableHiddenListener() {
            @Override
            public void tableShown(Event event) {
                com.ray3k.stripe.PopTableHoverListener.this.tableShown(event);
            }
            
            @Override
            public void tableHidden(Event event) {
                com.ray3k.stripe.PopTableHoverListener.this.tableHidden(event);
            }
        });
    }
    
    @Override
    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        if (popTable.isHidden()) {
            if (fromActor == null || !event.getListenerActor().isAscendantOf(fromActor)) {
                Stage stage = event.getListenerActor().getStage();
                Actor actor = event.getListenerActor();

                if (actor instanceof Disableable) {
                    if (((Disableable) actor).isDisabled()) return;
                }

                popTable.show(stage);
                popTable.attachToActor(actor, edge, align);
                

                popTable.moveToInsideStage();
            }
        }
    }

    @Override
    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
        if (hideOnExit && !popTable.isHidden()) {
            if (toActor == null || !event.getListenerActor().isAscendantOf(toActor)) {
                popTable.hide();
            }
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
