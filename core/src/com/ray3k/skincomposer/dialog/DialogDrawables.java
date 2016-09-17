package com.ray3k.skincomposer.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Values;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.Sort;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import com.kotcrab.vis.ui.widget.file.FileTypeFilter;
import com.ray3k.skincomposer.FilesDroppedListener;
import com.ray3k.skincomposer.IbeamListener;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.data.AtlasData;
import com.ray3k.skincomposer.data.ColorData;
import com.ray3k.skincomposer.data.DrawableData;
import com.ray3k.skincomposer.data.JsonData;
import com.ray3k.skincomposer.data.ProjectData;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.data.StyleProperty;
import com.ray3k.skincomposer.panel.PanelClassBar;
import com.ray3k.skincomposer.panel.PanelMenuBar;
import com.ray3k.skincomposer.panel.PanelPreviewProperties;
import com.ray3k.skincomposer.panel.PanelStatusBar;
import com.ray3k.skincomposer.panel.PanelStyleProperties;
import com.ray3k.skincomposer.utils.SynchronousJFXFileChooser;
import com.ray3k.skincomposer.utils.Utils;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import javafx.stage.FileChooser;

public class DialogDrawables extends Dialog {
    public static DialogDrawables instance;
    private static int[] sizes = {125, 150, 200, 250};
    private SelectBox sizeSelectBox;
    private ScrollPane scrollPane;
    private Slider zoomSlider;
    private StyleProperty property;
    private Array<DrawableData> drawables;
    private ObjectMap<DrawableData, Drawable> drawablePairs;
    private TextureAtlas atlas;
    private HorizontalGroup contentGroup;
    private FilesDroppedListener filesDroppedListener;
    private EventListener listener;
    
    public DialogDrawables(Skin skin, StyleProperty property, EventListener listener) {
        this(skin, "default", property, listener);
    }
    
    public DialogDrawables(Skin skin, String windowStyleName, StyleProperty property, EventListener listener) {
        super("", skin, windowStyleName);
        
        instance = this;
        
        this.listener = listener;
        
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
        
        this.property = property;
        drawablePairs = new ObjectMap<>();
        
        gatherDrawables();
        
        produceAtlas();
        
        populate();
    }
    
    /**
     * Recreates the drawables array only including visible drawables.
     */
    private void gatherDrawables() {
        drawables = new Array<>(AtlasData.getInstance().getDrawables());
        Iterator<DrawableData> iter = drawables.iterator();
        while(iter.hasNext()) {
            DrawableData drawable = iter.next();
            if (!drawable.visible) iter.remove();
        }
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
                    } else if (data.tintName != null) {
                        drawable = ((NinePatchDrawable) drawable).tint(JsonData.getInstance().getColorByName(data.tintName).color);
                    }
                } else {
                    drawable = new SpriteDrawable(atlas.createSprite(name));
                    if (data.tint != null) {
                        drawable = ((SpriteDrawable) drawable).tint(data.tint);
                    } else if (data.tintName != null) {
                        drawable = ((SpriteDrawable) drawable).tint(JsonData.getInstance().getColorByName(data.tintName).color);
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
    
    public void populate() {
        getContentTable().clear();
        
        if (property == null) {
            getContentTable().add(new Label("Drawables", getSkin(), "title"));
        } else {
            getContentTable().add(new Label("Select a Drawables", getSkin(), "title"));
        }
        
        getContentTable().row();
        Table table = new Table(getSkin());
        table.defaults().pad(10.0f);
        getContentTable().add(table).growX();
        
        table.add("Sort by:");
        
        sizeSelectBox = new SelectBox(getSkin());
        sizeSelectBox.setItems("A-Z", "Z-A", "Oldest", "Newest");
        sizeSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                sortBySelectedMode();
            }
        });
        table.add(sizeSelectBox);
        
        TextButton textButton = new TextButton("Add Drawable", getSkin());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                newDrawableDialog();
            }
        });
        table.add(textButton);
        
        table.add(new Label("Zoom:", getSkin())).right().expandX();
        zoomSlider = new Slider(0, 3, 1, false, getSkin());
        zoomSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                refreshDrawableDisplay();
            }
        });
        table.add(zoomSlider);
        
        getContentTable().row();
        contentGroup = new HorizontalGroup();
        contentGroup.center().wrap(true).space(5.0f).wrapSpace(5.0f).rowLeft();
        sortBySelectedMode();
        scrollPane = new ScrollPane(contentGroup, getSkin(), "no-bg");
        scrollPane.setFadeScrollBars(false);
        scrollPane.setFlickScroll(false);
        getContentTable().add(scrollPane).grow();
        
        getContentTable().row();
        if (property != null) {
            button("Clear Drawable", true);
            button("Cancel", false);
        } else {
            button("Close", false);
        }
    }

    @Override
    public Dialog show(Stage stage, Action action) {
        Dialog dialog = super.show(stage, action);
        stage.setScrollFocus(scrollPane);
        return dialog;
    }
    
    private void refreshDrawableDisplay() {
        contentGroup.clear();
        
        for (DrawableData drawable : drawables) {
            Button drawableButton = new Button(getSkin());
            
            if (property != null) {
                drawableButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        result(drawable);
                        hide();
                    }
                });
            } else {
                drawableButton.setTouchable(Touchable.childrenOnly);
            }
            contentGroup.addActor(drawableButton);
            
            Table table = new Table();
            drawableButton.add(table).width(sizes[MathUtils.floor(zoomSlider.getValue())]).height(sizes[MathUtils.floor(zoomSlider.getValue())]);

            //color wheel
            Button button = new Button(getSkin(), "color");
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    newTintedDrawable(drawable);
                }
            });
            table.add(button);

            //swatches
            button = new Button(getSkin(), "color");
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    colorSwatchesDialog(drawable);
                }
            });
            table.add(button);
            
            //rename (ONLY FOR TINTS)
            if (drawable.tint != null) {
                button = new Button(getSkin(), "color");
                button.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        renameDrawableDialog(drawable);
                    }
                });
                table.add(button);
            } else {
                table.add();
            }

            //todo: fix button being pressed with inner buttons
            //delete
            button = new Button(getSkin(), "close");
            button.addCaptureListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    event.setBubbles(false);
                    deleteDrawable(drawable);
                }
            });
            table.add(button).expandX().right();

            //preview
            table.row();
            Container bg = new Container();
            bg.setBackground(getSkin().getDrawable("white"));
            bg.setColor(drawable.bgColor);
            Image image = new Image(drawablePairs.get(drawable));
            if (MathUtils.isZero(zoomSlider.getValue())) {
                image.setScaling(Scaling.fit);
                bg.fill(false);
            } else {
                image.setScaling(Scaling.stretch);
                bg.fill();
            }
            bg.setActor(image);
            if (property == null) {
                bg.setTouchable(Touchable.disabled);
            }
            table.add(bg).colspan(4).grow();

            //name
            table.row();
            Label label = new Label(drawable.name, getSkin(), "white");
            label.setEllipsis("...");
            label.setEllipsis(true);
            label.setAlignment(Align.center);
            if (property == null) {
                label.setTouchable(Touchable.disabled);
            }
            table.add(label).colspan(4).growX().width(sizes[MathUtils.floor(zoomSlider.getValue())]);
        }
    }
    
    private void colorSwatchesDialog(DrawableData drawableData) {
        DialogColors dialog = new DialogColors(getSkin(), "dialog-panel", null, true, (ColorData colorData) -> {
            if (colorData != null) {
                final DrawableData tintedDrawable = new DrawableData(drawableData.file);
                    tintedDrawable.tintName = colorData.getName();
                    
                    //Fix background color for new, tinted drawable
                    Color temp = Utils.averageEdgeColor(tintedDrawable.file, colorData.color);
                    
                    if (Utils.brightness(temp) > .5f) {
                        tintedDrawable.bgColor = Color.BLACK;
                    } else {
                        tintedDrawable.bgColor = Color.WHITE;
                    }
                    
                    final TextField textField = new TextField(drawableData.name, getSkin());
                    final TextButton button = new TextButton("OK", getSkin());
                    button.setDisabled(!DrawableData.validate(textField.getText()) || checkIfNameExists(textField.getText()));
                    textField.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            button.setDisabled(!DrawableData.validate(textField.getText()) || checkIfNameExists(textField.getText()));
                        }
                    });
                    textField.addListener(IbeamListener.get());

                    Dialog approveDialog = new Dialog("TintedDrawable...", getSkin()) {
                        @Override
                        protected void result(Object object) {
                            if (object instanceof Boolean && (boolean) object) {
                                tintedDrawable.name = textField.getText();
                                AtlasData.getInstance().getDrawables().add(tintedDrawable);
                            }
                        }

                        @Override
                        public boolean remove() {
                            gatherDrawables();
                            produceAtlas();
                            sortBySelectedMode();
                            return super.remove();
                        }
                    };
                    approveDialog.addCaptureListener(new InputListener() {
                        @Override
                        public boolean keyDown(InputEvent event, int keycode2) {
                            if (keycode2 == Input.Keys.ENTER) {
                                if (!button.isDisabled()) {
                                    tintedDrawable.name = textField.getText();
                                    AtlasData.getInstance().getDrawables().add(tintedDrawable);
                                    approveDialog.hide();
                                }
                            }
                            return false;
                        }
                    });
                    approveDialog.text("What is the name of the new tinted drawable?");

                    Drawable drawable = drawablePairs.get(drawableData);
                    Drawable preview = null;
                    if (drawable instanceof SpriteDrawable) {
                        preview = ((SpriteDrawable) drawable).tint(colorData.color);
                    } else if (drawable instanceof NinePatchDrawable) {
                        preview = ((NinePatchDrawable) drawable).tint(colorData.color);
                    }
                    if (preview != null) {
                        approveDialog.getContentTable().row();
                        Table table = new Table();
                        table.setBackground(preview);
                        approveDialog.getContentTable().add(table);
                    }

                    approveDialog.getContentTable().row();
                    approveDialog.getContentTable().add(textField).growX();

                    approveDialog.button(button, true);
                    approveDialog.button("Cancel", false);
                    approveDialog.key(Input.Keys.ESCAPE, false);
                    approveDialog.show(getStage());
                    getStage().setKeyboardFocus(textField);
                    textField.selectAll();
            }
        });
        dialog.setFillParent(true);
        dialog.show(getStage());
        dialog.populate();
    }
    
    private void renameDrawableDialog(DrawableData drawable) {
        TextField textField = new TextField("", getSkin());
        Dialog dialog = new Dialog("Rename drawable?", getSkin()) {
            @Override
            protected void result(Object object) {
                super.result(object);
                
                if (object instanceof Boolean && (boolean) object == true) {
                    renameDrawable(drawable, textField.getText());
                }
            }

            @Override
            public Dialog show(Stage stage) {
                Dialog dialog = super.show(stage);
                stage.setKeyboardFocus(textField);
                return dialog;
            }
            
            
        };
        
        dialog.getContentTable().add(new Label("Please enter a new name for the drawable: ", getSkin()));
        dialog.getContentTable().row();
        dialog.getContentTable().add(textField);
        
        dialog.button("OK", true).key(Keys.ENTER, true);
        dialog.button("Cancel", false).key(Keys.ESCAPE, false);
        
        dialog.show(getStage());
    }
    
    private void renameDrawable(DrawableData drawable, String name) {
        String oldName = drawable.name;
        drawable.name = name;

        Main.instance.clearUndoables();
        updateStyleValuesForRename(oldName, name);
        
        PanelStyleProperties.instance.populate(PanelClassBar.instance.getStyleSelectBox().getSelected());
        PanelPreviewProperties.instance.produceAtlas();
        PanelPreviewProperties.instance.render();
        
        sortBySelectedMode();
    }
    
    private void updateStyleValuesForRename(String oldName, String newName) {
        Values<Array<StyleData>> values = JsonData.getInstance().getClassStyleMap().values();
        for (Array<StyleData> styles : values) {
            for (StyleData style : styles) {
                for (StyleProperty styleProperty : style.properties.values()) {
                    if (Drawable.class.isAssignableFrom(styleProperty.type)) {
                        if (styleProperty.value != null && styleProperty.value.equals(oldName)) {
                            styleProperty.value = newName;
                        }
                    }
                }
            }
        }
    }
    
    private void deleteDrawable(DrawableData drawable) {
        if (drawable.tint == null && checkDuplicateDrawables(drawable.file, 1)) {
            showConfirmDeleteDialog(drawable);
        } else {
            AtlasData.getInstance().getDrawables().removeValue(drawable, true);

            for (Array<StyleData> datas : JsonData.getInstance().getClassStyleMap().values()) {
                for (StyleData data : datas) {
                    for (StyleProperty styleProperty : data.properties.values()) {
                        if (styleProperty != null && styleProperty.type.equals(Drawable.class) && styleProperty.value != null && styleProperty.value.equals(drawable.toString())) {
                            styleProperty.value = null;
                        }
                    }
                }
            }

            PanelStyleProperties.instance.populate(PanelClassBar.instance.getStyleSelectBox().getSelected());
            PanelPreviewProperties.instance.render();

            Main.instance.clearUndoables();

            gatherDrawables();
            refreshDrawableDisplay();
        }
    }

    /**
     * Shows a dialog to confirm deletion of all TintedDrawables based on the
     * provided drawable data. This is called when the delete button is pressed
     * on a drawable in the drawable list.
     * @param drawable 
     */
    private void showConfirmDeleteDialog(DrawableData drawable) {
        Dialog dialog = new Dialog("Delete duplicates?", getSkin(), "dialog"){
            @Override
            protected void result(Object object) {
                if ((boolean) object) {
                    removeDuplicateDrawables(drawable.file);
                    gatherDrawables();
                    sortBySelectedMode();
                }
            }
        };
        dialog.text("Deleting this drawable will also delete one or more tinted drawables.\n"
                + "Delete duplicates?");
        dialog.button("OK", true);
        dialog.button("Cancel", false);
        dialog.key(Input.Keys.ENTER, true);
        dialog.key(Input.Keys.ESCAPE, false);
        dialog.show(getStage());
    }
    
    /**
     * Sorts by selected sort order and populates the list.
     */
    private void sortBySelectedMode() {
        switch (sizeSelectBox.getSelectedIndex()) {
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
        refreshDrawableDisplay();
    }
    
    /**
     * Sorts alphabetically from Z to A.
     */
    private void sortDrawablesZA() {
        Sort.instance().sort(drawables, (DrawableData o1, DrawableData o2) -> o1.toString().compareToIgnoreCase(o2.toString()) * -1);
        refreshDrawableDisplay();
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
        refreshDrawableDisplay();
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
        refreshDrawableDisplay();
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
        boolean refreshDrawables = false;
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
                
                refreshDrawables = true;
                i--;
            }
        }
        
        PanelStyleProperties.instance.populate(PanelClassBar.instance.getStyleSelectBox().getSelected());
        PanelPreviewProperties.instance.render();
        
        if (refreshDrawables) {
            gatherDrawables();
        }
    }
    
    /**
     * Show an error indicating a drawable that exceeds project specifications
     */
    private void showDrawableError() {
        Dialog dialog = new Dialog("Error...", getSkin());
        Label label = new Label("Error while adding new drawables.\nEnsure that image dimensions are\nless than maximums specified in project.\nRolling back changes...", getSkin());
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
        if (Utils.isWindows()) {
            newDrawableDialogWindows();
        } else {
            newDrawableDialogVisUI();
        }
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
        Array<DrawableData> backup = new Array<>(AtlasData.getInstance().getDrawables());
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
        Dialog dialog = new Dialog("Delete duplicates?", getSkin(), "dialog"){
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
        dialog.key(Input.Keys.ENTER, true);
        dialog.key(Input.Keys.ESCAPE, false);
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
        
        gatherDrawables();

        Main.instance.showDialogLoading(() -> {
            if (!produceAtlas()) {
                showDrawableError();
                Gdx.app.log(getClass().getName(), "Attempting to reload drawables backup...");
                AtlasData.getInstance().getDrawables().clear();
                AtlasData.getInstance().getDrawables().addAll(backup);
                gatherDrawables();
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
                    
                    //Fix background color for new, tinted drawable
                    Color temp = Utils.averageEdgeColor(tintedDrawable.file, tintedDrawable.tint);
                    
                    if (Utils.brightness(temp) > .5f) {
                        tintedDrawable.bgColor = Color.BLACK;
                    } else {
                        tintedDrawable.bgColor = Color.WHITE;
                    }
                    
                    final TextField textField = new TextField(drawableData.name, getSkin());
                    final TextButton button = new TextButton("OK", getSkin());
                    button.setDisabled(!DrawableData.validate(textField.getText()) || checkIfNameExists(textField.getText()));
                    textField.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            button.setDisabled(!DrawableData.validate(textField.getText()) || checkIfNameExists(textField.getText()));
                        }
                    });
                    textField.addListener(IbeamListener.get());

                    Dialog dialog = new Dialog("TintedDrawable...", getSkin()) {
                        @Override
                        protected void result(Object object) {
                            if (object instanceof Boolean && (boolean) object) {
                                tintedDrawable.name = textField.getText();
                                AtlasData.getInstance().getDrawables().add(tintedDrawable);
                            }
                        }

                        @Override
                        public boolean remove() {
                            gatherDrawables();
                            produceAtlas();
                            sortBySelectedMode();
                            return super.remove();
                        }
                    };
                    dialog.addCaptureListener(new InputListener() {
                        @Override
                        public boolean keyDown(InputEvent event, int keycode2) {
                            if (keycode2 == Input.Keys.ENTER) {
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
                    if (drawable instanceof SpriteDrawable) {
                        preview = ((SpriteDrawable) drawable).tint(color);
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
                    dialog.key(Input.Keys.ESCAPE, false);
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
            listener = null;
        }
    }
}