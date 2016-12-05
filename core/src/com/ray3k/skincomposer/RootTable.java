package com.ray3k.skincomposer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.ray3k.skincomposer.MenuButton.MenuButtonStyle;
import com.ray3k.skincomposer.MenuList.MenuListStyle;

public class RootTable extends Table {
    private Stage stage;
    private boolean draggingCursor;
    private DesktopWorker desktopWorker;
    
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
        menuListStyle.textButtonStyle = skin.get("default", TextButtonStyle.class);
        
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
        
        MenuButton menuButton = new MenuButton("File", getSkin());
        table.add(menuButton).padLeft(2.0f);

        menuButton = new MenuButton("Edit", getSkin());
        table.add(menuButton);
        
        menuButton = new MenuButton("Project", getSkin());
        table.add(menuButton);
        
        menuButton = new MenuButton("Help", getSkin());
        table.add(menuButton);
    }
    
    private void addClassBar() {
        Table table = new Table();
        table.setBackground(getSkin().getDrawable("class-bar"));
        add(table).expandX().left().growX();
        
        Label label = new Label("Class:", getSkin());
        table.add(label).padRight(10.0f).padLeft(10.0f);
        
        SelectBox selectBox = new SelectBox(getSkin());
        table.add(selectBox).padRight(5.0f).minWidth(150.0f);
        
        Button button = new Button(getSkin(), "new");
        table.add(button).padRight(30.0f);
        
        label = new Label("Style:", getSkin());
        table.add(label).padRight(10.0f);
        
        selectBox = new SelectBox(getSkin());
        table.add(selectBox).padRight(5.0f).minWidth(150.0f);
        
        button = new Button(getSkin(), "new");
        table.add(button);
        
        button = new Button(getSkin(), "duplicate");
        table.add(button);
        
        button = new Button(getSkin(), "delete");
        table.add(button);
        
        button = new Button(getSkin(), "settings");
        table.add(button).expandX().left();
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
}
