/** *****************************************************************************
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
 ***************************************************************************** */
package com.ray3k.skincomposer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.ray3k.skincomposer.BrowseField.BrowseFieldStyle;
import com.ray3k.skincomposer.MenuButton.MenuButtonListener;
import com.ray3k.skincomposer.MenuButton.MenuButtonStyle;
import com.ray3k.skincomposer.MenuList.MenuListStyle;
import com.ray3k.skincomposer.Spinner.SpinnerStyle;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.data.StyleProperty;
import com.ray3k.skincomposer.dialog.DialogColorPicker;
import com.ray3k.skincomposer.utils.Utils;

public class RootTable extends Table {

    private final Stage stage;
    private final Main main;
    private boolean draggingCursor;
    private SelectBox<String> classSelectBox;
    private SelectBox<StyleData> styleSelectBox;
    private Array<StyleProperty> styleProperties;
    private final BrowseFieldStyle bfColorStyle;
    private final BrowseFieldStyle bfDrawableStyle;
    private final BrowseFieldStyle bfFontStyle;
    private final SpinnerStyle spinnerStyle;
    private Table stylePropertiesTable;
    private Table previewPropertiesTable;
    private ScrollPane stylePropertiesScrollPane;
    private final ScrollPaneListener scrollPaneListener;
    private final ObjectMap<String, Object> previewProperties;
    //todo:make final
    private Color previewBgColor;
    private SelectBox<String> previewSizeSelectBox;
    private static final String[] DEFAULT_SIZES = {"small", "default", "large", "growX", "growY", "grow", "custom"};
    private static final String TEXT_SAMPLE = "Lorem ipsum dolor sit";
    private static final String PARAGRAPH_SAMPLE = "Lorem ipsum dolor sit"
            + "amet, consectetur adipiscing elit, sed do eiusmod"
            + "tempor incididunt ut labore et dolore magna aliqua.\n"
            + "Ut enim ad minim veniam, quis nostrud exercitation"
            + "ullamco laboris nisi ut aliquip ex ea commodo"
            + "consequat.\nDuis aute irure dolor in reprehenderit in"
            + "voluptate velit esse cillum dolore eu fugiat nulla"
            + "pariatur.\nExcepteur sint occaecat cupidatat non"
            + "proident, sunt in culpa qui officia deserunt mollit"
            + "anim id est laborum.";
    private static final String PARAGRAPH_SAMPLE_EXT = PARAGRAPH_SAMPLE
            + "\n\n\n" + PARAGRAPH_SAMPLE + "\n\n\n" + PARAGRAPH_SAMPLE + "\n\n\n"
            + PARAGRAPH_SAMPLE;

    public RootTable(Main main) {
        super(main.getSkin());
        this.stage = main.getStage();
        this.main = main;

        previewProperties = new ObjectMap<>();
        previewBgColor = new Color(Color.WHITE);

        spinnerStyle = new Spinner.SpinnerStyle(
                getSkin().get("spinner-minus-h", Button.ButtonStyle.class),
                getSkin().get("spinner-plus-h", Button.ButtonStyle.class),
                getSkin().get("spinner", TextField.TextFieldStyle.class));

        TextButtonStyle textButtonStyle = getSkin().get("file", TextButtonStyle.class);

        MenuButtonStyle menuButtonStyle = new MenuButton.MenuButtonStyle();
        menuButtonStyle.font = textButtonStyle.font;
        menuButtonStyle.up = textButtonStyle.up;
        menuButtonStyle.down = textButtonStyle.down;
        menuButtonStyle.over = textButtonStyle.over;
        menuButtonStyle.checked = textButtonStyle.checked;

        MenuListStyle menuListStyle = new MenuListStyle();
        menuListStyle.background = getSkin().getDrawable("list");
        menuListStyle.textButtonStyle = getSkin().get("menu-button", TextButtonStyle.class);
        menuListStyle.labelStyle = getSkin().get("white", LabelStyle.class);

        menuButtonStyle.menuListStyle = menuListStyle;

        bfColorStyle = new BrowseFieldStyle(getSkin().get("color", ImageButtonStyle.class), getSkin().get(TextFieldStyle.class), getSkin().get(LabelStyle.class));
        bfDrawableStyle = new BrowseFieldStyle(getSkin().get("drawable", ImageButtonStyle.class), getSkin().get(TextFieldStyle.class), getSkin().get(LabelStyle.class));
        bfFontStyle = new BrowseFieldStyle(getSkin().get("font", ImageButtonStyle.class), getSkin().get(TextFieldStyle.class), getSkin().get(LabelStyle.class));

        getSkin().add("default", menuButtonStyle);
        getSkin().add("default", menuListStyle);

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

    public void refreshStyleProperties(boolean preserveScroll) {
        if (stylePropertiesTable != null && stylePropertiesScrollPane != null) {
            float scrollY;
            if (preserveScroll) {
                scrollY = stylePropertiesScrollPane.getScrollY();
            } else {
                scrollY = 0;
            }

            stylePropertiesTable.clearChildren();
            addStyleProperties(stylePropertiesTable);

            if (preserveScroll) {
                validate();
                stylePropertiesScrollPane.setSmoothScrolling(false);
                stylePropertiesScrollPane.setScrollY(scrollY);
                stylePropertiesScrollPane.addAction(new SequenceAction(new DelayAction(.1f), new Action() {
                    @Override
                    public boolean act(float delta) {
                        stylePropertiesScrollPane.setSmoothScrolling(true);
                        return true;
                    }
                }));
            }
        }
    }

    public void refreshStyles(boolean scrollToNewest) {
        populate();
        if (scrollToNewest) {
            styleSelectBox.setSelectedIndex(styleSelectBox.getItems().size - 1);
        }
    }

    private void addStyleProperties(final Table left) {
        Label label = new Label("Style Properties", getSkin(), "title");
        left.add(label);

        left.row();
        Table table = new Table();
        table.defaults().padLeft(10.0f).padRight(10.0f).growX();
        stylePropertiesScrollPane = new ScrollPane(table, getSkin());
        stylePropertiesScrollPane.setFadeScrollBars(false);
        stylePropertiesScrollPane.setFlickScroll(false);
        stylePropertiesScrollPane.addListener(scrollPaneListener);
        stage.setScrollFocus(stylePropertiesScrollPane);
        left.add(stylePropertiesScrollPane).grow().padTop(10.0f).padBottom(10.0f);

        //gather all scrollPaneStyles
        Array<StyleData> scrollPaneStyles = main.getProjectData().getJsonData().getClassStyleMap().get(ScrollPane.class);

        //gather all listStyles
        Array<StyleData> listStyles = main.getProjectData().getJsonData().getClassStyleMap().get(List.class);

        //gather all labelStyles
        Array<StyleData> labelStyles = main.getProjectData().getJsonData().getClassStyleMap().get(Label.class);

        if (styleProperties != null) {
            for (StyleProperty styleProperty : styleProperties) {

                table.row();
                if (styleProperty.type == Color.class) {
                    BrowseField browseField = new BrowseField((String) styleProperty.value, styleProperty.name, bfColorStyle);
                    table.add(browseField).padTop(20.0f);

                    browseField.addListener(new StylePropertyChangeListener(styleProperty, browseField));
                } else if (styleProperty.type == BitmapFont.class) {
                    BrowseField browseField = new BrowseField((String) styleProperty.value, styleProperty.name, bfFontStyle);
                    table.add(browseField).padTop(20.0f);

                    browseField.addListener(new StylePropertyChangeListener(styleProperty, browseField));
                } else if (styleProperty.type == Drawable.class) {
                    BrowseField browseField = new BrowseField((String) styleProperty.value, styleProperty.name, bfDrawableStyle);
                    table.add(browseField).padTop(20.0f);

                    browseField.addListener(new StylePropertyChangeListener(styleProperty, browseField));
                } else if (styleProperty.type == Float.TYPE) {
                    label = new Label(styleProperty.name, getSkin());
                    table.add(label).padTop(20.0f).fill(false).expand(false, false);

                    table.row();
                    Spinner spinner = new Spinner((Double) styleProperty.value, 1.0, false, Spinner.Orientation.HORIZONTAL, spinnerStyle);
                    table.add(spinner);

                    spinner.addListener(new StylePropertyChangeListener(styleProperty, spinner));
                } else if (styleProperty.type == ScrollPaneStyle.class) {
                    label = new Label(styleProperty.name, getSkin());
                    table.add(label).padTop(20.0f).fill(false).expand(false, false);

                    table.row();
                    SelectBox<StyleData> selectBox = new SelectBox<>(getSkin());
                    selectBox.setItems(scrollPaneStyles);
                    table.add(selectBox);

                    selectBox.addListener(new StylePropertyChangeListener(styleProperty, selectBox));
                } else if (styleProperty.type == ListStyle.class) {
                    label = new Label(styleProperty.name, getSkin());
                    table.add(label).padTop(20.0f).fill(false).expand(false, false);

                    table.row();
                    SelectBox<StyleData> selectBox = new SelectBox<>(getSkin());
                    selectBox.setItems(listStyles);
                    table.add(selectBox);

                    selectBox.addListener(new StylePropertyChangeListener(styleProperty, selectBox));
                } else if (styleProperty.type == LabelStyle.class) {
                    label = new Label(styleProperty.name, getSkin());
                    table.add(label).padTop(20.0f).fill(false).expand(false, false);

                    table.row();
                    SelectBox<StyleData> selectBox = new SelectBox<>(getSkin());
                    selectBox.setItems(labelStyles);
                    table.add(selectBox);

                    selectBox.addListener(new StylePropertyChangeListener(styleProperty, selectBox));
                } else if (styleProperty.type == CustomStyle.class) {
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

    //todo: implement iBeamListener
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

    //todo: implement iBeamListener
    private void addPreviewProperties(Table bottom, InputListener scrollPaneListener, InputListener iBeamListener) {
        Label label = new Label("Preview Properties", getSkin(), "title");
        bottom.add(label);

        bottom.row();
        previewPropertiesTable = new Table();
        previewPropertiesTable.defaults().pad(5.0f);

        ScrollPane scrollPane = new ScrollPane(previewPropertiesTable, getSkin());
        scrollPane.setFadeScrollBars(false);
        scrollPane.setFlickScroll(false);
        scrollPane.addListener(scrollPaneListener);
        bottom.add(scrollPane).grow().padTop(10.0f).padBottom(10.0f);
        
        refreshPreviewProperties();
    }
    
    public void refreshPreviewProperties() {
        if (previewPropertiesTable != null) {
            previewPropertiesTable.clear();
            previewProperties.clear();

            Table t = new Table();
            previewPropertiesTable.add(t).center().expand();
            t.defaults().pad(3.0f);
            t.add(new Label("Stage Color: ", getSkin()));
            BrowseField browseField = new BrowseField(null, null, bfColorStyle);
            browseField.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    main.getDialogFactory().showDialogColorPicker((Color) previewProperties.get("bgcolor"), new DialogColorPicker.ColorListener() {
                        @Override
                        public void selected(Color color) {
                            if (color != null) {
                                browseField.getTextField().setText((int) (color.r * 255) + "," + (int) (color.g * 255) + "," + (int) (color.b * 255) + "," + (int) (color.a * 255));
                                previewProperties.put("bgcolor", color);
                                previewBgColor = color;
    //                            render();
                            }
                        }
                    });
                }
            });
            t.add(browseField).growX();
            if (previewBgColor == null) {
                previewBgColor = Color.WHITE;
            }
            previewProperties.put("bgcolor", previewBgColor);
            browseField.getTextField().setText((int) (previewBgColor.r * 255) + "," + (int) (previewBgColor.g * 255) + "," + (int) (previewBgColor.b * 255) + "," + (int) (previewBgColor.a * 255));

            t.row();
            t.add(new Label("Size: ", getSkin())).right();

            previewSizeSelectBox = new SelectBox<>(getSkin());
            previewSizeSelectBox.setItems(DEFAULT_SIZES);
            previewSizeSelectBox.setSelectedIndex(1);
            t.add(previewSizeSelectBox).growX().minWidth(200.0f);

            if (classSelectBox.getSelectedIndex() >= 0) {
                Class clazz = Main.BASIC_CLASSES[classSelectBox.getSelectedIndex()];
                if (clazz.equals(Button.class)) {
                    t.row();
                    t.add(new Label("Disabled: ", getSkin())).right();
                    CheckBox disabledCheckBox = new CheckBox("", getSkin(), "switch");
                    disabledCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("disabled", disabledCheckBox.isChecked());
    //                        render();
                        }
                    });
                    previewProperties.put("disabled", disabledCheckBox.isChecked());
                    t.add(disabledCheckBox).left();
                } else if (clazz.equals(CheckBox.class)) {
                    t.row();
                    t.add(new Label("Disabled: ", getSkin())).right();
                    CheckBox disabledCheckBox = new CheckBox("", getSkin(), "switch");
                    disabledCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("disabled", disabledCheckBox.isChecked());
    //                        render();
                        }
                    });
                    previewProperties.put("disabled", disabledCheckBox.isChecked());
                    t.add(disabledCheckBox).left();

                    t.row();
                    t.add(new Label("Text: ", getSkin()));
                    TextField previewTextField = new TextField(TEXT_SAMPLE, getSkin());
                    previewTextField.setFocusTraversal(false);
                    previewTextField.addListener(com.ray3k.skincomposer.IbeamListener.get());
                    previewTextField.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("text", previewTextField.getText());
    //                        render();
                        }
                    });
                    previewProperties.put("text", previewTextField.getText());
                    t.add(previewTextField).growX();

                } else if (clazz.equals(ImageButton.class)) {
                    t.row();
                    t.add(new Label("Disabled: ", getSkin())).right();
                    CheckBox disabledCheckBox = new CheckBox("", getSkin(), "switch");
                    disabledCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("disabled", disabledCheckBox.isChecked());
    //                        render();
                        }
                    });
                    previewProperties.put("disabled", disabledCheckBox.isChecked());
                    t.add(disabledCheckBox).left();
                } else if (clazz.equals(ImageTextButton.class)) {
                    t.row();
                    t.add(new Label("Disabled: ", getSkin())).right();
                    CheckBox disabledCheckBox = new CheckBox("", getSkin(), "switch");
                    disabledCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("disabled", disabledCheckBox.isChecked());
    //                        render();
                        }
                    });
                    previewProperties.put("disabled", disabledCheckBox.isChecked());
                    t.add(disabledCheckBox).left();

                    t.row();
                    t.add(new Label("Text: ", getSkin()));
                    TextField previewTextField = new TextField(TEXT_SAMPLE, getSkin());
                    previewTextField.setFocusTraversal(false);
                    previewTextField.addListener(com.ray3k.skincomposer.IbeamListener.get());
                    previewTextField.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("text", previewTextField.getText());
    //                        render();
                        }
                    });
                    previewProperties.put("text", previewTextField.getText());
                    t.add(previewTextField).growX();

                } else if (clazz.equals(Label.class)) {
                    t.row();
                    t.add(new Label("Text: ", getSkin()));
                    TextField previewTextField = new TextField(TEXT_SAMPLE, getSkin());
                    previewTextField.setFocusTraversal(false);
                    previewTextField.addListener(com.ray3k.skincomposer.IbeamListener.get());
                    previewTextField.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("text", previewTextField.getText());
    //                        render();
                        }
                    });
                    previewProperties.put("text", previewTextField.getText());
                    t.add(previewTextField).growX();

                } else if (clazz.equals(List.class)) {
                    t.row();
                    t.add(new Label("List Items: ", getSkin())).right();
                    TextArea listItemsTextArea = new TextArea("Lorem ipsum\ndolor sit\namet, consectetur", getSkin());
                    listItemsTextArea.setFocusTraversal(false);
                    listItemsTextArea.setPrefRows(3);
                    listItemsTextArea.addListener(com.ray3k.skincomposer.IbeamListener.get());
                    listItemsTextArea.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("text", listItemsTextArea.getText());
    //                        render();
                        }
                    });
                    previewProperties.put("text", listItemsTextArea.getText());
                    t.add(listItemsTextArea).growX();

                } else if (clazz.equals(ProgressBar.class)) {
                    t.row();
                    t.add(new Label("Disabled: ", getSkin())).right();
                    CheckBox disabledCheckBox = new CheckBox("", getSkin(), "switch");
                    disabledCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("disabled", disabledCheckBox.isChecked());
    //                        render();
                        }
                    });
                    previewProperties.put("disabled", disabledCheckBox.isChecked());
                    t.add(disabledCheckBox).left();

                    t.row();
                    t.add(new Label("Value: ", getSkin())).right();
                    Spinner valueSpinner = new Spinner(0.0f, 1.0f, false, Spinner.Orientation.HORIZONTAL, spinnerStyle);
                    valueSpinner.getTextField().setFocusTraversal(false);
                    valueSpinner.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("value", valueSpinner.getValue());
    //                        render();
                        }
                    });
                    previewProperties.put("value", valueSpinner.getValue());
                    t.add(valueSpinner).growX();

                    t.row();
                    t.add(new Label("Minimum: ", getSkin())).right();
                    Spinner minimumSpinner = new Spinner(0.0f, 1.0f, false, Spinner.Orientation.HORIZONTAL, spinnerStyle);
                    minimumSpinner.getTextField().setFocusTraversal(false);
                    minimumSpinner.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("minimum", minimumSpinner.getValue());
    //                        render();
                        }
                    });
                    previewProperties.put("minimum", minimumSpinner.getValue());
                    t.add(minimumSpinner).growX();

                    t.row();
                    t.add(new Label("Maximum: ", getSkin())).right();
                    Spinner maximumSpinner = new Spinner(100.0f, 1.0f, false, Spinner.Orientation.HORIZONTAL, spinnerStyle);
                    maximumSpinner.getTextField().setFocusTraversal(false);
                    maximumSpinner.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("maximum", maximumSpinner.getValue());
    //                        render();
                        }
                    });
                    previewProperties.put("maximum", maximumSpinner.getValue());
                    t.add(maximumSpinner).growX();

                    t.row();
                    t.add(new Label("Increment: ", getSkin())).right();
                    Spinner incrementSpinner = new Spinner(1.0f, 1.0f, false, Spinner.Orientation.HORIZONTAL, spinnerStyle);
                    incrementSpinner.getTextField().setFocusTraversal(false);
                    incrementSpinner.setMinimum(1);
                    incrementSpinner.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("increment", incrementSpinner.getValue());
    //                        render();
                        }
                    });
                    previewProperties.put("increment", incrementSpinner.getValue());
                    t.add(incrementSpinner).growX();

                    t.row();
                    t.add(new Label("Orientation: ", getSkin())).right();
                    SelectBox<String> selectBox = new SelectBox<>(getSkin());
                    selectBox.setItems(new String[]{"Horizontal", "Vertical"});
                    if (getSelectedStyle().name.contains("vert")) {
                        previewProperties.put("orientation", true);
                        selectBox.setSelectedIndex(1);
                    } else {
                        previewProperties.put("orientation", false);
                    }
                    selectBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            if (selectBox.getSelectedIndex() == 0) {
                                previewProperties.put("orientation", false);
                            } else {
                                previewProperties.put("orientation", true);
                            }
    //                        render();
                        }
                    });
                    t.add(selectBox).growX();

                } else if (clazz.equals(ScrollPane.class)) {
                    t.row();
                    t.add(new Label("Scrollbars On Top: ", getSkin())).right();
                    CheckBox onTopCheckBox = new CheckBox("", getSkin(), "switch");
                    onTopCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("scrollbarsOnTop", onTopCheckBox.isChecked());
    //                        render();
                        }
                    });
                    t.add(onTopCheckBox).left();
                    previewProperties.put("scrollbarsOnTop", onTopCheckBox.isChecked());

                    t.row();
                    t.add(new Label("H ScrollBar Position: ", getSkin())).right();
                    SelectBox<String> hScrollPosBox = new SelectBox<>(getSkin());
                    hScrollPosBox.setItems(new String[]{"Top", "Bottom"});
                    hScrollPosBox.setSelectedIndex(1);
                    hScrollPosBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            if (hScrollPosBox.getSelectedIndex() == 0) {
                                previewProperties.put("hScrollBarPosition", false);
                            } else {
                                previewProperties.put("hScrollBarPosition", true);
                            }
    //                        render();
                        }
                    });
                    t.add(hScrollPosBox).growX();
                    previewProperties.put("hScrollBarPosition", true);

                    t.row();
                    t.add(new Label("V ScrollBar Position: ", getSkin())).right();
                    SelectBox<String> vScrollPosBox = new SelectBox<>(getSkin());
                    vScrollPosBox.setItems(new String[]{"Left", "Right"});
                    vScrollPosBox.setSelectedIndex(1);
                    vScrollPosBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            if (vScrollPosBox.getSelectedIndex() == 0) {
                                previewProperties.put("vScrollBarPosition", false);
                            } else {
                                previewProperties.put("vScrollBarPosition", true);
                            }
    //                        render();
                        }
                    });
                    t.add(vScrollPosBox).growX();
                    previewProperties.put("vScrollBarPosition", true);

                    t.row();
                    t.add(new Label("H Scrolling Disabled: ", getSkin())).right();
                    CheckBox hScrollCheckBox = new CheckBox("", getSkin(), "switch");
                    hScrollCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("hScrollDisabled", hScrollCheckBox.isChecked());
    //                        render();
                        }
                    });
                    t.add(hScrollCheckBox).left();
                    previewProperties.put("hScrollDisabled", hScrollCheckBox.isChecked());

                    t.row();
                    t.add(new Label("V Scrolling Disabled: ", getSkin())).right();
                    CheckBox vScrollCheckBox = new CheckBox("", getSkin(), "switch");
                    vScrollCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("vScrollDisabled", vScrollCheckBox.isChecked());
    //                        render();
                        }
                    });
                    t.add(vScrollCheckBox).left();
                    previewProperties.put("vScrollDisabled", vScrollCheckBox.isChecked());

                    t.row();
                    t.add(new Label("Force H Scroll: ", getSkin())).right();
                    CheckBox forceHScrollCheckBox = new CheckBox("", getSkin(), "switch");
                    forceHScrollCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("forceHscroll", forceHScrollCheckBox.isChecked());
    //                        render();
                        }
                    });
                    t.add(forceHScrollCheckBox).left();
                    previewProperties.put("forceHscroll", forceHScrollCheckBox.isChecked());

                    t.row();
                    t.add(new Label("Force V Scroll: ", getSkin())).right();
                    CheckBox forceVScrollCheckBox = new CheckBox("", getSkin(), "switch");
                    forceVScrollCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("forceVscroll", forceVScrollCheckBox.isChecked());
    //                        render();
                        }
                    });
                    t.add(forceVScrollCheckBox).left();
                    previewProperties.put("forceVscroll", forceVScrollCheckBox.isChecked());

                    t.row();
                    t.add(new Label("Variable Size Knobs: ", getSkin())).right();
                    CheckBox variableSizeKnobsCheckBox = new CheckBox("", getSkin(), "switch");
                    variableSizeKnobsCheckBox.setChecked(true);
                    variableSizeKnobsCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("variableSizeKnobs", variableSizeKnobsCheckBox.isChecked());
    //                        render();
                        }
                    });
                    t.add(variableSizeKnobsCheckBox).left();
                    previewProperties.put("variableSizeKnobs", variableSizeKnobsCheckBox.isChecked());

                    t.row();
                    t.add(new Label("H Overscroll: ", getSkin())).right();
                    CheckBox hOverscrollCheckBox = new CheckBox("", getSkin(), "switch");
                    hOverscrollCheckBox.setChecked(true);
                    hOverscrollCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("hOverscroll", hOverscrollCheckBox.isChecked());
    //                        render();
                        }
                    });
                    t.add(hOverscrollCheckBox).left();
                    previewProperties.put("hOverscroll", hOverscrollCheckBox.isChecked());

                    t.row();
                    t.add(new Label("V Overscroll: ", getSkin())).right();
                    CheckBox vOverscrollCheckBox = new CheckBox("", getSkin(), "switch");
                    vOverscrollCheckBox.setChecked(true);
                    vOverscrollCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("vOverscroll", vOverscrollCheckBox.isChecked());
    //                        render();
                        }
                    });
                    t.add(vOverscrollCheckBox).left();
                    previewProperties.put("vOverscroll", vOverscrollCheckBox.isChecked());

                    t.row();
                    t.add(new Label("Fade Scroll Bars: ", getSkin())).right();
                    CheckBox fadeScrollCheckBox = new CheckBox("", getSkin(), "switch");
                    fadeScrollCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("fadeScroll", fadeScrollCheckBox.isChecked());
    //                        render();
                        }
                    });
                    t.add(fadeScrollCheckBox).left();
                    previewProperties.put("fadeScroll", fadeScrollCheckBox.isChecked());

                    t.row();
                    t.add(new Label("Smooth Scrolling: ", getSkin())).right();
                    CheckBox smoothScrollCheckBox = new CheckBox("", getSkin(), "switch");
                    smoothScrollCheckBox.setChecked(true);
                    smoothScrollCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("smoothScroll", smoothScrollCheckBox.isChecked());
    //                        render();
                        }
                    });
                    t.add(smoothScrollCheckBox).left();
                    previewProperties.put("smoothScroll", smoothScrollCheckBox.isChecked());

                    t.row();
                    t.add(new Label("Flick Scroll: ", getSkin())).right();
                    CheckBox flickScrollCheckBox = new CheckBox("", getSkin(), "switch");
                    flickScrollCheckBox.setChecked(true);
                    flickScrollCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("flickScroll", flickScrollCheckBox.isChecked());
    //                        render();
                        }
                    });
                    t.add(flickScrollCheckBox).left();
                    previewProperties.put("flickScroll", flickScrollCheckBox.isChecked());

                    t.row();
                    t.add(new Label("Clamp: ", getSkin())).right();
                    CheckBox clampCheckBox = new CheckBox("", getSkin(), "switch");
                    clampCheckBox.setChecked(true);
                    clampCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("clamp", clampCheckBox.isChecked());
    //                        render();
                        }
                    });
                    t.add(clampCheckBox).left();
                    previewProperties.put("clamp", clampCheckBox.isChecked());

                    t.row();
                    t.add(new Label("Sample Text: ", getSkin())).right();
                    TextArea previewTextArea = new TextArea(PARAGRAPH_SAMPLE_EXT, getSkin());
                    previewTextArea.setFocusTraversal(false);
                    previewTextArea.setPrefRows(5);
                    previewTextArea.addListener(com.ray3k.skincomposer.IbeamListener.get());
                    previewTextArea.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("text", previewTextArea.getText());
    //                        render();
                        }
                    });
                    previewProperties.put("text", previewTextArea.getText());
                    t.add(previewTextArea).growX();

                    previewSizeSelectBox.setSelectedIndex(2);
                } else if (clazz.equals(SelectBox.class)) {
                    t.row();
                    t.add(new Label("Disabled: ", getSkin())).right();
                    CheckBox disabledCheckBox = new CheckBox("", getSkin(), "switch");
                    disabledCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("disabled", disabledCheckBox.isChecked());
    //                        render();
                        }
                    });
                    previewProperties.put("disabled", disabledCheckBox.isChecked());
                    t.add(disabledCheckBox).left();

                    t.row();
                    t.add(new Label("Max List Count: ", getSkin())).right();
                    Spinner spinner = new Spinner(3, 1, true, Spinner.Orientation.HORIZONTAL, spinnerStyle);
                    spinner.getTextField().setFocusTraversal(false);
                    spinner.setMinimum(1);
                    t.add(spinner).growX();

                    t.row();
                    t.add(new Label("List Items: ", getSkin())).right();
                    TextArea listItemsTextArea = new TextArea("Lorem ipsum\ndolor sit\namet, consectetur", getSkin());
                    listItemsTextArea.setFocusTraversal(false);
                    listItemsTextArea.setPrefRows(3);
                    listItemsTextArea.addListener(com.ray3k.skincomposer.IbeamListener.get());
                    listItemsTextArea.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("text", listItemsTextArea.getText());
    //                        render();
                        }
                    });
                    previewProperties.put("text", listItemsTextArea.getText());
                    t.add(listItemsTextArea).growX();

                } else if (clazz.equals(Slider.class)) {
                    t.row();
                    t.add(new Label("Disabled: ", getSkin())).right();
                    CheckBox disabledCheckBox = new CheckBox("", getSkin(), "switch");
                    disabledCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("disabled", disabledCheckBox.isChecked());
    //                        render();
                        }
                    });
                    previewProperties.put("disabled", disabledCheckBox.isChecked());
                    t.add(disabledCheckBox).left();

                    t.row();
                    t.add(new Label("Minimum: ", getSkin())).right();
                    Spinner minimumSpinner = new Spinner(0.0f, 1.0f, false, Spinner.Orientation.HORIZONTAL, spinnerStyle);
                    minimumSpinner.getTextField().setFocusTraversal(false);
                    minimumSpinner.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("minimum", minimumSpinner.getValue());
    //                        render();
                        }
                    });
                    t.add(minimumSpinner).growX();
                    previewProperties.put("minimum", minimumSpinner.getValue());

                    t.row();
                    t.add(new Label("Maximum: ", getSkin())).right();
                    Spinner maximumSpinner = new Spinner(100.0f, 1.0f, false, Spinner.Orientation.HORIZONTAL, spinnerStyle);
                    maximumSpinner.getTextField().setFocusTraversal(false);
                    maximumSpinner.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("maximum", maximumSpinner.getValue());
    //                        render();
                        }
                    });
                    t.add(maximumSpinner).growX();
                    previewProperties.put("maximum", maximumSpinner.getValue());

                    t.row();
                    t.add(new Label("Increment: ", getSkin())).right();
                    Spinner incrementSpinner = new Spinner(1.0f, 1.0f, false, Spinner.Orientation.HORIZONTAL, spinnerStyle);
                    incrementSpinner.getTextField().setFocusTraversal(false);
                    incrementSpinner.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("increment", incrementSpinner.getValue());
    //                        render();
                        }
                    });
                    t.add(incrementSpinner).growX();
                    previewProperties.put("increment", incrementSpinner.getValue());

                    t.row();
                    t.add(new Label("Orientation: ", getSkin())).right();
                    SelectBox<String> selectBox = new SelectBox<>(getSkin());
                    selectBox.setItems(new String[]{"Horizontal", "Vertical"});
                    if (getSelectedStyle().name.contains("vert")) {
                        previewProperties.put("orientation", true);
                        selectBox.setSelectedIndex(1);
                    } else {
                        previewProperties.put("orientation", false);
                    }
                    selectBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            if (selectBox.getSelectedIndex() == 0) {
                                previewProperties.put("orientation", false);
                            } else {
                                previewProperties.put("orientation", true);
                            }
    //                        render();
                        }
                    });
                    t.add(selectBox).growX();
                } else if (clazz.equals(SplitPane.class)) {
                    t.row();
                    t.add(new Label("Orientation: ", getSkin())).right();
                    SelectBox<String> selectBox = new SelectBox<>(getSkin());
                    selectBox.setItems(new String[]{"Horizontal", "Vertical"});
                    if (getSelectedStyle().name.contains("vert")) {
                        previewProperties.put("orientation", true);
                        selectBox.setSelectedIndex(1);
                    } else {
                        previewProperties.put("orientation", false);
                    }
                    selectBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            if (selectBox.getSelectedIndex() == 0) {
                                previewProperties.put("orientation", false);
                            } else {
                                previewProperties.put("orientation", true);
                            }
    //                        render();
                        }
                    });
                    t.add(selectBox).growX();

                    t.row();
                    t.add(new Label("Sample Text: ", getSkin())).right();
                    TextArea textArea = new TextArea(PARAGRAPH_SAMPLE, getSkin());
                    textArea.setFocusTraversal(false);
                    textArea.setPrefRows(5);
                    textArea.addListener(com.ray3k.skincomposer.IbeamListener.get());
                    textArea.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("text", textArea.getText());
    //                        render();
                        }
                    });
                    previewProperties.put("text", textArea.getText());
                    t.add(textArea).growX();

                    previewSizeSelectBox.setSelectedIndex(2);
                } else if (clazz.equals(TextButton.class)) {
                    t.row();
                    t.add(new Label("Disabled: ", getSkin())).right();
                    CheckBox disabledCheckBox = new CheckBox("", getSkin(), "switch");
                    disabledCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("disabled", disabledCheckBox.isChecked());
    //                        render();
                        }
                    });
                    previewProperties.put("disabled", disabledCheckBox.isChecked());
                    t.add(disabledCheckBox).left();

                    t.row();
                    t.add(new Label("Text: ", getSkin()));
                    TextField previewTextField = new TextField(TEXT_SAMPLE, getSkin());
                    previewTextField.setFocusTraversal(false);
                    previewTextField.addListener(com.ray3k.skincomposer.IbeamListener.get());
                    previewTextField.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("text", previewTextField.getText());
    //                        render();
                        }
                    });
                    previewProperties.put("text", previewTextField.getText());
                    t.add(previewTextField).growX();

                } else if (clazz.equals(TextField.class)) {
                    t.row();
                    t.add(new Label("Disabled: ", getSkin())).right();
                    CheckBox disabledCheckBox = new CheckBox("", getSkin(), "switch");
                    disabledCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("disabled", disabledCheckBox.isChecked());
    //                        render();
                        }
                    });
                    previewProperties.put("disabled", disabledCheckBox.isChecked());
                    t.add(disabledCheckBox).left();

                    t.row();
                    t.add(new Label("Password Mode: ", getSkin())).right();
                    CheckBox checkBox = new CheckBox("", getSkin(), "switch");
                    checkBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("passwordMode", checkBox.isChecked());
    //                        render();
                        }
                    });
                    t.add(checkBox).left();
                    previewProperties.put("passwordMode", checkBox.isChecked());

                    t.row();
                    t.add(new Label("Password Character: ", getSkin()));
                    TextField pcTextField = new TextField("*", getSkin());
                    pcTextField.setFocusTraversal(false);
                    pcTextField.addListener(com.ray3k.skincomposer.IbeamListener.get());
                    pcTextField.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("password", pcTextField.getText());
    //                        render();
                        }
                    });
                    previewProperties.put("password", pcTextField.getText());
                    t.add(pcTextField).growX();

                    t.row();
                    t.add(new Label("Text Alignment: ", getSkin())).right();
                    SelectBox<String> selectBox = new SelectBox<>(getSkin());
                    selectBox.setItems(new String[]{"Left", "Center", "Right"});
                    selectBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            switch (selectBox.getSelectedIndex()) {
                                case 0:
                                    previewProperties.put("alignment", Align.left);
                                    break;
                                case 1:
                                    previewProperties.put("alignment", Align.center);
                                    break;
                                case 2:
                                    previewProperties.put("alignment", Align.right);
                                    break;
                            }
                        }
                    });
                    t.add(selectBox).growX();
                    previewProperties.put("alignment", Align.left);

                    t.row();
                    t.add(new Label("Text: ", getSkin()));
                    TextField previewTextField = new TextField(TEXT_SAMPLE, getSkin());
                    previewTextField.setFocusTraversal(false);
                    previewTextField.addListener(com.ray3k.skincomposer.IbeamListener.get());
                    previewTextField.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("text", previewTextField.getText());
    //                        render();
                        }
                    });
                    previewProperties.put("text", previewTextField.getText());
                    t.add(previewTextField).growX();

                    t.row();
                    t.add(new Label("Message Text: ", getSkin()));
                    TextField messageTextField = new TextField(TEXT_SAMPLE, getSkin());
                    messageTextField.setFocusTraversal(false);
                    messageTextField.addListener(com.ray3k.skincomposer.IbeamListener.get());
                    messageTextField.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("message", messageTextField.getText());
    //                        render();
                        }
                    });
                    previewProperties.put("message", messageTextField.getText());
                    t.add(messageTextField).growX();

                } else if (clazz.equals(TextTooltip.class)) {
                    t.row();
                    t.add(new Label("Text: ", getSkin()));
                    TextField previewTextField = new TextField(TEXT_SAMPLE, getSkin());
                    previewTextField.setFocusTraversal(false);
                    previewTextField.addListener(com.ray3k.skincomposer.IbeamListener.get());
                    previewTextField.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("text", previewTextField.getText());
    //                        render();
                        }
                    });
                    previewProperties.put("text", previewTextField.getText());
                    t.add(previewTextField).growX();

                } else if (clazz.equals(Touchpad.class)) {

                } else if (clazz.equals(Tree.class)) {
                    t.row();
                    t.add(new Label("Icon Spacing: ", getSkin())).right();
                    Spinner spinner = new Spinner(0.0, 1.0, false, Spinner.Orientation.HORIZONTAL, spinnerStyle);
                    spinner.getTextField().setFocusTraversal(false);
                    spinner.setMinimum(1);
                    t.add(spinner).growX();

                    t.row();
                    t.add(new Label("Y Spacing: ", getSkin())).right();
                    spinner = new Spinner(0.0, 1.0, false, Spinner.Orientation.HORIZONTAL, spinnerStyle);
                    spinner.getTextField().setFocusTraversal(false);
                    spinner.setMinimum(1);
                    t.add(spinner).growX();

                } else if (clazz.equals(Window.class)) {
                    t.row();
                    t.add(new Label("Title Text: ", getSkin()));
                    TextField previewTextField = new TextField(TEXT_SAMPLE, getSkin());
                    previewTextField.setFocusTraversal(false);
                    previewTextField.addListener(com.ray3k.skincomposer.IbeamListener.get());
                    previewTextField.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("title", previewTextField.getText());
    //                        render();
                        }
                    });
                    previewProperties.put("title", previewTextField.getText());
                    t.add(previewTextField).growX();

                    t.row();
                    t.add(new Label("Sample Text Color: ", getSkin()));
                    BrowseField textColorField = new BrowseField(null, null, bfColorStyle);
                    t.add(textColorField).growX();

                    t.row();
                    t.add(new Label("Sample Text: ", getSkin())).right();
                    TextArea textArea = new TextArea(PARAGRAPH_SAMPLE, getSkin());
                    textArea.setFocusTraversal(false);
                    textArea.setPrefRows(5);
                    textArea.addListener(com.ray3k.skincomposer.IbeamListener.get());
                    textArea.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("text", textArea.getText());
    //                        render();
                        }
                    });
                    previewProperties.put("text", textArea.getText());
                    t.add(textArea).growX();
                }

                previewSizeSelectBox.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        previewProperties.put("size", previewSizeSelectBox.getSelectedIndex());
                        if (previewSizeSelectBox.getSelectedIndex() != 7) {
    //                    render();
                        }
                    }
                });
                previewProperties.put("size", previewSizeSelectBox.getSelectedIndex());

    //        render();
            }
        }
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

    public Class getSelectedClass() {
        return Main.BASIC_CLASSES[classSelectBox.getSelectedIndex()];
    }

    public StyleData getSelectedStyle() {
        OrderedMap<Class, Array<StyleData>> classStyleMap = main.getProjectData().getJsonData().getClassStyleMap();
        return classStyleMap.get(getSelectedClass()).get(styleSelectBox.getSelectedIndex());
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
                loadClasses(((LoadClassesEvent) event).classSelectBox);
            } else if (event instanceof LoadStylesEvent) {
                loadStyles(((LoadStylesEvent) event).classSelectBox, ((LoadStylesEvent) event).styleSelectBox);
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
