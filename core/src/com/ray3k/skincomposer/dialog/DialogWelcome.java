/*******************************************************************************
 * MIT License
 * 
 * Copyright (c) 2018 Raymond Buckley
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package com.ray3k.skincomposer.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.StageResizeListener;

public class DialogWelcome extends Dialog {
    private final Main main;
    private float resetWidth, resetHeight;
    private ScrollPane scrollPane;

    @Override
    protected void result(Object object) {
        super.result(object);
        if (object instanceof Boolean && ((Boolean) object)) {
            fire(new WelcomeEvent(WelcomeValue.CANCEL));
        }
    }

    public DialogWelcome(Main main) {
        super("", main.getSkin(), "welcome");
        this.main = main;
        
        setMovable(false);
        
        Button button = new Button(getSkin(), "close");
        button.addListener(main.getHandListener());
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                fire(new WelcomeEvent(WelcomeValue.CANCEL));
                hide();
            }
        });
        getContentTable().add(button).expandX().right().pad(0.0f).space(0.0f).padTop(5.0f).padRight(5.0f);
        
        getContentTable().row();
        Table table = new Table();
        table.pad(10.0f).padTop(0.0f);
        table.defaults().space(10.0f);
        scrollPane = new ScrollPane(table, main.getSkin());
        scrollPane.setFadeScrollBars(false);
        scrollPane.setFlickScroll(false);
        scrollPane.setScrollingDisabled(true, false);
        getContentTable().add(scrollPane).grow();
        
        Table subTable = new Table();
        table.add(subTable);
        
        Label label = new Label("Watch a video tutorial:", getSkin(), "black");
        subTable.add(label).space(10.0f);
        
        subTable.row();
        ImageButton imageButton = new ImageButton(getSkin(), "thumb-video");
        imageButton.addListener(main.getHandListener());
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
        imageButton.addListener(main.getHandListener());
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
        imageButton.addListener(main.getHandListener());
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
        imageButton.addListener(main.getHandListener());
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
        imageButton.addListener(main.getHandListener());
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
        imageButton.addListener(main.getHandListener());
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
        
        getContentTable().row();
        final ImageTextButton checkBox = new ImageTextButton("Do not show again", main.getSkin(), "checkbox");
        checkBox.setChecked(!main.getProjectData().isAllowingWelcome());
        checkBox.addListener(main.getHandListener());
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                fire(new DontShowEvent(checkBox.isChecked()));
            }
        });
        getContentTable().add(checkBox).right().padRight(10.0f).expandX();
        
        key(Keys.ESCAPE, true);
        
        main.getRootTable().addListener(new StageResizeListener() {
            @Override
            public void resized(int width, int height) {
                if (!MathUtils.isEqual(getWidth(), width)) {
                    setWidth(Math.min(resetWidth, width));
                }
                
                if (!MathUtils.isEqual(getHeight(), height)) {
                    setHeight(Math.min(resetHeight, height));
                }
                
                setPosition(width / 2.0f, height / 2.0f, Align.center);
                setPosition((int) getX(), (int) getY());
            }
        });
    }

    @Override
    public Dialog show(Stage stage) {
        Dialog dialog = super.show(stage);
        
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
        main.getStage().setScrollFocus(scrollPane);
        fire(new DialogEvent(DialogEvent.Type.OPEN));
        return dialog;
    }

    @Override
    public Dialog show(Stage stage, Action action) {
        Dialog dialog = super.show(stage, action);
        
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
        main.getStage().setScrollFocus(scrollPane);
        fire(new DialogEvent(DialogEvent.Type.OPEN));
        return dialog;
    }

    @Override
    public boolean remove() {
        fire(new DialogEvent(DialogEvent.Type.CLOSE));
        return super.remove();
    }
    
    public static enum WelcomeValue {
        VIDEO, BLANK, VISUI, PLAIN_JAMES, NEON, NEUTRALIZER, CANCEL;
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
                    case CANCEL:
                        cancelled();
                        break;
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
        
        public abstract void cancelled();
        public abstract void videoClicked();
        public abstract void blankClicked();
        public abstract void visUIclicked();
        public abstract void plainJamesClicked();
        public abstract void neonClicked();
        public abstract void neutralizerClicked();
        public abstract void dontShowClicked(boolean value);
    }
}
