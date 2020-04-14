package com.ray3k.skincomposer.stripe;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;

public class DraggableTextList extends DraggableList {
    private DraggableTextListStyle style;
    private TextButtonStyle textButtonStyle;
    private TextButtonStyle dragButtonStyle;
    private TextButtonStyle validButtonStyle;
    private TextButtonStyle invalidButtonStyle;
    private ButtonGroup<TextButton> buttonGroup;
    
    public DraggableTextList(boolean vertical, Skin skin) {
        this(vertical, skin, null);
    }
    
    public DraggableTextList(boolean vertical, Skin skin, String style) {
        this(vertical, skin.get(style, DraggableTextListStyle.class));
    }
    
    public DraggableTextList(boolean vertical, DraggableTextListStyle style) {
        super(vertical, style);
        this.style = style;
        
        textButtonStyle = new TextButtonStyle();
        textButtonStyle.font = style.font;
        textButtonStyle.up = style.textBackgroundUp;
        textButtonStyle.over = style.textBackgroundOver;
        textButtonStyle.down = style.textBackgroundDown;
        textButtonStyle.checked = style.textBackgroundChecked;
        textButtonStyle.checkedOver = style.textBackgroundCheckedOver;
        textButtonStyle.fontColor = style.fontColor;
        textButtonStyle.overFontColor = style.overFontColor;
        textButtonStyle.downFontColor = style.downFontColor;
        textButtonStyle.checkedFontColor = style.checkedFontColor;
        textButtonStyle.checkedOverFontColor = style.checkedOverFontColor;
        
        dragButtonStyle = new TextButtonStyle();
        dragButtonStyle.font = style.font;
        dragButtonStyle.up = style.dragBackgroundUp != null ? style.dragBackgroundUp : style.textBackgroundUp;
        dragButtonStyle.fontColor = style.dragFontColor != null ? style.dragFontColor : style.fontColor;
        
        validButtonStyle = new TextButtonStyle();
        validButtonStyle.font = style.font;
        validButtonStyle.up = style.validBackgroundUp != null ? style.validBackgroundUp : dragButtonStyle.up;
        validButtonStyle.fontColor = style.validFontColor != null ? style.validFontColor : dragButtonStyle.fontColor;
    
        invalidButtonStyle = new TextButtonStyle();
        invalidButtonStyle.font = style.font;
        invalidButtonStyle.up = style.invalidBackgroundUp != null ? style.invalidBackgroundUp : dragButtonStyle.up;
        invalidButtonStyle.fontColor = style.invalidFontColor != null ? style.invalidFontColor : dragButtonStyle.fontColor;
        
        buttonGroup = new ButtonGroup<>();
    }
    
    public void addText(String text) {
        TextButton actor = new TextButton(text, textButtonStyle);
        actor.setProgrammaticChangeEvents(false);
        buttonGroup.add(actor);
        TextButton dragActor = new TextButton(text, dragButtonStyle);
        TextButton validDragActor = new TextButton(text, validButtonStyle);
        TextButton invalidDragActor = new TextButton(text, invalidButtonStyle);
        super.add(actor, dragActor, validDragActor, invalidDragActor);
    }
    
    public void addAllTexts(Array<String> texts) {
        for (String text : texts) {
            addText(text);
        }
    }
    
    public void addAllTexts(String... texts) {
        for (String text : texts) {
            addText(text);
        }
    }
    
    public Array<String> getTexts() {
        var returnValue = new Array<String>();
        for (Actor actor : actors) {
            TextButton textButton = (TextButton) actor;
            returnValue.add(textButton.getText().toString());
        }
        return returnValue;
    }
    
    @Override
    @Deprecated
    public void add(Actor actor) {
        super.add(actor);
    }
    
    @Override
    @Deprecated
    public void add(Actor actor, Actor dragActor, Actor validDragActor, Actor invalidDragActor) {
        super.add(actor, dragActor, validDragActor, invalidDragActor);
    }
    
    @Override
    @Deprecated
    public void addAll(Array<Actor> actors) {
        super.addAll(actors);
    }
    
    @Override
    @Deprecated
    public void addAll(Array<Actor> actors, Array<Actor> dragActors, Array<Actor> validDragActors, Array<Actor> invalidDragActors) {
        super.addAll(actors, dragActors, validDragActors, invalidDragActors);
    }
    
    public CharSequence getSelected() {
        return buttonGroup.getChecked().getText();
    }
    
    public int getSelectedIndex() {
        return buttonGroup.getCheckedIndex();
    }
    
    public void setSelected(String text) {
        buttonGroup.setChecked(text);
    }
    
    public void setSelected(int index) {
        buttonGroup.getButtons().get(index).setChecked(true);
    }
    
    public ButtonGroup getButtonGroup() {
        return buttonGroup;
    }
    
    @Override
    public void clearChildren() {
        super.clearChildren();
        buttonGroup.clear();
    }
    
    @Override
    protected void updateTable() {
        super.updateTable();
        buttonGroup.clear();
        for (var actor : actors) {
            buttonGroup.add((TextButton) actor);
        }
    }
    
    public static class DraggableTextListStyle extends DraggableListStyle {
        public BitmapFont font;
        /** Optional. */
        public Drawable textBackgroundUp, textBackgroundDown, textBackgroundOver, textBackgroundChecked,
                textBackgroundCheckedOver, dragBackgroundUp, validBackgroundUp, invalidBackgroundUp;
        public Color fontColor, downFontColor, overFontColor, checkedFontColor, checkedOverFontColor, dragFontColor,
                validFontColor, invalidFontColor;
    
        public DraggableTextListStyle() {
        }
    
        public DraggableTextListStyle(DraggableTextListStyle style) {
            background = style.background;
            dividerUp = style.dividerUp;
            dividerOver = style.dividerOver;
            font = style.font;
            textBackgroundUp = style.textBackgroundUp;
            textBackgroundDown = style.textBackgroundDown;
            textBackgroundOver = style.textBackgroundOver;
            textBackgroundChecked = style.textBackgroundChecked;
            textBackgroundCheckedOver = style.textBackgroundCheckedOver;
            dragBackgroundUp = style.dragBackgroundUp;
            validBackgroundUp = style.validBackgroundUp;
            invalidBackgroundUp = style.invalidBackgroundUp;
            fontColor = style.fontColor;
            downFontColor = style.downFontColor;
            overFontColor = style.overFontColor;
            checkedFontColor = style.checkedFontColor;
            checkedOverFontColor = style.checkedOverFontColor;
            dragFontColor = style.dragFontColor;
            validFontColor = style.validFontColor;
            invalidFontColor = style.invalidFontColor;
        }
    }
}
