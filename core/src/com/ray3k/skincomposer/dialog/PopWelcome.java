package com.ray3k.skincomposer.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.ray3k.stripe.PopTable;

import static com.ray3k.skincomposer.Main.*;

public class PopWelcome extends PopTable {
    private float resetWidth, resetHeight;
    private ScrollPane scrollPane;
    
    public PopWelcome() {
        super(skin, "dialog");
    
        setKeepCenteredInWindow(true);
        setModal(true);
        setHideOnUnfocus(true);
        
        Button button = new Button(getSkin(), "close");
        button.addListener(handListener);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                hide();
            }
        });
        add(button).expandX().right().pad(0.0f).space(0.0f).padTop(5.0f).padRight(5.0f);
        
        row();
        Table table = new Table();
        table.pad(10.0f).padTop(0.0f);
        table.defaults().space(10.0f);
        scrollPane = new ScrollPane(table, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setFlickScroll(false);
        scrollPane.setScrollingDisabled(true, false);
        add(scrollPane).grow();
        
        Table subTable = new Table();
        table.add(subTable);
        
        Label label = new Label("Watch a video tutorial:", getSkin(), "black");
        subTable.add(label).space(10.0f);
        
        subTable.row();
        ImageButton imageButton = new ImageButton(getSkin(), "thumb-video");
        imageButton.addListener(handListener);
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                fire(new WelcomeEvent(WelcomeValue.VIDEO));
                hide();
            }
        });
        subTable.add(imageButton).size(160.0f, 95.0f);
        
        table.row();
        Table separator = new Table(getSkin());
        separator.setBackground("welcome-separator");
        table.add(separator).size(240.0f, 2.0f).padTop(15.0f);
        
        table.row();
        label = new Label("Create New Project:", getSkin(), "black");
        table.add(label);
        
        table.row();
        Table organizerTable = new Table();
        organizerTable.defaults().space(10.0f);
        table.add(organizerTable);
        
        subTable = new Table();
        organizerTable.add(subTable);
        
        imageButton = new ImageButton(getSkin(), "thumb-scene2d");
        imageButton.addListener(handListener);
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                fire(new WelcomeEvent(WelcomeValue.BLANK));
                hide();
            }
        });
        subTable.add(imageButton).size(125.0f, 140.0f);
        
        subTable.row();
        label = new Label("Empty Project", getSkin(), "black");
        subTable.add(label);
        
        subTable = new Table();
        organizerTable.add(subTable);
        
        imageButton = new ImageButton(getSkin(), "thumb-vis-ui");
        imageButton.addListener(handListener);
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                fire(new WelcomeEvent(WelcomeValue.VISUI));
                hide();
            }
        });
        subTable.add(imageButton).size(125.0f, 140.0f);
        
        subTable.row();
        label = new Label("VisUI Template", getSkin(), "black");
        subTable.add(label);
        
        table.row();
        separator = new Table(getSkin());
        separator.setBackground("welcome-separator");
        table.add(separator).size(240.0f, 2.0f).padTop(15.0f);
        
        table.row();
        label = new Label("Open Sample Project:", getSkin(), "black");
        table.add(label);
        
        table.row();
        organizerTable = new Table();
        organizerTable.defaults().space(10.0f);
        table.add(organizerTable);
        
        subTable = new Table();
        organizerTable.add(subTable);
        
        imageButton = new ImageButton(getSkin(), "thumb-plain-james");
        imageButton.addListener(handListener);
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                fire(new WelcomeEvent(WelcomeValue.PLAIN_JAMES));
                hide();
            }
        });
        subTable.add(imageButton).size(125.0f, 140.0f);
        
        subTable.row();
        label = new Label("Plain James UI\nTutorial Project", getSkin(), "black");
        label.setAlignment(Align.center);
        subTable.add(label);
        
        subTable = new Table();
        organizerTable.add(subTable);
        
        imageButton = new ImageButton(getSkin(), "thumb-neon");
        imageButton.addListener(handListener);
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                fire(new WelcomeEvent(WelcomeValue.NEON));
                hide();
            }
        });
        subTable.add(imageButton).size(125.0f, 140.0f);
        
        subTable.row();
        label = new Label("Neon UI\nScene2D.UI Skin", getSkin(), "black");
        label.setAlignment(Align.center);
        subTable.add(label);
        
        subTable = new Table();
        organizerTable.add(subTable);
        
        imageButton = new ImageButton(getSkin(), "thumb-neutralizer");
        imageButton.addListener(handListener);
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                fire(new WelcomeEvent(WelcomeValue.NEUTRALIZER));
                hide();
            }
        });
        subTable.add(imageButton).size(125.0f, 140.0f);
        
        subTable.row();
        label = new Label("Neutralizer UI\nVisUI Skin", getSkin(), "black");
        label.setAlignment(Align.center);
        subTable.add(label);
        
        row();
        final ImageTextButton checkBox = new ImageTextButton("Do not show again", skin, "checkbox");
        checkBox.setChecked(!projectData.isAllowingWelcome());
        checkBox.addListener(handListener);
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                fire(new DontShowEvent(checkBox.isChecked()));
            }
        });
        add(checkBox).right().padRight(10.0f).expandX();
    
        key(Keys.ESCAPE,() -> {
            hide();
        });
    }
    
    @Override
    public void show(Stage stage) {
        super.show(stage);
        
        if (getWidth() > Gdx.graphics.getWidth()) {
            setWidth(Gdx.graphics.getWidth());
            setPosition(getWidth() / 2.0f, getHeight() / 2.0f, Align.center);
            setPosition((int) getX(), (int) getY());
        }
        
        if (getHeight() > Gdx.graphics.getHeight()) {
            setHeight(Gdx.graphics.getHeight());
            setPosition(getWidth() / 2.0f, getHeight() / 2.0f, Align.center);
            setPosition((int) getX(), (int) getY());
        }
        
        resetWidth = getWidth();
        resetHeight = getHeight();
        stage.setScrollFocus(scrollPane);
        fire(new DialogEvent(DialogEvent.Type.OPEN));
    }
    
    @Override
    public void show(Stage stage, Action action) {
        super.show(stage, action);
        
        if (getWidth() > Gdx.graphics.getWidth()) {
            setWidth(Gdx.graphics.getWidth());
            setPosition(getWidth() / 2.0f, getHeight() / 2.0f, Align.center);
            setPosition((int) getX(), (int) getY());
        }
        
        if (getHeight() > Gdx.graphics.getHeight()) {
            setHeight(Gdx.graphics.getHeight());
            setPosition(getWidth() / 2.0f, getHeight() / 2.0f, Align.center);
            setPosition((int) getX(), (int) getY());
        }
        
        resetWidth = getWidth();
        resetHeight = getHeight();
        stage.setScrollFocus(scrollPane);
        fire(new DialogEvent(DialogEvent.Type.OPEN));
    }
    
    @Override
    public boolean remove() {
        fire(new DialogEvent(DialogEvent.Type.CLOSE));
        return super.remove();
    }
    
    public static enum WelcomeValue {
        VIDEO, BLANK, VISUI, PLAIN_JAMES, NEON, NEUTRALIZER;
    }
    
    private static class WelcomeEvent extends Event {
        private WelcomeValue value;
        
        public WelcomeEvent(WelcomeValue value) {
            this.value = value;
        }
    }
    
    private static class DontShowEvent extends Event {
        private boolean value;
        
        public DontShowEvent(boolean value) {
            this.value = value;
        }
    }
    
    public abstract static class WelcomeListener implements EventListener {
        @Override
        public boolean handle(Event event) {
            
            if (event instanceof DontShowEvent) {
                dontShowClicked(((DontShowEvent) event).value);
            } else if (event instanceof WelcomeEvent) {
                WelcomeValue value = ((WelcomeEvent) event).value;
                
                switch (value) {
                    case VIDEO:
                        videoClicked();
                        break;
                    case BLANK:
                        blankClicked();
                        break;
                    case VISUI:
                        visUIclicked();
                        break;
                    case PLAIN_JAMES:
                        plainJamesClicked();
                        break;
                    case NEON:
                        neonClicked();
                        break;
                    case NEUTRALIZER:
                        neutralizerClicked();
                        break;
                }
            }
            return false;
        }
        
        public abstract void videoClicked();
        public abstract void blankClicked();
        public abstract void visUIclicked();
        public abstract void plainJamesClicked();
        public abstract void neonClicked();
        public abstract void neutralizerClicked();
        public abstract void dontShowClicked(boolean value);
    }
}
