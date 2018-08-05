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
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.StageResizeListener;
import com.ray3k.skincomposer.utils.Utils;
import java.io.IOException;

public class DialogWarnings extends Dialog {
    private final Main main;
    private float resetWidth, resetHeight;
    private final ScrollPane scrollPane;
    private Array<String> warnings;

    @Override
    protected void result(Object object) {
        super.result(object);
        if (object instanceof Boolean) {
            if ((Boolean) object) {
                FileHandle file = Main.appFolder.child("temp/warnings.txt");
                Utils.writeWarningsToFile(warnings, file);
                try {
                    Utils.openFileExplorer(file);
                } catch (IOException e) {}
            }
        }
    }

    public DialogWarnings(Main main, Array<String> warnings) {
        super("", main.getSkin(), "welcome");
        this.main = main;
        this.warnings = warnings;
        
        setMovable(false);
        
        Button button = new Button(getSkin(), "close");
        button.addListener(main.getHandListener());
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                hide();
            }
        });
        getContentTable().add(button).expandX().right().pad(0.0f).space(0.0f).padTop(5.0f).padRight(5.0f);
        
        getContentTable().row();
        Label label = new Label("Warnings:", getSkin(), "black-underline");
        getContentTable().add(label);
        
        getContentTable().row();
        Table table = new Table();
        table.pad(10.0f).padTop(0.0f);
        table.defaults().space(10.0f);
        scrollPane = new ScrollPane(table, main.getSkin());
        scrollPane.setFadeScrollBars(false);
        scrollPane.setFlickScroll(false);
        scrollPane.setScrollingDisabled(true, false);
        getContentTable().add(scrollPane).grow();
        
        for (String warning : warnings) {
            table.row();
            label = new Label(warning, getSkin());
            table.add(label);
        }
        
        getButtonTable().defaults().minWidth(100.0f).pad(10.0f);
        button("OK", false).key(Keys.ESCAPE, false).key(Keys.ENTER, false);
        button("Export to Text File", true);
        getButtonTable().getCells().first().getActor().addListener(main.getHandListener());
        
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
        return dialog;
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
