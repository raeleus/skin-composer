package com.ray3k.skincomposer;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class DraggableList extends WidgetGroup {
    private DraggableListStyle style;
    private Table table;
    protected Array<Actor> actors;
    private ObjectMap<Actor, Actor> dragActors;
    private ObjectMap<Actor, Actor> validDragActors;
    private ObjectMap<Actor, Actor> invalidDragActors;
    private DragAndDrop dragAndDrop;
    private ButtonStyle dividerStyle;
    private Array<Button> dividers;
    private boolean vertical;
    private boolean draggable;
    
    public DraggableList(boolean vertical, Skin skin) {
        this(vertical, skin, vertical ? "default-vertical" : "default-horizontal");
    }
    
    public DraggableList(boolean vertical, Skin skin, String style) {
        this(vertical, skin.get(style, DraggableListStyle.class));
    }
    
    public DraggableList(boolean vertical, DraggableListStyle style) {
        draggable = true;
        this.vertical = vertical;
        this.style = style;
        
        dragAndDrop = new DragAndDrop();
        
        dividerStyle = new ButtonStyle();
        dividerStyle.up = style.dividerUp;
        dividerStyle.over = style.dividerOver;
        dividerStyle.checked = style.dividerOver;
        
        dividers = new Array<>();
        dragActors = new ObjectMap<>();
        validDragActors = new ObjectMap<>();
        invalidDragActors = new ObjectMap<>();
        
        table = new Table();
        table.setBackground(style.background);
        addActor(table);
        
        actors = new Array<>();
    }
    
    public void add(Actor actor) {
        add(actor, null, null, null);
    }
    
    public void add(Actor actor, Actor dragActor, Actor validDragActor, Actor invalidDragActor) {
        actors.add(actor);
        dragActors.put(actor, dragActor);
        validDragActors.put(actor, validDragActor);
        invalidDragActors.put(actor, invalidDragActor);
        updateTable();
    }
    
    public void addAll(Array<Actor> actors) {
        addAll(actors, null, null, null);
    }
    
    public void addAll(Array<Actor> actors, Array<Actor> dragActors, Array<Actor> validDragActors, Array<Actor> invalidDragActors) {
        for (int i = 0; i < actors.size; i++) {
            Actor actor = actors.get(i);
            Actor dragActor = null;
            Actor validDragActor = null;
            Actor invalidDragActor = null;
            
            if (dragActors != null && i < dragActors.size) {
                dragActor = dragActors.get(i);
            }
            
            if (validDragActors != null && i < validDragActors.size) {
                validDragActor = validDragActors.get(i);
            }
            
            if (invalidDragActors != null && i < invalidDragActors.size) {
                invalidDragActor = invalidDragActors.get(i);
            }
            
            add(actor, dragActor, validDragActor, invalidDragActor);
        }
    }
    
    @Override
    public void clearChildren() {
        actors.clear();
        dragActors.clear();
        validDragActors.clear();
        invalidDragActors.clear();
        updateTable();
    }
    
    private void updateTable() {
        dragAndDrop.clear();
        for (Button divider : dividers) {
            removeActor(divider);
        }
        dividers.clear();
        
        table.clearChildren();
        for (Actor actor : actors) {
            if (vertical) table.row();
            table.add(actor);
            if (draggable) dragAndDrop.addSource(new Source(actor) {
                @Override
                public Payload dragStart(InputEvent event, float x, float y, int pointer) {
                    actor.setVisible(false);
                    
                    for (Button divider : dividers) {
                        divider.setVisible(true);
                        divider.setChecked(false);
                    }
                    
                    int index = actors.indexOf(getActor(), true);
                    if (index == 0) dividers.get(0).setVisible(false);
                    if (index == actors.size - 1) dividers.peek().setVisible(false);
                    
                    Payload payload = new Payload();
                    payload.setDragActor(dragActors.get(actor));
                    payload.setValidDragActor(validDragActors.get(actor));
                    payload.setInvalidDragActor(invalidDragActors.get(actor));
                    payload.setObject(actor);
                    return payload;
                }
    
                @Override
                public void drag(InputEvent event, float x, float y, int pointer) {
                    for (Button divider : dividers) {
                        divider.setChecked(false);
                    }
                }
    
                @Override
                public void dragStop(InputEvent event, float x, float y, int pointer, Payload payload, Target target) {
                    actor.setVisible(true);
    
                    for (Button divider : dividers) {
                        divider.setVisible(false);
                    }
                    
                    if (target == null) {
                        Actor payloadActor = (Actor) payload.getObject();
                        actors.removeValue(payloadActor, true);
                        updateTable();
                        fire(new ChangeEvent());
                    }
                }
            });
            dragAndDrop.addTarget(new Target(actor) {
                @Override
                public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
                    int index = actors.indexOf(getActor(), true);
                    if (vertical) {
                        if (y < getActor().getHeight() / 2) index++;
                    } else {
                        if (x > getActor().getWidth() / 2) index++;
                    }
                    for (Button divider : dividers) {
                        divider.setChecked(false);
                    }
                    dividers.get(index).setChecked(true);
                    return true;
                }
    
                @Override
                public void drop(Source source, Payload payload, float x, float y, int pointer) {
                    Actor payloadActor = (Actor) payload.getObject();
                    actors.removeValue(payloadActor, true);
                    int newIndex = actors.indexOf(getActor(), true);
                    if (vertical) {
                        if (y < getActor().getHeight() / 2) newIndex++;
                    } else {
                        if (x > getActor().getWidth() / 2) newIndex++;
                    }
                    actors.insert(Math.min(newIndex, actors.size), payloadActor);
                    updateTable();
                    fire(new ChangeEvent());
                }
            });
        }
        if (vertical) table.row();
        
        for (int i = 0; i < actors.size + 1; i++) {
            Button button = new Button(dividerStyle);
            button.setProgrammaticChangeEvents(false);
            button.setVisible(false);
            addActor(button);
            dividers.add(button);
            int index = i;
            dragAndDrop.addTarget(new Target(button) {
                @Override
                public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
                    return true;
                }
    
                @Override
                public void drop(Source source, Payload payload, float x, float y, int pointer) {
                    int newIndex = index;
                    Actor payloadActor = (Actor) payload.getObject();
                    int currentIndex = actors.indexOf(payloadActor, true);
                    if (currentIndex < newIndex) newIndex--;
                    actors.removeValue(payloadActor, true);
                    actors.insert(Math.min(newIndex, actors.size), payloadActor);
                    updateTable();
                }
            });
        }
    }
    
    
    
    @Override
    public float getMinWidth() {
        return table.getMinWidth();
    }
    
    @Override
    public float getMinHeight() {
        return table.getMinHeight();
    }
    
    @Override
    public float getPrefWidth() {
        return table.getPrefWidth();
    }
    
    @Override
    public float getPrefHeight() {
        return table.getPrefHeight();
    }
    
    @Override
    public float getMaxWidth() {
        return table.getMaxWidth();
    }
    
    @Override
    public float getMaxHeight() {
        return table.getMaxHeight();
    }
    
    @Override
    public void layout() {
        table.setSize(getWidth(), getHeight());
        table.layout();
        
        if (actors.size > 0) for (int i = 0; i < dividers.size; i++) {
            Button button = dividers.get(i);
            Actor actor;
            if (vertical) {
                if (i < actors.size) {
                    actor = actors.get(i);
                    button.setPosition(actor.getX(), actor.getY() + actor.getHeight() - button.getHeight() / 2.0f);
                } else {
                    actor = actors.get(actors.size - 1);
                    button.setPosition(actor.getX(), actor.getY() - button.getHeight() / 2.0f);
                }
                button.setWidth(actor.getWidth());
            } else {
                if (i < actors.size) {
                    actor = actors.get(i);
                    button.setPosition(actor.getX() - button.getWidth() / 2.0f, actor.getY());
                } else {
                    actor = actors.get(actors.size - 1);
                    button.setPosition(actor.getX() + actor.getWidth() - button.getWidth() / 2.0f, actor.getY());
                }
                button.setHeight(actor.getHeight());
            }
            button.layout();
        }
    }
    
    @Override
    @Deprecated
    public void addActor(Actor actor) {
        super.addActor(actor);
    }
    
    @Override
    @Deprecated
    public void addActorAt(int index, Actor actor) {
        super.addActorAt(index, actor);
    }
    
    @Override
    @Deprecated
    public void addActorBefore(Actor actorBefore, Actor actor) {
        super.addActorBefore(actorBefore, actor);
    }
    
    @Override
    @Deprecated
    public void addActorAfter(Actor actorAfter, Actor actor) {
        super.addActorAfter(actorAfter, actor);
    }
    
    @Override
    public boolean removeActor(Actor actor, boolean unfocus) {
        actors.removeValue(actor, true);
        return super.removeActor(actor, unfocus);
    }
    
    public Table getTable() {
        return table;
    }
    
    public boolean isDraggable() {
        return draggable;
    }
    
    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
        updateTable();
    }
    
    public DraggableListStyle getStyle() {
        return style;
    }
    
    public static class DraggableListStyle {
        public Drawable dividerUp, dividerOver;
        /** Optional **/
        public Drawable background;
        
        public DraggableListStyle() {
        }
        
        public DraggableListStyle(DraggableListStyle style) {
            background = style.background;
            dividerUp = style.dividerUp;
            dividerOver = style.dividerOver;
        }
    }
}
