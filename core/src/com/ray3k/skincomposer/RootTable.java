/** *****************************************************************************
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
 ***************************************************************************** */
package com.ray3k.skincomposer;

import java.util.Arrays;
import java.util.Locale;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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
import com.badlogic.gdx.scenes.scene2d.ui.TooltipManager;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.ray3k.skincomposer.MenuButton.MenuButtonListener;
import com.ray3k.skincomposer.data.ColorData;
import com.ray3k.skincomposer.data.CustomClass;
import com.ray3k.skincomposer.data.CustomProperty;
import com.ray3k.skincomposer.data.CustomProperty.PropertyType;
import com.ray3k.skincomposer.data.CustomStyle;
import com.ray3k.skincomposer.data.DrawableData;
import com.ray3k.skincomposer.data.FontData;
import com.ray3k.skincomposer.data.FreeTypeFontData;
import com.ray3k.skincomposer.data.PreviewActorCreator;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.data.StyleProperty;
import com.ray3k.skincomposer.dialog.DialogColorPicker;
import com.ray3k.skincomposer.utils.Utils;

public class RootTable extends Table {

    private final Stage stage;
    private final Main main;
    @SuppressWarnings("rawtypes")
	private SelectBox classSelectBox;
    @SuppressWarnings("rawtypes")
	private SelectBox styleSelectBox;
    private Array<StyleProperty> styleProperties;
    private Array<CustomProperty> customProperties;
    private Table stylePropertiesTable;
    private Table previewPropertiesTable;
    private Table previewTable;
    private ScrollPane stylePropertiesScrollPane;
    private final ScrollPaneListener scrollPaneListener;
    private final ObjectMap<String, Object> previewProperties;
    private final Color previewBgColor;
    public SelectBox<String> previewSizeSelectBox;
    public static final String[] DEFAULT_SIZES = {"small", "default", "large", "growX", "growY", "grow", "custom"};
    private static final String TEXT_SAMPLE = "Lorem ipsum dolor sit";
    private static final String PARAGRAPH_SAMPLE = 
            "Lorem ipsum dolor sit amet, consectetur\n"
            + "adipiscing elit, sed do eiusmod tempor\n"
            + "incididunt ut labore et dolore magna\n"
            + "aliqua. Ut enim ad minim veniam, quis\n"
            + "nostrud exercitation ullamco laboris\n"
            + "nisi ut aliquip ex ea commodo\n"
            + "consequat. Duis aute irure dolor in\n"
            + "reprehenderit in voluptate velit esse\n"
            + "cillum dolore eu fugiat nulla pariatur.\n"
            + "Excepteur sint occaecat cupidatat non\n"
            + "proident, sunt in culpa qui officia\n"
            + "deserunt mollit anim id est laborum.\n";
    public static final String PARAGRAPH_SAMPLE_EXT = PARAGRAPH_SAMPLE
            + "\n\n\n" + PARAGRAPH_SAMPLE + "\n\n\n" + PARAGRAPH_SAMPLE + "\n\n\n"
            + PARAGRAPH_SAMPLE;
    private final Array<BitmapFont> previewFonts;
    private final ObjectMap<String, Drawable> drawablePairs;
    private TextureAtlas atlas;
    private MenuItem undoButton;
    private MenuItem redoButton;
    private MenuItem recentFilesButton;
    @SuppressWarnings("rawtypes")
	private MenuButton fileMenu;
    @SuppressWarnings("rawtypes")
	private MenuButton editMenu;
    private Button classDuplicateButton;
    private Button classDeleteButton;
    private Button classRenameButton;
    private Button styleDeleteButton;
    private Button styleRenameButton;
    private FilesDroppedListener filesDroppedListener;

    public RootTable(Main main) {
        super(main.getSkin());
        this.stage = main.getStage();
        this.main = main;
        
        previewProperties = new ObjectMap<>();
        previewBgColor = new Color(Color.WHITE);
        
        scrollPaneListener = new ScrollPaneListener();
        previewFonts = new Array<>();
        drawablePairs = new ObjectMap<>();
        
        produceAtlas();
        
        main.getStage().addListener(new ShortcutListener(this));
        
        filesDroppedListener = (Array<FileHandle> files) -> {
            for (FileHandle fileHandle : files) {
                if (fileHandle.extension().toLowerCase(Locale.ROOT).equals("scmp")) {
                    fire(new ScmpDroppedEvent(fileHandle));
                    break;
                }
            }
        };
        
        main.getDesktopWorker().addFilesDroppedListener(filesDroppedListener);
    }

    public void populate() {
        Button button = (Button) findActor("downloadButton");
        boolean updateAvailable = button == null ? false : button.isVisible();
        
        clearChildren();
        addFileMenu();

        row();
        addClassBar();

        row();
        addStyleAndPreviewSplit();

        row();
        addStatusBar();
        
        ((Button) findActor("downloadButton")).setVisible(updateAvailable);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	private void addFileMenu() {
        Table table = new Table();
        table.defaults().padRight(2.0f);
        add(table).growX().padTop(2.0f);

        MenuButtonGroup menuButtonGroup = new MenuButtonGroup();

        fileMenu = new MenuButton("File", getSkin());
        fileMenu.addListener(getMain().getHandListener());
        fileMenu.getMenuList().addListener(getMain().getHandListener());
        menuButtonGroup.add(fileMenu);
        table.add(fileMenu).padLeft(2.0f);

        recentFilesButton = new MenuItem("Recent Files...", RootTableEnum.RECENT_FILES);
        
        fileMenu.setItems(new MenuItem("New", RootTableEnum.NEW),
                new MenuItem("Open...", RootTableEnum.OPEN),
                recentFilesButton,
                new MenuItem("Save", RootTableEnum.SAVE),
                new MenuItem("Save As...", RootTableEnum.SAVE_AS),
                new MenuItem("Welcome Screen...", RootTableEnum.WELCOME),
                new MenuItem("Import...", RootTableEnum.IMPORT),
                new MenuItem("Export...", RootTableEnum.EXPORT),
                new MenuItem("Exit", RootTableEnum.EXIT));
        if (Utils.isMac()) {
            fileMenu.setShortcuts("⌘+N", "⌘+O", null, "⌘+S", "Shift+⌘+S", null, null, "⌘+E");
        } else {
            fileMenu.setShortcuts("Ctrl+N", "Ctrl+O", null, "Ctrl+S", "Shift+Ctrl+S", null, null, "Ctrl+E");
        }
        fileMenu.addListener(new MenuBarListener(fileMenu));

        editMenu = new MenuButton("Edit", getSkin());
        editMenu.addListener(getMain().getHandListener());
        editMenu.getMenuList().addListener(getMain().getHandListener());
        menuButtonGroup.add(editMenu);
        table.add(editMenu);

        undoButton = new MenuItem("Undo", RootTableEnum.UNDO);
        redoButton = new MenuItem("Redo", RootTableEnum.REDO);
        
        editMenu.setItems(undoButton, redoButton);
        if (Utils.isMac()) {
            editMenu.setShortcuts("⌘+Z", "⌘+Y");
        } else {
            editMenu.setShortcuts("Ctrl+Z", "Ctrl+Y");
        }
        editMenu.setDisabled(undoButton, true);
        editMenu.setDisabled(redoButton, true);
        editMenu.addListener(new MenuBarListener(editMenu));

        MenuButton<MenuItem> menuButton = new MenuButton("Project", getSkin());
        menuButton.addListener(getMain().getHandListener());
        menuButton.getMenuList().addListener(getMain().getHandListener());
        menuButtonGroup.add(menuButton);
        table.add(menuButton);

        menuButton.setItems(new MenuItem("Settings...", RootTableEnum.SETTINGS),
                new MenuItem("Colors...", RootTableEnum.COLORS),
                new MenuItem("Fonts...", RootTableEnum.FONTS),
                new MenuItem("Drawables...", RootTableEnum.DRAWABLES),
                new MenuItem("Refresh Atlas", RootTableEnum.REFRESH_ATLAS));
        menuButton.setShortcuts(null, null, null, null, "F5");

        menuButton.addListener(new MenuBarListener(menuButton));

        menuButton = new MenuButton("Help", getSkin());
        menuButton.addListener(getMain().getHandListener());
        menuButton.getMenuList().addListener(getMain().getHandListener());
        menuButtonGroup.add(menuButton);
        table.add(menuButton);

        menuButton.setItems(new MenuItem("About...", RootTableEnum.ABOUT));
        menuButton.addListener(new MenuBarListener(menuButton));
        
        Button button = new Button(getSkin(), "download");
        button.setName("downloadButton");
        table.add(button).expandX().right();
        button.addListener(new TextTooltip("Update Available", getMain().getTooltipManager(), getSkin()));
        button.addListener(getMain().getHandListener());
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                fire(new RootTableEvent(RootTableEnum.DOWNLOAD_UPDATE));
            }
        });
        button.setVisible(false);
    }
    
    @SuppressWarnings("unchecked")
	public void setRecentFilesDisabled(boolean disabled) {
        fileMenu.setDisabled(recentFilesButton, disabled);
    }

    @SuppressWarnings("unchecked")
	public void setUndoDisabled(boolean disabled) {
        editMenu.setDisabled(undoButton, disabled);
    }
    
    @SuppressWarnings("unchecked")
	public void setRedoDisabled(boolean disabled) {
        editMenu.setDisabled(redoButton, disabled);
    }
    
    public void setUndoText(String text) {
        undoButton.text = text;
        editMenu.updateContents();
    }
    
    public void setRedoText(String text) {
        redoButton.text = text;
        editMenu.updateContents();
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void addClassBar() {
        Table table = new Table();
        table.setBackground(getSkin().getDrawable("class-bar"));
        add(table).expandX().left().growX();

        Label label = new Label("Class:", getSkin());
        table.add(label).padRight(10.0f).padLeft(10.0f);

        classSelectBox = new SelectBox(getSkin());
        classSelectBox.addListener(getMain().getHandListener());
        classSelectBox.getList().addListener(getMain().getHandListener());
        table.add(classSelectBox).padRight(5.0f).minWidth(150.0f);

        classSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                fire(new RootTableEvent(RootTableEnum.CLASS_SELECTED));
                fire(new LoadStylesEvent(classSelectBox, styleSelectBox));
            }
        });

        Button button = new Button(getSkin(), "new");
        button.addListener(getMain().getHandListener());
        table.add(button);

        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                fire(new RootTableEvent(RootTableEnum.NEW_CLASS));
            }
        });
        
        //Tooltip
        TextTooltip toolTip = new TextTooltip("New Class", getMain().getTooltipManager(), getSkin());
        button.addListener(toolTip);

        classDuplicateButton = new Button(getSkin(), "duplicate");
        classDuplicateButton.setDisabled(true);
        classDuplicateButton.addListener(getMain().getHandListener());
        table.add(classDuplicateButton);

        classDuplicateButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                fire(new RootTableEvent(RootTableEnum.DUPLICATE_CLASS));
            }
        });
        
        toolTip = new TextTooltip("Duplicate Class", getMain().getTooltipManager(), getSkin());
        classDuplicateButton.addListener(toolTip);
        
        classDeleteButton = new Button(getSkin(), "delete");
        classDeleteButton.setDisabled(true);
        table.add(classDeleteButton);

        classDeleteButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                fire(new RootTableEvent(RootTableEnum.DELETE_CLASS));
            }
        });
        
        toolTip = new TextTooltip("Delete Class", getMain().getTooltipManager(), getSkin());
        classDeleteButton.addListener(toolTip);
        
        classRenameButton = new Button(getSkin(), "settings");
        classRenameButton.setDisabled(true);
        table.add(classRenameButton).padRight(30.0f);

        classRenameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                fire(new RootTableEvent(RootTableEnum.RENAME_CLASS));
            }
        });
        
        toolTip = new TextTooltip("Rename Class", getMain().getTooltipManager(), getSkin());
        classRenameButton.addListener(toolTip);

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
        
        styleSelectBox.addListener(getMain().getHandListener());
        styleSelectBox.getList().addListener(getMain().getHandListener());

        button = new Button(getSkin(), "new");
        button.addListener(getMain().getHandListener());
        table.add(button);

        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                fire(new RootTableEvent(RootTableEnum.NEW_STYLE));
            }
        });
        
        toolTip = new TextTooltip("New Style", getMain().getTooltipManager(), getSkin());
        button.addListener(toolTip);

        button = new Button(getSkin(), "duplicate");
        button.addListener(getMain().getHandListener());
        table.add(button);

        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                fire(new RootTableEvent(RootTableEnum.DUPLICATE_STYLE));
            }
        });
        
        toolTip = new TextTooltip("Duplicate Style", getMain().getTooltipManager(), getSkin());
        button.addListener(toolTip);

        styleDeleteButton = new Button(getSkin(), "delete");
        styleDeleteButton.addListener(getMain().getHandListener());
        table.add(styleDeleteButton);

        styleDeleteButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                fire(new RootTableEvent(RootTableEnum.DELETE_STYLE));
            }
        });
        
        toolTip = new TextTooltip("Delete Style", getMain().getTooltipManager(), getSkin());
        styleDeleteButton.addListener(toolTip);

        styleRenameButton = new Button(getSkin(), "settings");
        table.add(styleRenameButton).expandX().left();

        styleRenameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                fire(new RootTableEvent(RootTableEnum.RENAME_STYLE));
            }
        });
        
        toolTip = new TextTooltip("Rename Style", getMain().getTooltipManager(), getSkin());
        styleRenameButton.addListener(toolTip);

        fire(new LoadClassesEvent(classSelectBox));
        fire(new LoadStylesEvent(classSelectBox, styleSelectBox));
    }
    
    public void setClassDuplicateButtonDisabled(boolean disabled) {
        classDuplicateButton.setDisabled(disabled);
        if (disabled) {
            if (classDuplicateButton.getListeners().contains(getMain().getHandListener(), true)) {
                classDuplicateButton.removeListener(getMain().getHandListener());
            }
        } else {
            if (!classDuplicateButton.getListeners().contains(getMain().getHandListener(), true)) {
                classDuplicateButton.addListener(getMain().getHandListener());
            }
        }
    }
    
    public void setClassDeleteButtonDisabled(boolean disabled) {
        classDeleteButton.setDisabled(disabled);
        if (disabled) {
            if (classDeleteButton.getListeners().contains(getMain().getHandListener(), true)) {
                classDeleteButton.removeListener(getMain().getHandListener());
            }
        } else {
            if (!classDeleteButton.getListeners().contains(getMain().getHandListener(), true)) {
                classDeleteButton.addListener(getMain().getHandListener());
            }
        }
    }
    
    public void setClassRenameButtonDisabled(boolean disabled) {
        classRenameButton.setDisabled(disabled);
        if (disabled) {
            if (classRenameButton.getListeners().contains(getMain().getHandListener(), true)) {
                classRenameButton.removeListener(getMain().getHandListener());
            }
        } else {
            if (!classRenameButton.getListeners().contains(getMain().getHandListener(), true)) {
                classRenameButton.addListener(getMain().getHandListener());
            }
        }
    }
    
    public void setStyleDeleteButtonDisabled(boolean disabled) {
        styleDeleteButton.setDisabled(disabled);
        if (disabled) {
            if (styleDeleteButton.getListeners().contains(getMain().getHandListener(), true)) {
                styleDeleteButton.removeListener(getMain().getHandListener());
            }
        } else {
            if (!styleDeleteButton.getListeners().contains(getMain().getHandListener(), true)) {
                styleDeleteButton.addListener(getMain().getHandListener());
            }
        }
    }
    
    public void setStyleRenameButtonDisabled(boolean disabled) {
        styleRenameButton.setDisabled(disabled);
        if (disabled) {
            if (styleRenameButton.getListeners().contains(getMain().getHandListener(), true)) {
                styleRenameButton.removeListener(getMain().getHandListener());
            }
        } else {
            if (!styleRenameButton.getListeners().contains(getMain().getHandListener(), true)) {
                styleRenameButton.addListener(getMain().getHandListener());
            }
        }
    }
    
    private void addStyleAndPreviewSplit() {
        stylePropertiesTable = new Table();
        stylePropertiesTable.setTouchable(Touchable.enabled);

        addStyleProperties(stylePropertiesTable);

        Table right = new Table();
        right.setTouchable(Touchable.enabled);

        addPreviewPreviewPropertiesSplit(right, scrollPaneListener);

        SplitPane splitPane = new SplitPane(stylePropertiesTable, right, false, getSkin());
        add(splitPane).grow();

        splitPane.addListener(getMain().getHorizontalResizeArrowListener());
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

    public void refreshClasses(boolean scrollToNewest) {
        int classSelectedIndex = classSelectBox.getSelectedIndex();
        populate();
        if (scrollToNewest) {
            classSelectBox.setSelectedIndex(classSelectBox.getItems().size - 1);
        } else {
            classSelectBox.setSelectedIndex(Math.min(classSelectedIndex, classSelectBox.getItems().size - 1));
        }
    }
    
    public void refreshStyles(boolean scrollToNewest) {
        int classSelectedIndex = classSelectBox.getSelectedIndex();
        populate();
        classSelectBox.setSelectedIndex(classSelectedIndex);
        if (scrollToNewest) {
            styleSelectBox.setSelectedIndex(styleSelectBox.getItems().size - 1);
        }
    }

    @SuppressWarnings("rawtypes")
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
        Array<StyleData> scrollPaneStyles = getMain().getProjectData().getJsonData().getClassStyleMap().get(ScrollPane.class);

        //gather all listStyles
        Array<StyleData> listStyles = getMain().getProjectData().getJsonData().getClassStyleMap().get(List.class);

        //gather all labelStyles
        Array<StyleData> labelStyles = getMain().getProjectData().getJsonData().getClassStyleMap().get(Label.class);

        if (styleProperties != null) {
            //add parent selection box
            label = new Label("parent", getSkin());
            table.add(label).padTop(20.0f).fill(false).expand(false, false);
            
            table.row();
            Array<String> parentNames = new Array<String>();
            parentNames.add("None");
            
            Class recursiveClass = getSelectedClass();
            Class recursiveStyleClass = Main.basicToStyleClass(recursiveClass);
            while (recursiveStyleClass != null && Arrays.asList(Main.STYLE_CLASSES).contains(recursiveStyleClass)) {
                for (StyleData style : getMain().getJsonData().getClassStyleMap().get(recursiveClass)) {
                    if (style != null && !(style.parent != null && style.parent.equals(getSelectedStyle().name)) && !(parentNames.contains(style.name, false) || style.equals(getSelectedStyle()) && recursiveClass.equals(getSelectedClass()))) {
                        parentNames.add(style.name);
                    }
                }
                
                recursiveClass = recursiveClass.getSuperclass();
                recursiveStyleClass = Main.basicToStyleClass(recursiveClass);
            }
            
            SelectBox<String> parentSelectBox = new SelectBox<String>(getSkin());
            parentSelectBox.setItems(parentNames);
            parentSelectBox.setSelected(getSelectedStyle().parent);
            table.add(parentSelectBox);
            parentSelectBox.addListener(getMain().getHandListener());
            parentSelectBox.addListener(new StyleParentChangeListener(getSelectedStyle(), parentSelectBox));
            //make preview respect parent
            
            for (StyleProperty styleProperty : styleProperties) {

                table.row();
                if (styleProperty.type == Color.class) {
                    BrowseField browseField;
                    if (styleProperty.optional) {
                        browseField = new BrowseField((String) styleProperty.value, styleProperty.name, getSkin(), "color");
                    } else {
                        browseField = new BrowseField((String) styleProperty.value, styleProperty.name, getSkin(), "color-required");
                    }
                    browseField.addListener(getMain().getHandListener());
                    table.add(browseField).padTop(20.0f);

                    browseField.addListener(new StylePropertyChangeListener(styleProperty, browseField));
                } else if (styleProperty.type == BitmapFont.class) {
                    BrowseField browseField;
                    if (styleProperty.optional) {
                        browseField = new BrowseField((String) styleProperty.value, styleProperty.name, getSkin(), "font");
                    } else {
                        browseField = new BrowseField((String) styleProperty.value, styleProperty.name, getSkin(), "font-required");
                    }
                    browseField.addListener(getMain().getHandListener());
                    table.add(browseField).padTop(20.0f);

                    browseField.addListener(new StylePropertyChangeListener(styleProperty, browseField));
                } else if (styleProperty.type == Drawable.class) {
                    BrowseField browseField;
                    if (styleProperty.optional) {
                        browseField = new BrowseField((String) styleProperty.value, styleProperty.name, getSkin(), "drawable");
                    } else {
                        browseField = new BrowseField((String) styleProperty.value, styleProperty.name, getSkin(), "drawable-required");
                    }
                    browseField.addListener(getMain().getHandListener());
                    table.add(browseField).padTop(20.0f);

                    browseField.addListener(new StylePropertyChangeListener(styleProperty, browseField));
                } else if (styleProperty.type == Float.TYPE) {
                    if (styleProperty.optional) {
                        label = new Label(styleProperty.name, getSkin());
                    } else {
                        label = new Label(styleProperty.name, getSkin(), "required");
                    }
                    table.add(label).padTop(20.0f).fill(false).expand(false, false);

                    table.row();
                    Spinner spinner = new Spinner((Double) styleProperty.value, 1.0, false, Spinner.Orientation.HORIZONTAL, getSkin());
                    spinner.getTextField().addListener(getMain().getIbeamListener());
                    spinner.getButtonMinus().addListener(getMain().getHandListener());
                    spinner.getButtonPlus().addListener(getMain().getHandListener());
                    table.add(spinner);

                    spinner.addListener(new StylePropertyChangeListener(styleProperty, spinner));
                } else if (styleProperty.type == ScrollPaneStyle.class) {
                    if (styleProperty.optional) {
                        label = new Label(styleProperty.name, getSkin());
                    } else {
                        label = new Label(styleProperty.name, getSkin(), "required");
                    }
                    table.add(label).padTop(20.0f).fill(false).expand(false, false);

                    table.row();
                    SelectBox<StyleData> selectBox = new SelectBox<>(getSkin());
                    selectBox.setItems(scrollPaneStyles);
                    selectBox.addListener(getMain().getHandListener());
                    selectBox.getList().addListener(getMain().getHandListener());
                    
                    if (styleProperty.value != null) {
                        String name = ((String) styleProperty.value);
                        int index = 0;
                        for (StyleData styleData : scrollPaneStyles) {
                            if (styleData.name.equals(name)) {
                                break;
                            } else {
                                index++;
                            }
                        }
                        if (index < scrollPaneStyles.size) {
                            selectBox.setSelectedIndex(index);
                        }
                    }
                    
                    table.add(selectBox);

                    selectBox.addListener(new StylePropertyChangeListener(styleProperty, selectBox));
                } else if (styleProperty.type == ListStyle.class) {
                    if (styleProperty.optional) {
                        label = new Label(styleProperty.name, getSkin());
                    } else {
                        label = new Label(styleProperty.name, getSkin(), "required");
                    }
                    table.add(label).padTop(20.0f).fill(false).expand(false, false);

                    table.row();
                    SelectBox<StyleData> selectBox = new SelectBox<>(getSkin());
                    selectBox.setItems(listStyles);
                    selectBox.addListener(getMain().getHandListener());
                    selectBox.getList().addListener(getMain().getHandListener());
                    
                    if (styleProperty.value != null) {
                        String name = ((String) styleProperty.value);
                        int index = 0;
                        for (StyleData styleData : listStyles) {
                            if (styleData.name.equals(name)) {
                                break;
                            } else {
                                index++;
                            }
                        }
                        if (index < listStyles.size) {
                            selectBox.setSelectedIndex(index);
                        }
                    }
                    table.add(selectBox);

                    selectBox.addListener(new StylePropertyChangeListener(styleProperty, selectBox));
                } else if (styleProperty.type == LabelStyle.class) {
                    if (styleProperty.optional) {
                        label = new Label(styleProperty.name, getSkin());
                    } else {
                        label = new Label(styleProperty.name, getSkin(), "required");
                    }
                    table.add(label).padTop(20.0f).fill(false).expand(false, false);

                    table.row();
                    SelectBox<StyleData> selectBox = new SelectBox<>(getSkin());
                    selectBox.setItems(labelStyles);
                    selectBox.addListener(getMain().getHandListener());
                    selectBox.getList().addListener(getMain().getHandListener());
                    
                    if (styleProperty.value != null) {
                        String name = ((String) styleProperty.value);
                        int index = 0;
                        for (StyleData styleData : labelStyles) {
                            if (styleData.name.equals(name)) {
                                break;
                            } else {
                                index++;
                            }
                        }
                        if (index < labelStyles.size) {
                            selectBox.setSelectedIndex(index);
                        }
                    }
                    
                    table.add(selectBox);

                    selectBox.addListener(new StylePropertyChangeListener(styleProperty, selectBox));
                }

                table.row();
            }
        } else if (customProperties != null) {
            for (CustomProperty styleProperty : customProperties) {
                if (styleProperty.getType() == PropertyType.COLOR) {
                    String value = "";
                    if (styleProperty.getValue() instanceof String) {
                        for (ColorData color : getMain().getJsonData().getColors()) {
                            if (color.getName().equals(styleProperty.getValue())) {
                                value = (String) styleProperty.getValue();
                                break;
                            }
                        }
                    }
                    BrowseField browseField = new BrowseField(value, styleProperty.getName(), getSkin(), "color");
                    browseField.addListener(getMain().getHandListener());
                    table.add(browseField).padTop(20.0f);

                    browseField.addListener(new CustomPropertyChangeListener(styleProperty, browseField));
                } else if (styleProperty.getType() == PropertyType.FONT) {
                    String value = "";
                    if (styleProperty.getValue() instanceof String) {
                        for (FontData font : getMain().getJsonData().getFonts()) {
                            if (font.getName().equals(styleProperty.getValue())) {
                                value = (String) styleProperty.getValue();
                                break;
                            }
                        }
                        
                        for (FreeTypeFontData font : getMain().getJsonData().getFreeTypeFonts()) {
                            if (font.name.equals(styleProperty.getValue())) {
                                value = (String) styleProperty.getValue();
                            }
                        }
                    }
                    BrowseField browseField = new BrowseField(value, styleProperty.getName(), getSkin(), "font");
                    browseField.addListener(getMain().getHandListener());
                    table.add(browseField).padTop(20.0f);

                    browseField.addListener(new CustomPropertyChangeListener(styleProperty, browseField));
                } else if (styleProperty.getType() == PropertyType.DRAWABLE) {
                    String value = "";
                    if (styleProperty.getValue() instanceof String) {
                        for (DrawableData drawable : getMain().getAtlasData().getDrawables()) {
                            if (drawable.name.equals(styleProperty.getValue())) {
                                value = (String) styleProperty.getValue();
                                break;
                            }
                        }
                    }
                    BrowseField browseField = new BrowseField(value, styleProperty.getName(), getSkin(), "drawable");
                    browseField.addListener(getMain().getHandListener());
                    table.add(browseField).padTop(20.0f);

                    browseField.addListener(new CustomPropertyChangeListener(styleProperty, browseField));
                } else if (styleProperty.getType() == PropertyType.NUMBER) {
                    label = new Label(styleProperty.getName(), getSkin());
                    table.add(label).padTop(20.0f).fill(false).expand(false, false);

                    table.row();
                    if (styleProperty.getValue() instanceof Float) {
                        styleProperty.setValue((double) (float) styleProperty.getValue());
                    }
                    Double value = 0.0;
                    if (styleProperty.getValue() instanceof Double) {
                        value = (Double) styleProperty.getValue();
                    }
                    Spinner spinner = new Spinner(value, 1.0, false, Spinner.Orientation.HORIZONTAL, getSkin());
                    spinner.setRound(false);
                    spinner.getTextField().addListener(getMain().getIbeamListener());
                    spinner.getButtonMinus().addListener(getMain().getHandListener());
                    spinner.getButtonPlus().addListener(getMain().getHandListener());
                    table.add(spinner);

                    spinner.addListener(new CustomPropertyChangeListener(styleProperty, spinner));
                } else if (styleProperty.getType() == PropertyType.TEXT || styleProperty.getType() == PropertyType.RAW_TEXT) {
                    label = new Label(styleProperty.getName(), getSkin());
                    table.add(label).padTop(20.0f).fill(false).expand(false, false);
                    
                    table.row();
                    String value = "";
                    if (styleProperty.getValue() instanceof String) {
                        value = (String) styleProperty.getValue();
                    }
                    TextField textField = new TextField(value, getSkin());
                    textField.setAlignment(Align.center);
                    textField.addListener(getMain().getIbeamListener());
                    table.add(textField);
                    
                    textField.addListener(new CustomPropertyChangeListener(styleProperty, textField));
                } else if (styleProperty.getType() == PropertyType.BOOL) {
                    label = new Label(styleProperty.getName(), getSkin());
                    table.add(label).padTop(20.0f).fill(false).expand(false, false);
                    
                    table.row();
                    Button button = new Button(getSkin(), "switch");
                    boolean value = false;
                    if (styleProperty.getValue() instanceof Boolean) {
                        value = (boolean) styleProperty.getValue();
                    }
                    button.setChecked(value);
                    table.add(button).fill(false);
                    
                    button.addListener(new CustomPropertyChangeListener(styleProperty, button));
                    button.addListener(getMain().getHandListener());
                } else if (styleProperty.getType() == PropertyType.STYLE) {
                    String value = "";
                    if (styleProperty.getValue() instanceof String) {
                        value = (String) styleProperty.getValue();
                    }
                    BrowseField browseField = new BrowseField(value, styleProperty.getName(), getSkin(), "style");
                    browseField.addListener(getMain().getHandListener());
                    table.add(browseField).padTop(20.0f);

                    browseField.addListener(new CustomPropertyChangeListener(styleProperty, browseField));
                }
                
                Button duplicateButton = new Button(getSkin(), "duplicate");
                table.add(duplicateButton).fill(false).expand(false, false).pad(0).bottom();
                duplicateButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event,
                            Actor actor) {
                        fire(new CustomPropertyEvent(styleProperty, duplicateButton, CustomPropertyEnum.DUPLICATE));
                    }
                });
                
                TextTooltip toolTip = new TextTooltip("Duplicate Style Property", getMain().getTooltipManager(), getSkin());
                duplicateButton.addListener(toolTip);
                duplicateButton.addListener(getMain().getHandListener());
                
                Button deleteButton = new Button(getSkin(), "delete");
                table.add(deleteButton).fill(false).expand(false, false).pad(0).bottom();
                deleteButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event,
                            Actor actor) {
                        fire(new CustomPropertyEvent(styleProperty, duplicateButton, CustomPropertyEnum.DELETE));
                    }
                });
                
                toolTip = new TextTooltip("Delete Style Property", getMain().getTooltipManager(), getSkin());
                deleteButton.addListener(toolTip);
                deleteButton.addListener(getMain().getHandListener());
                
                Button renameButton = new Button(getSkin(), "settings");
                table.add(renameButton).fill(false).expand(false, false).pad(0).bottom();
                renameButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event,
                            Actor actor) {
                        fire(new CustomPropertyEvent(styleProperty, duplicateButton, CustomPropertyEnum.RENAME));
                    }
                });
                
                toolTip = new TextTooltip("Rename Style Property", getMain().getTooltipManager(), getSkin());
                renameButton.addListener(toolTip);
                renameButton.addListener(getMain().getHandListener());
                
                table.row();
            }
            
            left.row();
            table = new Table();
            left.add(table).right().padBottom(10.0f);

            Button button = new Button(getSkin(), "new");
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    fire(new CustomPropertyEvent(null, null, CustomPropertyEnum.NEW));
                }
            });
            table.add(button);
            
            TextTooltip toolTip = new TextTooltip("New Style Property", getMain().getTooltipManager(), getSkin());
            button.addListener(toolTip);
            button.addListener(getMain().getHandListener());
        }
    }

    private class StylePropertyChangeListener extends ChangeListener {

        private final StyleProperty styleProp;
        private final Actor styleActor;

        public StylePropertyChangeListener(StyleProperty styleProp, Actor styleActor) {
            this.styleProp = styleProp;
            this.styleActor = styleActor;
        }

        @Override
        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
            fire(new StylePropertyEvent(styleProp, styleActor));
        }
    }
    
    private class StyleParentChangeListener extends ChangeListener {
        private final StyleData style;
        private final SelectBox<String> selectBox;
        
        public StyleParentChangeListener(StyleData style, SelectBox<String> selectBox) {
            this.style = style;
            this.selectBox = selectBox;
        }

        @Override
        public void changed(ChangeEvent event, Actor actor) {
            fire(new StyleParentEvent(style, selectBox));
        }
    }
    
    private class CustomPropertyChangeListener extends ChangeListener {

        private final CustomProperty styleProp;
        private final Actor styleActor;

        public CustomPropertyChangeListener(CustomProperty styleProp, Actor styleActor) {
            this.styleProp = styleProp;
            this.styleActor = styleActor;
        }

        @Override
        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
            fire(new CustomPropertyEvent(styleProp, styleActor, CustomPropertyEnum.CHANGE_VALUE));
        }
    }

    private void addPreviewPreviewPropertiesSplit(final Table right, InputListener scrollPaneListener) {
        Table bottom = new Table();
        bottom.setTouchable(Touchable.enabled);

        addPreviewProperties(bottom, scrollPaneListener);
        
        Table top = new Table();
        top.setTouchable(Touchable.enabled);

        addPreview(top, scrollPaneListener);

        SplitPane splitPane = new SplitPane(top, bottom, true, getSkin());
        right.add(splitPane).grow();

        splitPane.addListener(getMain().getVerticalResizeArrowListener());
    }

    private void addPreview(Table top, InputListener scrollPaneListener) {
        Label label = new Label("Preview", getSkin(), "title");
        top.add(label);

        top.row();
        previewTable = new Table(getSkin());
        previewTable.setBackground("white");
        ScrollPane scrollPane = new ScrollPane(previewTable, getSkin());
        scrollPane.setFadeScrollBars(false);
        scrollPane.setFlickScroll(false);
        scrollPane.addListener(scrollPaneListener);
        top.add(scrollPane).grow().padTop(10.0f).padBottom(10.0f);

        refreshPreview();
    }
    
    private void addPreviewProperties(Table bottom, InputListener scrollPaneListener) {
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
    
    @SuppressWarnings("rawtypes")
	public void refreshPreviewProperties() {
        if (previewPropertiesTable != null) {
            previewPropertiesTable.clear();
            getPreviewProperties().clear();

            Table t = new Table();
            previewPropertiesTable.add(t).grow();
            t.defaults().pad(3.0f);
            
            if (previewBgColor == null) {
                previewBgColor.set(Color.WHITE);
            }
            getPreviewProperties().put("bgcolor", previewBgColor);

            if (classSelectBox.getSelectedIndex() >= 0 && classSelectBox.getSelectedIndex() < Main.BASIC_CLASSES.length) {
                t.add(new Label("Stage Color: ", getSkin())).right();
                BrowseField browseField = new BrowseField(null, null, getSkin(), "color");
                browseField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        getMain().getDialogFactory().showDialogColorPicker((Color) getPreviewProperties().get("bgcolor"), new DialogColorPicker.ColorListener() {
                            @Override
                            public void selected(Color color) {
                                if (color != null) {
                                    browseField.getTextButton().setText((int) (color.r * 255) + "," + (int) (color.g * 255) + "," + (int) (color.b * 255) + "," + (int) (color.a * 255));
                                    getPreviewProperties().put("bgcolor", color);
                                    previewBgColor.set(color);
                                    refreshPreview();
                                }
                            }
                        });
                    }
                });
                
                browseField.addListener(getMain().getHandListener());
                t.add(browseField).growX();
                browseField.getTextButton().setText((int) (previewBgColor.r * 255) + "," + (int) (previewBgColor.g * 255) + "," + (int) (previewBgColor.b * 255) + "," + (int) (previewBgColor.a * 255));

                t.row();
                t.add(new Label("Size: ", getSkin())).right();

                previewSizeSelectBox = new SelectBox<>(getSkin());
                previewSizeSelectBox.setItems(DEFAULT_SIZES);
                previewSizeSelectBox.setSelectedIndex(1);
                previewSizeSelectBox.addListener(getMain().getHandListener());
                previewSizeSelectBox.getList().addListener(getMain().getHandListener());
                t.add(previewSizeSelectBox).growX().minWidth(200.0f);
                Class clazz = Main.BASIC_CLASSES[classSelectBox.getSelectedIndex()];
                //TODO: nici
                if (clazz.equals(Button.class)) {
                    t.row();
                    t.add(new Label("Disabled: ", getSkin())).right();
                    ImageTextButton disabledCheckBox = new ImageTextButton("", getSkin(), "switch");
                    disabledCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("disabled", disabledCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    disabledCheckBox.addListener(getMain().getHandListener());
                    getPreviewProperties().put("disabled", disabledCheckBox.isChecked());
                    t.add(disabledCheckBox).left();
                } else if (clazz.equals(CheckBox.class)) {
                    t.row();
                    t.add(new Label("Disabled: ", getSkin())).right();
                    ImageTextButton disabledCheckBox = new ImageTextButton("", getSkin(), "switch");
                    disabledCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("disabled", disabledCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    disabledCheckBox.addListener(getMain().getHandListener());
                    getPreviewProperties().put("disabled", disabledCheckBox.isChecked());
                    t.add(disabledCheckBox).left();

                    t.row();
                    t.add(new Label("Text: ", getSkin())).right();
                    TextField previewTextField = new TextField(TEXT_SAMPLE, getSkin());
                    previewTextField.setFocusTraversal(false);
                    previewTextField.addListener(getMain().getIbeamListener());
                    previewTextField.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("text", previewTextField.getText());
                            refreshPreview();
                        }
                    });
                    getPreviewProperties().put("text", previewTextField.getText());
                    t.add(previewTextField).growX();

                } else if (clazz.equals(ImageButton.class)) {
                    t.row();
                    t.add(new Label("Disabled: ", getSkin())).right();
                    ImageTextButton disabledCheckBox = new ImageTextButton("", getSkin(), "switch");
                    disabledCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("disabled", disabledCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    disabledCheckBox.addListener(getMain().getHandListener());
                    getPreviewProperties().put("disabled", disabledCheckBox.isChecked());
                    t.add(disabledCheckBox).left();
                } else if (clazz.equals(ImageTextButton.class)) {
                    t.row();
                    t.add(new Label("Disabled: ", getSkin())).right();
                    ImageTextButton disabledCheckBox = new ImageTextButton("", getSkin(), "switch");
                    disabledCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("disabled", disabledCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    disabledCheckBox.addListener(getMain().getHandListener());
                    getPreviewProperties().put("disabled", disabledCheckBox.isChecked());
                    t.add(disabledCheckBox).left();

                    t.row();
                    t.add(new Label("Text: ", getSkin())).right();
                    TextField previewTextField = new TextField(TEXT_SAMPLE, getSkin());
                    previewTextField.setFocusTraversal(false);
                    previewTextField.addListener(getMain().getIbeamListener());
                    previewTextField.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("text", previewTextField.getText());
                            refreshPreview();
                        }
                    });
                    getPreviewProperties().put("text", previewTextField.getText());
                    t.add(previewTextField).growX();

                } else if (clazz.equals(Label.class)) {
                    t.row();
                    t.add(new Label("Text: ", getSkin())).right();
                    TextField previewTextField = new TextField(TEXT_SAMPLE, getSkin());
                    previewTextField.setFocusTraversal(false);
                    previewTextField.addListener(getMain().getIbeamListener());
                    previewTextField.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("text", previewTextField.getText());
                            refreshPreview();
                        }
                    });
                    getPreviewProperties().put("text", previewTextField.getText());
                    t.add(previewTextField).growX();

                } else if (clazz.equals(List.class)) {
                    t.row();
                    t.add(new Label("List Items: ", getSkin())).right();
                    TextArea listItemsTextArea = new TextArea("Lorem ipsum\ndolor sit\namet, consectetur", getSkin());
                    listItemsTextArea.setFocusTraversal(false);
                    listItemsTextArea.setPrefRows(3);
                    listItemsTextArea.addListener(getMain().getIbeamListener());
                    listItemsTextArea.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("text", listItemsTextArea.getText());
                            refreshPreview();
                        }
                    });
                    getPreviewProperties().put("text", listItemsTextArea.getText());
                    t.add(listItemsTextArea).growX();

                } else if (clazz.equals(ProgressBar.class)) {
                    t.row();
                    t.add(new Label("Disabled: ", getSkin())).right();
                    ImageTextButton disabledCheckBox = new ImageTextButton("", getSkin(), "switch");
                    disabledCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("disabled", disabledCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    disabledCheckBox.addListener(getMain().getHandListener());
                    getPreviewProperties().put("disabled", disabledCheckBox.isChecked());
                    t.add(disabledCheckBox).left();

                    t.row();
                    t.add(new Label("Value: ", getSkin())).right();
                    Spinner valueSpinner = new Spinner(0.0f, 1.0f, false, Spinner.Orientation.HORIZONTAL, getSkin());
                    valueSpinner.getTextField().setFocusTraversal(false);
                    valueSpinner.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("value", valueSpinner.getValue());
                            refreshPreview();
                        }
                    });
                    valueSpinner.getButtonMinus().addListener(getMain().getHandListener());
                    valueSpinner.getButtonPlus().addListener(getMain().getHandListener());
                    valueSpinner.getTextField().addListener(getMain().getIbeamListener());
                    getPreviewProperties().put("value", valueSpinner.getValue());
                    t.add(valueSpinner).growX();

                    t.row();
                    t.add(new Label("Minimum: ", getSkin())).right();
                    Spinner minimumSpinner = new Spinner(0.0f, 1.0f, false, Spinner.Orientation.HORIZONTAL, getSkin());
                    minimumSpinner.getTextField().setFocusTraversal(false);
                    minimumSpinner.getTextField().addListener(getMain().getIbeamListener());
                    minimumSpinner.getButtonMinus().addListener(getMain().getHandListener());
                    minimumSpinner.getButtonPlus().addListener(getMain().getHandListener());
                    getPreviewProperties().put("minimum", minimumSpinner.getValue());
                    t.add(minimumSpinner).growX();

                    t.row();
                    t.add(new Label("Maximum: ", getSkin())).right();
                    Spinner maximumSpinner = new Spinner(100.0f, 1.0f, false, Spinner.Orientation.HORIZONTAL, getSkin());
                    maximumSpinner.getTextField().setFocusTraversal(false);
                    maximumSpinner.getTextField().addListener(getMain().getIbeamListener());
                    maximumSpinner.getButtonMinus().addListener(getMain().getHandListener());
                    maximumSpinner.getButtonPlus().addListener(getMain().getHandListener());
                    getPreviewProperties().put("maximum", maximumSpinner.getValue());
                    t.add(maximumSpinner).growX();

                    minimumSpinner.setMaximum(maximumSpinner.getValue());
                    maximumSpinner.setMinimum(minimumSpinner.getValue());
                    minimumSpinner.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("minimum", minimumSpinner.getValue());
                            maximumSpinner.setMinimum(minimumSpinner.getValue());
                            refreshPreview();
                        }
                    });
                    maximumSpinner.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("maximum", maximumSpinner.getValue());
                            minimumSpinner.setMaximum(maximumSpinner.getValue());
                            refreshPreview();
                        }
                    });
                    
                    t.row();
                    t.add(new Label("Increment: ", getSkin())).right();
                    Spinner incrementSpinner = new Spinner(1.0f, 1.0f, false, Spinner.Orientation.HORIZONTAL, getSkin());
                    incrementSpinner.getTextField().setFocusTraversal(false);
                    incrementSpinner.setMinimum(1);
                    incrementSpinner.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("increment", incrementSpinner.getValue());
                            refreshPreview();
                        }
                    });
                    incrementSpinner.getTextField().addListener(getMain().getIbeamListener());
                    incrementSpinner.getButtonMinus().addListener(getMain().getHandListener());
                    incrementSpinner.getButtonPlus().addListener(getMain().getHandListener());
                    getPreviewProperties().put("increment", incrementSpinner.getValue());
                    t.add(incrementSpinner).growX();

                    t.row();
                    t.add(new Label("Orientation: ", getSkin())).right();
                    SelectBox<String> selectBox = new SelectBox<>(getSkin());
                    selectBox.setItems(new String[]{"Horizontal", "Vertical"});
                    if (getSelectedStyle().name.contains("vert")) {
                        getPreviewProperties().put("orientation", true);
                        selectBox.setSelectedIndex(1);
                    } else {
                        getPreviewProperties().put("orientation", false);
                    }
                    selectBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            if (selectBox.getSelectedIndex() == 0) {
                                getPreviewProperties().put("orientation", false);
                            } else {
                                getPreviewProperties().put("orientation", true);
                            }
                            refreshPreview();
                        }
                    });
                    selectBox.addListener(getMain().getHandListener());
                    selectBox.getList().addListener(getMain().getHandListener());
                    t.add(selectBox).growX();

                } else if (clazz.equals(ScrollPane.class)) {
                    t.row();
                    t.add(new Label("Scrollbars On Top: ", getSkin())).right();
                    ImageTextButton onTopCheckBox = new ImageTextButton("", getSkin(), "switch");
                    onTopCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("scrollbarsOnTop", onTopCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    t.add(onTopCheckBox).left();
                    onTopCheckBox.addListener(getMain().getHandListener());
                    getPreviewProperties().put("scrollbarsOnTop", onTopCheckBox.isChecked());

                    t.row();
                    t.add(new Label("H ScrollBar Position: ", getSkin())).right();
                    SelectBox<String> hScrollPosBox = new SelectBox<>(getSkin());
                    hScrollPosBox.setItems(new String[]{"Top", "Bottom"});
                    hScrollPosBox.setSelectedIndex(1);
                    hScrollPosBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            if (hScrollPosBox.getSelectedIndex() == 0) {
                                getPreviewProperties().put("hScrollBarPosition", false);
                            } else {
                                getPreviewProperties().put("hScrollBarPosition", true);
                            }
                            refreshPreview();
                        }
                    });
                    t.add(hScrollPosBox).growX();
                    hScrollPosBox.addListener(getMain().getHandListener());
                    hScrollPosBox.getList().addListener(getMain().getHandListener());
                    getPreviewProperties().put("hScrollBarPosition", true);

                    t.row();
                    t.add(new Label("V ScrollBar Position: ", getSkin())).right();
                    SelectBox<String> vScrollPosBox = new SelectBox<>(getSkin());
                    vScrollPosBox.setItems(new String[]{"Left", "Right"});
                    vScrollPosBox.setSelectedIndex(1);
                    vScrollPosBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            if (vScrollPosBox.getSelectedIndex() == 0) {
                                getPreviewProperties().put("vScrollBarPosition", false);
                            } else {
                                getPreviewProperties().put("vScrollBarPosition", true);
                            }
                            refreshPreview();
                        }
                    });
                    t.add(vScrollPosBox).growX();
                    vScrollPosBox.addListener(getMain().getHandListener());
                    vScrollPosBox.getList().addListener(getMain().getHandListener());
                    getPreviewProperties().put("vScrollBarPosition", true);

                    t.row();
                    t.add(new Label("H Scrolling Disabled: ", getSkin())).right();
                    ImageTextButton hScrollCheckBox = new ImageTextButton("", getSkin(), "switch");
                    hScrollCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("hScrollDisabled", hScrollCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    t.add(hScrollCheckBox).left();
                    hScrollCheckBox.addListener(getMain().getHandListener());
                    getPreviewProperties().put("hScrollDisabled", hScrollCheckBox.isChecked());

                    t.row();
                    t.add(new Label("V Scrolling Disabled: ", getSkin())).right();
                    ImageTextButton vScrollCheckBox = new ImageTextButton("", getSkin(), "switch");
                    vScrollCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("vScrollDisabled", vScrollCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    t.add(vScrollCheckBox).left();
                    vScrollCheckBox.addListener(getMain().getHandListener());
                    getPreviewProperties().put("vScrollDisabled", vScrollCheckBox.isChecked());

                    t.row();
                    t.add(new Label("Force H Scroll: ", getSkin())).right();
                    ImageTextButton forceHScrollCheckBox = new ImageTextButton("", getSkin(), "switch");
                    forceHScrollCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("forceHscroll", forceHScrollCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    t.add(forceHScrollCheckBox).left();
                    forceHScrollCheckBox.addListener(getMain().getHandListener());
                    getPreviewProperties().put("forceHscroll", forceHScrollCheckBox.isChecked());

                    t.row();
                    t.add(new Label("Force V Scroll: ", getSkin())).right();
                    ImageTextButton forceVScrollCheckBox = new ImageTextButton("", getSkin(), "switch");
                    forceVScrollCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("forceVscroll", forceVScrollCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    t.add(forceVScrollCheckBox).left();
                    forceVScrollCheckBox.addListener(getMain().getHandListener());
                    getPreviewProperties().put("forceVscroll", forceVScrollCheckBox.isChecked());

                    t.row();
                    t.add(new Label("Variable Size Knobs: ", getSkin())).right();
                    ImageTextButton variableSizeKnobsCheckBox = new ImageTextButton("", getSkin(), "switch");
                    variableSizeKnobsCheckBox.setChecked(true);
                    variableSizeKnobsCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("variableSizeKnobs", variableSizeKnobsCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    t.add(variableSizeKnobsCheckBox).left();
                    variableSizeKnobsCheckBox.addListener(getMain().getHandListener());
                    getPreviewProperties().put("variableSizeKnobs", variableSizeKnobsCheckBox.isChecked());

                    t.row();
                    t.add(new Label("H Overscroll: ", getSkin())).right();
                    ImageTextButton hOverscrollCheckBox = new ImageTextButton("", getSkin(), "switch");
                    hOverscrollCheckBox.setChecked(true);
                    hOverscrollCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("hOverscroll", hOverscrollCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    t.add(hOverscrollCheckBox).left();
                    hOverscrollCheckBox.addListener(getMain().getHandListener());
                    getPreviewProperties().put("hOverscroll", hOverscrollCheckBox.isChecked());

                    t.row();
                    t.add(new Label("V Overscroll: ", getSkin())).right();
                    ImageTextButton vOverscrollCheckBox = new ImageTextButton("", getSkin(), "switch");
                    vOverscrollCheckBox.setChecked(true);
                    vOverscrollCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("vOverscroll", vOverscrollCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    t.add(vOverscrollCheckBox).left();
                    vOverscrollCheckBox.addListener(getMain().getHandListener());
                    getPreviewProperties().put("vOverscroll", vOverscrollCheckBox.isChecked());

                    t.row();
                    t.add(new Label("Fade Scroll Bars: ", getSkin())).right();
                    ImageTextButton fadeScrollCheckBox = new ImageTextButton("", getSkin(), "switch");
                    fadeScrollCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("fadeScroll", fadeScrollCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    t.add(fadeScrollCheckBox).left();
                    fadeScrollCheckBox.addListener(getMain().getHandListener());
                    getPreviewProperties().put("fadeScroll", fadeScrollCheckBox.isChecked());

                    t.row();
                    t.add(new Label("Smooth Scrolling: ", getSkin())).right();
                    ImageTextButton smoothScrollCheckBox = new ImageTextButton("", getSkin(), "switch");
                    smoothScrollCheckBox.setChecked(true);
                    smoothScrollCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("smoothScroll", smoothScrollCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    t.add(smoothScrollCheckBox).left();
                    smoothScrollCheckBox.addListener(getMain().getHandListener());
                    getPreviewProperties().put("smoothScroll", smoothScrollCheckBox.isChecked());

                    t.row();
                    t.add(new Label("Flick Scroll: ", getSkin())).right();
                    ImageTextButton flickScrollCheckBox = new ImageTextButton("", getSkin(), "switch");
                    flickScrollCheckBox.setChecked(true);
                    flickScrollCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("flickScroll", flickScrollCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    t.add(flickScrollCheckBox).left();
                    flickScrollCheckBox.addListener(getMain().getHandListener());
                    getPreviewProperties().put("flickScroll", flickScrollCheckBox.isChecked());

                    t.row();
                    t.add(new Label("Clamp: ", getSkin())).right();
                    ImageTextButton clampCheckBox = new ImageTextButton("", getSkin(), "switch");
                    clampCheckBox.setChecked(true);
                    clampCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("clamp", clampCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    t.add(clampCheckBox).left();
                    clampCheckBox.addListener(getMain().getHandListener());
                    getPreviewProperties().put("clamp", clampCheckBox.isChecked());

                    t.row();
                    t.add(new Label("Sample Text: ", getSkin())).right();
                    TextArea previewTextArea = new TextArea(PARAGRAPH_SAMPLE_EXT, getSkin());
                    previewTextArea.setFocusTraversal(false);
                    previewTextArea.setPrefRows(5);
                    previewTextArea.addListener(getMain().getIbeamListener());
                    previewTextArea.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("text", previewTextArea.getText());
                            refreshPreview();
                        }
                    });
                    getPreviewProperties().put("text", previewTextArea.getText());
                    t.add(previewTextArea).growX();

                    previewSizeSelectBox.setSelectedIndex(2);
                } else if (clazz.equals(SelectBox.class)) {
                    t.row();
                    t.add(new Label("Disabled: ", getSkin())).right();
                    ImageTextButton disabledCheckBox = new ImageTextButton("", getSkin(), "switch");
                    disabledCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("disabled", disabledCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    getPreviewProperties().put("disabled", disabledCheckBox.isChecked());
                    disabledCheckBox.addListener(getMain().getHandListener());
                    t.add(disabledCheckBox).left();

                    t.row();
                    t.add(new Label("Max List Count: ", getSkin())).right();
                    Spinner spinner = new Spinner(3, 1, true, Spinner.Orientation.HORIZONTAL, getSkin());
                    spinner.getTextField().setFocusTraversal(false);
                    spinner.setMinimum(1);
                    spinner.getTextField().addListener(getMain().getIbeamListener());
                    spinner.getButtonMinus().addListener(getMain().getHandListener());
                    spinner.getButtonPlus().addListener(getMain().getHandListener());
                    t.add(spinner).growX();
                    spinner.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("max-list-count", spinner.getValueAsInt());
                            refreshPreview();
                        }
                    });
                    getPreviewProperties().put("max-list-count", spinner.getValueAsInt());

                    t.row();
                    t.add(new Label("List Items: ", getSkin())).right();
                    TextArea listItemsTextArea = new TextArea("Lorem ipsum\ndolor sit\namet, consectetur", getSkin());
                    listItemsTextArea.setFocusTraversal(false);
                    listItemsTextArea.setPrefRows(3);
                    listItemsTextArea.addListener(getMain().getIbeamListener());
                    listItemsTextArea.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("text", listItemsTextArea.getText());
                            refreshPreview();
                        }
                    });
                    getPreviewProperties().put("text", listItemsTextArea.getText());
                    t.add(listItemsTextArea).growX();
                    
                    t.row();
                    t.add(new Label("Alignment: ", getSkin())).right();
                    SelectBox<String> selectBox = new SelectBox<String>(getSkin());
                    selectBox.setItems("left", "center", "right");
                    selectBox.addListener(getMain().getHandListener());
                    selectBox.getList().addListener(getMain().getHandListener());
                    selectBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("alignment", (new int[] {Align.left, Align.center, Align.right})[selectBox.getSelectedIndex()]);
                            refreshPreview();
                        }
                    });
                    t.add(selectBox).growX();
                    getPreviewProperties().put("alignment", (new int[] {Align.left, Align.center, Align.right})[selectBox.getSelectedIndex()]);
                } else if (clazz.equals(Slider.class)) {
                    t.row();
                    t.add(new Label("Disabled: ", getSkin())).right();
                    ImageTextButton disabledCheckBox = new ImageTextButton("", getSkin(), "switch");
                    disabledCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("disabled", disabledCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    getPreviewProperties().put("disabled", disabledCheckBox.isChecked());
                    disabledCheckBox.addListener(getMain().getHandListener());
                    t.add(disabledCheckBox).left();

                    t.row();
                    t.add(new Label("Minimum: ", getSkin())).right();
                    Spinner minimumSpinner = new Spinner(0.0f, 1.0f, false, Spinner.Orientation.HORIZONTAL, getSkin());
                    minimumSpinner.getTextField().setFocusTraversal(false);
                    minimumSpinner.getTextField().addListener(getMain().getIbeamListener());
                    t.add(minimumSpinner).growX();
                    minimumSpinner.getButtonMinus().addListener(getMain().getHandListener());
                    minimumSpinner.getButtonPlus().addListener(getMain().getHandListener());
                    getPreviewProperties().put("minimum", minimumSpinner.getValue());

                    t.row();
                    t.add(new Label("Maximum: ", getSkin())).right();
                    Spinner maximumSpinner = new Spinner(100.0f, 1.0f, false, Spinner.Orientation.HORIZONTAL, getSkin());
                    maximumSpinner.getTextField().setFocusTraversal(false);
                    maximumSpinner.getTextField().addListener(getMain().getIbeamListener());
                    t.add(maximumSpinner).growX();
                    maximumSpinner.getButtonMinus().addListener(getMain().getHandListener());
                    maximumSpinner.getButtonPlus().addListener(getMain().getHandListener());
                    getPreviewProperties().put("maximum", maximumSpinner.getValue());
                    
                    minimumSpinner.setMaximum(maximumSpinner.getValue());
                    maximumSpinner.setMinimum(minimumSpinner.getValue());
                    minimumSpinner.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("minimum", minimumSpinner.getValue());
                            maximumSpinner.setMinimum(minimumSpinner.getValue());
                            refreshPreview();
                        }
                    });
                    maximumSpinner.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("maximum", maximumSpinner.getValue());
                            minimumSpinner.setMaximum(maximumSpinner.getValue());
                            refreshPreview();
                        }
                    });

                    t.row();
                    t.add(new Label("Increment: ", getSkin())).right();
                    Spinner incrementSpinner = new Spinner(1.0f, 1.0f, false, Spinner.Orientation.HORIZONTAL, getSkin());
                    incrementSpinner.getTextField().setFocusTraversal(false);
                    incrementSpinner.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("increment", incrementSpinner.getValue());
                            refreshPreview();
                        }
                    });
                    incrementSpinner.getTextField().addListener(getMain().getIbeamListener());
                    t.add(incrementSpinner).growX();
                    incrementSpinner.getButtonMinus().addListener(getMain().getHandListener());
                    incrementSpinner.getButtonPlus().addListener(getMain().getHandListener());
                    incrementSpinner.setMinimum(1.0f);
                    getPreviewProperties().put("increment", incrementSpinner.getValue());

                    t.row();
                    t.add(new Label("Orientation: ", getSkin())).right();
                    SelectBox<String> selectBox = new SelectBox<>(getSkin());
                    selectBox.setItems(new String[]{"Horizontal", "Vertical"});
                    if (getSelectedStyle().name.contains("vert")) {
                        getPreviewProperties().put("orientation", true);
                        selectBox.setSelectedIndex(1);
                    } else {
                        getPreviewProperties().put("orientation", false);
                    }
                    selectBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            if (selectBox.getSelectedIndex() == 0) {
                                getPreviewProperties().put("orientation", false);
                            } else {
                                getPreviewProperties().put("orientation", true);
                            }
                            refreshPreview();
                        }
                    });
                    selectBox.addListener(getMain().getHandListener());
                    selectBox.getList().addListener(getMain().getHandListener());
                    t.add(selectBox).growX();
                } else if (clazz.equals(SplitPane.class)) {
                    t.row();
                    t.add(new Label("Orientation: ", getSkin())).right();
                    SelectBox<String> selectBox = new SelectBox<>(getSkin());
                    selectBox.setItems(new String[]{"Horizontal", "Vertical"});
                    if (getSelectedStyle().name.contains("vert")) {
                        getPreviewProperties().put("orientation", true);
                        selectBox.setSelectedIndex(1);
                    } else {
                        getPreviewProperties().put("orientation", false);
                    }
                    selectBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            if (selectBox.getSelectedIndex() == 0) {
                                getPreviewProperties().put("orientation", false);
                            } else {
                                getPreviewProperties().put("orientation", true);
                            }
                            refreshPreview();
                        }
                    });
                    selectBox.addListener(getMain().getHandListener());
                    selectBox.getList().addListener(getMain().getHandListener());
                    t.add(selectBox).growX();

                    t.row();
                    t.add(new Label("Sample Text: ", getSkin())).right();
                    TextArea textArea = new TextArea(PARAGRAPH_SAMPLE, getSkin());
                    textArea.setFocusTraversal(false);
                    textArea.setPrefRows(5);
                    textArea.addListener(getMain().getIbeamListener());
                    textArea.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("text", textArea.getText());
                            refreshPreview();
                        }
                    });
                    getPreviewProperties().put("text", textArea.getText());
                    t.add(textArea).growX();

                    previewSizeSelectBox.setSelectedIndex(2);
                } else if (clazz.equals(TextButton.class)) {
                    t.row();
                    t.add(new Label("Disabled: ", getSkin())).right();
                    ImageTextButton disabledCheckBox = new ImageTextButton("", getSkin(), "switch");
                    disabledCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("disabled", disabledCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    getPreviewProperties().put("disabled", disabledCheckBox.isChecked());
                    disabledCheckBox.addListener(getMain().getHandListener());
                    t.add(disabledCheckBox).left();

                    t.row();
                    t.add(new Label("Text: ", getSkin())).right();
                    TextField previewTextField = new TextField(TEXT_SAMPLE, getSkin());
                    previewTextField.setFocusTraversal(false);
                    previewTextField.addListener(getMain().getIbeamListener());
                    previewTextField.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("text", previewTextField.getText());
                            refreshPreview();
                        }
                    });
                    getPreviewProperties().put("text", previewTextField.getText());
                    t.add(previewTextField).growX();

                } else if (clazz.equals(TextField.class)) {
                    t.row();
                    t.add(new Label("Disabled: ", getSkin())).right();
                    ImageTextButton disabledCheckBox = new ImageTextButton("", getSkin(), "switch");
                    disabledCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("disabled", disabledCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    getPreviewProperties().put("disabled", disabledCheckBox.isChecked());
                    disabledCheckBox.addListener(getMain().getHandListener());
                    t.add(disabledCheckBox).left();

                    t.row();
                    t.add(new Label("Password Mode: ", getSkin())).right();
                    ImageTextButton checkBox = new ImageTextButton("", getSkin(), "switch");
                    checkBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("passwordMode", checkBox.isChecked());
                            refreshPreview();
                        }
                    });
                    t.add(checkBox).left();
                    checkBox.addListener(getMain().getHandListener());
                    getPreviewProperties().put("passwordMode", checkBox.isChecked());

                    t.row();
                    t.add(new Label("Password Character: ", getSkin()));
                    TextField pcTextField = new TextField("*", getSkin());
                    pcTextField.setFocusTraversal(false);
                    pcTextField.addListener(getMain().getIbeamListener());
                    pcTextField.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("password", pcTextField.getText());
                            refreshPreview();
                        }
                    });
                    getPreviewProperties().put("password", pcTextField.getText());
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
                                    getPreviewProperties().put("alignment", Align.left);
                                    break;
                                case 1:
                                    getPreviewProperties().put("alignment", Align.center);
                                    break;
                                case 2:
                                    getPreviewProperties().put("alignment", Align.right);
                                    break;
                            }
                            refreshPreview();
                        }
                    });
                    t.add(selectBox).growX();
                    selectBox.addListener(getMain().getHandListener());
                    selectBox.getList().addListener(getMain().getHandListener());
                    getPreviewProperties().put("alignment", Align.left);

                    t.row();
                    t.add(new Label("Text: ", getSkin())).right();
                    TextField previewTextField = new TextField(TEXT_SAMPLE, getSkin());
                    previewTextField.setFocusTraversal(false);
                    previewTextField.addListener(getMain().getIbeamListener());
                    previewTextField.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("text", previewTextField.getText());
                            refreshPreview();
                        }
                    });
                    getPreviewProperties().put("text", previewTextField.getText());
                    t.add(previewTextField).growX();

                    t.row();
                    t.add(new Label("Message Text: ", getSkin())).right();
                    TextField messageTextField = new TextField(TEXT_SAMPLE, getSkin());
                    messageTextField.setFocusTraversal(false);
                    messageTextField.addListener(getMain().getIbeamListener());
                    messageTextField.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("message", messageTextField.getText());
                            refreshPreview();
                        }
                    });
                    getPreviewProperties().put("message", messageTextField.getText());
                    t.add(messageTextField).growX();

                } else if (clazz.equals(TextTooltip.class)) {
                    t.row();
                    t.add(new Label("Text: ", getSkin())).right();
                    TextField previewTextField = new TextField(TEXT_SAMPLE, getSkin());
                    previewTextField.setFocusTraversal(false);
                    previewTextField.addListener(getMain().getIbeamListener());
                    previewTextField.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("text", previewTextField.getText());
                            refreshPreview();
                        }
                    });
                    getPreviewProperties().put("text", previewTextField.getText());
                    t.add(previewTextField).growX();

                } else if (clazz.equals(Touchpad.class)) {

                } else if (clazz.equals(Tree.class)) {
                    t.row();
                    t.add(new Label("Icon Spacing Left: ", getSkin())).right();
                    Spinner spinner = new Spinner(2, 1.0, false, Spinner.Orientation.HORIZONTAL, getSkin());
                    spinner.getTextField().setFocusTraversal(false);
                    spinner.setMinimum(1);
                    spinner.getTextField().addListener(getMain().getIbeamListener());
                    spinner.getButtonMinus().addListener(getMain().getHandListener());
                    spinner.getButtonPlus().addListener(getMain().getHandListener());
                    spinner.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent changeEvent, Actor actor) {
                            getPreviewProperties().put("icon-spacing-left", ((Spinner) actor).getValueAsInt());
                            refreshPreview();
                        }
                    });
                    getPreviewProperties().put("icon-spacing-left", spinner.getValueAsInt());
                    t.add(spinner).growX();
                    
                    t.row();
                    t.add(new Label("Icon Spacing Right: ", getSkin())).right();
                    spinner = new Spinner(2, 1.0, false, Spinner.Orientation.HORIZONTAL, getSkin());
                    spinner.getTextField().setFocusTraversal(false);
                    spinner.setMinimum(1);
                    spinner.getTextField().addListener(getMain().getIbeamListener());
                    spinner.getButtonMinus().addListener(getMain().getHandListener());
                    spinner.getButtonPlus().addListener(getMain().getHandListener());
                    spinner.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent changeEvent, Actor actor) {
                            getPreviewProperties().put("icon-spacing-right", ((Spinner) actor).getValueAsInt());
                            refreshPreview();
                        }
                    });
                    getPreviewProperties().put("icon-spacing-right", spinner.getValueAsInt());
                    t.add(spinner).growX();
                    
                    t.row();
                    t.add(new Label("Indent Spacing: ", getSkin())).right();
                    spinner = new Spinner(0, 1.0, false, Spinner.Orientation.HORIZONTAL, getSkin());
                    spinner.getTextField().setFocusTraversal(false);
                    spinner.setMinimum(0);
                    spinner.getTextField().addListener(getMain().getIbeamListener());
                    spinner.getButtonMinus().addListener(getMain().getHandListener());
                    spinner.getButtonPlus().addListener(getMain().getHandListener());
                    spinner.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent changeEvent, Actor actor) {
                            getPreviewProperties().put("indent-spacing", ((Spinner) actor).getValueAsInt());
                            refreshPreview();
                        }
                    });
                    getPreviewProperties().put("indent-spacing", spinner.getValueAsInt());
                    t.add(spinner).growX();

                    t.row();
                    t.add(new Label("Y Spacing: ", getSkin())).right();
                    spinner = new Spinner(4, 1.0, false, Spinner.Orientation.HORIZONTAL, getSkin());
                    spinner.getTextField().setFocusTraversal(false);
                    spinner.setMinimum(1);
                    spinner.getTextField().addListener(getMain().getIbeamListener());
                    spinner.getButtonMinus().addListener(getMain().getHandListener());
                    spinner.getButtonPlus().addListener(getMain().getHandListener());
                    spinner.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent changeEvent, Actor actor) {
                            getPreviewProperties().put("y-spacing", ((Spinner) actor).getValueAsInt());
                            refreshPreview();
                        }
                    });
                    getPreviewProperties().put("y-spacing", spinner.getValueAsInt());
                    t.add(spinner).growX();

                } else if (clazz.equals(Window.class)) {
                    t.row();
                    t.add(new Label("Title Text: ", getSkin())).right();
                    TextField previewTextField = new TextField(TEXT_SAMPLE, getSkin());
                    previewTextField.setFocusTraversal(false);
                    previewTextField.addListener(getMain().getIbeamListener());
                    previewTextField.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("title", previewTextField.getText());
                            refreshPreview();
                        }
                    });
                    getPreviewProperties().put("title", previewTextField.getText());
                    t.add(previewTextField).growX();

                    t.row();
                    t.add(new Label("Sample Text Color: ", getSkin()));
                    BrowseField textColorField = new BrowseField(null, null, getSkin(), "color");
                    textColorField.addListener(getMain().getHandListener());
                    t.add(textColorField).growX();

                    t.row();
                    t.add(new Label("Sample Text: ", getSkin())).right();
                    TextArea textArea = new TextArea(PARAGRAPH_SAMPLE, getSkin());
                    textArea.setFocusTraversal(false);
                    textArea.setPrefRows(5);
                    textArea.addListener(getMain().getIbeamListener());
                    textArea.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            getPreviewProperties().put("text", textArea.getText());
                            refreshPreview();
                        }
                    });
                    getPreviewProperties().put("text", textArea.getText());
                    t.add(textArea).growX();
                }

                previewSizeSelectBox.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        getPreviewProperties().put("size", previewSizeSelectBox.getSelectedIndex());
                        if (previewSizeSelectBox.getSelectedIndex() != 7) {
                            refreshPreview();
                        }
                    }
                });
                getPreviewProperties().put("size", previewSizeSelectBox.getSelectedIndex());

                refreshPreview();
            } else {
            	//TODO fim
                t.add(new Label("Stage Color: ", getSkin())).right();
                BrowseField browseField = new BrowseField(null, null, getSkin(), "color");
                browseField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        getMain().getDialogFactory().showDialogColorPicker((Color) getPreviewProperties().get("bgcolor"), new DialogColorPicker.ColorListener() {
                            @Override
                            public void selected(Color color) {
                                if (color != null) {
                                    browseField.getTextButton().setText((int) (color.r * 255) + "," + (int) (color.g * 255) + "," + (int) (color.b * 255) + "," + (int) (color.a * 255));
                                    getPreviewProperties().put("bgcolor", color);
                                    previewBgColor.set(color);
                                    refreshPreview();
                                }
                            }
                        });
                    }
                });
                
                browseField.addListener(getMain().getHandListener());
                t.add(browseField).growX();
                browseField.getTextButton().setText((int) (previewBgColor.r * 255) + "," + (int) (previewBgColor.g * 255) + "," + (int) (previewBgColor.b * 255) + "," + (int) (previewBgColor.a * 255));
            
                //PreviewActorCreator
                try {
                CustomClass customClass = (CustomClass) classSelectBox.getSelected();
                Class c = ClassReflection.forName(customClass.getFullyQualifiedName());
				
                PreviewActorCreator.createPreviewProperties(c, t, this);
				} catch (ReflectionException e) {
					e.printStackTrace();
				}
            }
        }
    }
 

    @SuppressWarnings({ "rawtypes", "unchecked", "incomplete-switch" })
	public void refreshPreview() {
        if (previewTable != null) {
            previewTable.clear();
            previewTable.setBackground("white");
            previewTable.setColor((Color) getPreviewProperties().get("bgcolor"));

            for (BitmapFont font : getPreviewFonts()) {
                font.dispose();
            }

            if (classSelectBox.getSelectedIndex() >= 0 && classSelectBox.getSelectedIndex() < Main.BASIC_CLASSES.length) {
                StyleData styleData = getSelectedStyle();
                Class clazz = Main.BASIC_CLASSES[classSelectBox.getSelectedIndex()];

                if (!styleData.hasMandatoryFields()) {
                    Label label;
                    if (clazz.equals(SelectBox.class)) {
                        label = new Label("Please fill all mandatory fields\n(Highlighted on the left)\n\nscrollStyle and listStyle\nmust already be defined", getSkin());
                    } else if (clazz.equals(TextTooltip.class)) {
                        label = new Label("Please fill all mandatory fields\n(Highlighted on the left)\n\nlabel must already be defined", getSkin());
                    } else {
                        label = new Label("Please fill all mandatory fields\n(Highlighted on the left)", getSkin());
                    }
                    label.setAlignment(Align.center);
                    previewTable.add(label);
                } else if (styleData.hasAllNullFields()) {
                    Label label;
                    label = new Label("All fields are empty!\nEmpty classes are not exported\nAdd style properties in the menu to the left", getSkin());
                    label.setAlignment(Align.center);
                    previewTable.add(label);
                } else {
                	//TODO inicio
                    Actor widget = null;
                    if (clazz.equals(Button.class)) {
                        Button.ButtonStyle style = createPreviewStyle(Button.ButtonStyle.class, styleData);
                        widget = new Button(style);
                        ((Button)widget).setDisabled((boolean) getPreviewProperties().get("disabled"));
                        widget.addListener(getMain().getHandListener());
                    } else if (clazz.equals(CheckBox.class)) {
                        CheckBox.CheckBoxStyle style = createPreviewStyle(CheckBox.CheckBoxStyle.class, styleData);
                        widget = new CheckBox("", style);
                        ((CheckBox)widget).setDisabled((boolean) getPreviewProperties().get("disabled"));
                        ((CheckBox)widget).setText((String) getPreviewProperties().get("text"));
                        widget.addListener(getMain().getHandListener());
                    } else if (clazz.equals(ImageButton.class)) {
                        ImageButtonStyle style = createPreviewStyle(ImageButtonStyle.class, styleData);
                        widget = new ImageButton(style);
                        ((ImageButton)widget).setDisabled((boolean) getPreviewProperties().get("disabled"));
                        widget.addListener(getMain().getHandListener());
                    } else if (clazz.equals(ImageTextButton.class)) {
                        ImageTextButton.ImageTextButtonStyle style = createPreviewStyle(ImageTextButton.ImageTextButtonStyle.class, styleData);
                        widget = new ImageTextButton("", style);
                        ((ImageTextButton)widget).setDisabled((boolean) getPreviewProperties().get("disabled"));
                        ((ImageTextButton)widget).setText((String) getPreviewProperties().get("text"));
                        widget.addListener(getMain().getHandListener());
                    } else if (clazz.equals(Label.class)) {
                        LabelStyle style = createPreviewStyle(LabelStyle.class, styleData);
                        widget = new Label("", style);
                        ((Label)widget).setText((String) getPreviewProperties().get("text"));
                    } else if (clazz.equals(List.class)) {
                        ListStyle style = createPreviewStyle(ListStyle.class, styleData);
                        widget = new List(style);
                        Array<String> items = new Array<>(((String) getPreviewProperties().get("text")).split("\\n"));
                        ((List)widget).setItems(items);
                        widget.addListener(getMain().getHandListener());
                    } else if (clazz.equals(ProgressBar.class)) {
                        ProgressBar.ProgressBarStyle style = createPreviewStyle(ProgressBar.ProgressBarStyle.class, styleData);
                        widget = new ProgressBar((float) (double) getPreviewProperties().get("minimum"), (float) (double) getPreviewProperties().get("maximum"), (float) (double) getPreviewProperties().get("increment"), (boolean) getPreviewProperties().get("orientation"), style);
                        ((ProgressBar) widget).setValue((float) (double) getPreviewProperties().get("value"));
                        ((ProgressBar)widget).setDisabled((boolean) getPreviewProperties().get("disabled"));
                    } else if (clazz.equals(ScrollPane.class)) {
                        ScrollPaneStyle style = createPreviewStyle(ScrollPaneStyle.class, styleData);
                        Label label = new Label("", getSkin());
                        widget = new ScrollPane(label, style);
                        ((ScrollPane) widget).setScrollbarsOnTop((boolean) getPreviewProperties().get("scrollbarsOnTop"));
                        ((ScrollPane) widget).setScrollBarPositions((boolean) getPreviewProperties().get("hScrollBarPosition"), (boolean) getPreviewProperties().get("vScrollBarPosition"));
                        ((ScrollPane) widget).setScrollingDisabled((boolean) getPreviewProperties().get("hScrollDisabled"), (boolean) getPreviewProperties().get("vScrollDisabled"));
                        ((ScrollPane) widget).setForceScroll((boolean) getPreviewProperties().get("forceHscroll"), (boolean) getPreviewProperties().get("forceVscroll"));
                        ((ScrollPane) widget).setVariableSizeKnobs((boolean) getPreviewProperties().get("variableSizeKnobs"));
                        ((ScrollPane) widget).setOverscroll((boolean) getPreviewProperties().get("hOverscroll"), (boolean) getPreviewProperties().get("vOverscroll"));
                        ((ScrollPane) widget).setFadeScrollBars((boolean) getPreviewProperties().get("fadeScroll"));
                        ((ScrollPane) widget).setSmoothScrolling((boolean) getPreviewProperties().get("smoothScroll"));
                        ((ScrollPane) widget).setFlickScroll((boolean) getPreviewProperties().get("flickScroll"));
                        ((ScrollPane) widget).setClamp((boolean) getPreviewProperties().get("clamp"));
                        label.setText((String) getPreviewProperties().get("text"));
                    } else if (clazz.equals(SelectBox.class)) {
                        SelectBox.SelectBoxStyle style = createPreviewStyle(SelectBox.SelectBoxStyle.class, styleData);
                        widget = new SelectBox(style);
                        ((SelectBox)widget).setDisabled((boolean) getPreviewProperties().get("disabled"));
                        Array<String> items = new Array<>(((String) getPreviewProperties().get("text")).split("\\n"));
                        ((SelectBox)widget).setItems(items);
                        ((SelectBox)widget).setMaxListCount((int) getPreviewProperties().get("max-list-count"));
                        ((SelectBox)widget).setAlignment((int) getPreviewProperties().get("alignment"));
                        widget.addListener(getMain().getHandListener());
                        ((SelectBox)widget).getList().addListener(getMain().getHandListener());
                    } else if (clazz.equals(Slider.class)) {
                        Slider.SliderStyle style = createPreviewStyle(Slider.SliderStyle.class, styleData);
                        widget = new Slider((float) (double) getPreviewProperties().get("minimum"), (float) (double) getPreviewProperties().get("maximum"), (float) (double) getPreviewProperties().get("increment"), (boolean) getPreviewProperties().get("orientation"), style);
                        ((Slider)widget).setDisabled((boolean) getPreviewProperties().get("disabled"));
                        widget.addListener(getMain().getHandListener());
                    } else if (clazz.equals(SplitPane.class)) {
                        SplitPane.SplitPaneStyle style = createPreviewStyle(SplitPane.SplitPaneStyle.class, styleData);
                        
                        Table table1 = new Table();
                        Label label1 = new Label("", getSkin());
                        table1.add(label1).minSize(0);
                        
                        Table table2 = new Table();
                        Label label2 = new Label("", getSkin());
                        table2.add(label2).minSize(0);
                        
                        widget = new SplitPane(table1, table2, (boolean) getPreviewProperties().get("orientation"), style);
                        ((SplitPane) widget).setMinSplitAmount(0);
                        ((SplitPane) widget).setMaxSplitAmount(1);
                        label1.setText((String) getPreviewProperties().get("text"));
                        label2.setText((String) getPreviewProperties().get("text"));
                        
                        if ((boolean) getPreviewProperties().get("orientation")) {
                            widget.addListener(getMain().getVerticalResizeArrowListener());
                        } else {
                            widget.addListener(getMain().getHorizontalResizeArrowListener());
                        }
                    } else if (clazz.equals(TextButton.class)) {
                        TextButtonStyle style = createPreviewStyle(TextButtonStyle.class, styleData);
                        widget = new TextButton("", style);
                        ((TextButton)widget).setDisabled((boolean) getPreviewProperties().get("disabled"));
                        ((TextButton)widget).setText((String) getPreviewProperties().get("text"));
                        widget.addListener(getMain().getHandListener());
                    } else if (clazz.equals(TextField.class)) {
                        TextFieldStyle style = createPreviewStyle(TextFieldStyle.class, styleData);
                        widget = new TextField("", style);
                        ((TextField)widget).setFocusTraversal(false);
                        ((TextField)widget).setDisabled((boolean) getPreviewProperties().get("disabled"));
                        ((TextField)widget).setPasswordMode((boolean) getPreviewProperties().get("passwordMode"));
                        ((TextField)widget).setAlignment((int) getPreviewProperties().get("alignment"));
                        ((TextField)widget).setText((String) getPreviewProperties().get("text"));
                        ((TextField)widget).setMessageText((String) getPreviewProperties().get("message"));
                        String string = (String) getPreviewProperties().get("password");
                        if (string.length() > 0) {
                            ((TextField)widget).setPasswordCharacter(string.charAt(0));
                        }
                        widget.addListener(getMain().getIbeamListener());
                    } else if (clazz.equals(TextTooltip.class)) {
                        TextTooltip.TextTooltipStyle style = createPreviewStyle(TextTooltip.TextTooltipStyle.class, styleData);

                        TooltipManager manager = new TooltipManager();
                        manager.animations = false;
                        manager.initialTime = 0.0f;
                        manager.resetTime = 0.0f;
                        manager.subsequentTime = 0.0f;
                        manager.hideAll();
                        manager.instant();
                        TextTooltip toolTip = new TextTooltip((String) getPreviewProperties().get("text"), manager, style);

                        widget = new Label("Hover over me", getSkin());
                        widget.addListener(toolTip);
                    } else if (clazz.equals(Touchpad.class)) {
                        Touchpad.TouchpadStyle style = createPreviewStyle(Touchpad.TouchpadStyle.class, styleData);
                        widget = new Touchpad(0, style);
                        widget.addListener(getMain().getHandListener());
                    } else if (clazz.equals(Tree.class)) {
                        Tree.TreeStyle style = createPreviewStyle(Tree.TreeStyle.class, styleData);
                        widget = new Tree(style);
                        ((Tree) widget).setIconSpacing((int) getPreviewProperties().get("icon-spacing-left"), (int) getPreviewProperties().get("icon-spacing-right"));
                        ((Tree) widget).setIndentSpacing((int) getPreviewProperties().get("indent-spacing"));
                        ((Tree) widget).setYSpacing((int) getPreviewProperties().get("y-spacing"));
                        String[] lines = {"this", "is", "a", "test"};
                        Tree.Node parentNode = null;
                        for (String line: lines) {
                            Label label = new Label(line, getSkin());
                            Tree.Node node = new Tree.Node(label);
                            if (parentNode == null) {
                                ((Tree) widget).add(node);
                            } else {
                                parentNode.add(node);
                            }
                            parentNode = node;
                        }
                        widget.addListener(getMain().getHandListener());
                    } else if (clazz.equals(Window.class))  {
                        Window.WindowStyle style = createPreviewStyle(Window.WindowStyle.class, styleData);
                        
                        if (style.stageBackground != null) {
                            previewTable.setBackground(style.stageBackground);
                            previewTable.setColor(Color.WHITE);
                            style.stageBackground = null;
                        }

                        Label sampleText = new Label("", getSkin());
                        sampleText.setText((String) getPreviewProperties().get("text"));

                        widget = new Window((String) getPreviewProperties().get("title"), style);
                        ((Window)widget).add(sampleText);
                    }
                    else {
                    	//TODO
                    }

                    if (widget != null) {

                        updateSize(widget);
                    }
                }
            } else {
            	//TODO fim
            	CustomClass customClass = (CustomClass) classSelectBox.getSelected();
                CustomStyle customStyle = (CustomStyle) styleSelectBox.getSelected();
                
	            	try {
	                	Class c = ClassReflection.forName(customClass.getFullyQualifiedName());
			            Actor actor = PreviewActorCreator.createActorToPreview(this, c, customStyle, getPreviewProperties());
			            
			            Actor a = stage.getKeyboardFocus();
			            updateSize(actor);
			            stage.setKeyboardFocus(a);

	            	} catch (ReflectionException e) {
						e.printStackTrace();
					}
	            	
	                boolean showMessage = true;
	                
	                if (customStyle.getProperties().size == 0) {
	                    Label label = new Label("No style properties!\nEmpty classes are not exported\nAdd style properties in the menu to the left", getSkin());
	                    label.setAlignment(0);
	                    previewTable.add(label);

	                } else {
	                    for (CustomProperty customProperty : customStyle.getProperties()) {
	                        if (customProperty.getValue() != null && !(customProperty.getValue() instanceof String) || customProperty.getValue() != null && !((String)customProperty.getValue()).equals("")) {
	                            showMessage = false;
	                            break;
	                        }
	                    }

	                    if (showMessage) {
	                        Label label = new Label("All properties are empty!\nEmpty classes are not exported\nAdd style properties in the menu to the left", getSkin());
	                        label.setAlignment(0);
	                        previewTable.add(label);
	                    }

	               }
            }
        }
    }
    
    public void updateSize(Actor widget) {
    	if(getPreviewProperties().containsKey("size")) {
    	switch ((int) getPreviewProperties().get("size")) {
        case (0):
            previewTable.add(widget).size(10.0f);
            previewSizeSelectBox.setItems(DEFAULT_SIZES);
            break;
        case (1):
            previewTable.add(widget);
            previewSizeSelectBox.setItems(DEFAULT_SIZES);
            break;
        case (2):
            previewTable.add(widget).size(200.0f);
            previewSizeSelectBox.setItems(DEFAULT_SIZES);
            break;
        case (3):
            previewTable.add(widget).growX();
            previewSizeSelectBox.setItems(DEFAULT_SIZES);
            break;
        case (4):
            previewTable.add(widget).growY();
            previewSizeSelectBox.setItems(DEFAULT_SIZES);
            break;
        case (5):
            previewTable.add(widget).grow();
            previewSizeSelectBox.setItems(DEFAULT_SIZES);
            break;
        case (6):
            Actor addWidget = widget;
            TraversalTextField widthField = new TraversalTextField("", getSkin());
            TraversalTextField heightField = new TraversalTextField("", getSkin());
            widthField.setNextFocus(heightField);
            heightField.setNextFocus(widthField);
            Dialog dialog = new Dialog("Enter dimensions...", getSkin()) {
                @Override
                protected void result(Object object) {
                    if ((boolean)object) {
                        previewTable.add(addWidget).size(Integer.parseInt(widthField.getText()), Integer.parseInt(heightField.getText()));
                        Array<String> items = new Array<>(DEFAULT_SIZES);
                        items.add(widthField.getText() + "x" + heightField.getText());
                        getPreviewProperties().put("sizeX", Integer.parseInt(widthField.getText()));
                        getPreviewProperties().put("sizeY", Integer.parseInt(heightField.getText()));
                        previewSizeSelectBox.setItems(items);
                        previewSizeSelectBox.setSelectedIndex(7);
                    } else {
                        previewSizeSelectBox.setSelectedIndex(1);
                    }
                }
            };
            dialog.getTitleTable().getCells().first().padLeft(5.0f);
            dialog.text("Enter the preview dimensions: ");
            dialog.getContentTable().getCells().first().pad(10.0f);
            dialog.getContentTable().row();
            Table sizeTable = new Table();
            sizeTable.add(widthField).padLeft(10.0f);
            sizeTable.add(new Label(" x ", getSkin()));
            sizeTable.add(heightField).padRight(10.0f);
            dialog.getContentTable().add(sizeTable);
            dialog.getButtonTable().defaults().padBottom(10.0f).minWidth(50.0f);
            dialog.button("OK", true);
            dialog.button("Cancel", false);
            TextButton okButton = (TextButton) dialog.getButtonTable().getCells().first().getActor();
            okButton.setDisabled(true);
            okButton.addListener(getMain().getHandListener());
            widthField.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    okButton.setDisabled(!widthField.getText().matches("^\\d+$") || !heightField.getText().matches("^\\d+$"));
                }
            });
            heightField.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    okButton.setDisabled(!widthField.getText().matches("^\\d+$") || !heightField.getText().matches("^\\d+$"));
                }
            });
            dialog.getButtonTable().getCells().get(1).getActor().addListener(getMain().getHandListener());
            dialog.key(Input.Keys.ESCAPE, false);
            dialog.show(stage);
            stage.setKeyboardFocus(widthField);
            break;
        case (7):
            previewTable.add(widget).size((int) getPreviewProperties().get("sizeX"), (int) getPreviewProperties().get("sizeY"));
            break;
    }
    	}
    }
    
    @SuppressWarnings({ "incomplete-switch", "rawtypes", "unchecked" })
	public void putProperties(CustomStyle customStyle) {

        HorizontalGroup horizontalGroup = new HorizontalGroup();
        horizontalGroup.wrap();
        //todo: resolve the following crash line
        //the following causes a crash. LibGDX bug.
//                    horizontalGroup.space(10.0f);
        horizontalGroup.wrapSpace(10.0f);
        horizontalGroup.setTouchable(Touchable.disabled);
        previewTable.add(horizontalGroup).grow().pad(10.0f);

        for (CustomProperty customProperty : customStyle.getProperties()) {
            if (customProperty.getValue() != null) {
                Container container = new Container();
                container.pad(5.0f);
                horizontalGroup.addActor(container);
                
                switch (customProperty.getType()) {
                    case TEXT:
                    case RAW_TEXT:
                    case STYLE:
                        if (!(customProperty.getValue() instanceof String)) {
                            customProperty.setValue("");
                        }

                        Label labelText = new Label((String) customProperty.getValue(), getSkin());
                        container.setActor(labelText);
                        break;
                    case NUMBER:
                        if (!(customProperty.getValue() instanceof Double)) {
                            customProperty.setValue(0.0);
                        }

                        Label labelNumber = new Label(Double.toString((double) customProperty.getValue()), getSkin());
                        container.setActor(labelNumber);
                        break;
                    case BOOL:
                        if (!(customProperty.getValue() instanceof Boolean)) {
                            customProperty.setValue(false);
                        }

                        Label labelBoolean = new Label(Boolean.toString((boolean) customProperty.getValue()), getSkin());
                        container.setActor(labelBoolean);
                        break;
                    case COLOR:
                        if (!(customProperty.getValue() instanceof String)) {
                            customProperty.setValue("");
                        }

                        ColorData colorData = null;

                        String colorName = (String) customProperty.getValue();
                        for (ColorData cd : getMain().getJsonData().getColors()) {
                            if (cd.getName().equals(colorName)) {
                                colorData = cd;
                                break;
                            }
                        }

                        if (colorData != null) {
                            Table colorTable = new Table(getSkin());
                            colorTable.setBackground("white");
                            colorTable.setColor(colorData.color);
                            colorTable.add().size(25.0f);

                            container.setActor(colorTable);
                        }
                        break;
                    case FONT:
                        if (!(customProperty.getValue() instanceof String)) {
                            customProperty.setValue("");
                        }

                        BitmapFont font = null;
                        FontData fontData = null;

                        String fontName = (String) customProperty.getValue();
                        for (FontData fd : getMain().getJsonData().getFonts()) {
                            if (fd.getName().equals(fontName)) {
                                fontData = fd;
                                font = new BitmapFont(fd.file);
                                getPreviewFonts().add(font);
                                break;
                            }
                        }

                        if (font != null) {
                            Label labelFont = new Label(fontData.getName(), new LabelStyle(font, Color.WHITE));
                            container.setActor(labelFont);
                        }

                        FreeTypeFontData freeTypeFontData = null;
                        for (FreeTypeFontData fd : getMain().getJsonData().getFreeTypeFonts()) {
                            if (fd.name.equals(fontName)) {
                                freeTypeFontData = fd;
                                break;
                            }
                        }

                        if (freeTypeFontData != null && freeTypeFontData.bitmapFont != null) {
                            Label labelFont = new Label(freeTypeFontData.name, new LabelStyle(freeTypeFontData.bitmapFont, Color.WHITE));
                            container.setActor(labelFont);
                        }

                        break;
                    case DRAWABLE:
                        if (!(customProperty.getValue() instanceof String)) {
                            customProperty.setValue("");
                        }

                        DrawableData drawable = null;

                        String drawableName = (String) customProperty.getValue();
                        for (DrawableData dd : getMain().getAtlasData().getDrawables()) {
                            if (dd.name.equals(drawableName)) {
                                drawable = dd;
                                break;
                            }
                        }

                        if (drawable != null) {
                            Image image = new Image(drawablePairs.get(drawable.name));
                            container.setActor(image);
                        }
                        break;
                }
            }
        }
    }
    
    @SuppressWarnings("finally")
	public <T> T createPreviewStyle(Class<T> clazz, StyleData styleData) {
        T returnValue = null;
        try {
            returnValue = ClassReflection.newInstance(clazz);
            Field[] fields = ClassReflection.getFields(clazz);
            for (Field field : fields) {
                Object value = styleData.getInheritedValue(field.getName());
                if (value != null) {
                    if (field.getType().equals(Drawable.class)) {
                        field.set(returnValue, drawablePairs.get((String) value));
                    } else if (field.getType().equals(Color.class)) {
                        for (ColorData data : getMain().getProjectData().getJsonData().getColors()) {
                            if (value.equals(data.getName())) {
                                field.set(returnValue, data.color);
                                break;
                            }
                        }
                    } else if (field.getType().equals(BitmapFont.class)) {
                        for (FontData data : getMain().getProjectData().getJsonData().getFonts()) {
                            if (value.equals(data.getName())) {
                                BitmapFont font = new BitmapFont(data.file);
                                getPreviewFonts().add(font);
                                field.set(returnValue, font);
                            }
                        }
                        
                        for (FreeTypeFontData data : getMain().getJsonData().getFreeTypeFonts()) {
                            if (value.equals(data.name)) {
                                field.set(returnValue, data.bitmapFont);
                            }
                        }
                    } else if (field.getType().equals(Float.TYPE)) {
                        field.set(returnValue, (float) value);
                    } else if (field.getType().equals(ListStyle.class)) {
                        Array<StyleData> datas = getMain().getProjectData().getJsonData().getClassStyleMap().get(List.class);

                        for (StyleData data : datas) {
                            if (value.equals(data.name)) {
                                ListStyle style = createPreviewStyle(ListStyle.class, data);
                                field.set(returnValue, style);
                                break;
                            }
                        }
                    } else if (field.getType().equals(ScrollPaneStyle.class)) {
                        Array<StyleData> datas = getMain().getProjectData().getJsonData().getClassStyleMap().get(ScrollPane.class);

                        for (StyleData data : datas) {
                            if (value.equals(data.name)) {
                                ScrollPaneStyle style = createPreviewStyle(ScrollPaneStyle.class, data);
                                field.set(returnValue, style);
                                break;
                            }
                        }
                    } else if (field.getType().equals(LabelStyle.class)) {
                        Array<StyleData> datas = getMain().getProjectData().getJsonData().getClassStyleMap().get(Label.class);

                        for (StyleData data : datas) {
                            if (value.equals(data.name)) {
                                LabelStyle style = createPreviewStyle(LabelStyle.class, data);
                                field.set(returnValue, style);
                                break;
                            }
                        }
                    }
                }
            }
        } finally {
            return returnValue;
        }
    }
    
    /**
     * Writes a TextureAtlas based on drawables list. Creates drawables to be
     * displayed on screen
     * @return 
     */
    public boolean produceAtlas() {
        try {
            if (atlas != null) {
                atlas.dispose();
                atlas = null;
            }
            
            if (!getMain().getProjectData().getAtlasData().atlasCurrent) {
                getMain().getProjectData().getAtlasData().writeAtlas();
                getMain().getProjectData().getAtlasData().atlasCurrent = true;
            }
            atlas = getMain().getProjectData().getAtlasData().getAtlas();

            for (DrawableData data : getMain().getProjectData().getAtlasData().getDrawables()) {
                Drawable drawable;
                if (data.customized) {
                    drawable = getSkin().getDrawable("custom-drawable-skincomposer-image");
                } else if (data.tiled) {
                    String name = data.file.name();
                    name = DrawableData.proper(name);
                    drawable = new TiledDrawable(atlas.findRegion(name));
                    drawable.setMinWidth(data.minWidth);
                    drawable.setMinHeight(data.minHeight);
                    ((TiledDrawable) drawable).getColor().set(getMain().getJsonData().getColorByName(data.tintName).color);
                } else if (data.file.name().matches(".*\\.9\\.[a-zA-Z0-9]*$")) {
                    String name = data.file.name();
                    name = DrawableData.proper(name);
                    drawable = new NinePatchDrawable(atlas.createPatch(name));
                    if (data.tint != null) {
                        drawable = ((NinePatchDrawable) drawable).tint(data.tint);
                    } else if (data.tintName != null) {
                        drawable = ((NinePatchDrawable) drawable).tint(getMain().getProjectData().getJsonData().getColorByName(data.tintName).color);
                    }
                } else {
                    String name = data.file.name();
                    name = DrawableData.proper(name);
                    drawable = new SpriteDrawable(atlas.createSprite(name));
                    if (data.tint != null) {
                        drawable = ((SpriteDrawable) drawable).tint(data.tint);
                    } else if (data.tintName != null) {
                        drawable = ((SpriteDrawable) drawable).tint(getMain().getProjectData().getJsonData().getColorByName(data.tintName).color);
                    }
                }
                
                drawablePairs.put(data.name, drawable);
            }
            return true;
        } catch (Exception e) {
            Gdx.app.error(getClass().getName(), "Error while attempting to generate drawables.", e);
            getMain().getDialogFactory().showDialogError("Atlas Error...", "Error while attempting to generate drawables.\n\nOpen log?");
            return false;
        }
    }
    
    private void addStatusBar() {
        Table table = new Table();
        table.setBackground(getSkin().getDrawable("status-bar"));
        add(table).growX();
        
        Label label = new Label("ver. " + Main.VERSION + "    RAY3K.WORDPRESS.COM    © 2018 Raymond \"Raeleus\" Buckley", getSkin());
        table.add(label).expandX().right().padRight(25.0f);
    }

    @SuppressWarnings("rawtypes")
	public SelectBox getClassSelectBox() {
        return classSelectBox;
    }

    @SuppressWarnings("rawtypes")
	public SelectBox getStyleSelectBox() {
        return styleSelectBox;
    }

    @SuppressWarnings("rawtypes")
	public Class getSelectedClass() {
        return Main.BASIC_CLASSES[classSelectBox.getSelectedIndex()];
    }

    @SuppressWarnings("rawtypes")
	public StyleData getSelectedStyle() {
        OrderedMap<Class, Array<StyleData>> classStyleMap = getMain().getProjectData().getJsonData().getClassStyleMap();
        return classStyleMap.get(getSelectedClass()).get(styleSelectBox.getSelectedIndex());
    }

    public void setStyleProperties(Array<StyleProperty> styleProperties) {
        this.styleProperties = styleProperties;
        customProperties = null;
    }
    
    public void setCustomStyleProperties(Array<CustomProperty> styleProperties) {
        this.styleProperties = null;
        customProperties = styleProperties;
    }

    public FilesDroppedListener getFilesDroppedListener() {
        return filesDroppedListener;
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
        NEW_CLASS, DUPLICATE_CLASS, DELETE_CLASS, RENAME_CLASS, STYLE_SELECTED,
        NEW_STYLE, DUPLICATE_STYLE, DELETE_STYLE, RENAME_STYLE, PREVIEW_PROPERTY,
        WELCOME, REFRESH_ATLAS, DOWNLOAD_UPDATE, CHECK_FOR_UPDATES_COMPLETE;
    }

    public static class RootTableEvent extends Event {

        public RootTableEnum rootTableEnum;

        public RootTableEvent(RootTableEnum rootTableEnum) {
            this.rootTableEnum = rootTableEnum;
        }
    }

    private static class LoadClassesEvent extends Event {

        @SuppressWarnings("rawtypes")
		SelectBox classSelectBox;

        public LoadClassesEvent(@SuppressWarnings("rawtypes") SelectBox classSelectBox) {
            this.classSelectBox = classSelectBox;
        }
    }

    private static class LoadStylesEvent extends Event {
        @SuppressWarnings("rawtypes")
		SelectBox classSelectBox;
        @SuppressWarnings("rawtypes")
		SelectBox styleSelectBox;

        @SuppressWarnings("rawtypes")
		public LoadStylesEvent(SelectBox classSelectBox,
                SelectBox styleSelectBox) {
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
    
    private static class StyleParentEvent extends Event {
        StyleData style;
        SelectBox<String> selectBox;
        
        public StyleParentEvent(StyleData style, SelectBox<String> selectBox) {
            this.style = style;
            this.selectBox = selectBox;
        }
    }

    private static enum CustomPropertyEnum {
        NEW, DUPLICATE, DELETE, RENAME, CHANGE_VALUE;
    }
    
    private static class ScmpDroppedEvent extends Event {
        FileHandle fileHandle;
        
        public ScmpDroppedEvent(FileHandle fileHandle) {
            this.fileHandle = fileHandle;
        }
    }
    
    private static class CustomPropertyEvent extends Event {
        private CustomProperty customProperty;
        private CustomPropertyEnum customPropertyEnum;
        private Actor styleActor;
        
        public CustomPropertyEvent(CustomProperty customProperty, Actor styleActor, CustomPropertyEnum customPropertyEnum) {
            this.customProperty = customProperty;
            this.customPropertyEnum = customPropertyEnum;
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
            } else if (event instanceof StyleParentEvent) {
                styleParentChanged(((StyleParentEvent) event).style, ((StyleParentEvent) event).selectBox);
            } else if (event instanceof CustomPropertyEvent) {
                CustomPropertyEvent propertyEvent = (CustomPropertyEvent) event;
                if (null != propertyEvent.customPropertyEnum) switch (propertyEvent.customPropertyEnum) {
                    case NEW:
                        newCustomProperty();
                        break;
                    case DELETE:
                        deleteCustomProperty(propertyEvent.customProperty);
                        break;
                    case RENAME:
                        renameCustomProperty(propertyEvent.customProperty);
                        break;
                    case DUPLICATE:
                        duplicateCustomProperty(propertyEvent.customProperty);
                        break;
                    case CHANGE_VALUE:
                        customPropertyValueChanged(propertyEvent.customProperty, propertyEvent.styleActor);
                        break;
                    default:
                        break;
                }
            } else if (event instanceof ScmpDroppedEvent) {
                droppedScmpFile(((ScmpDroppedEvent) event).fileHandle);
            }
            return false;
        }

        public abstract void rootEvent(RootTableEvent event);

        public abstract void stylePropertyChanged(StyleProperty styleProperty, Actor styleActor);

        public abstract void styleParentChanged(StyleData style, SelectBox<String> selectBox);
        
        @SuppressWarnings({ "rawtypes"})
		public abstract void loadClasses(SelectBox classSelectBox);

        @SuppressWarnings("rawtypes")
		public abstract void loadStyles(SelectBox classSelectBox, SelectBox styleSelectBox);
    
        public abstract void newCustomProperty();
        
        public abstract void duplicateCustomProperty(CustomProperty customProperty);
        
        public abstract void deleteCustomProperty(CustomProperty customProperty);
        
        public abstract void customPropertyValueChanged(CustomProperty customProperty, Actor styleActor);
        
        public abstract void renameCustomProperty(CustomProperty customProperty);
    
        public abstract void droppedScmpFile(FileHandle fileHandle);
    }

    public static class ShortcutListener extends InputListener {

        private final RootTable rootTable;

        public ShortcutListener(RootTable rootTable) {
            this.rootTable = rootTable;
        }

        @Override
        public boolean keyDown(InputEvent event, int keycode) {
            boolean listenForShortcuts = true;
            for (Actor actor : rootTable.getStage().getActors()) {
                if (actor instanceof Dialog) {
                    listenForShortcuts = false;
                    break;
                }
            }
            
            //trigger shortcuts only if no dialogs are open.
            if (listenForShortcuts) {
                if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)) {
                    char character = rootTable.getMain().getDesktopWorker().getKeyName(keycode);
                    switch (character) {
                        case 'z':
                            rootTable.fire(new RootTable.RootTableEvent(RootTable.RootTableEnum.UNDO));
                            break;
                        case 'y':
                            rootTable.fire(new RootTable.RootTableEvent(RootTable.RootTableEnum.REDO));
                            break;
                        case 'n':
                            rootTable.fire(new RootTable.RootTableEvent(RootTable.RootTableEnum.NEW));
                            break;
                        case 'o':
                            rootTable.fire(new RootTable.RootTableEvent(RootTable.RootTableEnum.OPEN));
                            break;
                        case 's':
                            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
                                rootTable.fire(new RootTable.RootTableEvent(RootTable.RootTableEnum.SAVE_AS));
                            } else {
                                rootTable.fire(new RootTable.RootTableEvent(RootTable.RootTableEnum.SAVE));
                            }
                            break;
                        case 'e':
                            rootTable.fire(new RootTable.RootTableEvent(RootTableEnum.EXPORT));
                            break;
                        default:
                            break;
                    }
                }
                
                switch (keycode) {
                    case Input.Keys.F5:
                        rootTable.fire(new RootTable.RootTableEvent(RootTableEnum.REFRESH_ATLAS));
                        break;
                }
            }
            return false;
        }
    }

    public ObjectMap<String, Drawable> getDrawablePairs() {
        return drawablePairs;
    }

	public Array<BitmapFont> getPreviewFonts() {
		return previewFonts;
	}

	public Main getMain() {
		return main;
	}

	public ObjectMap<String, Object> getPreviewProperties() {
		return previewProperties;
	}
}
