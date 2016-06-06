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
package com.ray3k.skincomposer.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.ray3k.skincomposer.panel.PanelStatusBar;
import com.ray3k.skincomposer.data.DrawableData;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Sort;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import com.kotcrab.vis.ui.widget.file.FileTypeFilter;
import com.ray3k.skincomposer.FilesDroppedListener;
import com.ray3k.skincomposer.IbeamListener;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.data.StyleProperty;
import com.ray3k.skincomposer.data.AtlasData;
import com.ray3k.skincomposer.data.JsonData;
import com.ray3k.skincomposer.data.ProjectData;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.panel.PanelClassBar;
import com.ray3k.skincomposer.panel.PanelMenuBar;
import com.ray3k.skincomposer.panel.PanelPreviewProperties;
import com.ray3k.skincomposer.panel.PanelStyleProperties;
import com.ray3k.skincomposer.utils.SynchronousJFXFileChooser;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import javafx.stage.FileChooser;

/**
 * A dialog that allows the user to manage and select drawables to be used with
 * the currently loaded project.
 * @author Raymond Buckley
 */
public class DialogDrawables extends Dialog {
    private static int[] sizes = {125, 150, 200, 250};
    private int size;
    private Table root;
    private Skin skin;
    public static DialogDrawables instance;
    private boolean resize;
    private ScrollPane scrollPane;
    private StyleProperty property;
    private Array<DrawableData> drawables;
    private SelectBox<String> selectBox;
    private TextureAtlas atlas;
    private ObjectMap<DrawableData, Drawable> drawablePairs;
    private EventListener listener;
    private FilesDroppedListener filesDroppedListener;

    /**
     * Creates the drawables dialog with a default WindowStyle.
     * @param skin
     * @param property If null, the dialog will disable selection mode.
     * @param listener
     */
    public DialogDrawables(Skin skin, StyleProperty property, EventListener listener) {
        this(skin, "default", property, listener);
    }
    
    /**
     * Creates the drawables dialog
     * @param skin
     * @param windowStyleName
     * @param property If null, the dialog will disable selection mode.
     * @param listener
     */
    public DialogDrawables(Skin skin, String windowStyleName, StyleProperty property, EventListener listener) {
        super("", skin, windowStyleName);
        
        Main.instance.setListeningForKeys(false);
        
        this.listener = listener;
        
        drawablePairs = new ObjectMap<>();
        
        this.property = property;
        size = sizes[0];
        instance = this;
        initializeDrawables();
        produceAtlas();
        
        this.skin = skin;

        filesDroppedListener = (Array<FileHandle> files) -> {
            Iterator<FileHandle> iter = files.iterator();
            while (iter.hasNext()) {
                FileHandle file = iter.next();
                if (file.isDirectory() || !(file.name().toLowerCase().endsWith(".png") || file.name().toLowerCase().endsWith(".jpg") || file.name().toLowerCase().endsWith(".jpeg") || file.name().toLowerCase().endsWith(".bmp") || file.name().toLowerCase().endsWith(".gif"))) {
                    iter.remove();
                }
            }
            if (files.size > 0) {
                drawablesSelected(files);
            }
        };
        
        Main.instance.getDesktopWorker().addFilesDroppedListener(filesDroppedListener);
        
        getTitleLabel().setAlignment(Align.center);
        
        Label label;
        if (property == null) {
            label = new Label("Drawables", skin, "title");
        } else {
            label = new Label("Select a Drawable...", skin, "title");
        }
        label.setAlignment(Align.center);
        getContentTable().add(label).growX();
        getContentTable().row();
        Table controlTable = new Table();
        controlTable.add(new Label("Sort by: ", skin)).padLeft(20.0f);
        selectBox = new SelectBox<>(skin);
        selectBox.setItems(new String[] {"A-Z", "Z-A", "Oldest", "Newest"});
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                sortBySelectedMode();
            }
        });
        controlTable.add(selectBox);
        ImageTextButtonStyle imageButtonStyle = new ImageTextButtonStyle();
        imageButtonStyle.imageUp = skin.getDrawable("image-plus");
        imageButtonStyle.imageDown = skin.getDrawable("image-plus-down");
        imageButtonStyle.up = skin.getDrawable("button-orange");
        imageButtonStyle.down = skin.getDrawable("button-orange-down");
        imageButtonStyle.over = skin.getDrawable("button-orange-over");
        imageButtonStyle.font = skin.getFont("font");
        imageButtonStyle.fontColor = skin.getColor("white");
        imageButtonStyle.downFontColor = skin.getColor("maroon");
        imageButtonStyle.imageUp = skin.getDrawable("image-plus");
        imageButtonStyle.imageDown = skin.getDrawable("image-plus-down");
        ImageTextButton imageButton = new ImageTextButton(" Add Drawable", imageButtonStyle);
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                newDrawableDialog();
            }
        });
        controlTable.add(imageButton).padLeft(10.0f);
        controlTable.add().growX();
        controlTable.add(new Label("Zoom:", skin)).padLeft(10.0f);
        final Slider slider = new Slider(0, 3, 1, false, skin);
        slider.setValue(0.0f);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                size = sizes[(int)slider.getValue()];
                populate();
            }
        });
        controlTable.add(slider).padLeft(10.0f).padRight(20.0f);
        getContentTable().add(controlTable).padTop(5.0f).growX();
        getContentTable().row();
        root = new Table();
        scrollPane = new ScrollPane(root, skin, "no-bg");
        scrollPane.setFlickScroll(false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        getContentTable().add(scrollPane).grow();
        if (property == null) {
            button("Close", false);
        } else {
            button("Clear Drawable", true);
            button("Cancel", false);
        }
        key(Keys.ESCAPE, false);
        
        sortBySelectedMode();
    }
    
    /**
     * Recreates the drawables array, only including visible drawables.
     */
    private void initializeDrawables() {
        drawables = new Array<>(AtlasData.getInstance().getDrawables());
        Iterator<DrawableData> iter = drawables.iterator();
        while(iter.hasNext()) {
            DrawableData drawable = iter.next();
            if (!drawable.visible) iter.remove();
        }
    }
    
    /**
     * Sorts by selected sort order and populates the list.
     */
    private void sortBySelectedMode() {
        switch (selectBox.getSelectedIndex()) {
            case 0:
                sortDrawablesAZ();
                break;
            case 1:
                sortDrawablesZA();
                break;
            case 2:
                sortDrawablesOldest();
                break;
            case 3:
                sortDrawablesNewest();
                break;
        }
    }
    
    /**
     * Sorts alphabetically from A to Z.
     */
    private void sortDrawablesAZ() {
        Sort.instance().sort(drawables, (DrawableData o1, DrawableData o2) -> o1.toString().compareToIgnoreCase(o2.toString()));
        populate();
    }
    
    /**
     * Sorts alphabetically from Z to A.
     */
    private void sortDrawablesZA() {
        Sort.instance().sort(drawables, (DrawableData o1, DrawableData o2) -> o1.toString().compareToIgnoreCase(o2.toString()) * -1);
        populate();
    }
    
    /**
     * Sorts by modified date with oldest first.
     */
    private void sortDrawablesOldest() {
        Sort.instance().sort(drawables, (DrawableData o1, DrawableData o2) -> {
            if (o1.file.lastModified() < o2.file.lastModified()) {
                return -1;
            } else if (o1.file.lastModified() > o2.file.lastModified()) {
                return 1;
            } else {
                return 0;
            }
        });
        populate();
    }
    
    /**
     * Sorts by modified date with newest first.
     */
    private void sortDrawablesNewest() {
        Sort.instance().sort(drawables, (DrawableData o1, DrawableData o2) -> {
            if (o1.file.lastModified() < o2.file.lastModified()) {
                return 1;
            } else if (o1.file.lastModified() > o2.file.lastModified()) {
                return -1;
            } else {
                return 0;
            }
        });
        populate();
    }
    
    @Override
    protected void result(Object object) {
        instance = null;
        if (object != null) {
            if (object instanceof DrawableData) {
                ProjectData.instance().setChangesSaved(false);
                PanelStatusBar.instance.message("Drawable selected: " + object.toString() + " for \"" + property.name + "\"");
                if (object instanceof DrawableData) {
                    DrawableData drawable = (DrawableData) object;
                    property.value = drawable.name;
                    PanelStyleProperties.instance.populate(PanelClassBar.instance.getStyleSelectBox().getSelected());
                }
            } else if (object instanceof Boolean && property != null) {
                if ((boolean) object) {
                    ProjectData.instance().setChangesSaved(false);
                    property.value = null;
                    PanelStatusBar.instance.message("Drawable emptied for \"" + property.name + "\"");
                    PanelStyleProperties.instance.populate(PanelClassBar.instance.getStyleSelectBox().getSelected());
                } else {
                    boolean hasDrawable = false;
                    for (DrawableData drawable : AtlasData.getInstance().getDrawables()) {
                        if (drawable.name.equals(property.value)) {
                            hasDrawable = true;
                            break;
                        }
                    }
                    
                    if (!hasDrawable) {
                        property.value = null;
                        PanelStatusBar.instance.message("Drawable deleted for \"" + property.name + "\"");
                        PanelStyleProperties.instance.populate(PanelClassBar.instance.getStyleSelectBox().getSelected());
                    }
                }
            }
        }
        
        if (listener != null) {
            listener.handle(null);
        }
    }

    @Override
    public Dialog show(Stage stage) {
        Dialog dialog = super.show(stage);
        stage.setScrollFocus(scrollPane);
        return dialog;
    }
    
    /**
     * Refreshes the drawables list and lays out all drawable components.
     */
    public void populate() {
        root.clear();
        
        if (drawables.size > 0) {
            float cellWidth = size;
            float cellHeight = cellWidth;
            float spacing = 10;
            int columns = (int) (root.getWidth() / (cellWidth + spacing));
            if (columns <= 0) {
                columns = 1;
            }
            int rows = MathUtils.ceil((float) drawables.size / columns);

            int cellCount = 0;
            for (int row = 0; row < rows && cellCount < drawables.size; row++) {
                if (row > 0) {
                    root.row();
                    root.defaults().padTop(0.0f);
                } else {
                    root.defaults().padTop(spacing);
                }
                for (int column = 0; column < columns && cellCount < drawables.size; column++) {
                    final DrawableData drawable = drawables.get(cellCount);
                    Button button = new Button(skin);
                    
                    //tint button
                    Table table = new Table();
                    Button tintButton = new Button(skin, "color");
                    tintButton.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            event.setBubbles(false);
                            newTintedDrawable(drawable);
                        }
                    });
                    tintButton.addCaptureListener(new ClickListener() {
                        @Override
                        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                            event.setBubbles(false);
                            return super.touchDown(event, x, y, pointer, button);
                        }
                    });
                    table.add(tintButton).left().expandX();
                    
                    //delete drawable button
                    Button deleteButton = new Button(skin, "close");
                    deleteButton.addCaptureListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            event.setBubbles(false);
                            
                            if (drawable.tint == null && checkDuplicateDrawables(drawable.file, 1)) {
                                showConfirmDeleteDialog(drawable);
                            } else {
                                AtlasData.getInstance().getDrawables().removeValue(drawable, true);
                                
                                for (Array<StyleData> datas : JsonData.getInstance().getClassStyleMap().values()) {
                                    for (StyleData data : datas) {
                                        for (StyleProperty property : data.properties.values()) {
                                            if (property != null && property.type.equals(Drawable.class) && property.value != null && property.value.equals(drawable.toString())) {
                                                property.value = null;
                                            }
                                        }
                                    }
                                }

                                PanelStyleProperties.instance.populate(PanelClassBar.instance.getStyleSelectBox().getSelected());
                                PanelPreviewProperties.instance.render();
                                
                                Main.instance.clearUndoables();
                                
                                initializeDrawables();
                                populate();
                            }
                        }
                    });
                    deleteButton.addCaptureListener(new ClickListener() {
                        @Override
                        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                            event.setBubbles(false);
                            return super.touchDown(event, x, y, pointer, button);
                        }
                        
                    });
                    table.add(deleteButton).right();
                    button.add(table).growX().padBottom(3.0f);
                    
                    button.row();
                    Table bg = new Table(skin);
                    bg.setBackground("white");
                    bg.setColor(drawable.bgColor);
                    bg.setClip(true);
                    Table image = new Table();
                    image.setBackground(drawablePairs.get(drawable));
                    bg.defaults().pad(5.0f).top();
                    if (size == sizes[0]) {
                        bg.add(image).expandY();
                    } else {
                        bg.add(image).grow();
                    }
                    button.add(bg).grow();
                    button.row();
                    Label label;
                    label = new Label(drawable.name, skin, "white");
                    label.setWrap(true);
                    label.setAlignment(Align.center);
                    label.setTouchable(Touchable.disabled);
                    button.add(label).width(cellWidth);
                    if (property != null) {
                        button.addListener(new ChangeListener() {
                            @Override
                            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                                result(drawable);
                                hide();
                            }
                        });
                    } else {
                        button.setTouchable(Touchable.childrenOnly);
                    }
                    root.add(button).width(cellWidth).minHeight(cellHeight).spaceRight(spacing).padBottom(spacing).fillY();
                    cellCount++;
                }
            }
        } else {
            Label label = new Label("No drawables have been added!", skin);
            label.setAlignment(Align.center);
            root.add(label).grow();
        }
    }
    
    /**
     * Shows a dialog to confirm deletion of all TintedDrawables based on the
     * provided drawable data. This is called when the delete button is pressed
     * on a drawable in the drawable list.
     * @param drawable 
     */
    private void showConfirmDeleteDialog(DrawableData drawable) {
        Dialog dialog = new Dialog("Delete duplicates?", skin, "dialog"){
            @Override
            protected void result(Object object) {
                if ((boolean) object) {
                    removeDuplicateDrawables(drawable.file);
                    initializeDrawables();
                    sortBySelectedMode();
                }
            }
        };
        dialog.text("Deleting this drawable will also delete one or more tinted drawables.\n"
                + "Delete duplicates?");
        dialog.button("OK", true);
        dialog.button("Cancel", false);
        dialog.key(Keys.ENTER, true);
        dialog.key(Keys.ESCAPE, false);
        dialog.show(getStage());
    }
    
    public void resized() {
        resize = true;
    }

    @Override
    public void layout() {
        super.layout();
        
        if (resize) {
            populate();
            resize = false;
        }
    }

    @Override
    public boolean remove() {
        Main.instance.setListeningForKeys(true);
        
        Main.instance.getDesktopWorker().removeFilesDroppedListener(filesDroppedListener);
        
        try {
            if (!AtlasData.getInstance().atlasCurrent) {
                AtlasData.getInstance().writeAtlas();
                AtlasData.getInstance().atlasCurrent = true;
            }
        } catch (Exception e) {
            Gdx.app.error(getClass().getName(), "Error creating atlas upon drawable dialog exit", e);
        }
        
        if (atlas != null) {
            atlas.dispose();
            atlas = null;
        }
        return super.remove();
    }

    /**
     * Writes a TextureAtlas based on drawables list. Creates drawables to be
     * displayed on screen
     * @return 
     */
    private boolean produceAtlas() {
        try {
            if (atlas != null) {
                atlas.dispose();
                atlas = null;
            }
            if (!AtlasData.getInstance().atlasCurrent) {
                AtlasData.getInstance().writeAtlas();
                AtlasData.getInstance().atlasCurrent = true;
            }
            atlas = AtlasData.getInstance().getAtlas();

            for (DrawableData data : AtlasData.getInstance().getDrawables()) {
                String name = data.file.name();
                name = DrawableData.proper(name);
                
                Drawable drawable;
                if (data.file.name().matches(".*\\.9\\.[a-zA-Z0-9]*$")) {
                    drawable = new NinePatchDrawable(atlas.createPatch(name));
                    if (data.tint != null) {
                        drawable = ((NinePatchDrawable) drawable).tint(data.tint);
                    }
                } else {
                    drawable = new TextureRegionDrawable(atlas.findRegion(name));
                    if (data.tint != null) {
                        drawable = ((TextureRegionDrawable) drawable).tint(data.tint);
                    }
                }
                
                drawablePairs.put(data, drawable);
            }
            return true;
        } catch (Exception e) {
            Gdx.app.error(getClass().getName(), "Error while attempting to generate drawables.", e);
            return false;
        }
    }
    
    /**
     * Checks if there are any drawables that have the same file name as the specified file.
     * This ignores the file extension.
     * @param handle
     * @param minimum The minimum allowed matches before it's considered a duplicate
     * @return 
     */
    private boolean checkDuplicateDrawables(FileHandle handle, int minimum) {
        int count = 0;
        String name = DrawableData.proper(handle.name());
        for (int i = 0; i < AtlasData.getInstance().getDrawables().size; i++) {
            DrawableData data = AtlasData.getInstance().getDrawables().get(i);
            if (name.equals(DrawableData.proper(data.file.name()))) {
                count++;
            }
        }
        
        return count > minimum;
    }
    
    /**
     * Removes any duplicate drawables that share the same file name. This
     * ignores the file extension and also deletes TintedDrawables from the
     * same file.
     * @param handle 
     */
    private void removeDuplicateDrawables(FileHandle handle) {
        boolean initialize = false;
        String name = DrawableData.proper(handle.name());
        for (int i = 0; i < AtlasData.getInstance().getDrawables().size; i++) {
            DrawableData data = AtlasData.getInstance().getDrawables().get(i);
            if (name.equals(DrawableData.proper(data.file.name()))) {
                AtlasData.getInstance().getDrawables().removeValue(data, true);
                
                for (Array<StyleData> datas : JsonData.getInstance().getClassStyleMap().values()) {
                    for (StyleData tempData : datas) {
                        for (StyleProperty prop : tempData.properties.values()) {
                            if (prop != null && prop.type.equals(Drawable.class) && prop.value != null && prop.value.equals(data.toString())) {
                                prop.value = null;
                            }
                        }
                    }
                }
                
                initialize = true;
                i--;
            }
        }
        
        PanelStyleProperties.instance.populate(PanelClassBar.instance.getStyleSelectBox().getSelected());
        PanelPreviewProperties.instance.render();
        
        if (initialize) {
            initializeDrawables();
        }
    }
    
    /**
     * Show an error indicating a drawable that exceeds project specifications
     */
    private void showDrawableError() {
        Dialog dialog = new Dialog("Error...", skin);
        Label label = new Label("Error while adding new drawables.\nEnsure that image dimensions are\nless than maximums specified in project.\nRolling back changes...", skin);
        label.setAlignment(Align.center);
        dialog.text(label);
        dialog.button("OK");
        dialog.show(getStage());
    }
    
    /**
     * Show a Java FX file chooser for new drawables. This allows selection of
     * multiple files at once.
     */
    private void newDrawableDialog() {
        newDrawableDialogVisUI();
    }
    
    private void newDrawableDialogWindows() {
        SynchronousJFXFileChooser chooser = new SynchronousJFXFileChooser(() -> {
            FileChooser ch = new FileChooser();
            FileChooser.ExtensionFilter ex = new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif");
            ch.getExtensionFilters().add(ex);
            ch.setTitle("Choose drawable file(s)...");
            if (ProjectData.instance().getLastDirectory() != null) {
                File file = new File(ProjectData.instance().getLastDirectory());
                if (file.exists()) {
                    ch.setInitialDirectory(file);
                }
            }
            return ch;
        });
        List<File> files = chooser.showOpenMultipleDialog();
        if (files != null && files.size() > 0) {
            drawablesSelected(files);
        }
    }
    
    private void newDrawableDialogVisUI() {
        com.kotcrab.vis.ui.widget.file.FileChooser fileChooser = PanelMenuBar.instance().getFileChooser();
        fileChooser.setMode(com.kotcrab.vis.ui.widget.file.FileChooser.Mode.OPEN);
        fileChooser.setMultiSelectionEnabled(true);
        FileHandle defaultFile = new FileHandle(ProjectData.instance().getLastDirectory());
        if (defaultFile.exists()) {
            if (defaultFile.isDirectory()) {
                fileChooser.setDirectory(defaultFile);
            } else {
                fileChooser.setDirectory(defaultFile.parent());
            }
        }
        FileTypeFilter typeFilter = new FileTypeFilter(false);
        typeFilter.addRule("Image files (*.png, *.jpg, *.jpeg, *.bmp, *.gif)", "png", "jpg", "jpeg", "bmp", "gif");
        fileChooser.setFileTypeFilter(typeFilter);
        fileChooser.setViewMode(com.kotcrab.vis.ui.widget.file.FileChooser.ViewMode.BIG_ICONS);
        fileChooser.setViewModeButtonVisible(true);
        fileChooser.setWatchingFilesEnabled(true);
        fileChooser.getTitleLabel().setText("Choose drawable file(s)...");
        fileChooser.setListener(new FileChooserAdapter() {
            @Override
            public void selected(Array<FileHandle> files) {
                if (files.size > 0) {
                    drawablesSelected(files);
                }
            }
        });
        getStage().addActor(fileChooser.fadeIn());
    }

    /**
     * Called when a selection of drawables has been chosen from the
     * newDrawablesDialog(). Adds the new drawables to the project.
     * @param files 
     */
    private void drawablesSelected(List<File> files) {
        Array<FileHandle> fileHandles = new Array<>();
        
        for (File file : files) {
            fileHandles.add(new FileHandle(file));
        }
        
        drawablesSelected(fileHandles);
    }
    
    private void drawablesSelected(Array<FileHandle> files) {
        AtlasData.getInstance().atlasCurrent = false;
        Array<DrawableData> backup = new Array<>(drawables);
        Array<FileHandle> unhandledFiles = new Array<>();
        Array<FileHandle> filesToProcess = new Array<>();
        
        ProjectData.instance().setLastDirectory(files.get(0).parent().path());
        for (FileHandle fileHandle : files) {
            if (checkDuplicateDrawables(fileHandle, 0)) {
                unhandledFiles.add(fileHandle);
            } else {
                filesToProcess.add(fileHandle);
            }
        }
        
        if (unhandledFiles.size > 0) {
            showRemoveDuplicatesDialog(unhandledFiles, backup, filesToProcess);
        } else {
            finalizeDrawables(backup, filesToProcess);
        }
    }
    
    /**
     * Shows a dialog to confirm removal of duplicate drawables that have the
     * same name without extension. This is called after selecting new drawables.
     * @param unhandledFiles
     * @param backup
     * @param filesToProcess 
     */
    private void showRemoveDuplicatesDialog(Array<FileHandle> unhandledFiles, Array<DrawableData> backup, Array<FileHandle> filesToProcess) {
        Dialog dialog = new Dialog("Delete duplicates?", skin, "dialog"){
            @Override
            protected void result(Object object) {
                if ((boolean) object) {
                    for (FileHandle fileHandle : unhandledFiles) {
                        removeDuplicateDrawables(fileHandle);
                        filesToProcess.add(fileHandle);
                    }
                }
                finalizeDrawables(backup, filesToProcess);
            }
        };
        dialog.text("Adding this drawable will overwrite one or more drawables\n"
                + "Delete duplicates?");
        dialog.button("OK", true);
        dialog.button("Cancel", false);
        dialog.key(Keys.ENTER, true);
        dialog.key(Keys.ESCAPE, false);
        dialog.show(getStage());
    }
    
    /**
     * Adds the drawables to the project.
     * @param backup If there is a failure, the drawable list will be rolled
     * back to the provided backup.
     * @param filesToProcess 
     */
    private void finalizeDrawables(Array<DrawableData> backup, Array<FileHandle> filesToProcess) {
        for (FileHandle file : filesToProcess) {
            DrawableData data = new DrawableData(file);
            if (!checkIfNameExists(data.name)) {
                AtlasData.getInstance().getDrawables().add(data);
            }
        }        
        
        initializeDrawables();

        Main.instance.showDialogLoading(() -> {
            if (!produceAtlas()) {
                showDrawableError();
                Gdx.app.log(getClass().getName(), "Attempting to reload drawables backup...");
                AtlasData.getInstance().getDrawables().clear();
                AtlasData.getInstance().getDrawables().addAll(backup);
                initializeDrawables();
                if (produceAtlas()) {
                    Gdx.app.log(getClass().getName(), "Successfully rolled back changes to drawables");
                } else {
                    Gdx.app.error(getClass().getName(), "Critical failure, could not roll back changes to drawables");
                }
            } else {
                ProjectData.instance().setChangesSaved(false);
            }

            sortBySelectedMode();
        });
    }
    
    /**
     * Creates a TintedDrawable based on the provided DrawableData. Prompts
     * user for a Color and name.
     * @param drawableData 
     */
    private void newTintedDrawable(DrawableData drawableData) {
        Color previousColor = Color.WHITE;
        if (drawableData.tint != null) {
            previousColor = drawableData.tint;
        }
        Main.instance.showDialogColorPicker(previousColor, new DialogColorPicker.ColorListener() {
            @Override
            public void selected(Color color) {
                if (color != null) {
                    final DrawableData tintedDrawable = new DrawableData(drawableData.file);
                    tintedDrawable.tint = color;
                    final TextField textField = new TextField(drawableData.name, skin);
                    final TextButton button = new TextButton("OK", skin);
                    button.setDisabled(!DrawableData.validate(textField.getText()) || checkIfNameExists(textField.getText()));
                    textField.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            button.setDisabled(!DrawableData.validate(textField.getText()) || checkIfNameExists(textField.getText()));
                        }
                    });
                    textField.addListener(IbeamListener.get());

                    Dialog dialog = new Dialog("TintedDrawable...", skin) {
                        @Override
                        protected void result(Object object) {
                            if (object instanceof Boolean && (boolean) object) {
                                tintedDrawable.name = textField.getText();
                                AtlasData.getInstance().getDrawables().add(tintedDrawable);
                            }
                        }

                        @Override
                        public boolean remove() {
                            initializeDrawables();
                            produceAtlas();
                            sortBySelectedMode();
                            return super.remove();
                        }
                    };
                    dialog.addCaptureListener(new InputListener() {
                        @Override
                        public boolean keyDown(InputEvent event, int keycode2) {
                            if (keycode2 == Keys.ENTER) {
                                if (!button.isDisabled()) {
                                    tintedDrawable.name = textField.getText();
                                    AtlasData.getInstance().getDrawables().add(tintedDrawable);
                                    dialog.hide();
                                }
                            }
                            return false;
                        }
                    });
                    dialog.text("What is the name of the new tinted drawable?");

                    Drawable drawable = drawablePairs.get(drawableData);
                    Drawable preview = null;
                    if (drawable instanceof TextureRegionDrawable) {
                        preview = ((TextureRegionDrawable) drawable).tint(color);
                    } else if (drawable instanceof NinePatchDrawable) {
                        preview = ((NinePatchDrawable) drawable).tint(color);
                    }
                    if (preview != null) {
                        dialog.getContentTable().row();
                        Table table = new Table();
                        table.setBackground(preview);
                        dialog.getContentTable().add(table);
                    }

                    dialog.getContentTable().row();
                    dialog.getContentTable().add(textField).growX();

                    dialog.button(button, true);
                    dialog.button("Cancel", false);
                    dialog.key(Keys.ESCAPE, false);
                    dialog.show(getStage());
                    getStage().setKeyboardFocus(textField);
                    textField.selectAll();
                }
            }
        });
    }
    
    /**
     * Returns true if any existing drawable has the indicated name.
     * @param name
     * @return 
     */
    private boolean checkIfNameExists(String name) {
        boolean returnValue = false;
        
        for (DrawableData drawable : drawables) {
            if (drawable.name.equals(name)) {
                returnValue = true;
                break;
            }
        }
        
        return returnValue;
    }
}
