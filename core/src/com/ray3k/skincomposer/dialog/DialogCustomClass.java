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

public class DialogCustomClass extends Dialog {
    private boolean customNameEntered;
    private final TextField classField, displayField;
    private final TextButton okButton;
    private Main main;
    
    public DialogCustomClass(Main main) {
        this(main, false, null, null);
    }
    
    public DialogCustomClass(Main main, boolean renameMode, String fullyQualifiedName, String displayName) {
        super(!renameMode ? "New Custom Class" : "Rename Custom Class", main.getSkin(), "bg");
        getTitleLabel().setAlignment(Align.center);
        
        this.main = main;
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
        classField.setText(fullyQualifiedName);
        classField.selectAll();
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
        displayField.setText(displayName);
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
        
        fire(new CustomClassEvent((boolean) object, classField.getText(), displayField.getText()));
    }
    
    private void updateOkButton() {
        if (classField.getText().matches("^.*[^\\.]$") && !displayField.getText().equals("")) {
            okButton.setDisabled(false);
            if (!okButton.getListeners().contains(main.getHandListener(), true)) {
                okButton.addListener(main.getHandListener());
            }
        } else {
            okButton.setDisabled(true);
            if (okButton.getListeners().contains(main.getHandListener(), true)) {
                okButton.removeListener(main.getHandListener());
            }
        }
    }
    
    private static class CustomClassEvent extends Event {
        boolean result;
        String fullyQualifiedName;
        String displayName;
        
        public CustomClassEvent(boolean result, String fullyQualifiedName, String displayName) {
            this.result = result;
            this.fullyQualifiedName = fullyQualifiedName;
            this.displayName = displayName;
        }
    }
    
    public static abstract class CustomClassListener implements EventListener {
        @Override
        public boolean handle(Event event) {
            if (event instanceof CustomClassEvent) {
                CustomClassEvent newClassEvent = (CustomClassEvent) event;
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