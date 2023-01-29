package com.ray3k.skincomposer.dialog.scenecomposer.menulisteners;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerEvents;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimActor;
import com.ray3k.skincomposer.dialog.scenecomposer.StyleSelectorPopTable;
import com.ray3k.stripe.DraggableTextList;
import com.ray3k.stripe.PopTableClickListener;

import static com.ray3k.skincomposer.Main.*;
import static com.ray3k.skincomposer.dialog.scenecomposer.menulisteners.ListenersUtils.TEXT_FIELD_WIDTH;

public class ListListeners {
    public static EventListener listNameListener(final DialogSceneComposer dialogSceneComposer) {
        var simList = (DialogSceneComposerModel.SimList) dialogSceneComposer.simActor;
        var textField = new TextField("", skin, "scene");
        var popTableClickListener = new PopTableClickListener(skin) {
            {
                getPopTable().key(Keys.ESCAPE, popTable::hide);
            }
            
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                dialogSceneComposer.getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
                
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("Name:", skin, "scene-label-colored");
                popTable.add(label);
                
                popTable.row();
                textField.setText(simList.name);
                popTable.add(textField).minWidth(TEXT_FIELD_WIDTH);
                textField.addListener(ibeamListener);
                textField.addListener((Main.makeTooltip("The name of the List to allow for convenient searching via Group#findActor().", tooltipManager, skin, "scene")));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        dialogSceneComposer.events.listName(textField.getText());
                    }
                });
                textField.addListener(new InputListener() {
                    @Override
                    public boolean keyDown(InputEvent event, int keycode) {
                        if (keycode == Input.Keys.ENTER || keycode == Keys.NUMPAD_ENTER) {
                            popTable.hide();
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
                
                dialogSceneComposer.getStage().setKeyboardFocus(textField);
                textField.setSelection(0, textField.getText().length());
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static EventListener listStyleListener(final DialogSceneComposerEvents events, SimActor simActor) {
        var simList = (DialogSceneComposerModel.SimList) simActor;
        var popTableClickListener = new StyleSelectorPopTable(List.class, simList.style == null ? "default" : simList.style.name) {
            @Override
            public void accepted(StyleData styleData) {
                events.listStyle(styleData);
            }
        };
        
        return popTableClickListener;
    }
    
    public static EventListener listTextListListener(final DialogSceneComposer dialogSceneComposer) {
        var simList = (DialogSceneComposerModel.SimList) dialogSceneComposer.simActor;
        var textField = new TextField("", skin, "scene");
        textField.setFocusTraversal(false);
        var draggableTextList = new DraggableTextList(true, skin, "scene-unchecked");
        draggableTextList.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dialogSceneComposer.events.listList(draggableTextList.getTexts());
            }
        });
        var scrollPane = new ScrollPane(draggableTextList, skin, "scene");
        scrollPane.setFadeScrollBars(false);
        scrollPane.addListener(scrollFocusListener);
        var popTableClickListener = new PopTableClickListener(skin) {
            {
                popTable.key(Keys.ESCAPE, popTable::hide);
                popTable.setAutomaticallyResized(true);
                
                textField.addListener(ibeamListener);
                textField.addListener((Main.makeTooltip("The text to add to the list.", tooltipManager, skin, "scene")));
                textField.addListener(new InputListener() {
                    @Override
                    public boolean keyDown(InputEvent event, int keycode) {
                        if (keycode == Input.Keys.ENTER || keycode == Keys.NUMPAD_ENTER) {
                            addItem(textField.getText());
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
            }
            
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var label = new Label("List Entries:\n(Drag to change order)", skin, "scene-label-colored");
                label.setAlignment(Align.center);
                popTable.add(label).colspan(2);
                
                popTable.row();
                draggableTextList.clearChildren();
                draggableTextList.addAllTexts(simList.list);
                popTable.add(scrollPane).colspan(2).minHeight(150).growX();
    
                popTable.row();
                label = new Label("Add New Item:", skin, "scene-label-colored");
                popTable.add(label).colspan(2).padTop(10);
                
                popTable.row();
                textField.setText("");
                popTable.add(textField).minWidth(TEXT_FIELD_WIDTH);
                
                var textButton = new Button(skin, "scene-plus");
                popTable.add(textButton);
                textButton.addListener(handListener);
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        addItem(textField.getText());
                    }
                });
                
                dialogSceneComposer.getStage().setKeyboardFocus(textField);
                dialogSceneComposer.getStage().setScrollFocus(scrollPane);
            }
            
            public void addItem(String item) {
                draggableTextList.addText(item);
                dialogSceneComposer.events.listList(draggableTextList.getTexts());
                update();
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
}
