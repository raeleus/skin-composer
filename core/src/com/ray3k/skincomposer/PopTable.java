package com.ray3k.skincomposer;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class PopTable extends Table {
    private Stage stage;
    private Image stageBackground;
    private WidgetGroup group;
    private final static Vector2 temp = new Vector2();
    private boolean hideOnUnfocus;
    private int preferredEdge;
    private boolean keepSizedWithinStage;
    private boolean automaticallyResized;
    private Actor attachToActor;
    private int attachToActorEdge;
    
    public PopTable() {
        this(new PopTableStyle());
    }
    
    public PopTable(Skin skin) {
        this(skin.get(PopTableStyle.class));
    }
    
    public PopTable(Skin skin, String style) {
        this(skin.get(style, PopTableStyle.class));
    }
    
    public PopTable(PopTableStyle style) {
        setTouchable(Touchable.enabled);
        
        stageBackground = new Image(style.stageBackground);
        stageBackground.setFillParent(true);
        stageBackground.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (hideOnUnfocus) {
                    hide();
                }
            }
        });
        
        setBackground(style.background);
        
        hideOnUnfocus = true;
        preferredEdge = Align.top;
        keepSizedWithinStage = true;
        automaticallyResized = true;
    }
    
    public void alignToActorEdge(Actor actor, int edge) {
        float widgetX;
        switch (edge) {
            case Align.left:
            case Align.bottomLeft:
            case Align.topLeft:
                widgetX = -getWidth();
                break;
            case Align.right:
            case Align.bottomRight:
            case Align.topRight:
                widgetX = actor.getWidth();
                break;
            default:
                widgetX = actor.getWidth() / 2f - getWidth() / 2f;
                break;
        }
    
        float widgetY;
        switch (edge) {
            case Align.bottom:
            case Align.bottomLeft:
            case Align.bottomRight:
                widgetY = -getHeight();
                break;
            case Align.top:
            case Align.topLeft:
            case Align.topRight:
                widgetY = actor.getHeight();
                break;
            default:
                widgetY = actor.getHeight() / 2f - getHeight() / 2f;
                break;
        }
    
        temp.set(widgetX, widgetY);
        actor.localToStageCoordinates(temp);
        setPosition(temp.x, temp.y);
    }
    
    private float actorEdgeStageHorizontalDistance(Actor actor, int edge) {
        temp.set(0, 0);
        actor.localToStageCoordinates(temp);
        setPosition(temp.x, temp.y);
        
        float returnValue;
        switch (edge) {
            case Align.left:
            case Align.bottomLeft:
            case Align.topLeft:
                returnValue = temp.x;
                break;
            case Align.right:
            case Align.bottomRight:
            case Align.topRight:
                returnValue = stage.getWidth() - (temp.x + actor.getWidth());
                break;
            default:
                returnValue = 0;
                break;
        }
        
        return returnValue;
    }
    
    private float actorEdgeStageVerticalDistance(Actor actor, int edge) {
        temp.set(0, 0);
        actor.localToStageCoordinates(temp);
        setPosition(temp.x, temp.y);
        
        float returnValue;
        switch (edge) {
            case Align.bottom:
            case Align.bottomLeft:
            case Align.bottomRight:
                returnValue = temp.y;
                break;
            case Align.top:
            case Align.topLeft:
            case Align.topRight:
                returnValue = stage.getHeight() - (temp.y + actor.getHeight());
                break;
            default:
                returnValue = 0;
                break;
        }
        
        return returnValue;
    }
    
    public void moveToInsideStage() {
        if (getStage() != null) {
            if (getX() < 0) setX(0);
            else if (getX() + getWidth() > getStage().getWidth()) setX(getStage().getWidth() - getWidth());
            
            if (getY() < 0) setY(0);
            else if (getY() + getHeight() > getStage().getHeight()) setY(getStage().getHeight() - getHeight());
        }
    }
    
    private void resizeWindowWithinStage() {
        if (getWidth() > stage.getWidth()) {
            setWidth(stage.getWidth());
        }
    
        if (getHeight() > stage.getHeight()) {
            setHeight(stage.getHeight());
        }

        invalidateHierarchy();
        
        moveToInsideStage();
    }
    
    public boolean isOutsideStage() {
        return getX() < 0 || getX() + getWidth() > getStage().getWidth() || getY() < 0 || getY() + getHeight() > getStage().getHeight();
    }
    
    public void hide() {
        hide(fadeOut(.2f));
    }
    
    public void hide(Action action) {
        group.addAction(sequence(action, Actions.removeActor()));
        fire(new TableHiddenEvent());
    }
    
    public void show(Stage stage) {
        Action action = sequence(alpha(0), fadeIn(.2f));
        this.show(stage, action);
    }
    
    public void show(Stage stage, Action action) {
        this.stage = stage;
        group = new WidgetGroup();
        group.setFillParent(true);
        stage.addActor(group);
        
        group.addActor(stageBackground);
        group.addActor(this);
    
        pack();
        
        if (keepSizedWithinStage) {
            resizeWindowWithinStage();
        }
        
        setPosition((int) (stage.getWidth() / 2f - getWidth() / 2f), (int) (stage.getHeight() / 2f - getHeight() / 2f));
        
        group.addAction(action);
        fire(new TableShownEvent());
    }
    
    public static class PopTableStyle {
        /*Optional*/
        public Drawable background, stageBackground;
        
        public PopTableStyle() {
        
        }
        
        public PopTableStyle(PopTableStyle style) {
            background = style.background;
            stageBackground = style.stageBackground;
        }
    }
    
    public static class PopTableClickListener extends ClickListener {
        protected PopTable popTable;
    
        public PopTableClickListener(Skin skin) {
            this(skin.get(PopTableStyle.class));
        }
        
        public PopTableClickListener(Skin skin, String style) {
            this(skin.get(style, PopTableStyle.class));
        }
        
        public PopTableClickListener(PopTableStyle style) {
            popTable = new PopTable(style);
            popTable.addListener(new TableHiddenListener() {
                @Override
                public void tableShown(Event event) {
                    PopTableClickListener.this.tableShown(event);
                }
                
                @Override
                public void tableHidden(Event event) {
                    PopTableClickListener.this.tableHidden(event);
                }
            });
        }
    
        @Override
        public void clicked(InputEvent event, float x, float y) {
            super.clicked(event, x, y);
            var stage = event.getListenerActor().getStage();
            var actor = event.getListenerActor();
            
            if (actor instanceof Disableable) {
                if (((Disableable) actor).isDisabled()) return;
            }
            
            popTable.show(stage);
            int edge = popTable.getPreferredEdge();
            
            popTable.setAttachToActor(actor, edge);
            popTable.alignToActorEdge(actor, edge);
            
            popTable.moveToInsideStage();
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
    
    public static class TableShownEvent extends Event {
    
    }
    
    public static class TableHiddenEvent extends Event {
    
    }
    
    public static abstract class TableHiddenListener implements EventListener {
        @Override
        public boolean handle(Event event) {
            if (event instanceof TableHiddenEvent) {
                tableHidden(event);
                return true;
            } else if (event instanceof TableShownEvent) {
                tableShown(event);
                return true;
            } else {
                return false;
            }
        }
        
        public abstract void tableShown(Event event);
        public abstract void tableHidden(Event event);
    }
    
    public boolean isHideOnUnfocus() {
        return hideOnUnfocus;
    }
    
    public void setHideOnUnfocus(boolean hideOnUnfocus) {
        this.hideOnUnfocus = hideOnUnfocus;
    }
    
    public int getPreferredEdge() {
        return preferredEdge;
    }
    
    public void setPreferredEdge(int preferredEdge) {
        this.preferredEdge = preferredEdge;
    }
    
    public boolean isKeepSizedWithinStage() {
        return keepSizedWithinStage;
    }
    
    public void setKeepSizedWithinStage(boolean keepSizedWithinStage) {
        this.keepSizedWithinStage = keepSizedWithinStage;
    }
    
    public boolean isAutomaticallyResized() {
        return automaticallyResized;
    }
    
    public void setAutomaticallyResized(boolean automaticallyResized) {
        this.automaticallyResized = automaticallyResized;
    }
    
    public Actor getAttachToActor() {
        return attachToActor;
    }
    
    public int getAttachToActorEdge() {
        return attachToActorEdge;
    }
    
    public void setAttachToActor(Actor attachToActor, int edge) {
        this.attachToActor = attachToActor;
        this.attachToActorEdge = edge;
    }
    
    @Override
    public void layout() {
        if (automaticallyResized) {
            var centerX = getX(Align.center);
            var centerY = getY(Align.center);
            pack();
            setPosition(centerX, centerY, Align.center);
            setPosition(MathUtils.floor(getX()), MathUtils.floor(getY()));
        }
        
        if (attachToActor != null) {
            alignToActorEdge(attachToActor, attachToActorEdge);
        }
        
        if (keepSizedWithinStage) {
            resizeWindowWithinStage();
        }
        super.layout();
    }
}