/** *****************************************************************************
 * MIT License
 *
 * Copyright (c) 2024 Raymond Buckley
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
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane.SplitPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip.TextTooltipStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.TreeStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.ray3k.skincomposer.data.*;
import com.ray3k.skincomposer.data.CustomProperty.PropertyType;
import com.ray3k.skincomposer.data.ProjectData.RecentFile;
import com.ray3k.skincomposer.utils.Utils;
import com.ray3k.stripe.DraggableList.DraggableListListener;
import com.ray3k.stripe.*;
import com.ray3k.stripe.PopColorPicker.PopColorPickerAdapter;
import com.ray3k.stripe.StripeMenuBar.KeyboardShortcut;
import com.ray3k.tenpatch.TenPatchDrawable;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.IntStream;

import static com.ray3k.skincomposer.Main.*;

public class RootTable extends Table {
    private SelectBox classSelectBox;
    private DraggableSelectBox styleBox;
    private Table stylePropertiesTable;
    private Table previewPropertiesTable;
    private Table previewTable;
    private ScrollPane stylePropertiesScrollPane;
    private final ScrollPaneListener scrollPaneListener;
    private final ObjectMap<String, Object> previewProperties;
    private SelectBox<String> previewSizeSelectBox;
    private static final String[] DEFAULT_SIZES = {"small", "default", "large", "growX", "growY", "grow", "free transform", "custom"};
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
    private static final String PARAGRAPH_SAMPLE_EXT = PARAGRAPH_SAMPLE
            + "\n\n\n" + PARAGRAPH_SAMPLE + "\n\n\n" + PARAGRAPH_SAMPLE + "\n\n\n"
            + PARAGRAPH_SAMPLE;
    private final Array<BitmapFont> previewFonts;
    private TextButton undoButton;
    private TextButton redoButton;
    private StripeMenu recentFilesMenu;
    private Button classDuplicateButton;
    private Button classDeleteButton;
    private Button classRenameButton;
    private Button styleDeleteButton;
    private Button styleRenameButton;
    private FilesDroppedListener filesDroppedListener;
    private ResizeWidget previewResizeWidget;

    public RootTable() {
        super(skin);
        
        previewProperties = new ObjectMap<>();
        
        scrollPaneListener = new ScrollPaneListener();
        previewFonts = new Array<>();
        
        atlasData.produceAtlas();
        
        filesDroppedListener = (Array<FileHandle> files) -> {
            for (FileHandle fileHandle : files) {
                if (fileHandle.extension().toLowerCase(Locale.ROOT).equals("scmp")) {
                    fire(new ScmpDroppedEvent(fileHandle));
                    break;
                }
            }
        };
        
        desktopWorker.addFilesDroppedListener(filesDroppedListener);
    
        previewResizeWidget = new ResizeWidget(null, skin);
        previewResizeWidget.setName("resizer");
        previewResizeWidget.setTouchable(Touchable.enabled);
        previewResizeWidget.setResizingFromCenter(true);
        previewResizeWidget.setAllowDragging(false);
    
        Utils.applyResizeArrowListener(previewResizeWidget);
    }

    public void populate() {
        Button button = findActor("downloadButton");
        var updateAvailable = button == null ? false : button.isVisible();
        
        clearChildren();
        addFileMenu();

        row();
        addClassBar();

        row();
        addStyleAndPreviewSplit();

        row();
        addStatusBar();
        
        findActor("downloadButton").setVisible(updateAvailable);
    }

    private void addFileMenu() {
        Table table = new Table();
        table.defaults().padRight(2.0f);
        add(table).growX().padTop(2.0f);
    
        var bar = new StripeMenuBar(stage, getSkin(), "main");
        table.add(bar).growX();
        
        String modifier;
        KeyboardShortcut saveAsKeyboardShortcut;
        
        if (Utils.isMac()) {
            modifier = "⌘";
            saveAsKeyboardShortcut = new KeyboardShortcut("⌘+Shift+Option+S", Keys.S, Keys.CONTROL_LEFT, Keys.ALT_LEFT, Keys.SHIFT_LEFT);
        } else {
            modifier = "Ctrl";
            saveAsKeyboardShortcut = new KeyboardShortcut("Ctrl+Alt+S", Keys.S,Keys.CONTROL_LEFT, Keys.ALT_LEFT);
        }
        
        bar.menu("File", handListener)
                .item("New", new KeyboardShortcut(modifier + "+N", Keys.N, Keys.CONTROL_LEFT), handListener, new MenuBarListener(RootTableEnum.NEW))
                .item("Open...", new KeyboardShortcut(modifier + "+O", Keys.O, Keys.CONTROL_LEFT), handListener, new MenuBarListener(RootTableEnum.OPEN))
                .menu("Recent Files", handListener)
                
                .parent()
                .item("Save", new KeyboardShortcut(modifier + "+S", Keys.S, Keys.CONTROL_LEFT), handListener, new MenuBarListener(RootTableEnum.SAVE))
                .item("Save As...", saveAsKeyboardShortcut, handListener, new MenuBarListener(RootTableEnum.SAVE_AS))
                .item("Welcome Screen...", handListener, new MenuBarListener(RootTableEnum.WELCOME))
                .item("Import...", handListener, new MenuBarListener(RootTableEnum.IMPORT))
                .item("Export...", new KeyboardShortcut(modifier + "+E", Keys.E, Keys.CONTROL_LEFT), handListener, new MenuBarListener(RootTableEnum.EXPORT))
                .item("Exit", handListener, new MenuBarListener(RootTableEnum.EXIT));
        
        bar.menu("Edit", handListener)
                .item("Undo", new KeyboardShortcut(modifier + "+Z", Keys.Z, Keys.CONTROL_LEFT), handListener, new MenuBarListener(RootTableEnum.UNDO))
                .item("Redo", new KeyboardShortcut(modifier + "+Y", Keys.Y, Keys.CONTROL_LEFT), handListener, new MenuBarListener(RootTableEnum.REDO));
        
        bar.menu("Project", handListener)
                .item("Settings", handListener, new MenuBarListener(RootTableEnum.SETTINGS))
                .item("Colors...", handListener, new MenuBarListener(RootTableEnum.COLORS))
                .item("Fonts...", handListener, new MenuBarListener(RootTableEnum.FONTS))
                .item("Drawables...", handListener, new MenuBarListener(RootTableEnum.DRAWABLES))
                .item("Refresh Atlas", new KeyboardShortcut("F5", Keys.F5), handListener, new MenuBarListener(RootTableEnum.REFRESH_ATLAS))
                .item("Scene Composer...", handListener, new MenuBarListener(RootTableEnum.SCENE_COMPOSER))
                .item("TextraTypist Playground...", new MenuBarListener(RootTableEnum.TEXTRA_TYPIST), handListener);
        
        bar.menu("Help", handListener)
                .item("About...", handListener, new MenuBarListener(RootTableEnum.ABOUT));
    
        recentFilesMenu = bar.findMenu("File").findMenu("Recent Files");
        updateRecentFiles();
        
        undoButton = bar.findMenu("Edit").findButton("Undo");
        undoButton.setDisabled(true);
        
        redoButton = bar.findMenu("Edit").findButton("Redo");
        redoButton.setDisabled(true);
        
        Button button = new Button(getSkin(), "download");
        button.setName("downloadButton");
        table.add(button);
        button.addListener((Main.makeTooltip("Update Available", tooltipManager, getSkin())));
        button.addListener(handListener);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                fire(new RootTableEvent(RootTableEnum.DOWNLOAD_UPDATE));
            }
        });
        button.setVisible(false);
    }
    
    public void updateRecentFiles() {
        recentFilesMenu.clear();
        var recentFiles = projectData.getRecentFiles();
        recentFiles.reverse();
        for (var recentFile : recentFiles) {
            recentFilesMenu.item(recentFile.toString(), handListener, new RecentFileListener(recentFile));
        }
        recentFilesMenu.getParentButton().setDisabled(recentFiles.size == 0);
    }

    public void setUndoDisabled(boolean disabled) {
        undoButton.setDisabled(disabled);
    }
    
    public void setRedoDisabled(boolean disabled) {
        redoButton.setDisabled(disabled);
    }
    
    public void setUndoText(String text) {
        undoButton.setText(text);
    }
    
    public void setRedoText(String text) {
        redoButton.setText(text);
    }
    
    private void addClassBar() {
        Table table = new Table();
        table.setBackground(getSkin().getDrawable("class-bar"));
        add(table).expandX().left().growX();

        Label label = new Label("Class:", getSkin());
        table.add(label).padRight(10.0f).padLeft(10.0f);

        classSelectBox = new SelectBox(getSkin());
        classSelectBox.addListener(handListener);
        classSelectBox.getList().addListener(handListener);
        classSelectBox.getList().addListener(scrollFocusListener);
        table.add(classSelectBox).padRight(5.0f).minWidth(150.0f);

        classSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                fire(new LoadStylesEvent(classSelectBox, styleBox));
                fire(new RootTableEvent(RootTableEnum.CLASS_SELECTED));
            }
        });

        Button button = new Button(getSkin(), "new");
        button.addListener(handListener);
        table.add(button);

        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                fire(new RootTableEvent(RootTableEnum.NEW_CLASS));
            }
        });
        
        //Tooltip
        TextTooltip toolTip = (Main.makeTooltip("New Class", tooltipManager, getSkin()));
        button.addListener(toolTip);

        classDuplicateButton = new Button(getSkin(), "duplicate");
        classDuplicateButton.setDisabled(true);
        classDuplicateButton.addListener(handListener);
        table.add(classDuplicateButton);

        classDuplicateButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                fire(new RootTableEvent(RootTableEnum.DUPLICATE_CLASS));
            }
        });
        
        toolTip = (Main.makeTooltip("Duplicate Class", tooltipManager, getSkin()));
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
        
        toolTip = (Main.makeTooltip("Delete Class", tooltipManager, getSkin()));
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
        
        toolTip = (Main.makeTooltip("Rename Class", tooltipManager, getSkin()));
        classRenameButton.addListener(toolTip);

        label = new Label("Style:", getSkin());
        table.add(label).padRight(10.0f);

        styleBox = new DraggableSelectBox(skin);
        styleBox.setAlignment(Align.left);
        styleBox.getDraggableTextList().setAlignment(Align.left);
        styleBox.getDraggableTextList().setAllowRemoval(false);
        styleBox.addListener(new DraggableListListener() {
            @Override
            public void removed(Actor actor) {
            }
    
            @Override
            public void reordered(Actor actor, int indexBefore, int indexAfter) {
                var selectedClass = rootTable.getSelectedClass();
                if (selectedClass != null) {
                    fire(new ReorderStylesEvent(selectedClass, indexBefore, indexAfter));
                } else {
                    var customClass = rootTable.getSelectedCustomClass();
                    fire(new ReorderCustomStylesEvent(customClass, indexBefore, indexAfter));
                }
                styleBox.getPopTable().hide();
            }
    
            @Override
            public void selected(Actor actor) {
                fire(new RootTable.RootTableEvent(RootTable.RootTableEnum.STYLE_SELECTED));
            }
        });
        
        table.add(styleBox).padRight(5.0f).minWidth(150.0f);
        styleBox.addListener(handListener);
        styleBox.getDraggableTextList().addListener(handListener);
        styleBox.getScrollPane().addListener(scrollFocusListener);

        button = new Button(getSkin(), "new");
        button.addListener(handListener);
        table.add(button);

        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                fire(new RootTableEvent(RootTableEnum.NEW_STYLE));
            }
        });
        
        toolTip = (Main.makeTooltip("New Style", tooltipManager, getSkin()));
        button.addListener(toolTip);

        button = new Button(getSkin(), "duplicate");
        button.addListener(handListener);
        table.add(button);

        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                fire(new RootTableEvent(RootTableEnum.DUPLICATE_STYLE));
            }
        });
        
        toolTip = (Main.makeTooltip("Duplicate Style", tooltipManager, getSkin()));
        button.addListener(toolTip);

        styleDeleteButton = new Button(getSkin(), "delete");
        styleDeleteButton.addListener(handListener);
        table.add(styleDeleteButton);

        styleDeleteButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                fire(new RootTableEvent(RootTableEnum.DELETE_STYLE));
            }
        });
        
        toolTip = (Main.makeTooltip("Delete Style", tooltipManager, getSkin()));
        styleDeleteButton.addListener(toolTip);

        styleRenameButton = new Button(getSkin(), "settings");
        table.add(styleRenameButton).expandX().left();

        styleRenameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                fire(new RootTableEvent(RootTableEnum.RENAME_STYLE));
            }
        });
        
        toolTip = (Main.makeTooltip("Rename Style", tooltipManager, getSkin()));
        styleRenameButton.addListener(toolTip);

        fire(new LoadClassesEvent(classSelectBox));
        fire(new LoadStylesEvent(classSelectBox, styleBox));
    }
    
    public void setClassDuplicateButtonDisabled(boolean disabled) {
        classDuplicateButton.setDisabled(disabled);
        if (disabled) {
            if (classDuplicateButton.getListeners().contains(handListener, true)) {
                classDuplicateButton.removeListener(handListener);
            }
        } else {
            if (!classDuplicateButton.getListeners().contains(handListener, true)) {
                classDuplicateButton.addListener(handListener);
            }
        }
    }
    
    public void setClassDeleteButtonDisabled(boolean disabled) {
        classDeleteButton.setDisabled(disabled);
        if (disabled) {
            if (classDeleteButton.getListeners().contains(handListener, true)) {
                classDeleteButton.removeListener(handListener);
            }
        } else {
            if (!classDeleteButton.getListeners().contains(handListener, true)) {
                classDeleteButton.addListener(handListener);
            }
        }
    }
    
    public void setClassRenameButtonDisabled(boolean disabled) {
        classRenameButton.setDisabled(disabled);
        if (disabled) {
            if (classRenameButton.getListeners().contains(handListener, true)) {
                classRenameButton.removeListener(handListener);
            }
        } else {
            if (!classRenameButton.getListeners().contains(handListener, true)) {
                classRenameButton.addListener(handListener);
            }
        }
    }
    
    public void setStyleDeleteButtonDisabled(boolean disabled) {
        styleDeleteButton.setDisabled(disabled);
        if (disabled) {
            if (styleDeleteButton.getListeners().contains(handListener, true)) {
                styleDeleteButton.removeListener(handListener);
            }
        } else {
            if (!styleDeleteButton.getListeners().contains(handListener, true)) {
                styleDeleteButton.addListener(handListener);
            }
        }
    }
    
    public void setStyleRenameButtonDisabled(boolean disabled) {
        styleRenameButton.setDisabled(disabled);
        if (disabled) {
            if (styleRenameButton.getListeners().contains(handListener, true)) {
                styleRenameButton.removeListener(handListener);
            }
        } else {
            if (!styleRenameButton.getListeners().contains(handListener, true)) {
                styleRenameButton.addListener(handListener);
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

        splitPane.addListener(horizontalResizeArrowListener);
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
            styleBox.setSelected(styleBox.getItems().size - 1);
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
        Array<StyleData> scrollPaneStyles = projectData.getJsonData().getClassStyleMap().get(ScrollPane.class);

        //gather all listStyles
        Array<StyleData> listStyles = projectData.getJsonData().getClassStyleMap().get(List.class);

        //gather all labelStyles
        Array<StyleData> labelStyles = projectData.getJsonData().getClassStyleMap().get(Label.class);
        
        if (getClassSelectBox().getSelectedIndex() < BASIC_CLASSES.length) {
            Array<StyleProperty> styleProperties = rootTable.getSelectedStyle().properties.values().toArray();
            
            //add parent selection box
            label = new Label("parent", getSkin());
            table.add(label).padTop(20.0f).fill(false).expand(false, false);
            
            table.row();
            var parentNames = new Array<String>();
            parentNames.add("None");
            
            Class recursiveClass = getSelectedClass();
            Class recursiveStyleClass = Main.basicToStyleClass(recursiveClass);
            while (recursiveStyleClass != null && Arrays.asList(Main.STYLE_CLASSES).contains(recursiveStyleClass)) {
                int index = 0;
                for (var style : jsonData.getClassStyleMap().get(recursiveClass)) {
                    if (style != null && !(style.parent != null && style.parent.equals(getSelectedStyle().name)) &&
                            !(parentNames.contains(style.name, false) || style.equals(getSelectedStyle()) && recursiveClass.equals(getSelectedClass()))
                    && (getSelectedClass() != recursiveClass || index < getSelectedStyleIndex())) {
                        parentNames.add(style.name);
                    }
                    index++;
                }
                
                recursiveClass = recursiveClass.getSuperclass();
                recursiveStyleClass = Main.basicToStyleClass(recursiveClass);
            }
            
            var parentSelectBox = new SelectBox<String>(getSkin());
            parentSelectBox.setItems(parentNames);
            parentSelectBox.setSelected(getSelectedStyle().parent);
            table.add(parentSelectBox);
            parentSelectBox.addListener(handListener);
            parentSelectBox.getList().addListener(handListener);
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
                    browseField.addListener(handListener);
                    table.add(browseField).padTop(20.0f);

                    browseField.addListener(new StylePropertyChangeListener(styleProperty, browseField));
                } else if (styleProperty.type == BitmapFont.class) {
                    BrowseField browseField;
                    if (styleProperty.optional) {
                        browseField = new BrowseField((String) styleProperty.value, styleProperty.name, getSkin(), "font");
                    } else {
                        browseField = new BrowseField((String) styleProperty.value, styleProperty.name, getSkin(), "font-required");
                    }
                    browseField.addListener(handListener);
                    table.add(browseField).padTop(20.0f);

                    browseField.addListener(new StylePropertyChangeListener(styleProperty, browseField));
                } else if (styleProperty.type == Drawable.class) {
                    BrowseField browseField;
                    if (styleProperty.optional) {
                        browseField = new BrowseField((String) styleProperty.value, styleProperty.name, getSkin(), "drawable");
                    } else {
                        browseField = new BrowseField((String) styleProperty.value, styleProperty.name, getSkin(), "drawable-required");
                    }
                    browseField.addListener(handListener);
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
                    spinner.getTextField().addListener(ibeamListener);
                    spinner.getButtonMinus().addListener(handListener);
                    spinner.getButtonPlus().addListener(handListener);
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
                    selectBox.addListener(handListener);
                    selectBox.getList().addListener(handListener);
                    
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
                    selectBox.addListener(handListener);
                    selectBox.getList().addListener(handListener);
                    
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
                    selectBox.addListener(handListener);
                    selectBox.getList().addListener(handListener);
                    
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
        } else {
            var customStyle = getSelectedCustomStyle();
            Array<CustomProperty> customProperties = customStyle.getProperties();
            
            for (CustomProperty styleProperty : customProperties) {
                if (styleProperty.getType() == PropertyType.COLOR) {
                    String value = "";
                    if (styleProperty.getValue() instanceof String) {
                        for (ColorData color : jsonData.getColors()) {
                            if (color.getName().equals(styleProperty.getValue())) {
                                value = (String) styleProperty.getValue();
                                break;
                            }
                        }
                    }
                    BrowseField browseField = new BrowseField(value, styleProperty.getName(), getSkin(), "color");
                    browseField.addListener(handListener);
                    table.add(browseField).padTop(20.0f);

                    browseField.addListener(new CustomPropertyChangeListener(styleProperty, browseField));
                } else if (styleProperty.getType() == PropertyType.FONT) {
                    String value = "";
                    if (styleProperty.getValue() instanceof String) {
                        for (FontData font : jsonData.getFonts()) {
                            if (font.getName().equals(styleProperty.getValue())) {
                                value = (String) styleProperty.getValue();
                                break;
                            }
                        }
                        
                        for (FreeTypeFontData font : jsonData.getFreeTypeFonts()) {
                            if (font.name.equals(styleProperty.getValue())) {
                                value = (String) styleProperty.getValue();
                            }
                        }
                    }
                    BrowseField browseField = new BrowseField(value, styleProperty.getName(), getSkin(), "font");
                    browseField.addListener(handListener);
                    table.add(browseField).padTop(20.0f);

                    browseField.addListener(new CustomPropertyChangeListener(styleProperty, browseField));
                } else if (styleProperty.getType() == PropertyType.DRAWABLE) {
                    String value = "";
                    if (styleProperty.getValue() instanceof String) {
                        for (DrawableData drawable : atlasData.getDrawables()) {
                            if (drawable.name.equals(styleProperty.getValue())) {
                                value = (String) styleProperty.getValue();
                                break;
                            }
                        }
                    }
                    BrowseField browseField = new BrowseField(value, styleProperty.getName(), getSkin(), "drawable");
                    browseField.addListener(handListener);
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
                    spinner.getTextField().addListener(ibeamListener);
                    spinner.getButtonMinus().addListener(handListener);
                    spinner.getButtonPlus().addListener(handListener);
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
                    textField.addListener(ibeamListener);
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
                    button.addListener(handListener);
                } else if (styleProperty.getType() == PropertyType.STYLE) {
                    String value = "";
                    if (styleProperty.getValue() instanceof String) {
                        value = (String) styleProperty.getValue();
                    }
                    BrowseField browseField = new BrowseField(value, styleProperty.getName(), getSkin(), "style");
                    browseField.addListener(handListener);
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
                
                TextTooltip toolTip = (Main.makeTooltip("Duplicate Style Property", tooltipManager, getSkin()));
                duplicateButton.addListener(toolTip);
                duplicateButton.addListener(handListener);
                
                Button deleteButton = new Button(getSkin(), "delete");
                table.add(deleteButton).fill(false).expand(false, false).pad(0).bottom();
                deleteButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event,
                            Actor actor) {
                        fire(new CustomPropertyEvent(styleProperty, duplicateButton, CustomPropertyEnum.DELETE));
                    }
                });
                
                toolTip = (Main.makeTooltip("Delete Style Property", tooltipManager, getSkin()));
                deleteButton.addListener(toolTip);
                deleteButton.addListener(handListener);
                
                Button renameButton = new Button(getSkin(), "settings");
                table.add(renameButton).fill(false).expand(false, false).pad(0).bottom();
                renameButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event,
                            Actor actor) {
                        fire(new CustomPropertyEvent(styleProperty, duplicateButton, CustomPropertyEnum.RENAME));
                    }
                });
                
                toolTip = (Main.makeTooltip("Rename Style Property", tooltipManager, getSkin()));
                renameButton.addListener(toolTip);
                renameButton.addListener(handListener);
                
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
            
            TextTooltip toolTip = (Main.makeTooltip("New Style Property", tooltipManager, getSkin()));
            button.addListener(toolTip);
            button.addListener(handListener);
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

        splitPane.addListener(verticalResizeArrowListener);
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
    
    public void refreshPreviewProperties() {
        if (previewPropertiesTable != null) {
            previewPropertiesTable.clear();
            previewProperties.clear();

            Table t = new Table();
            previewPropertiesTable.add(t).grow();
            t.defaults().pad(3.0f);
            
            previewProperties.put("bgcolor", projectData.getPreviewBgColor());

            if (classSelectBox.getSelectedIndex() >= 0 && classSelectBox.getSelectedIndex() < Main.BASIC_CLASSES.length) {
                t.add(new Label("Stage Color: ", getSkin())).right();
                BrowseField browseField = new BrowseField(null, null, getSkin(), "color");
                browseField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
    
                        dialogFactory.showDialogColorPicker((Color) previewProperties.get("bgcolor"),
                                new PopColorPickerAdapter() {
                                    @Override
                                    public void picked(Color color) {
                                        browseField.getTextButton().setText(
                                                (int) (color.r * 255) + "," + (int) (color.g * 255) + "," + (int) (color.b * 255) + "," + (int) (color.a * 255));
                                        previewProperties.put("bgcolor", color);
                                        projectData.setPreviewBgColor(color);
                                        refreshPreview();
                                    }
                                });
                    }
                });
                
                browseField.addListener(handListener);
                t.add(browseField).growX();
                var previewBgColor = projectData.getPreviewBgColor();
                browseField.getTextButton().setText((int) (previewBgColor.r * 255) + "," + (int) (previewBgColor.g * 255) + "," + (int) (previewBgColor.b * 255) + "," + (int) (previewBgColor.a * 255));

                t.row();
                t.add(new Label("Size: ", getSkin())).right();

                previewSizeSelectBox = new SelectBox<>(getSkin());
                previewSizeSelectBox.setItems(DEFAULT_SIZES);
                previewSizeSelectBox.setSelectedIndex(1);
                previewSizeSelectBox.addListener(handListener);
                previewSizeSelectBox.getList().addListener(handListener);
                t.add(previewSizeSelectBox).growX().minWidth(200.0f);
                Class clazz = Main.BASIC_CLASSES[classSelectBox.getSelectedIndex()];
                if (clazz.equals(Button.class)) {
                    t.row();
                    t.add(new Label("Disabled: ", getSkin())).right();
                    ImageTextButton disabledCheckBox = new ImageTextButton("", getSkin(), "switch");
                    disabledCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("disabled", disabledCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    disabledCheckBox.addListener(handListener);
                    previewProperties.put("disabled", disabledCheckBox.isChecked());
                    t.add(disabledCheckBox).left();
                } else if (clazz.equals(CheckBox.class)) {
                    t.row();
                    t.add(new Label("Disabled: ", getSkin())).right();
                    ImageTextButton disabledCheckBox = new ImageTextButton("", getSkin(), "switch");
                    disabledCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("disabled", disabledCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    disabledCheckBox.addListener(handListener);
                    previewProperties.put("disabled", disabledCheckBox.isChecked());
                    t.add(disabledCheckBox).left();

                    t.row();
                    t.add(new Label("Text: ", getSkin())).right();
                    TextField previewTextField = new TextField(TEXT_SAMPLE, getSkin());
                    previewTextField.setFocusTraversal(false);
                    previewTextField.addListener(ibeamListener);
                    previewTextField.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("text", previewTextField.getText());
                            refreshPreview();
                        }
                    });
                    previewProperties.put("text", previewTextField.getText());
                    t.add(previewTextField).growX();

                } else if (clazz.equals(ImageButton.class)) {
                    t.row();
                    t.add(new Label("Disabled: ", getSkin())).right();
                    ImageTextButton disabledCheckBox = new ImageTextButton("", getSkin(), "switch");
                    disabledCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("disabled", disabledCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    disabledCheckBox.addListener(handListener);
                    previewProperties.put("disabled", disabledCheckBox.isChecked());
                    t.add(disabledCheckBox).left();
                } else if (clazz.equals(ImageTextButton.class)) {
                    t.row();
                    t.add(new Label("Disabled: ", getSkin())).right();
                    ImageTextButton disabledCheckBox = new ImageTextButton("", getSkin(), "switch");
                    disabledCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("disabled", disabledCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    disabledCheckBox.addListener(handListener);
                    previewProperties.put("disabled", disabledCheckBox.isChecked());
                    t.add(disabledCheckBox).left();

                    t.row();
                    t.add(new Label("Text: ", getSkin())).right();
                    TextField previewTextField = new TextField(TEXT_SAMPLE, getSkin());
                    previewTextField.setFocusTraversal(false);
                    previewTextField.addListener(ibeamListener);
                    previewTextField.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("text", previewTextField.getText());
                            refreshPreview();
                        }
                    });
                    previewProperties.put("text", previewTextField.getText());
                    t.add(previewTextField).growX();

                } else if (clazz.equals(Label.class)) {
                    t.row();
                    t.add(new Label("Text: ", getSkin())).right();
                    TextField previewTextField = new TextField(TEXT_SAMPLE, getSkin());
                    previewTextField.setFocusTraversal(false);
                    previewTextField.addListener(ibeamListener);
                    previewTextField.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("text", previewTextField.getText());
                            refreshPreview();
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
                    listItemsTextArea.addListener(ibeamListener);
                    listItemsTextArea.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("text", listItemsTextArea.getText());
                            refreshPreview();
                        }
                    });
                    previewProperties.put("text", listItemsTextArea.getText());
                    t.add(listItemsTextArea).growX();

                } else if (clazz.equals(ProgressBar.class)) {
                    t.row();
                    t.add(new Label("Disabled: ", getSkin())).right();
                    ImageTextButton disabledCheckBox = new ImageTextButton("", getSkin(), "switch");
                    disabledCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("disabled", disabledCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    disabledCheckBox.addListener(handListener);
                    previewProperties.put("disabled", disabledCheckBox.isChecked());
                    t.add(disabledCheckBox).left();

                    t.row();
                    t.add(new Label("Value: ", getSkin())).right();
                    Spinner valueSpinner = new Spinner(0.0f, 1.0f, false, Spinner.Orientation.HORIZONTAL, getSkin());
                    valueSpinner.getTextField().setFocusTraversal(false);
                    valueSpinner.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("value", valueSpinner.getValue());
                            refreshPreview();
                        }
                    });
                    valueSpinner.getButtonMinus().addListener(handListener);
                    valueSpinner.getButtonPlus().addListener(handListener);
                    valueSpinner.getTextField().addListener(ibeamListener);
                    previewProperties.put("value", valueSpinner.getValue());
                    t.add(valueSpinner).growX();

                    t.row();
                    t.add(new Label("Minimum: ", getSkin())).right();
                    Spinner minimumSpinner = new Spinner(0.0f, 1.0f, false, Spinner.Orientation.HORIZONTAL, getSkin());
                    minimumSpinner.getTextField().setFocusTraversal(false);
                    minimumSpinner.getTextField().addListener(ibeamListener);
                    minimumSpinner.getButtonMinus().addListener(handListener);
                    minimumSpinner.getButtonPlus().addListener(handListener);
                    previewProperties.put("minimum", minimumSpinner.getValue());
                    t.add(minimumSpinner).growX();

                    t.row();
                    t.add(new Label("Maximum: ", getSkin())).right();
                    Spinner maximumSpinner = new Spinner(10f, 1.0f, false, Spinner.Orientation.HORIZONTAL, getSkin());
                    maximumSpinner.getTextField().setFocusTraversal(false);
                    maximumSpinner.getTextField().addListener(ibeamListener);
                    maximumSpinner.getButtonMinus().addListener(handListener);
                    maximumSpinner.getButtonPlus().addListener(handListener);
                    previewProperties.put("maximum", maximumSpinner.getValue());
                    t.add(maximumSpinner).growX();

                    minimumSpinner.setMaximum(maximumSpinner.getValue());
                    maximumSpinner.setMinimum(minimumSpinner.getValue());
                    minimumSpinner.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("minimum", minimumSpinner.getValue());
                            maximumSpinner.setMinimum(minimumSpinner.getValue());
                            refreshPreview();
                        }
                    });
                    maximumSpinner.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("maximum", maximumSpinner.getValue());
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
                            previewProperties.put("increment", incrementSpinner.getValue());
                            refreshPreview();
                        }
                    });
                    incrementSpinner.getTextField().addListener(ibeamListener);
                    incrementSpinner.getButtonMinus().addListener(handListener);
                    incrementSpinner.getButtonPlus().addListener(handListener);
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
                            refreshPreview();
                        }
                    });
                    selectBox.addListener(handListener);
                    selectBox.getList().addListener(handListener);
                    t.add(selectBox).growX();

                } else if (clazz.equals(ScrollPane.class)) {
                    t.row();
                    t.add(new Label("Scrollbars On Top: ", getSkin())).right();
                    ImageTextButton onTopCheckBox = new ImageTextButton("", getSkin(), "switch");
                    onTopCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("scrollbarsOnTop", onTopCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    t.add(onTopCheckBox).left();
                    onTopCheckBox.addListener(handListener);
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
                            refreshPreview();
                        }
                    });
                    t.add(hScrollPosBox).growX();
                    hScrollPosBox.addListener(handListener);
                    hScrollPosBox.getList().addListener(handListener);
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
                            refreshPreview();
                        }
                    });
                    t.add(vScrollPosBox).growX();
                    vScrollPosBox.addListener(handListener);
                    vScrollPosBox.getList().addListener(handListener);
                    previewProperties.put("vScrollBarPosition", true);

                    t.row();
                    t.add(new Label("H Scrolling Disabled: ", getSkin())).right();
                    ImageTextButton hScrollCheckBox = new ImageTextButton("", getSkin(), "switch");
                    hScrollCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("hScrollDisabled", hScrollCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    t.add(hScrollCheckBox).left();
                    hScrollCheckBox.addListener(handListener);
                    previewProperties.put("hScrollDisabled", hScrollCheckBox.isChecked());

                    t.row();
                    t.add(new Label("V Scrolling Disabled: ", getSkin())).right();
                    ImageTextButton vScrollCheckBox = new ImageTextButton("", getSkin(), "switch");
                    vScrollCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("vScrollDisabled", vScrollCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    t.add(vScrollCheckBox).left();
                    vScrollCheckBox.addListener(handListener);
                    previewProperties.put("vScrollDisabled", vScrollCheckBox.isChecked());

                    t.row();
                    t.add(new Label("Force H Scroll: ", getSkin())).right();
                    ImageTextButton forceHScrollCheckBox = new ImageTextButton("", getSkin(), "switch");
                    forceHScrollCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("forceHscroll", forceHScrollCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    t.add(forceHScrollCheckBox).left();
                    forceHScrollCheckBox.addListener(handListener);
                    previewProperties.put("forceHscroll", forceHScrollCheckBox.isChecked());

                    t.row();
                    t.add(new Label("Force V Scroll: ", getSkin())).right();
                    ImageTextButton forceVScrollCheckBox = new ImageTextButton("", getSkin(), "switch");
                    forceVScrollCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("forceVscroll", forceVScrollCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    t.add(forceVScrollCheckBox).left();
                    forceVScrollCheckBox.addListener(handListener);
                    previewProperties.put("forceVscroll", forceVScrollCheckBox.isChecked());

                    t.row();
                    t.add(new Label("Variable Size Knobs: ", getSkin())).right();
                    ImageTextButton variableSizeKnobsCheckBox = new ImageTextButton("", getSkin(), "switch");
                    variableSizeKnobsCheckBox.setChecked(true);
                    variableSizeKnobsCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("variableSizeKnobs", variableSizeKnobsCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    t.add(variableSizeKnobsCheckBox).left();
                    variableSizeKnobsCheckBox.addListener(handListener);
                    previewProperties.put("variableSizeKnobs", variableSizeKnobsCheckBox.isChecked());

                    t.row();
                    t.add(new Label("H Overscroll: ", getSkin())).right();
                    ImageTextButton hOverscrollCheckBox = new ImageTextButton("", getSkin(), "switch");
                    hOverscrollCheckBox.setChecked(true);
                    hOverscrollCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("hOverscroll", hOverscrollCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    t.add(hOverscrollCheckBox).left();
                    hOverscrollCheckBox.addListener(handListener);
                    previewProperties.put("hOverscroll", hOverscrollCheckBox.isChecked());

                    t.row();
                    t.add(new Label("V Overscroll: ", getSkin())).right();
                    ImageTextButton vOverscrollCheckBox = new ImageTextButton("", getSkin(), "switch");
                    vOverscrollCheckBox.setChecked(true);
                    vOverscrollCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("vOverscroll", vOverscrollCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    t.add(vOverscrollCheckBox).left();
                    vOverscrollCheckBox.addListener(handListener);
                    previewProperties.put("vOverscroll", vOverscrollCheckBox.isChecked());

                    t.row();
                    t.add(new Label("Fade Scroll Bars: ", getSkin())).right();
                    ImageTextButton fadeScrollCheckBox = new ImageTextButton("", getSkin(), "switch");
                    fadeScrollCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("fadeScroll", fadeScrollCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    t.add(fadeScrollCheckBox).left();
                    fadeScrollCheckBox.addListener(handListener);
                    previewProperties.put("fadeScroll", fadeScrollCheckBox.isChecked());

                    t.row();
                    t.add(new Label("Smooth Scrolling: ", getSkin())).right();
                    ImageTextButton smoothScrollCheckBox = new ImageTextButton("", getSkin(), "switch");
                    smoothScrollCheckBox.setChecked(true);
                    smoothScrollCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("smoothScroll", smoothScrollCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    t.add(smoothScrollCheckBox).left();
                    smoothScrollCheckBox.addListener(handListener);
                    previewProperties.put("smoothScroll", smoothScrollCheckBox.isChecked());

                    t.row();
                    t.add(new Label("Flick Scroll: ", getSkin())).right();
                    ImageTextButton flickScrollCheckBox = new ImageTextButton("", getSkin(), "switch");
                    flickScrollCheckBox.setChecked(true);
                    flickScrollCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("flickScroll", flickScrollCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    t.add(flickScrollCheckBox).left();
                    flickScrollCheckBox.addListener(handListener);
                    previewProperties.put("flickScroll", flickScrollCheckBox.isChecked());

                    t.row();
                    t.add(new Label("Clamp: ", getSkin())).right();
                    ImageTextButton clampCheckBox = new ImageTextButton("", getSkin(), "switch");
                    clampCheckBox.setChecked(true);
                    clampCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("clamp", clampCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    t.add(clampCheckBox).left();
                    clampCheckBox.addListener(handListener);
                    previewProperties.put("clamp", clampCheckBox.isChecked());

                    t.row();
                    t.add(new Label("Sample Text: ", getSkin())).right();
                    TextArea previewTextArea = new TextArea(PARAGRAPH_SAMPLE_EXT, getSkin());
                    previewTextArea.setFocusTraversal(false);
                    previewTextArea.setPrefRows(5);
                    previewTextArea.addListener(ibeamListener);
                    previewTextArea.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("text", previewTextArea.getText());
                            refreshPreview();
                        }
                    });
                    previewProperties.put("text", previewTextArea.getText());
                    t.add(previewTextArea).growX();

                    previewSizeSelectBox.setSelectedIndex(2);
                } else if (clazz.equals(SelectBox.class)) {
                    t.row();
                    t.add(new Label("Disabled: ", getSkin())).right();
                    ImageTextButton disabledCheckBox = new ImageTextButton("", getSkin(), "switch");
                    disabledCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("disabled", disabledCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    previewProperties.put("disabled", disabledCheckBox.isChecked());
                    disabledCheckBox.addListener(handListener);
                    t.add(disabledCheckBox).left();

                    t.row();
                    t.add(new Label("Max List Count: ", getSkin())).right();
                    Spinner spinner = new Spinner(3, 1, true, Spinner.Orientation.HORIZONTAL, getSkin());
                    spinner.getTextField().setFocusTraversal(false);
                    spinner.setMinimum(1);
                    spinner.getTextField().addListener(ibeamListener);
                    spinner.getButtonMinus().addListener(handListener);
                    spinner.getButtonPlus().addListener(handListener);
                    t.add(spinner).growX();
                    spinner.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("max-list-count", spinner.getValueAsInt());
                            refreshPreview();
                        }
                    });
                    previewProperties.put("max-list-count", spinner.getValueAsInt());

                    t.row();
                    t.add(new Label("List Items: ", getSkin())).right();
                    TextArea listItemsTextArea = new TextArea("Lorem ipsum\ndolor sit\namet, consectetur", getSkin());
                    listItemsTextArea.setFocusTraversal(false);
                    listItemsTextArea.setPrefRows(3);
                    listItemsTextArea.addListener(ibeamListener);
                    listItemsTextArea.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("text", listItemsTextArea.getText());
                            refreshPreview();
                        }
                    });
                    previewProperties.put("text", listItemsTextArea.getText());
                    t.add(listItemsTextArea).growX();
                    
                    t.row();
                    t.add(new Label("Alignment: ", getSkin())).right();
                    var selectBox = new SelectBox<String>(getSkin());
                    selectBox.setItems("left", "center", "right");
                    selectBox.addListener(handListener);
                    selectBox.getList().addListener(handListener);
                    selectBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("alignment", (new int[] {Align.left, Align.center, Align.right})[selectBox.getSelectedIndex()]);
                            refreshPreview();
                        }
                    });
                    t.add(selectBox).growX();
                    previewProperties.put("alignment", (new int[] {Align.left, Align.center, Align.right})[selectBox.getSelectedIndex()]);
                } else if (clazz.equals(Slider.class)) {
                    t.row();
                    t.add(new Label("Disabled: ", getSkin())).right();
                    ImageTextButton disabledCheckBox = new ImageTextButton("", getSkin(), "switch");
                    disabledCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("disabled", disabledCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    previewProperties.put("disabled", disabledCheckBox.isChecked());
                    disabledCheckBox.addListener(handListener);
                    t.add(disabledCheckBox).left();

                    t.row();
                    t.add(new Label("Minimum: ", getSkin())).right();
                    Spinner minimumSpinner = new Spinner(0.0f, 1.0f, false, Spinner.Orientation.HORIZONTAL, getSkin());
                    minimumSpinner.getTextField().setFocusTraversal(false);
                    minimumSpinner.getTextField().addListener(ibeamListener);
                    t.add(minimumSpinner).growX();
                    minimumSpinner.getButtonMinus().addListener(handListener);
                    minimumSpinner.getButtonPlus().addListener(handListener);
                    previewProperties.put("minimum", minimumSpinner.getValue());

                    t.row();
                    t.add(new Label("Maximum: ", getSkin())).right();
                    Spinner maximumSpinner = new Spinner(100.0f, 1.0f, false, Spinner.Orientation.HORIZONTAL, getSkin());
                    maximumSpinner.getTextField().setFocusTraversal(false);
                    maximumSpinner.getTextField().addListener(ibeamListener);
                    t.add(maximumSpinner).growX();
                    maximumSpinner.getButtonMinus().addListener(handListener);
                    maximumSpinner.getButtonPlus().addListener(handListener);
                    previewProperties.put("maximum", maximumSpinner.getValue());
                    
                    minimumSpinner.setMaximum(maximumSpinner.getValue());
                    maximumSpinner.setMinimum(minimumSpinner.getValue());
                    minimumSpinner.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("minimum", minimumSpinner.getValue());
                            maximumSpinner.setMinimum(minimumSpinner.getValue());
                            refreshPreview();
                        }
                    });
                    maximumSpinner.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("maximum", maximumSpinner.getValue());
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
                            previewProperties.put("increment", incrementSpinner.getValue());
                            refreshPreview();
                        }
                    });
                    incrementSpinner.getTextField().addListener(ibeamListener);
                    t.add(incrementSpinner).growX();
                    incrementSpinner.getButtonMinus().addListener(handListener);
                    incrementSpinner.getButtonPlus().addListener(handListener);
                    incrementSpinner.setMinimum(1.0f);
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
                            refreshPreview();
                        }
                    });
                    selectBox.addListener(handListener);
                    selectBox.getList().addListener(handListener);
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
                            refreshPreview();
                        }
                    });
                    selectBox.addListener(handListener);
                    selectBox.getList().addListener(handListener);
                    t.add(selectBox).growX();

                    t.row();
                    t.add(new Label("Sample Text: ", getSkin())).right();
                    TextArea textArea = new TextArea(PARAGRAPH_SAMPLE, getSkin());
                    textArea.setFocusTraversal(false);
                    textArea.setPrefRows(5);
                    textArea.addListener(ibeamListener);
                    textArea.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("text", textArea.getText());
                            refreshPreview();
                        }
                    });
                    previewProperties.put("text", textArea.getText());
                    t.add(textArea).growX();

                    previewSizeSelectBox.setSelectedIndex(2);
                } else if (clazz.equals(TextButton.class)) {
                    t.row();
                    t.add(new Label("Disabled: ", getSkin())).right();
                    ImageTextButton disabledCheckBox = new ImageTextButton("", getSkin(), "switch");
                    disabledCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("disabled", disabledCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    previewProperties.put("disabled", disabledCheckBox.isChecked());
                    disabledCheckBox.addListener(handListener);
                    t.add(disabledCheckBox).left();

                    t.row();
                    t.add(new Label("Text: ", getSkin())).right();
                    TextField previewTextField = new TextField(TEXT_SAMPLE, getSkin());
                    previewTextField.setFocusTraversal(false);
                    previewTextField.addListener(ibeamListener);
                    previewTextField.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("text", previewTextField.getText());
                            refreshPreview();
                        }
                    });
                    previewProperties.put("text", previewTextField.getText());
                    t.add(previewTextField).growX();

                } else if (clazz.equals(TextField.class)) {
                    t.row();
                    t.add(new Label("Disabled: ", getSkin())).right();
                    ImageTextButton disabledCheckBox = new ImageTextButton("", getSkin(), "switch");
                    disabledCheckBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("disabled", disabledCheckBox.isChecked());
                            refreshPreview();
                        }
                    });
                    previewProperties.put("disabled", disabledCheckBox.isChecked());
                    disabledCheckBox.addListener(handListener);
                    t.add(disabledCheckBox).left();

                    t.row();
                    t.add(new Label("Password Mode: ", getSkin())).right();
                    ImageTextButton checkBox = new ImageTextButton("", getSkin(), "switch");
                    checkBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("passwordMode", checkBox.isChecked());
                            refreshPreview();
                        }
                    });
                    t.add(checkBox).left();
                    checkBox.addListener(handListener);
                    previewProperties.put("passwordMode", checkBox.isChecked());

                    t.row();
                    t.add(new Label("Password Character: ", getSkin()));
                    TextField pcTextField = new TextField("*", getSkin());
                    pcTextField.setFocusTraversal(false);
                    pcTextField.addListener(ibeamListener);
                    pcTextField.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("password", pcTextField.getText());
                            refreshPreview();
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
                            refreshPreview();
                        }
                    });
                    t.add(selectBox).growX();
                    selectBox.addListener(handListener);
                    selectBox.getList().addListener(handListener);
                    previewProperties.put("alignment", Align.left);

                    t.row();
                    t.add(new Label("Text: ", getSkin())).right();
                    TextField previewTextField = new TextField(TEXT_SAMPLE, getSkin());
                    previewTextField.setFocusTraversal(false);
                    previewTextField.addListener(ibeamListener);
                    previewTextField.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("text", previewTextField.getText());
                            refreshPreview();
                        }
                    });
                    previewProperties.put("text", previewTextField.getText());
                    t.add(previewTextField).growX();

                    t.row();
                    t.add(new Label("Message Text: ", getSkin())).right();
                    TextField messageTextField = new TextField(TEXT_SAMPLE, getSkin());
                    messageTextField.setFocusTraversal(false);
                    messageTextField.addListener(ibeamListener);
                    messageTextField.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("message", messageTextField.getText());
                            refreshPreview();
                        }
                    });
                    previewProperties.put("message", messageTextField.getText());
                    t.add(messageTextField).growX();

                } else if (clazz.equals(TextTooltip.class)) {
                    t.row();
                    t.add(new Label("Text: ", getSkin())).right();
                    TextField previewTextField = new TextField(TEXT_SAMPLE, getSkin());
                    previewTextField.setFocusTraversal(false);
                    previewTextField.addListener(ibeamListener);
                    previewTextField.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("text", previewTextField.getText());
                            refreshPreview();
                        }
                    });
                    previewProperties.put("text", previewTextField.getText());
                    t.add(previewTextField).growX();

                } else if (clazz.equals(Touchpad.class)) {

                } else if (clazz.equals(Tree.class)) {
                    t.row();
                    t.add(new Label("Icon Spacing Left: ", getSkin())).right();
                    Spinner spinner = new Spinner(2, 1.0, false, Spinner.Orientation.HORIZONTAL, getSkin());
                    spinner.getTextField().setFocusTraversal(false);
                    spinner.setMinimum(1);
                    spinner.getTextField().addListener(ibeamListener);
                    spinner.getButtonMinus().addListener(handListener);
                    spinner.getButtonPlus().addListener(handListener);
                    spinner.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent changeEvent, Actor actor) {
                            previewProperties.put("icon-spacing-left", ((Spinner) actor).getValueAsInt());
                            refreshPreview();
                        }
                    });
                    previewProperties.put("icon-spacing-left", spinner.getValueAsInt());
                    t.add(spinner).growX();
                    
                    t.row();
                    t.add(new Label("Icon Spacing Right: ", getSkin())).right();
                    spinner = new Spinner(2, 1.0, false, Spinner.Orientation.HORIZONTAL, getSkin());
                    spinner.getTextField().setFocusTraversal(false);
                    spinner.setMinimum(1);
                    spinner.getTextField().addListener(ibeamListener);
                    spinner.getButtonMinus().addListener(handListener);
                    spinner.getButtonPlus().addListener(handListener);
                    spinner.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent changeEvent, Actor actor) {
                            previewProperties.put("icon-spacing-right", ((Spinner) actor).getValueAsInt());
                            refreshPreview();
                        }
                    });
                    previewProperties.put("icon-spacing-right", spinner.getValueAsInt());
                    t.add(spinner).growX();
                    
                    t.row();
                    t.add(new Label("Indent Spacing: ", getSkin())).right();
                    spinner = new Spinner(0, 1.0, false, Spinner.Orientation.HORIZONTAL, getSkin());
                    spinner.getTextField().setFocusTraversal(false);
                    spinner.setMinimum(0);
                    spinner.getTextField().addListener(ibeamListener);
                    spinner.getButtonMinus().addListener(handListener);
                    spinner.getButtonPlus().addListener(handListener);
                    spinner.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent changeEvent, Actor actor) {
                            previewProperties.put("indent-spacing", ((Spinner) actor).getValueAsInt());
                            refreshPreview();
                        }
                    });
                    previewProperties.put("indent-spacing", spinner.getValueAsInt());
                    t.add(spinner).growX();

                    t.row();
                    t.add(new Label("Y Spacing: ", getSkin())).right();
                    spinner = new Spinner(4, 1.0, false, Spinner.Orientation.HORIZONTAL, getSkin());
                    spinner.getTextField().setFocusTraversal(false);
                    spinner.setMinimum(1);
                    spinner.getTextField().addListener(ibeamListener);
                    spinner.getButtonMinus().addListener(handListener);
                    spinner.getButtonPlus().addListener(handListener);
                    spinner.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent changeEvent, Actor actor) {
                            previewProperties.put("y-spacing", ((Spinner) actor).getValueAsInt());
                            refreshPreview();
                        }
                    });
                    previewProperties.put("y-spacing", spinner.getValueAsInt());
                    t.add(spinner).growX();

                } else if (clazz.equals(Window.class)) {
                    t.row();
                    t.add(new Label("Title Text: ", getSkin())).right();
                    TextField previewTextField = new TextField(TEXT_SAMPLE, getSkin());
                    previewTextField.setFocusTraversal(false);
                    previewTextField.addListener(ibeamListener);
                    previewTextField.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("title", previewTextField.getText());
                            refreshPreview();
                        }
                    });
                    previewProperties.put("title", previewTextField.getText());
                    t.add(previewTextField).growX();

                    t.row();
                    t.add(new Label("Sample Text Color: ", getSkin()));
                    BrowseField textColorField = new BrowseField(null, null, getSkin(), "color");
                    textColorField.addListener(handListener);
                    t.add(textColorField).growX();

                    t.row();
                    t.add(new Label("Sample Text: ", getSkin())).right();
                    TextArea textArea = new TextArea(PARAGRAPH_SAMPLE, getSkin());
                    textArea.setFocusTraversal(false);
                    textArea.setPrefRows(5);
                    textArea.addListener(ibeamListener);
                    textArea.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            previewProperties.put("text", textArea.getText());
                            refreshPreview();
                        }
                    });
                    previewProperties.put("text", textArea.getText());
                    t.add(textArea).growX();
                }

                previewSizeSelectBox.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        previewProperties.put("size", previewSizeSelectBox.getSelectedIndex());
                        if (previewSizeSelectBox.getSelectedIndex() != 8) {
                            refreshPreview();
                        }
                    }
                });
                previewProperties.put("size", previewSizeSelectBox.getSelectedIndex());

                refreshPreview();
            } else {
                t.add(new Label("Stage Color: ", getSkin())).right();
                BrowseField browseField = new BrowseField(null, null, getSkin(), "color");
                browseField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        dialogFactory.showDialogColorPicker((Color) previewProperties.get("bgcolor"), new PopColorPickerAdapter() {
                            @Override
                            public void picked(Color color) {
                                browseField.getTextButton().setText(
                                        (int) (color.r * 255) + "," + (int) (color.g * 255) + "," + (int) (color.b * 255) + "," + (int) (color.a * 255));
                                previewProperties.put("bgcolor", color);
                                projectData.setPreviewBgColor(color);
                                refreshPreview();
                            }
                        });
                    }
                });
                
                browseField.addListener(handListener);
                t.add(browseField).growX();
                
                var previewBgColor = projectData.getPreviewBgColor();
                browseField.getTextButton().setText((int) (previewBgColor.r * 255) + "," + (int) (previewBgColor.g * 255) + "," + (int) (previewBgColor.b * 255) + "," + (int) (previewBgColor.a * 255));
            }
        }
    }
    
    public void refreshPreview() {        
        if (previewTable != null) {
            previewTable.clear();
            previewTable.setBackground("white");
            previewTable.setColor((Color) previewProperties.get("bgcolor"));

            for (BitmapFont font : previewFonts) {
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
                    Actor widget = null;
                    
                    var style = createPreviewStyle(Main.basicToStyleClass(clazz), styleData);
                    
                    if (style != null) {
                        if (clazz.equals(Button.class)) {
                            widget = new Button((ButtonStyle) style);
                            ((Button) widget).setDisabled((boolean) previewProperties.get("disabled"));
                            widget.addListener(handListener);
                        } else if (clazz.equals(CheckBox.class)) {
                            widget = new CheckBox("", (CheckBoxStyle) style);
                            ((CheckBox) widget).setDisabled((boolean) previewProperties.get("disabled"));
                            ((CheckBox) widget).setText((String) previewProperties.get("text"));
                            widget.addListener(handListener);
                        } else if (clazz.equals(ImageButton.class)) {
                            widget = new ImageButton((ImageButtonStyle) style);
                            ((ImageButton) widget).setDisabled((boolean) previewProperties.get("disabled"));
                            widget.addListener(handListener);
                        } else if (clazz.equals(ImageTextButton.class)) {
                            widget = new ImageTextButton("", (ImageTextButtonStyle) style);
                            ((ImageTextButton) widget).setDisabled((boolean) previewProperties.get("disabled"));
                            ((ImageTextButton) widget).setText((String) previewProperties.get("text"));
                            widget.addListener(handListener);
                        } else if (clazz.equals(Label.class)) {
                            widget = new Label("", (LabelStyle) style);
                            ((Label) widget).setText((String) previewProperties.get("text"));
                        } else if (clazz.equals(List.class)) {
                            widget = new List((ListStyle) style);
                            Array<String> items = new Array<>(((String) previewProperties.get("text")).split("\\n"));
                            ((List) widget).setItems(items);
                            widget.addListener(handListener);
                        } else if (clazz.equals(ProgressBar.class)) {
                            widget = new ProgressBar((float) (double) previewProperties.get("minimum"),
                                    (float) (double) previewProperties.get("maximum"),
                                    (float) (double) previewProperties.get("increment"),
                                    (boolean) previewProperties.get("orientation"), (ProgressBarStyle) style);
                            ((ProgressBar) widget).setValue((float) (double) previewProperties.get("value"));
                            ((ProgressBar) widget).setDisabled((boolean) previewProperties.get("disabled"));
                        } else if (clazz.equals(ScrollPane.class)) {
                            Label label = new Label("", getSkin());
                            widget = new ScrollPane(label, (ScrollPaneStyle) style);
                            ((ScrollPane) widget).setScrollbarsOnTop(
                                    (boolean) previewProperties.get("scrollbarsOnTop"));
                            ((ScrollPane) widget).setScrollBarPositions(
                                    (boolean) previewProperties.get("hScrollBarPosition"),
                                    (boolean) previewProperties.get("vScrollBarPosition"));
                            ((ScrollPane) widget).setScrollingDisabled(
                                    (boolean) previewProperties.get("hScrollDisabled"),
                                    (boolean) previewProperties.get("vScrollDisabled"));
                            ((ScrollPane) widget).setForceScroll((boolean) previewProperties.get("forceHscroll"),
                                    (boolean) previewProperties.get("forceVscroll"));
                            ((ScrollPane) widget).setVariableSizeKnobs(
                                    (boolean) previewProperties.get("variableSizeKnobs"));
                            ((ScrollPane) widget).setOverscroll((boolean) previewProperties.get("hOverscroll"),
                                    (boolean) previewProperties.get("vOverscroll"));
                            ((ScrollPane) widget).setFadeScrollBars((boolean) previewProperties.get("fadeScroll"));
                            ((ScrollPane) widget).setSmoothScrolling((boolean) previewProperties.get("smoothScroll"));
                            ((ScrollPane) widget).setFlickScroll((boolean) previewProperties.get("flickScroll"));
                            ((ScrollPane) widget).setClamp((boolean) previewProperties.get("clamp"));
                            label.setText((String) previewProperties.get("text"));
                        } else if (clazz.equals(SelectBox.class)) {
                            widget = new SelectBox((SelectBoxStyle) style);
                            ((SelectBox) widget).setDisabled((boolean) previewProperties.get("disabled"));
                            Array<String> items = new Array<>(((String) previewProperties.get("text")).split("\\n"));
                            ((SelectBox) widget).setItems(items);
                            ((SelectBox) widget).setMaxListCount((int) previewProperties.get("max-list-count"));
                            ((SelectBox) widget).setAlignment((int) previewProperties.get("alignment"));
                            widget.addListener(handListener);
                            ((SelectBox) widget).getList().addListener(handListener);
                        } else if (clazz.equals(Slider.class)) {
                            widget = new Slider((float) (double) previewProperties.get("minimum"),
                                    (float) (double) previewProperties.get("maximum"),
                                    (float) (double) previewProperties.get("increment"),
                                    (boolean) previewProperties.get("orientation"), (SliderStyle) style);
                            ((Slider) widget).setDisabled((boolean) previewProperties.get("disabled"));
                            widget.addListener(handListener);
                        } else if (clazz.equals(SplitPane.class)) {
                            var table1 = new Table();
                            Label label1 = new Label("", getSkin());
                            table1.add(label1).minSize(0);
        
                            var table2 = new Table();
                            Label label2 = new Label("", getSkin());
                            table2.add(label2).minSize(0);
        
                            widget = new SplitPane(table1, table2, (boolean) previewProperties.get("orientation"),
                                    (SplitPaneStyle) style);
                            ((SplitPane) widget).setMinSplitAmount(0);
                            ((SplitPane) widget).setMaxSplitAmount(1);
                            label1.setText((String) previewProperties.get("text"));
                            label2.setText((String) previewProperties.get("text"));
        
                            if ((boolean) previewProperties.get("orientation")) {
                                widget.addListener(verticalResizeArrowListener);
                            } else {
                                widget.addListener(horizontalResizeArrowListener);
                            }
                        } else if (clazz.equals(TextButton.class)) {
                            widget = new TextButton("", (TextButtonStyle) style);
                            ((TextButton) widget).setDisabled((boolean) previewProperties.get("disabled"));
                            ((TextButton) widget).setText((String) previewProperties.get("text"));
                            widget.addListener(handListener);
                        } else if (clazz.equals(TextField.class)) {
                            widget = new TextField("", (TextFieldStyle) style);
                            ((TextField) widget).setFocusTraversal(false);
                            ((TextField) widget).setDisabled((boolean) previewProperties.get("disabled"));
                            ((TextField) widget).setPasswordMode((boolean) previewProperties.get("passwordMode"));
                            ((TextField) widget).setAlignment((int) previewProperties.get("alignment"));
                            ((TextField) widget).setText((String) previewProperties.get("text"));
                            ((TextField) widget).setMessageText((String) previewProperties.get("message"));
                            String string = (String) previewProperties.get("password");
                            if (string.length() > 0) {
                                ((TextField) widget).setPasswordCharacter(string.charAt(0));
                            }
                            widget.addListener(ibeamListener);
                        } else if (clazz.equals(TextTooltip.class)) {
                            TooltipManager manager = new TooltipManager();
                            manager.animations = false;
                            manager.initialTime = 0.0f;
                            manager.resetTime = 0.0f;
                            manager.subsequentTime = 0.0f;
                            manager.hideAll();
                            manager.instant();
                            TextTooltip toolTip = (Main.makeTooltip(
                                    (String) previewProperties.get("text"), manager, (TextTooltipStyle) style));

                            widget = new Label("Hover over me", getSkin());
                            widget.addListener(toolTip);
                        } else if (clazz.equals(Touchpad.class)) {
                            widget = new Touchpad(0, (TouchpadStyle) style);
                            widget.addListener(handListener);
                        } else if (clazz.equals(Tree.class)) {
                            widget = new Tree((TreeStyle) style);
                            ((Tree) widget).setIconSpacing((int) previewProperties.get("icon-spacing-left"),
                                    (int) previewProperties.get("icon-spacing-right"));
                            ((Tree) widget).setIndentSpacing((int) previewProperties.get("indent-spacing"));
                            ((Tree) widget).setYSpacing((int) previewProperties.get("y-spacing"));
                            String[] lines = {"this", "is", "a", "test"};
                            Tree.Node parentNode = null;
                            for (String line : lines) {
                                Label label = new Label(line, getSkin());
                                Tree.Node node = new Tree.Node(label) {
                
                                };
                                if (parentNode == null) {
                                    ((Tree) widget).add(node);
                                } else {
                                    parentNode.add(node);
                                }
                                parentNode = node;
                            }
                            widget.addListener(handListener);
                        } else if (clazz.equals(Window.class)) {
                            WindowStyle windowStyle = (WindowStyle) style;
                            if (windowStyle.stageBackground != null) {
                                previewTable.setBackground(windowStyle.stageBackground);
                                previewTable.setColor(Color.WHITE);
                                windowStyle.stageBackground = null;
                            }
        
                            Label sampleText = new Label("", getSkin());
                            sampleText.setText((String) previewProperties.get("text"));
        
                            widget = new Window((String) previewProperties.get("title"), windowStyle);
                            ((Window) widget).add(sampleText);
                        }
                    }

                    if (widget != null) {
                        switch ((int) previewProperties.get("size")) {
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
                                previewResizeWidget.setActor(widget);
                                previewResizeWidget.setTouchable(Touchable.enabled);
                                previewTable.add(previewResizeWidget).grow();
                                previewSizeSelectBox.setItems(DEFAULT_SIZES);
                                break;
                            case (7):
                                Actor addWidget = widget;
                                TraversalTextField widthField = new TraversalTextField("", getSkin());
                                widthField.setText(Integer.toString(projectData.getPreviewCustomWidth()));
                                TraversalTextField heightField = new TraversalTextField("", getSkin());
                                heightField.setText(Integer.toString(projectData.getPreviewCustomHeight()));
                                widthField.setNextFocus(heightField);
                                heightField.setNextFocus(widthField);
                                Dialog dialog = new Dialog("Enter dimensions...", getSkin()) {
                                    @Override
                                    protected void result(Object object) {
                                        if ((boolean)object) {
                                            previewTable.add(addWidget).size(Integer.parseInt(widthField.getText()), Integer.parseInt(heightField.getText()));
                                            Array<String> items = new Array<>(DEFAULT_SIZES);
                                            items.add(widthField.getText() + "x" + heightField.getText());
                                            
                                            int width = Integer.parseInt(widthField.getText());
                                            int height = Integer.parseInt(heightField.getText());
                                            previewProperties.put("sizeX", width);
                                            previewProperties.put("sizeY", height);
                                            projectData.setPreviewCustomSize(width, height);
                                            
                                            previewSizeSelectBox.setItems(items);
                                            previewSizeSelectBox.setSelectedIndex(8);
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
                                dialog.key(Keys.ENTER, true).key(Keys.NUMPAD_ENTER, true).key(Keys.ESCAPE, false);
                                TextButton okButton = (TextButton) dialog.getButtonTable().getCells().first().getActor();
                                okButton.addListener(handListener);
                                widthField.addListener(ibeamListener);
                                widthField.addListener(new ChangeListener() {
                                    @Override
                                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                                        okButton.setDisabled(!widthField.getText().matches("^\\d+$") || !heightField.getText().matches("^\\d+$"));
                                    }
                                });
                                heightField.addListener(ibeamListener);
                                heightField.addListener(new ChangeListener() {
                                    @Override
                                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                                        okButton.setDisabled(!widthField.getText().matches("^\\d+$") || !heightField.getText().matches("^\\d+$"));
                                    }
                                });
                                dialog.getButtonTable().getCells().get(1).getActor().addListener(handListener);
                                dialog.key(Input.Keys.ESCAPE, false);
                                dialog.show(stage);
                                stage.setKeyboardFocus(widthField);
                                break;
                            case (8):
                                previewTable.add(widget).size((int) previewProperties.get("sizeX"), (int) previewProperties.get("sizeY"));
                                break;
                        }
                    }
                }
            } else {
                var customClass = (CustomClass) classSelectBox.getSelected();
                CustomStyle customStyle = customClass.getStyles().get(styleBox.getSelectedIndex());
                
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
                 
                if (!showMessage) {
                    HorizontalGroup horizontalGroup = new HorizontalGroup();
                    horizontalGroup.wrap();
                    horizontalGroup.wrapSpace(10.0f);
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
                                    for (ColorData cd : jsonData.getColors()) {
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
                                        colorTable.setTouchable(Touchable.enabled);

                                        container.setActor(colorTable);
                                        container.addListener((Main.makeTooltip(colorName, tooltipManager, getSkin())));
                                    }
                                    break;
                                case FONT:
                                    if (!(customProperty.getValue() instanceof String)) {
                                        customProperty.setValue("");
                                    }

                                    BitmapFont font = null;
                                    FontData fontData = null;

                                    String fontName = (String) customProperty.getValue();
                                    for (FontData fd : jsonData.getFonts()) {
                                        if (fd.getName().equals(fontName)) {
                                            fontData = fd;
                                            font = new BitmapFont(fd.file, fd.isFlip());
                                            if (fd.getScaling() != -1) font.getData().setScale(fd.getScaling() / font.getCapHeight());
                                            font.getData().markupEnabled = fd.isMarkupEnabled();
                                            font.getData().flipped = fd.isFlip();
                                            previewFonts.add(font);
                                            break;
                                        }
                                    }

                                    if (font != null) {
                                        Label labelFont = new Label(fontData.getName(), new LabelStyle(font, Color.WHITE));
                                        container.setActor(labelFont);
    
                                        container.addListener((Main.makeTooltip(fontData.getName(), tooltipManager, getSkin())));
                                    }

                                    FreeTypeFontData freeTypeFontData = null;
                                    for (FreeTypeFontData fd : jsonData.getFreeTypeFonts()) {
                                        if (fd.name.equals(fontName)) {
                                            freeTypeFontData = fd;
                                            break;
                                        }
                                    }

                                    if (freeTypeFontData != null && freeTypeFontData.bitmapFont != null) {
                                        Label labelFont = new Label(freeTypeFontData.name, new LabelStyle(freeTypeFontData.bitmapFont, Color.WHITE));
                                        container.setActor(labelFont);
    
                                        container.addListener((Main.makeTooltip(freeTypeFontData.name, tooltipManager, getSkin())));
                                    }

                                    break;
                                case DRAWABLE:
                                    if (!(customProperty.getValue() instanceof String)) {
                                        customProperty.setValue("");
                                    }

                                    DrawableData drawable = null;

                                    String drawableName = (String) customProperty.getValue();
                                    for (DrawableData dd : atlasData.getDrawables()) {
                                        if (dd.name.equals(drawableName)) {
                                            drawable = dd;
                                            break;
                                        }
                                    }

                                    if (drawable != null) {
                                        Image image = new Image(atlasData.getDrawablePairs().get(drawable));
                                        container.setActor(image);
                                        
                                        container.addListener((Main.makeTooltip(drawable.name, tooltipManager, getSkin())));
                                    }
                                    break;
                            }
                        }
                    }
                }
            }
        }
    }
    
    public <T> T createPreviewStyle(Class<T> clazz, StyleData styleData) {
        T returnValue = null;
        try {
            T instance = ClassReflection.newInstance(clazz);
            Field[] fields = ClassReflection.getFields(clazz);
            for (Field field : fields) {
                Object value = styleData.getInheritedValue(field.getName());
                if (value != null) {
                    if (field.getType().equals(Drawable.class)) {
                        field.set(instance, atlasData.getDrawablePairs().get(atlasData.getDrawable((String) value)));
                    } else if (field.getType().equals(Color.class)) {
                        for (ColorData data : projectData.getJsonData().getColors()) {
                            if (value.equals(data.getName())) {
                                field.set(instance, data.color);
                                break;
                            }
                        }
                    } else if (field.getType().equals(BitmapFont.class)) {
                        for (FontData data : projectData.getJsonData().getFonts()) {
                            if (value.equals(data.getName())) {
                                BitmapFont font = new BitmapFont(data.file, data.isFlip());
                                if (data.getScaling() != -1) font.getData().setScale(data.getScaling() / font.getCapHeight());
                                font.getData().markupEnabled = data.isMarkupEnabled();
                                font.getData().flipped = data.isFlip();
                                previewFonts.add(font);
                                field.set(instance, font);
                            }
                        }
                        
                        for (FreeTypeFontData data : jsonData.getFreeTypeFonts()) {
                            if (value.equals(data.name)) {
                                field.set(instance, data.bitmapFont);
                            }
                        }
                    } else if (field.getType().equals(Float.TYPE)) {
                        field.set(instance, (float) (double) value);
                    } else if (field.getType().equals(ListStyle.class)) {
                        Array<StyleData> datas = projectData.getJsonData().getClassStyleMap().get(List.class);

                        for (StyleData data : datas) {
                            if (value.equals(data.name)) {
                                ListStyle style = createPreviewStyle(ListStyle.class, data);
                                field.set(instance, style);
                                break;
                            }
                        }
                    } else if (field.getType().equals(ScrollPaneStyle.class)) {
                        Array<StyleData> datas = projectData.getJsonData().getClassStyleMap().get(ScrollPane.class);

                        for (StyleData data : datas) {
                            if (value.equals(data.name)) {
                                ScrollPaneStyle style = createPreviewStyle(ScrollPaneStyle.class, data);
                                field.set(instance, style);
                                break;
                            }
                        }
                    } else if (field.getType().equals(LabelStyle.class)) {
                        Array<StyleData> datas = projectData.getJsonData().getClassStyleMap().get(Label.class);

                        for (StyleData data : datas) {
                            if (value.equals(data.name)) {
                                LabelStyle style = createPreviewStyle(LabelStyle.class, data);
                                field.set(instance, style);
                                break;
                            }
                        }
                    }
                }
            }
            returnValue = instance;
        } catch (Exception e) {
            Gdx.app.error(RootTable.class.getName(), "Error creating style", e);
            dialogFactory.showDialogError("Error Creating Style", "Unable to create style " + styleData.name + " for class " + clazz.getSimpleName() + "\nOpen log?");
        }
        return returnValue;
    }
    
    private void addStatusBar() {
        Table table = new Table();
        table.setBackground(getSkin().getDrawable("status-bar"));
        add(table).growX();
        
        Label label = new Label("ver. " + Main.VERSION + "    RAY3K.WORDPRESS.COM    © 2024 Raymond \"Raeleus\" Buckley", getSkin());
        table.add(label).expandX().right().padRight(25.0f);
    }

    public SelectBox getClassSelectBox() {
        return classSelectBox;
    }

    public DraggableSelectBox getStyleSelectBox() {
        return styleBox;
    }

    public Class getSelectedClass() {
        if (classSelectBox.getSelectedIndex() >= BASIC_CLASSES.length) return null;
        return Main.BASIC_CLASSES[classSelectBox.getSelectedIndex()];
    }
    
    public void setSelectedClass(Class clazz) {
        int index = IntStream.range(0, BASIC_CLASSES.length).filter(i -> clazz.equals(BASIC_CLASSES[i])).findFirst().orElse(-1);
        
        if (index != -1) classSelectBox.setSelectedIndex(index);
    }

    public StyleData getSelectedStyle() {
        var classStyleMap = projectData.getJsonData().getClassStyleMap();
        var styles = classStyleMap.get(getSelectedClass());
        return styles.get(styleBox.getSelectedIndex());
    }
    
    public int getSelectedStyleIndex() {
        return styleBox.getSelectedIndex();
    }
    
    public CustomClass getSelectedCustomClass() {
        if (getClassSelectBox().getSelectedIndex() < BASIC_CLASSES.length) return null;
        else {
            return (CustomClass) classSelectBox.getSelected();
        }
    }
    
    public CustomStyle getSelectedCustomStyle() {
        var customClass = getSelectedCustomClass();
        if (customClass == null) return null;
         else {
             return customClass.getStyles().get(styleBox.getSelectedIndex());
        }
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

    private class MenuBarListener extends ChangeListener {
        private RootTableEnum value;

        public MenuBarListener(RootTableEnum value) {
            this.value = value;
        }
    
        @Override
        public void changed(ChangeEvent event, Actor actor) {
            boolean listenForShortcuts = true;
            for (Actor temp : getStage().getActors()) {
                if (temp instanceof Dialog) {
                    listenForShortcuts = false;
                    break;
                }
            }
            
            if (listenForShortcuts) fire(new RootTableEvent(value));
        }
    }
    
    private class RecentFileListener extends ChangeListener {
        private RecentFile recentFile;
    
        public RecentFileListener(RecentFile recentFile) {
            this.recentFile = recentFile;
        }
    
        @Override
        public void changed(ChangeEvent event, Actor actor) {
            FileHandle file = recentFile.getFileHandle();
            if (file.exists()) {
                dialogFactory.showDialogLoading(() -> {
                    Gdx.app.postRunnable(() -> {
                        projectData.load(file);
                        Array<DrawableData> drawableErrors = projectData.verifyDrawablePaths();
                        Array<FontData> fontErrors = projectData.verifyFontPaths();
                        var freeTypeFontErrors = projectData.verifyFreeTypeFontPaths();
                        if (drawableErrors.size > 0 || fontErrors.size > 0 || freeTypeFontErrors.size > 0) {
                            dialogFactory.showDialogPathErrors(drawableErrors, fontErrors,
                                    freeTypeFontErrors);
                        }
        
                        if (projectData.checkForInvalidMinWidthHeight()) {
                            projectData.setLoadedVersion(Main.VERSION);
                            dialogFactory.yesNoDialog("Fix minWidth and minHeight errors?",
                                    "Old project (< v.30) detected.\nResolve minWidth and minHeight errors?",
                                    selection -> {
                                        if (selection == 0) {
                                            projectData.fixInvalidMinWidthHeight();
                                            mainListener.refreshTextureAtlas();
                                        }
                                    }, null);
                        }
                    });
                });
            }
        }
    }

    public enum RootTableEnum {
        NEW, OPEN, SAVE, SAVE_AS, IMPORT, EXPORT, EXIT, UNDO,
        REDO, SETTINGS, COLORS, FONTS, DRAWABLES, ABOUT, CLASS_SELECTED,
        NEW_CLASS, DUPLICATE_CLASS, DELETE_CLASS, RENAME_CLASS, STYLE_SELECTED,
        NEW_STYLE, DUPLICATE_STYLE, DELETE_STYLE, RENAME_STYLE, PREVIEW_PROPERTY,
        WELCOME, REFRESH_ATLAS, SCENE_COMPOSER, TEXTRA_TYPIST, DOWNLOAD_UPDATE, CHECK_FOR_UPDATES_COMPLETE;
    }

    public static class RootTableEvent extends Event {

        public RootTableEnum rootTableEnum;

        public RootTableEvent(RootTableEnum rootTableEnum) {
            this.rootTableEnum = rootTableEnum;
        }
    }

    private static class LoadClassesEvent extends Event {

        SelectBox classSelectBox;

        public LoadClassesEvent(SelectBox classSelectBox) {
            this.classSelectBox = classSelectBox;
        }
    }

    private static class LoadStylesEvent extends Event {
        SelectBox classSelectBox;
        DraggableSelectBox styleBox;

        public LoadStylesEvent(SelectBox classSelectBox, DraggableSelectBox styleBox) {
            this.classSelectBox = classSelectBox;
            this.styleBox = styleBox;
        }
    }
    
    private static class ReorderStylesEvent extends Event {
        Class widgetClass;
        int indexBefore;
        int indexAfter;
        
        public ReorderStylesEvent(Class widgetClass, int indexBefore, int indexAfter) {
            this.widgetClass = widgetClass;
            this.indexBefore = indexBefore;
            this.indexAfter = indexAfter;
        }
    }
    
    private static class ReorderCustomStylesEvent extends Event {
        CustomClass customClass;
        int indexBefore;
        int indexAfter;
        
        public ReorderCustomStylesEvent(CustomClass customClass, int indexBefore, int indexAfter) {
            this.customClass = customClass;
            this.indexBefore = indexBefore;
            this.indexAfter = indexAfter;
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

    private enum CustomPropertyEnum {
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
                loadStyles(((LoadStylesEvent) event).classSelectBox, ((LoadStylesEvent) event).styleBox);
            } else if (event instanceof  ReorderStylesEvent) {
                ReorderStylesEvent reorderStylesEvent = (ReorderStylesEvent) event;
                reorderStyles(reorderStylesEvent.widgetClass, reorderStylesEvent.indexBefore, reorderStylesEvent.indexAfter);
            } else if (event instanceof  ReorderCustomStylesEvent) {
                ReorderCustomStylesEvent reorderStylesEvent = (ReorderCustomStylesEvent) event;
                reorderCustomStyles(reorderStylesEvent.customClass, reorderStylesEvent.indexBefore, reorderStylesEvent.indexAfter);
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
        
        public abstract void loadClasses(SelectBox classSelectBox);

        public abstract void loadStyles(SelectBox classSelectBox, DraggableSelectBox styleBox);
    
        public abstract void reorderStyles(Class widgetClass, int indexBefore, int indexAfter);
    
        public abstract void reorderCustomStyles(CustomClass customClass, int indexBefore, int indexAfter);
        
        public abstract void newCustomProperty();
        
        public abstract void duplicateCustomProperty(CustomProperty customProperty);
        
        public abstract void deleteCustomProperty(CustomProperty customProperty);
        
        public abstract void customPropertyValueChanged(CustomProperty customProperty, Actor styleActor);
        
        public abstract void renameCustomProperty(CustomProperty customProperty);
    
        public abstract void droppedScmpFile(FileHandle fileHandle);
    }
    
    @Override
    public void act(float delta) {
        super.act(delta);
        
        for (var drawable : atlasData.getDrawablePairs().values()) {
            if (drawable instanceof TenPatchDrawable) {
                ((TenPatchDrawable) drawable).update(delta);
            }
        }
    }
}
