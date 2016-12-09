/*******************************************************************************
 * MIT License
 * 
 * Copyright (c) 2016 Raymond Buckley
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
package com.ray3k.skincomposer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.ray3k.skincomposer.MenuButton.MenuButtonListener;
import com.ray3k.skincomposer.MenuButton.MenuButtonStyle;
import com.ray3k.skincomposer.MenuList.MenuListStyle;
import com.ray3k.skincomposer.utils.Utils;

public class RootTable extends Table {
    private Stage stage;
    private boolean draggingCursor;
    private DesktopWorker desktopWorker;
    private SelectBox classSelectBox;
    private SelectBox styleSelectBox;
    
    public RootTable(Stage stage, Skin skin, DesktopWorker desktopWorker) {
        super(skin);
        this.stage = stage;
        this.desktopWorker = desktopWorker;
        
        TextButtonStyle textButtonStyle = skin.get("file", TextButtonStyle.class);
        
        MenuButtonStyle menuButtonStyle = new MenuButton.MenuButtonStyle();
        menuButtonStyle.font = textButtonStyle.font;
        menuButtonStyle.up = textButtonStyle.up;
        menuButtonStyle.down = textButtonStyle.down;
        menuButtonStyle.over = textButtonStyle.over;
        menuButtonStyle.checked = textButtonStyle.checked;
        
        MenuListStyle menuListStyle = new MenuListStyle();
        menuListStyle.background = skin.getDrawable("list");
        menuListStyle.textButtonStyle = skin.get("menu-button", TextButtonStyle.class);
        menuListStyle.labelStyle = skin.get("white", LabelStyle.class);
        
        menuButtonStyle.menuListStyle = menuListStyle;
        
        skin.add("default", menuButtonStyle);
        skin.add("default", menuListStyle);
        
        populate();
    }
    
    private void populate() {
        addFileMenu();
        
        row();
        addClassBar();
        
        row();
        addStyleAndPreviewSplit(new ScrollPaneListener(), new IbeamListener());
        
        row();
        addStatusBar();
    }
    
    private void addFileMenu() {
        Table table = new Table();
        table.defaults().padRight(2.0f);
        add(table).expandX().left().padTop(2.0f);
        
        MenuButtonGroup menuButtonGroup = new MenuButtonGroup();
        
        MenuButton<MenuItem> menuButton = new MenuButton("File", getSkin());
        menuButtonGroup.add(menuButton);
        table.add(menuButton).padLeft(2.0f);
        
        menuButton.setItems(new MenuItem("New", RootTableEnum.NEW),
                new MenuItem("Open...", RootTableEnum.OPEN),
                new MenuItem("Recent Files...", RootTableEnum.RECENT_FILES),
                new MenuItem("Save", RootTableEnum.SAVE),
                new MenuItem("Save As...", RootTableEnum.SAVE_AS),
                new MenuItem("Import...", RootTableEnum.IMPORT),
                new MenuItem("Export...", RootTableEnum.EXPORT),
                new MenuItem("Exit", RootTableEnum.EXIT));
        if (Utils.isMac()) {
            menuButton.setShortcuts("⌘+N", "⌘+O", "⌘+S", "Shift+⌘+S");
        } else {
            menuButton.setShortcuts("Ctrl+N", "Ctrl+O", "Ctrl+S", "Shift+Ctrl+S");
        }
        menuButton.addListener(new MenuBarListener(menuButton));

        menuButton = new MenuButton("Edit", getSkin());
        menuButtonGroup.add(menuButton);
        table.add(menuButton);
        
        menuButton.setItems(new MenuItem("Undo", RootTableEnum.UNDO),
                new MenuItem("Redo", RootTableEnum.REDO));
        if (Utils.isMac()) {
            menuButton.setShortcuts("⌘+Z", "⌘+Y");
        } else {
            menuButton.setShortcuts("Ctrl+Z", "Ctrl+Y");
        }
        menuButton.addListener(new MenuBarListener(menuButton));
        
        menuButton = new MenuButton("Project", getSkin());
        menuButtonGroup.add(menuButton);
        table.add(menuButton);
        
        menuButton.setItems(new MenuItem("Settings...", RootTableEnum.SETTINGS),
                new MenuItem("Colors...", RootTableEnum.COLORS),
                new MenuItem("Fonts...", RootTableEnum.FONTS),
                new MenuItem("Drawables...", RootTableEnum.DRAWABLES));

        menuButton.addListener(new MenuBarListener(menuButton));
        
        menuButton = new MenuButton("Help", getSkin());
        menuButtonGroup.add(menuButton);
        table.add(menuButton);
        
        menuButton.setItems(new MenuItem("About...", RootTableEnum.ABOUT));
        menuButton.addListener(new MenuBarListener(menuButton));
    }
    
    private void addClassBar() {
        Table table = new Table();
        table.setBackground(getSkin().getDrawable("class-bar"));
        add(table).expandX().left().growX();
        
        Label label = new Label("Class:", getSkin());
        table.add(label).padRight(10.0f).padLeft(10.0f);
        
        classSelectBox = new SelectBox(getSkin());
        table.add(classSelectBox).padRight(5.0f).minWidth(150.0f);
        
        classSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                fire(new RootTableEvent(RootTableEnum.CLASS_SELECTED));
            }
        });
        
        Button button = new Button(getSkin(), "new");
        table.add(button);
        
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                fire(new RootTableEvent(RootTableEnum.NEW_CLASS));
            }
        });
        
        button = new Button(getSkin(), "delete");
        table.add(button).padRight(30.0f);
        
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                fire(new RootTableEvent(RootTableEnum.DELETE_CLASS));
            }
        });
        
        label = new Label("Style:", getSkin());
        table.add(label).padRight(10.0f);
        
        styleSelectBox = new SelectBox(getSkin());
        table.add(styleSelectBox).padRight(5.0f).minWidth(150.0f);
        
        styleSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                fire(new RootTableEvent(RootTableEnum.STYLE_SELECTED));
            }
        });
        
        button = new Button(getSkin(), "new");
        table.add(button);
        
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                fire(new RootTableEvent(RootTableEnum.NEW_STYLE));
            }
        });
        
        button = new Button(getSkin(), "duplicate");
        table.add(button);
        
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                fire(new RootTableEvent(RootTableEnum.DUPLICATE_STYLE));
            }
        });
        
        button = new Button(getSkin(), "delete");
        table.add(button);
        
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                fire(new RootTableEvent(RootTableEnum.DELETE_STYLE));
            }
        });
        
        button = new Button(getSkin(), "settings");
        table.add(button).expandX().left();
        
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                fire(new RootTableEvent(RootTableEnum.RENAME_STYLE));
            }
        });
    }
    
    private void addStyleAndPreviewSplit(InputListener scrollPaneListener, InputListener iBeamListener) {
        Table left = new Table();
        left.setTouchable(Touchable.enabled);
        
        addStyleProperties(left, scrollPaneListener);
        
        Table right = new Table();
        right.setTouchable(Touchable.enabled);
        
        addPreviewPreviewPropertiesSplit(right, scrollPaneListener, iBeamListener);
        
        SplitPane splitPane = new SplitPane(left, right, false, getSkin());
        add(splitPane).grow();
        
        splitPane.addListener(new InputListener() {
            @Override
            public void exit(InputEvent event, float x, float y, int pointer,
                    Actor toActor) {
                if (!draggingCursor && event.getListenerActor().equals(event.getTarget())) {
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                }
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer,
                    Actor fromActor) {
                if (!draggingCursor && event.getListenerActor().equals(event.getTarget())) {
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.HorizontalResize);
                }
            }
            
        });
        
        splitPane.addListener(new DragListener() {
            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                draggingCursor = false;
            }

            @Override
            public void dragStart(InputEvent event, float x, float y,
                    int pointer) {
                if (!draggingCursor && event.getListenerActor().equals(event.getTarget())) {
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.HorizontalResize);
                    draggingCursor = true;
                }
            }
            
        });
        
    }
    
    private void addStyleProperties(final Table left, InputListener scrollPaneListener) {
        Label label = new Label("Style Properties", getSkin(), "title");
        left.add(label);
        
        left.row();
        Table table = new Table();
        ScrollPane scrollPane = new ScrollPane(table, getSkin());
        scrollPane.setFadeScrollBars(false);
        scrollPane.setFlickScroll(false);
        scrollPane.addListener(scrollPaneListener);
        stage.setScrollFocus(scrollPane);
        left.add(scrollPane).grow().padTop(10.0f).padBottom(10.0f);
        
        
    }
    
    private void addPreviewPreviewPropertiesSplit(final Table right, InputListener scrollPaneListener, InputListener iBeamListener) {
        Table top = new Table();
        top.setTouchable(Touchable.enabled);
        
        addPreview(top, scrollPaneListener, iBeamListener);
        
        Table bottom = new Table();
        bottom.setTouchable(Touchable.enabled);
        
        addPreviewProperties(bottom, scrollPaneListener, iBeamListener);
        
        SplitPane splitPane = new SplitPane(top, bottom, true, getSkin());
        right.add(splitPane).grow();
        
        splitPane.addListener(new InputListener() {
            @Override
            public void exit(InputEvent event, float x, float y, int pointer,
                    Actor toActor) {
                if (!draggingCursor && event.getListenerActor().equals(event.getTarget())) {
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                }
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer,
                    Actor fromActor) {
                if (!draggingCursor && event.getListenerActor().equals(event.getTarget())) {
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.VerticalResize);
                }
            }
        });
        
        splitPane.addListener(new DragListener() {
            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                draggingCursor = false;
            }

            @Override
            public void dragStart(InputEvent event, float x, float y,
                    int pointer) {
                if (!draggingCursor && event.getListenerActor().equals(event.getTarget())) {
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.VerticalResize);
                    draggingCursor = true;
                }
            }
            
        });
    }
    
    private void addPreview(Table top, InputListener scrollPaneListener, InputListener iBeamListener) {
        Label label = new Label("Preview", getSkin(), "title");
        top.add(label);
        
        top.row();
        Table table = new Table();
        ScrollPane scrollPane = new ScrollPane(table, getSkin());
        scrollPane.setFadeScrollBars(false);
        scrollPane.setFlickScroll(false);
        scrollPane.addListener(scrollPaneListener);
        top.add(scrollPane).grow().padTop(10.0f).padBottom(10.0f);
        
        
    }
    
    private void addPreviewProperties(Table bottom, InputListener scrollPaneListener, InputListener iBeamListener) {
        Label label = new Label("Preview Properties", getSkin(), "title");
        bottom.add(label);
        
        bottom.row();
        Table table = new Table();
        table.defaults().pad(5.0f);
        
        ScrollPane scrollPane = new ScrollPane(table, getSkin());
        scrollPane.setFadeScrollBars(false);
        scrollPane.setFlickScroll(false);
        scrollPane.addListener(scrollPaneListener);
        bottom.add(scrollPane).grow().padTop(10.0f).padBottom(10.0f);
        
        
    }
    
    private void addStatusBar() {
        Table table = new Table();
        table.setBackground(getSkin().getDrawable("status-bar"));
        add(table).growX();
        
        Label label = new Label("ver. " + Main.VERSION + "    RAY3K.WORDPRESS.COM    © 2016 Raymond \"Raeleus\" Buckley", getSkin());
        table.add(label).expandX().right().padRight(25.0f);
    }

    public SelectBox getClassSelectBox() {
        return classSelectBox;
    }

    public SelectBox getStyleSelectBox() {
        return styleSelectBox;
    }

    private class ScrollPaneListener extends InputListener {
        @Override
        public void enter(InputEvent event, float x, float y, int pointer,
                Actor fromActor) {
            if (event.getTarget() instanceof ScrollPane) {
                ScrollPane scrollPane = (ScrollPane) event.getTarget();
                //if the scroll pane is scrollable
                if (!Float.isNaN(scrollPane.getScrollPercentY())) {
                    stage.setScrollFocus(scrollPane);
                }
            }
        }
    }
    
    private class IbeamListener extends InputListener {

        @Override
        public void exit(InputEvent event, float x, float y, int pointer,
                Actor toActor) {
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        }

        @Override
        public void enter(InputEvent event, float x, float y, int pointer,
                Actor fromActor) {
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Ibeam);
        }
        
    }

    private class MenuBarListener extends MenuButtonListener {
        private final MenuButton<MenuItem> menuButton;
        
        public MenuBarListener(MenuButton<MenuItem> menuButton) {
            this.menuButton = menuButton;
        }
        
        @Override
        public void menuClicked() {
            fire(new RootTableEvent(menuButton.getSelectedItem().event));
        }
    }
    
    private static class MenuItem {
        String text;
        RootTableEnum event;
        
        public MenuItem(String text, RootTableEnum enumeration) {
            this.text = text;
            this.event = enumeration;
        }

        @Override
        public String toString() {
            return text;
        }
    }
    
    public static enum RootTableEnum {
        NEW, OPEN, RECENT_FILES, SAVE, SAVE_AS, IMPORT, EXPORT, EXIT, UNDO,
        REDO, SETTINGS, COLORS, FONTS, DRAWABLES, ABOUT, CLASS_SELECTED,
        NEW_CLASS, DELETE_CLASS, STYLE_SELECTED, NEW_STYLE, DUPLICATE_STYLE,
        DELETE_STYLE, RENAME_STYLE, STYLE_PROPERTY, PREVIEW_PROPERTY
    }
    
    public static class RootTableEvent extends Event {
        public RootTableEnum rootTableEnum;
        public RootTableEvent(RootTableEnum rootTableEnum) {
            this.rootTableEnum = rootTableEnum;
        }
    }
    
    public static abstract class RootTableListener implements EventListener {
        @Override
        public boolean handle(Event event) {
            if (event instanceof RootTableEvent) {
                rootEvent(((RootTableEvent) event).rootTableEnum);
            }
            return false;
        }
        
        public abstract void rootEvent(RootTableEnum rootTableEnum);
    }
}
