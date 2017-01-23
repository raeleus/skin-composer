package com.ray3k.skincomposer.dialog;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.ray3k.skincomposer.Main;

public class DialogNewClass extends Dialog {
    private boolean customNameEntered;
    private final TextField classField, displayField;
    private final TextButton okButton;
    
    public DialogNewClass(Main main) {
        super("New Custom Class", main.getSkin(), "bg");
        getTitleLabel().setAlignment(Align.center);
        
        customNameEntered = false;
        
        Label label = new Label("What is the fully qualified name?", getSkin());
        getContentTable().add(label).pad(10.0f).padBottom(0.0f);
        getContentTable().row();
        label = new Label("(ex. com.badlogic.gdx.scenes.scene2d.ui.List$ListStyle)", getSkin());
        getContentTable().add(label).pad(10.0f).padTop(0.0f).padBottom(5.0f);
        
        getContentTable().row();
        classField = new TextField("", getSkin()) {
            @Override
            public void next(boolean up) {
                getStage().setKeyboardFocus(displayField);
                displayField.selectAll();
            }
        };
        classField.addListener(main.getIbeamListener());
        getContentTable().add(classField).growX().padLeft(10.0f).padRight(10.0f);
        
        getContentTable().row();
        label = new Label("What is the display name?", getSkin());
        getContentTable().add(label).pad(10.0f).padBottom(0.0f);
        getContentTable().row();
        label = new Label("(ex. List)", getSkin());
        getContentTable().add(label).pad(10.0f).padTop(0.0f).padBottom(5.0f);
        
        getContentTable().row();
        displayField = new TextField("", main.getSkin()) {
            @Override
            public void next(boolean up) {
                getStage().setKeyboardFocus(classField);
                classField.selectAll();
            }
        };
        displayField.addListener(main.getIbeamListener());
        getContentTable().add(displayField).growX().padLeft(10.0f).padRight(10.0f);
        
        getButtonTable().defaults().padBottom(10.0f).minWidth(50.0f);
        button("OK", true).key(Keys.ENTER, true);
        button("Cancel", false).key(Keys.ESCAPE, false);
        
        okButton = (TextButton) getButtonTable().getCells().first().getActor();
        okButton.setDisabled(true);
        
        getButtonTable().getCells().get(1).getActor().addListener(main.getHandListener());
        
        classField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                if (!customNameEntered) {
                    if (!customNameEntered) {
                        String text = classField.getText();
                        text = text.replaceFirst(".*(\\.|\\$)", "");
                        displayField.setText(text);
                    }
                }
                
                updateOkButton();
            }
        });
        
        displayField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                customNameEntered = true;
                updateOkButton();
            }
        });
    }

    @Override
    public Dialog show(Stage stage) {
        Dialog dialog = super.show(stage);
        
        stage.setKeyboardFocus(classField);
        
        return dialog;
    }

    @Override
    protected void result(Object object) {
        super.result(object);
        
        fire(new NewClassEvent((boolean) object, classField.getText(), displayField.getText()));
    }
    
    private void updateOkButton() {
        if (classField.getText().matches("^.*[^\\.]$") && !displayField.getText().equals("")) {
            okButton.setDisabled(false);
        } else {
            okButton.setDisabled(true);
        }
    }
    
    private static class NewClassEvent extends Event {
        boolean result;
        String fullyQualifiedName;
        String displayName;
        
        public NewClassEvent(boolean result, String fullyQualifiedName, String displayName) {
            this.result = result;
            this.fullyQualifiedName = fullyQualifiedName;
            this.displayName = displayName;
        }
    }
    
    public static abstract class NewClassListener implements EventListener {
        @Override
        public boolean handle(Event event) {
            if (event instanceof NewClassEvent) {
                NewClassEvent newClassEvent = (NewClassEvent) event;
                if (newClassEvent.result) {
                    newClassEntered(newClassEvent.fullyQualifiedName, newClassEvent.displayName);
                } else {
                    cancelled();
                }
            }
            return false;
        }
        
        public abstract void newClassEntered(String fullyQualifiedName, String displayName);
        
        public abstract void cancelled();
    }
}