package com.ray3k.skincomposer.stripe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;

public class RangeSlider extends WidgetGroup implements Disableable {
    private float minimum;
    private float maximum;
    private float valueBegin;
    private float valueEnd;
    private float visualValueBegin;
    private float visualValueEnd;
    private float increment;
    private RangeSliderStyle style;
    private Image background;
    private Button knobBegin;
    private Button knobEnd;
    private Image progressKnob;
    private boolean vertical;
    private boolean lockToIntegerPositions;
    private static final Vector2 temp = new Vector2();
    private boolean disabled;
    
    public RangeSlider(Skin skin) {
        this(skin, "default-horizontal");
    }
    
    public RangeSlider(Skin skin, String style) {
        this(skin.get(style, RangeSliderStyle.class));
    }
    
    public RangeSlider(RangeSliderStyle style) {
        minimum = 0;
        maximum = 1;
        valueBegin = 0f;
        visualValueBegin = valueBegin;
        valueEnd = 1;
        visualValueEnd = valueEnd;
        increment = .1f;
        this.style = style;
        vertical = false;
        lockToIntegerPositions = true;
        
        background = new Image(style.background);
        background.setScaling(Scaling.stretch);
        addActor(background);
        
        progressKnob = new Image(style.progressKnob);
        progressKnob.setScaling(Scaling.stretch);
        addActor(progressKnob);
        
        ButtonStyle knobStyle = new ButtonStyle();
        knobStyle.up = style.knobBeginUp;
        knobStyle.over = style.knobBeginOver;
        knobStyle.down = style.knobBeginDown;
        knobStyle.checked = style.knobBeginDown;
        knobStyle.disabled = style.knobBeginDisabled;
        knobBegin = new Button(knobStyle);
        knobBegin.setProgrammaticChangeEvents(false);
        addActor(knobBegin);
        ChangeListener disableCheckingListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ((Button) actor).setChecked(false);
                event.cancel();
            }
        };
        knobBegin.addListener(disableCheckingListener);
        knobBegin.addListener(new DragListener() {
            {
                setTapSquareSize(0);
            }
            
            @Override
            public void dragStart(InputEvent event, float x, float y, int pointer) {
                if (!disabled) {
                    knobBegin.setTouchable(Touchable.disabled);
                    knobBegin.setChecked(true);
                    swapActor(getChildren().indexOf(knobBegin, true), getChildren().size - 1);
                }
            }
    
            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                if (!disabled) {
                    float previousValue = valueBegin;
                    float padLeft = style.background == null ? 0 : style.background.getLeftWidth();
                    float padRight = style.background == null ? 0 : style.background.getRightWidth();
                    float padBottom = style.background == null ? 0 : style.background.getTopHeight();
                    float padTop = style.background == null ? 0 : style.background.getBottomHeight();
                    float innerWidth = MathUtils.clamp(getWidth() - padLeft - padRight, 0, getWidth());
                    float innerHeight = MathUtils.clamp(getHeight() - padBottom - padTop, 0, getHeight());
                    temp.set(Gdx.input.getX(), Gdx.input.getY());
                    background.screenToLocalCoordinates(temp);
    
                    if (!vertical) {
                        valueBegin = MathUtils.clamp((temp.x - padLeft) / innerWidth * (maximum - minimum), minimum, valueEnd);
                    } else {
                        valueBegin = MathUtils.clamp((temp.y - padBottom) / innerHeight * (maximum - minimum), minimum, valueEnd);
                    }
                    valueBegin = snap(valueBegin, increment);
                    visualValueBegin = valueBegin;
                    if (!MathUtils.isEqual(valueBegin, previousValue)) {
                        updateKnobs();
                        fire(new ValueBeginChangeEvent(valueBegin));
                        fire(new ChangeEvent());
                    }
                }
            }
    
            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer) {
                if (!disabled) {
                    knobBegin.setTouchable(Touchable.enabled);
                    knobBegin.setChecked(false);
                    fire(new ValueBeginChangeEvent(valueBegin));
                    fire(new ChangeEvent());
                }
            }
        });
    
        knobStyle = new ButtonStyle();
        knobStyle.up = style.knobEndUp;
        knobStyle.over = style.knobEndOver;
        knobStyle.down = style.knobEndDown;
        knobStyle.checked = style.knobEndDown;
        knobStyle.disabled = style.knobEndDisabled;
        knobEnd = new Button(knobStyle);
        knobEnd.setProgrammaticChangeEvents(false);
        addActor(knobEnd);
        knobEnd.addListener(disableCheckingListener);
        knobEnd.addListener(new DragListener() {
            {
                setTapSquareSize(0);
            }
    
            @Override
            public void dragStart(InputEvent event, float x, float y, int pointer) {
                if (!disabled) {
                    knobEnd.setTouchable(Touchable.disabled);
                    knobEnd.setChecked(true);
                    swapActor(getChildren().indexOf(knobEnd, true), getChildren().size - 1);
                }
            }
    
            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                if (!disabled) {
                    float previousValue = valueEnd;
                    float padLeft = style.background == null ? 0 : style.background.getLeftWidth();
                    float padRight = style.background == null ? 0 : style.background.getRightWidth();
                    float padBottom = style.background == null ? 0 : style.background.getTopHeight();
                    float padTop = style.background == null ? 0 : style.background.getBottomHeight();
                    float innerWidth = MathUtils.clamp(getWidth() - padLeft - padRight, 0, getWidth());
                    float innerHeight = MathUtils.clamp(getHeight() - padBottom - padTop, 0, getHeight());
                    temp.set(Gdx.input.getX(), Gdx.input.getY());
                    background.screenToLocalCoordinates(temp);
    
                    if (!vertical) {
                        valueEnd = MathUtils.clamp((temp.x - padLeft) / innerWidth * (maximum - minimum), valueBegin, maximum);
                    } else {
                        valueEnd = MathUtils.clamp((temp.y - padBottom) / innerHeight * (maximum - minimum), valueBegin, maximum);
                    }
                    valueEnd = snap(valueEnd, increment);
                    visualValueEnd = valueEnd;
                    if (!MathUtils.isEqual(valueEnd, previousValue)) {
                        updateKnobs();
                        fire(new ValueEndChangeEvent(valueEnd));
                        fire(new ChangeEvent());
                    }
                }
            }
    
            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer) {
                if (!disabled) {
                    knobEnd.setTouchable(Touchable.enabled);
                    knobEnd.setChecked(false);
                    fire(new ValueEndChangeEvent(valueEnd));
                    fire(new ChangeEvent());
                }
            }
        });
    }
    
    @Override
    public void layout() {
        background.setSize(getWidth(), getHeight());
        
        updateKnobs();
    }
    
    private void updateKnobs() {
        float padLeft = style.background == null ? 0 : style.background.getLeftWidth();
        float padRight = style.background == null ? 0 : style.background.getRightWidth();
        float padBottom = style.background == null ? 0 : style.background.getBottomHeight();
        float padTop = style.background == null ? 0 : style.background.getTopHeight();
        float innerWidth = MathUtils.clamp(getWidth() - padLeft - padRight, 0, getWidth());
        float innerHeight = MathUtils.clamp(getHeight() - padBottom - padTop, 0, getHeight());
    
        if (!vertical) {
            progressKnob.setX(padLeft + visualValueBegin / (maximum - minimum) * innerWidth);
            progressKnob.setY(padBottom + MathUtils.round(innerHeight / 2f - progressKnob.getPrefHeight() / 2f));
            progressKnob.setWidth(padLeft + (visualValueEnd - visualValueBegin) / (maximum - minimum) * innerWidth);
            
            knobBegin.setX(padLeft + visualValueBegin / (maximum - minimum) * innerWidth - knobBegin.getPrefWidth() / 2f);
            knobBegin.setY(padBottom + innerHeight / 2f - knobBegin.getPrefHeight() / 2f);
            if (lockToIntegerPositions) knobBegin.setPosition(MathUtils.round(knobBegin.getX()),
                    MathUtils.round(knobBegin.getY()));
        
            knobEnd.setX(padLeft + visualValueEnd / (maximum - minimum) * innerWidth - knobEnd.getPrefWidth() / 2f);
            knobEnd.setY(padBottom + innerHeight / 2f - knobEnd.getPrefHeight() / 2f);
            if (lockToIntegerPositions) knobEnd.setPosition(MathUtils.round(knobEnd.getX()),
                    MathUtils.round(knobEnd.getY()));
        } else {
            progressKnob.setX(padLeft + innerWidth / 2f - progressKnob.getPrefWidth() / 2f);
            progressKnob.setY(padBottom + visualValueBegin / (maximum - minimum) * innerHeight - knobBegin.getPrefHeight() / 2f);
            progressKnob.setHeight(padBottom + (visualValueEnd - visualValueBegin) * innerHeight);
            
            knobBegin.setX(padLeft + innerWidth / 2f - knobBegin.getPrefWidth() / 2f);
            knobBegin.setY(padBottom + visualValueBegin / (maximum - minimum) * innerHeight - knobBegin.getPrefHeight() / 2f);
            if (lockToIntegerPositions) knobBegin.setPosition(MathUtils.round(knobBegin.getX()),
                    MathUtils.round(knobBegin.getY()));
        
            knobEnd.setX(padLeft + innerWidth / 2f - knobEnd.getPrefWidth() / 2f);
            knobEnd.setY(padBottom + visualValueEnd / (maximum - minimum) * innerHeight - knobEnd.getPrefHeight() / 2f);
            if (lockToIntegerPositions) knobEnd.setPosition(MathUtils.round(knobEnd.getX()),
                    MathUtils.round(knobEnd.getY()));
        }
        
        if (MathUtils.isEqual(valueBegin, valueEnd) && MathUtils.isEqual(valueEnd, maximum)) {
            swapActor(getChildren().indexOf(knobBegin, true), getChildren().size - 1);
        }
        
        if (disabled) {
            background.setDrawable(style.backgroundDisabled);
            knobBegin.setDisabled(true);
            knobEnd.setDisabled(true);
            progressKnob.setDrawable(style.progressKnobDisabled);
        } else {
            background.setDrawable(style.background);
            knobBegin.setDisabled(false);
            knobEnd.setDisabled(false);
            progressKnob.setDrawable(style.progressKnob);
        }
    }
    
    @Override
    public float getPrefWidth() {
        return style.background == null ? 0 : style.background.getMinWidth();
    }
    
    @Override
    public float getPrefHeight() {
        return style.background == null ? 0 : style.background.getMinHeight();
    }
    
    public float getMinimum() {
        return minimum;
    }
    
    public void setMinimum(float minimum) {
        this.minimum = minimum;
    }
    
    public float getMaximum() {
        return maximum;
    }
    
    public void setMaximum(float maximum) {
        this.maximum = maximum;
    }
    
    public float getValueBegin() {
        return valueBegin;
    }
    
    public void setValueBegin(float valueBegin) {
        this.valueBegin = valueBegin;
        this.visualValueBegin = snap(valueBegin, increment);
        updateKnobs();
    }
    
    public float getValueEnd() {
        return valueEnd;
    }
    
    public void setValueEnd(float valueEnd) {
        this.valueEnd = valueEnd;
        this.visualValueEnd = snap(valueEnd, increment);
        updateKnobs();
    }
    
    public float getVisualValueBegin() {
        return visualValueBegin;
    }
    
    public float getVisualValueEnd() {
        return visualValueEnd;
    }
    
    public float getIncrement() {
        return increment;
    }
    
    public void setIncrement(float increment) {
        this.increment = increment;
    }
    
    public Button getKnobBegin() {
        return knobBegin;
    }
    
    public Button getKnobEnd() {
        return knobEnd;
    }
    
    private static float snap(float value, float increment) {
        int whole = MathUtils.floor(value / increment);
        float first = whole * increment;
        float second = first + increment;
        return value - first < second - value ? first : second;
    }
    
    @Override
    public void setDisabled(boolean isDisabled) {
        disabled = isDisabled;
        updateKnobs();
    }
    
    @Override
    public boolean isDisabled() {
        return disabled;
    }
    
    public static class ValueBeginChangeEvent extends Event {
        public float value;
        public ValueBeginChangeEvent(float value) {
            this.value = value;
        }
    }
    
    public static class ValueEndChangeEvent extends Event {
        public float value;
        public ValueEndChangeEvent(float value) {
            this.value = value;
        }
    }
    
    public static abstract class ValueBeginChangeListener implements EventListener {
        public boolean handle (Event event) {
            if (!(event instanceof ValueBeginChangeEvent)) return false;
            changed((ValueBeginChangeEvent) event, ((ValueBeginChangeEvent) event).value, event.getTarget());
            return false;
        }
    
        /** @param actor The event target, which is the actor that emitted the change event. */
        abstract public void changed (ValueBeginChangeEvent event, float valueBegin, Actor actor);
    }
    
    public static abstract class ValueEndChangeListener implements EventListener {
        public boolean handle (Event event) {
            if (!(event instanceof ValueEndChangeEvent)) return false;
            changed((ValueEndChangeEvent)event, ((ValueEndChangeEvent) event).value, event.getTarget());
            return false;
        }
        
        /** @param actor The event target, which is the actor that emitted the change event. */
        abstract public void changed (ValueEndChangeEvent event, float valueEnd, Actor actor);
    }
    
    public static class RangeSliderStyle {
        /**Optional**/
        public Drawable background;
        public Drawable progressKnob;
        public Drawable knobBeginUp;
        public Drawable knobBeginOver;
        public Drawable knobBeginDown;
        public Drawable knobEndUp;
        public Drawable knobEndOver;
        public Drawable knobEndDown;
        public Drawable backgroundDisabled;
        public Drawable progressKnobDisabled;
        public Drawable knobBeginDisabled;
        public Drawable knobEndDisabled;
    }
}
