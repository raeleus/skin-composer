package com.ray3k.skincomposer.stripe;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class PopTable extends Table {
    private Stage stage;
    private Image stageBackground;
    private WidgetGroup group;
    private final static Vector2 temp = new Vector2();
    private boolean hideOnUnfocus;
    private int attachEdge;
    private int attachAlign;
    private boolean keepSizedWithinStage;
    private boolean automaticallyResized;
    private Actor attachToActor;
    private HideListener hideListener;
    private boolean modal;
    private boolean hidden;
    private PopTableStyle style;
    
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
        hideListener = new HideListener();
        
        stageBackground = new Image(style.stageBackground);
        stageBackground.setFillParent(true);
        
        setBackground(style.background);
        
        attachEdge = Align.top;
        attachAlign = Align.top;
        keepSizedWithinStage = true;
        automaticallyResized = true;
        setModal(false);
        setHideOnUnfocus(false);
        hidden = true;
        this.style = style;
    }
    
    private void alignToActorEdge(Actor actor, int edge, int alignment) {
        float widgetX;
        switch (edge) {
            case Align.left:
            case Align.bottomLeft:
            case Align.topLeft:
                widgetX = 0;
                break;
            case Align.right:
            case Align.bottomRight:
            case Align.topRight:
                widgetX = actor.getWidth();
                break;
            default:
                widgetX = actor.getWidth() / 2f;
                break;
        }
    
        float widgetY;
        switch (edge) {
            case Align.bottom:
            case Align.bottomLeft:
            case Align.bottomRight:
                widgetY = 0;
                break;
            case Align.top:
            case Align.topLeft:
            case Align.topRight:
                widgetY = actor.getHeight();
                break;
            default:
                widgetY = actor.getHeight() / 2f;
                break;
        }
        
        switch (alignment) {
            case Align.bottom:
            case Align.top:
            case Align.center:
                widgetX -= getWidth() / 2;
                break;
                
            case Align.left:
            case Align.bottomLeft:
            case Align.topLeft:
                widgetX -= getWidth();
                break;
        }
        
        switch (alignment) {
            case Align.right:
            case Align.left:
            case Align.center:
                widgetY -= getHeight() / 2;
                break;
                
            case Align.bottom:
            case Align.bottomLeft:
            case Align.bottomRight:
                widgetY -= getHeight();
                break;
        }
    
        temp.set(widgetX, widgetY);
        actor.localToStageCoordinates(temp);
        setPosition(temp.x, temp.y);
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
        if (!hidden) {
            hidden = true;
            stage.removeCaptureListener(hideListener);
            group.addAction(sequence(action, Actions.removeActor()));
            fire(new TableHiddenEvent());
        }
    }
    
    public void show(Stage stage) {
        Action action = sequence(alpha(0), fadeIn(.2f));
        this.show(stage, action);
    }
    
    public void show(Stage stage, Action action) {
        hidden = false;
        this.stage = stage;
        group = new WidgetGroup();
        group.setFillParent(true);
        group.setTouchable(Touchable.childrenOnly);
        stage.addActor(group);
        stage.addCaptureListener(hideListener);
        
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
    
    private class HideListener extends InputListener {
        @Override
        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
            if (hideOnUnfocus) {
                Actor target = event.getTarget();
                if (isAscendantOf(target)) return false;
                hide();
            }
            return false;
        }
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
    
    public int getAttachEdge() {
        return attachEdge;
    }
    
    public int getAttachAlign() {
        return attachAlign;
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
    
    public void attachToActor() {
        attachToActor(attachToActor, attachEdge, attachAlign);
    }
    
    public void attachToActor(Actor attachToActor, int edge, int align) {
        alignToActorEdge(attachToActor, edge, align);
        this.attachToActor = attachToActor;
        this.attachEdge = edge;
        this.attachAlign = align;
    }
    
    public boolean isModal() {
        return modal;
    }
    
    public void setModal(boolean modal) {
        this.modal = modal;
        stageBackground.setTouchable(modal ? Touchable.enabled : Touchable.disabled);
    }
    
    public boolean isHidden() {
        return hidden;
    }
    
    public PopTableStyle getStyle() {
        return style;
    }
    
    @Override
    public void layout() {
        if (automaticallyResized) {
            float centerX = getX(Align.center);
            float centerY = getY(Align.center);
            pack();
            setPosition(centerX, centerY, Align.center);
            setPosition(MathUtils.floor(getX()), MathUtils.floor(getY()));
        }
        
        if (attachToActor != null) {
            alignToActorEdge(attachToActor, attachEdge, attachAlign);
        }
        
        if (keepSizedWithinStage) {
            resizeWindowWithinStage();
        }
        super.layout();
    }
}