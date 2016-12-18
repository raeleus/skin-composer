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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.BrowseField.BrowseFieldStyle;
import com.ray3k.skincomposer.MenuButton.MenuButtonListener;
import com.ray3k.skincomposer.MenuButton.MenuButtonStyle;
import com.ray3k.skincomposer.MenuList.MenuListStyle;
import com.ray3k.skincomposer.Spinner.SpinnerStyle;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.data.StyleProperty;
import com.ray3k.skincomposer.utils.Utils;

public class RootTable extends Table {
    private final Stage stage;
    private boolean draggingCursor;
    private SelectBox<String> classSelectBox;
    private SelectBox<StyleData> styleSelectBox;
    private Array<StyleProperty> styleProperties;
    private BrowseFieldStyle bfColorStyle;
    private BrowseFieldStyle bfDrawableStyle;
    private BrowseFieldStyle bfFontStyle;
    private final SpinnerStyle spinnerStyle;
    private Array<ScrollPaneStyle> scrollPaneStyles;
    private Array<ListStyle> listStyles;
    private Array<LabelStyle> labelStyles;
    private Table stylePropertiesTable;
    private final ScrollPaneListener scrollPaneListener;
    
    public RootTable(Stage stage, Skin skin) {
        super(skin);
        this.stage = stage;
        
        spinnerStyle = new Spinner.SpinnerStyle(
                skin.get("spinner-minus-h", Button.ButtonStyle.class),
                skin.get("spinner-plus-h", Button.ButtonStyle.class),
                skin.get("spinner", TextField.TextFieldStyle.class));
        
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
        
        bfColorStyle = new BrowseFieldStyle(skin.get("color", ImageButtonStyle.class), skin.get(TextFieldStyle.class), skin.get(LabelStyle.class));
        bfDrawableStyle = new BrowseFieldStyle(skin.get("drawable", ImageButtonStyle.class), skin.get(TextFieldStyle.class), skin.get(LabelStyle.class));
        bfFontStyle = new BrowseFieldStyle(skin.get("font", ImageButtonStyle.class), skin.get(TextFieldStyle.class), skin.get(LabelStyle.class));
        
        skin.add("default", menuButtonStyle);
        skin.add("default", menuListStyle);
        
        scrollPaneListener = new ScrollPaneListener();
    }
    
    public void populate() {
        clearChildren();
        addFileMenu();
        
        row();
        addClassBar();
        
        row();
        addStyleAndPreviewSplit(new IbeamListener());
        
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
                fire(new LoadStylesEvent(classSelectBox, styleSelectBox));
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
                fire(new RootTable.RootTableEvent(RootTable.RootTableEnum.STYLE_SELECTED));
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
        
        fire(new LoadClassesEvent(classSelectBox));
        fire(new LoadStylesEvent(classSelectBox, styleSelectBox));
    }
    
    private void addStyleAndPreviewSplit(InputListener iBeamListener) {
        stylePropertiesTable = new Table();
        stylePropertiesTable.setTouchable(Touchable.enabled);
        
        addStyleProperties(stylePropertiesTable);
        
        Table right = new Table();
        right.setTouchable(Touchable.enabled);
        
        addPreviewPreviewPropertiesSplit(right, scrollPaneListener, iBeamListener);
        
        SplitPane splitPane = new SplitPane(stylePropertiesTable, right, false, getSkin());
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
    
    public void refreshStyleProperties() {
        if (stylePropertiesTable != null) {
            stylePropertiesTable.clearChildren();
            addStyleProperties(stylePropertiesTable);
        }
    }
    
    private void addStyleProperties(final Table left) {
        Label label = new Label("Style Properties", getSkin(), "title");
        left.add(label);
        
        left.row();
        Table table = new Table();
        table.defaults().padLeft(10.0f).padRight(10.0f).growX();
        ScrollPane scrollPane = new ScrollPane(table, getSkin());
        scrollPane.setFadeScrollBars(false);
        scrollPane.setFlickScroll(false);
        scrollPane.addListener(scrollPaneListener);
        stage.setScrollFocus(scrollPane);
        left.add(scrollPane).grow().padTop(10.0f).padBottom(10.0f);
        
        if (styleProperties != null) {
            for (StyleProperty styleProperty : styleProperties) {
                label = new Label(styleProperty.name, getSkin());
                table.add(label).padTop(20.0f).fill(false).expand(false, false);
                
                table.row();
                if (styleProperty.type == Color.class) {
                    BrowseField browseField = new BrowseField((String) styleProperty.value, bfColorStyle);
                    table.add(browseField);
                    
                    browseField.addListener(new StylePropertyChangeListener(styleProperty, browseField));
                } else if (styleProperty.type == BitmapFont.class) {
                    BrowseField browseField = new BrowseField((String) styleProperty.value, bfFontStyle);
                    table.add(browseField);
                    
                    browseField.addListener(new StylePropertyChangeListener(styleProperty, browseField));
                } else if (styleProperty.type == Drawable.class) {
                    BrowseField browseField = new BrowseField((String) styleProperty.value, bfDrawableStyle);
                    table.add(browseField);
                    
                    browseField.addListener(new StylePropertyChangeListener(styleProperty, browseField));
                } else if (styleProperty.type == Float.TYPE) {
                    Spinner spinner = new Spinner((Double)styleProperty.value, 1.0, false, Spinner.Orientation.HORIZONTAL, spinnerStyle);
                    table.add(spinner);
                    
                    spinner.addListener(new StylePropertyChangeListener(styleProperty, spinner));
                } else if (styleProperty.type == ScrollPaneStyle.class) {
                    SelectBox selectBox = new SelectBox(getSkin());
                    table.add(selectBox);
                    
                    selectBox.addListener(new StylePropertyChangeListener(styleProperty, selectBox));
                } else if (styleProperty.type == ListStyle.class) {
                    SelectBox selectBox = new SelectBox(getSkin());
                    table.add(selectBox);
                    
                    selectBox.addListener(new StylePropertyChangeListener(styleProperty, selectBox));
                } else if (styleProperty.type == LabelStyle.class) {
                    SelectBox selectBox = new SelectBox(getSkin());
                    table.add(selectBox);
                    
                    selectBox.addListener(new StylePropertyChangeListener(styleProperty, selectBox));
                }
                else if (styleProperty.type == CustomStyle.class) {
                    //todo: implement custom styles...
                }
                
                table.row();
            }
        }
    }
    
    private class StylePropertyChangeListener extends ChangeListener {
        private StyleProperty styleProp;
        private Actor styleActor;

        public StylePropertyChangeListener(StyleProperty styleProp, Actor styleActor) {
            this.styleProp = styleProp;
            this.styleActor = styleActor;
        }
        
        @Override
        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
            fire(new StylePropertyEvent(styleProp, styleActor));
        }
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

    public void setStyleProperties(Array<StyleProperty> styleProperties) {
        this.styleProperties = styleProperties;
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
        DELETE_STYLE, RENAME_STYLE, PREVIEW_PROPERTY
    }
    
    public static class RootTableEvent extends Event {
        public RootTableEnum rootTableEnum;
        
        public RootTableEvent(RootTableEnum rootTableEnum) {
            this.rootTableEnum = rootTableEnum;
        }
    }
    
    private static class LoadClassesEvent extends Event {
        SelectBox<String> classSelectBox;

        public LoadClassesEvent(SelectBox<String> classSelectBox) {
            this.classSelectBox = classSelectBox;
        }
    }
    
    private static class LoadStylesEvent extends Event {
        SelectBox<String> classSelectBox;
        SelectBox<StyleData> styleSelectBox;

        public LoadStylesEvent(SelectBox<String> classSelectBox,
                SelectBox<StyleData> styleSelectBox) {
            this.classSelectBox = classSelectBox;
            this.styleSelectBox = styleSelectBox;
        }
    }
    
    private static class StylePropertyEvent extends Event {
        StyleProperty styleProperty;
        Actor styleActor;

        public StylePropertyEvent(StyleProperty styleProperty, Actor styleActor) {
            this.styleProperty = styleProperty;
            this.styleActor = styleActor;
        }
    }
    
    public static abstract class RootTableListener implements EventListener {
        @Override
        public boolean handle(Event event) {
            if (event instanceof RootTableEvent) {
                rootEvent((RootTableEvent) event);
            } else if (event instanceof LoadClassesEvent) {
                loadClasses(((LoadClassesEvent)event).classSelectBox);
            } else if (event instanceof LoadStylesEvent) {
                loadStyles(((LoadStylesEvent)event).classSelectBox, ((LoadStylesEvent)event).styleSelectBox);
            } else if (event instanceof StylePropertyEvent) {
                stylePropertyChanged(((StylePropertyEvent) event).styleProperty, ((StylePropertyEvent) event).styleActor);
            }
            return false;
        }
        
        public abstract void rootEvent(RootTableEvent event);
        
        public abstract void stylePropertyChanged(StyleProperty styleProperty, Actor styleActor);
    
        public abstract void loadClasses(SelectBox<String> classSelectBox);
        
        public abstract void loadStyles(SelectBox<String> classSelectBox, SelectBox<StyleData> styleSelectBox);
    }
}
