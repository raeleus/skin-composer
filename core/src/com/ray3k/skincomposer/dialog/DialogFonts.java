package com.ray3k.skincomposer.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Sort;
import com.ray3k.skincomposer.FilesDroppedListener;
import com.ray3k.skincomposer.IbeamListener;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.data.AtlasData;
import com.ray3k.skincomposer.data.DrawableData;
import com.ray3k.skincomposer.data.FontData;
import com.ray3k.skincomposer.data.JsonData;
import com.ray3k.skincomposer.data.ProjectData;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.data.StyleProperty;
import com.ray3k.skincomposer.panel.PanelClassBar;
import com.ray3k.skincomposer.panel.PanelPreviewProperties;
import com.ray3k.skincomposer.panel.PanelStatusBar;
import com.ray3k.skincomposer.panel.PanelStyleProperties;
import com.ray3k.skincomposer.utils.SynchronousJFXFileChooser;
import com.ray3k.skincomposer.utils.Utils;
import java.io.File;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javafx.stage.FileChooser;

public class DialogFonts extends Dialog {

    private Skin skin;
    private StyleProperty styleProperty;
    private Array<FontData> fonts;
    private Array<DrawableData> drawables;
    private Table fontsTable;
    private SelectBox<String> selectBox;
    private ObjectMap<FontData, BitmapFont> fontMap;
    private TextureAtlas atlas;
    private EventListener listener;
    private FilesDroppedListener filesDroppedListener;

    public DialogFonts(Skin skin, StyleProperty styleProperty, EventListener listener) {
        this(skin, "default", styleProperty, listener);
    }

    public DialogFonts(final Skin skin, String styleName, StyleProperty styleProperty, EventListener listener) {
        super("", skin, styleName);

        this.listener = listener;
        this.skin = skin;
        this.styleProperty = styleProperty;
        fonts = JsonData.getInstance().getFonts();
        drawables = AtlasData.getInstance().getDrawables();

        fontMap = new ObjectMap<>();
        produceAtlas();
        
        filesDroppedListener = new FilesDroppedListener() {
            @Override
            public void filesDropped(Array<FileHandle> files) {
                Iterator<FileHandle> iter = files.iterator();
                while (iter.hasNext()) {
                    FileHandle file = iter.next();
                    if (file.isDirectory() || !file.name().toLowerCase().endsWith(".fnt")) {
                        iter.remove();
                    }
                }
                
                if (files.size > 0) {
                    fontNameDialog(files, 0);
                }
            }
        };
        
        Main.instance.getDesktopWorker().addFilesDroppedListener(filesDroppedListener);

        setFillParent(true);

        if (styleProperty != null) {
            getContentTable().add(new Label("Select a Font...", skin, "title"));
            getContentTable().row();
        } else {
            getContentTable().add(new Label("Fonts", skin, "title"));
            getContentTable().row();
        }

        Table table = new Table();
        table.defaults().pad(2.0f);

        table.add(new Label("Sort by: ", skin)).padLeft(20.0f);
        selectBox = new SelectBox<>(skin);
        selectBox.setItems(new String[]{"A-Z", "Z-A", "Oldest", "Newest"});
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                sortBySelectedMode();
            }
        });
        table.add(selectBox);

        ImageTextButtonStyle imageButtonStyle = new ImageTextButtonStyle();
        imageButtonStyle.imageUp = skin.getDrawable("image-plus");
        imageButtonStyle.imageDown = skin.getDrawable("image-plus-down");
        imageButtonStyle.up = skin.getDrawable("button-orange");
        imageButtonStyle.down = skin.getDrawable("button-orange-down");
        imageButtonStyle.font = skin.getFont("font");
        imageButtonStyle.fontColor = skin.getColor("white");
        imageButtonStyle.downFontColor = skin.getColor("maroon");
        ImageTextButton imageButton = new ImageTextButton(" New Font", imageButtonStyle);
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                newFontDialog();
            }
        });
        table.add(imageButton).expandX();
        getContentTable().add(table).expandX().left();
        getContentTable().row();

        key(Keys.ESCAPE, false);
        if (styleProperty != null) {
            button("Clear Font", true);
            button("Cancel", false);
        } else {
            button("Close", false);
        }

        fontsTable = new Table();

        table = new Table();
        table.add(fontsTable).pad(5.0f);
        ScrollPane scrollPane = new ScrollPane(table, skin, "no-bg");
        scrollPane.setFadeScrollBars(false);
        getContentTable().add(scrollPane).grow();
    }

    private boolean addFont(String name, FileHandle file) {
        if (FontData.validate(name)) {
            try {
                FontData font = new FontData(name, file);
                
                //remove any existing FontData that shares the same name.
                if (fonts.contains(font, false)) {
                    FontData deleteFont = fonts.get(fonts.indexOf(font, false));
                    
                    BitmapFontData deleteFontData = new BitmapFontData(deleteFont.file, false);
                    for (String path : deleteFontData.imagePaths) {
                        FileHandle imagefile = new FileHandle(path);
                        drawables.removeValue(new DrawableData(imagefile), false);
                    }
                    
                    fonts.removeValue(font, false);
                }
                
                BitmapFontData bitmapFontData = new BitmapFontData(file, false);
                for (String path : bitmapFontData.imagePaths) {
                    DrawableData drawable = new DrawableData(new FileHandle(path));
                    drawable.visible = false;
                    if (!drawables.contains(drawable, false)) {
                        drawables.add(drawable);
                    }
                }
                produceAtlas();
                fonts.add(font);
                
                Array<TextureRegion> regions = new Array<>();
                for (String path : bitmapFontData.imagePaths) {
                    FileHandle imageFile = new FileHandle(path);
                    regions.add(atlas.findRegion(imageFile.nameWithoutExtension()));
                }
                fontMap.put(font, new BitmapFont(bitmapFontData, regions, true));
                
                
                
                sortBySelectedMode();
                populate();
            } catch (Exception e) {
                Gdx.app.error(getClass().getName(), "Error creating font from file", e);
                showAddFontErrorMessage();
            }
            return true;
        } else {
            return false;
        }
    }

    public void populate() {
        fontsTable.clear();
        fontsTable.defaults().growX().pad(5.0f);

        if (fonts.size == 0) {
            fontsTable.add(new Label("No fonts have been set!", skin));
        } else {
            for (FontData font : fonts) {
                Button button = new Button(skin);
                Label label = new Label(font.getName() + ":", skin, "white");
                label.setTouchable(Touchable.disabled);
                button.add(label).left();

                LabelStyle style = new LabelStyle();
                style.font = fontMap.get(font);
                style.fontColor = Color.WHITE;
                label = new Label("Lorem Ipsum", style);
                label.setAlignment(Align.center);
                label.setTouchable(Touchable.disabled);
                Table bg = new Table(skin);
                bg.setBackground("white");
                BitmapFontData bf = new BitmapFontData(font.file, false);
                if (bf.imagePaths.length > 0) {
                    FileHandle file = new FileHandle(bf.imagePaths[0]);
                    if (!file.exists()) {
                        file = bf.fontFile.sibling(bf.fontFile.nameWithoutExtension() + ".png");
                    }
                    if (Utils.brightness(Utils.averageEdgeColor(file)) < .5f) {
                        bg.setColor(Color.WHITE);
                    } else {
                        bg.setColor(Color.BLACK);
                    }
                }
                bg.add(label).pad(5.0f).grow();
                button.add(bg).padLeft(15).growX();

                Button closeButton = new Button(skin, "close");
                final FontData deleteFont = font;
                closeButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        fonts.removeValue(deleteFont, true);
                        BitmapFontData bitmapFontData = new BitmapFontData(deleteFont.file, false);
                        for (String path : bitmapFontData.imagePaths) {
                            FileHandle imagefile = new FileHandle(path);
                            drawables.removeValue(new DrawableData(imagefile), false);
                        }
                        
                        for (Array<StyleData> datas : JsonData.getInstance().getClassStyleMap().values()) {
                            for (StyleData data : datas) {
                                for (StyleProperty property : data.properties.values()) {
                                    if (property != null && property.type.equals(BitmapFont.class) && property.value != null && property.value.equals(deleteFont.getName())) {
                                        property.value = null;
                                    }
                                }
                            }
                        }
                        
                        Main.instance.clearUndoables();
                        
                        PanelStyleProperties.instance.populate(PanelClassBar.instance.getStyleSelectBox().getSelected());
                        PanelPreviewProperties.instance.render();
                        
                        event.setBubbles(false);
                        populate();
                    }
                });
                closeButton.addListener(new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        event.setBubbles(false);
                        return true;
                    }

                });
                button.add(closeButton).padLeft(5.0f).right();

                if (styleProperty == null) {
                    button.setTouchable(Touchable.childrenOnly);
                } else {
                    final FontData fontResult = font;
                    button.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                            result(fontResult);
                            hide();
                        }
                    });
                }

                fontsTable.add(button);
                fontsTable.row();
            }
        }
    }

    @Override
    protected void result(Object object) {
        if (styleProperty != null) {
            if (object instanceof FontData) {
                FontData font = (FontData) object;
                PanelStatusBar.instance.message("Selected Font: " + font.getName());
                styleProperty.value = font.getName();
                PanelStyleProperties.instance.populate(PanelClassBar.instance.getStyleSelectBox().getSelected());
            } else if (object instanceof Boolean) {
                if ((boolean) object) {
                    styleProperty.value = null;
                    PanelStyleProperties.instance.populate(PanelClassBar.instance.getStyleSelectBox().getSelected());
                } else {
                    boolean hasFont = false;
                    for (FontData font : JsonData.getInstance().getFonts()) {
                        if (font.getName().equals(styleProperty.value)) {
                            hasFont = true;
                            break;
                        }
                    }

                    if (!hasFont) {
                        styleProperty.value = null;
                        PanelStyleProperties.instance.populate(PanelClassBar.instance.getStyleSelectBox().getSelected());
                    }
                }
            }
        }
        
        if (listener != null) {
            listener.handle(null);
        }
    }

    private void sortBySelectedMode() {
        switch (selectBox.getSelectedIndex()) {
            case 0:
                sortFontsAZ();
                break;
            case 1:
                sortFontsZA();
                break;
            case 2:
                sortFontsOldest();
                break;
            case 3:
                sortFontsNewest();
                break;
        }
    }

    private void sortFontsAZ() {
        Sort.instance().sort(fonts, new Comparator<FontData>() {
            @Override
            public int compare(FontData o1, FontData o2) {
                return o1.toString().compareToIgnoreCase(o2.toString());
            }
        });
        populate();
    }

    private void sortFontsZA() {
        Sort.instance().sort(fonts, new Comparator<FontData>() {
            @Override
            public int compare(FontData o1, FontData o2) {
                return o1.toString().compareToIgnoreCase(o2.toString()) * -1;
            }
        });
        populate();
    }

    private void sortFontsOldest() {
        Sort.instance().sort(fonts, new Comparator<FontData>() {
            @Override
            public int compare(FontData o1, FontData o2) {
                if (o1.file.lastModified() < o2.file.lastModified()) {
                    return -1;
                } else if (o1.file.lastModified() > o2.file.lastModified()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        populate();
    }

    private void sortFontsNewest() {
        Sort.instance().sort(fonts, new Comparator<FontData>() {
            @Override
            public int compare(FontData o1, FontData o2) {
                if (o1.file.lastModified() < o2.file.lastModified()) {
                    return 1;
                } else if (o1.file.lastModified() > o2.file.lastModified()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        populate();
    }

    @Override
    public boolean remove() {
        Main.instance.getDesktopWorker().removeFilesDroppedListener(filesDroppedListener);
        
        produceAtlas();
        
        for (BitmapFont font : fontMap.values()) {
            font.dispose();
        }
        fontMap.clear();
        return super.remove();
    }

    private boolean produceAtlas() {
        try {
            if (atlas != null) {
                atlas.dispose();
                atlas = null;
            }
            AtlasData.getInstance().writeAtlas();
            atlas = AtlasData.getInstance().getAtlas();

            for (FontData font : fonts) {
                BitmapFontData fontData = new BitmapFontData(font.file, false);
                Array<TextureRegion> regions = new Array<>();
                for (String path : fontData.imagePaths) {
                    FileHandle file = new FileHandle(path);
                    if (!file.exists()) {
                        file = fontData.fontFile.sibling(fontData.fontFile.nameWithoutExtension() + ".png");
                    }
                    TextureRegion region = atlas.findRegion(file.nameWithoutExtension());
                    if (region != null) {
                        regions.add(region);
                    }
                }
                fontMap.put(font, new BitmapFont(fontData, regions, true));
            }
            return true;
        } catch (Exception e) {
            Gdx.app.error(getClass().getName(), "Error while attempting to generate drawables.", e);
            return false;
        }
    }
    
    private void newFontDialog() {
        SynchronousJFXFileChooser chooser = new SynchronousJFXFileChooser(() -> {
            FileChooser ch = new FileChooser();
            FileChooser.ExtensionFilter ex = new FileChooser.ExtensionFilter("Font files (*.fnt)", "*.fnt");
            ch.getExtensionFilters().add(ex);
            ch.setTitle("Choose font file(s)...");
            if (ProjectData.instance().getLastDirectory() != null) {
                ch.setInitialDirectory(new File(ProjectData.instance().getLastDirectory()));
            }
            return ch;
        });
        List<File> files = chooser.showOpenMultipleDialog();
        if (files != null && files.size() > 0) {
            ProjectData.instance().setLastDirectory(files.get(0).getParentFile().getPath());
            fontNameDialog(files, 0);
        }
    }
    
    private void fontNameDialog(List<File> files, int index) {
        Array<FileHandle> handles = new Array<>();
        for (File file : files) {
            handles.add(new FileHandle(file));
        }
        
        fontNameDialog(handles, index);
    }
    
    private void fontNameDialog(Array<FileHandle> files, int index) {
        if (index < files.size) {
            try {
                final FileHandle fileHandle = files.get(index);

                final TextField textField = new TextField(FontData.filter(fileHandle.nameWithoutExtension()), skin);
                final Dialog nameDialog = new Dialog("Enter a name...", skin, "dialog") {
                    @Override
                    protected void result(Object object) {
                        if ((Boolean) object) {
                            String name = textField.getText();

                            addFont(name, fileHandle);

                        }
                    }

                    @Override
                    public boolean remove() {
                        fontNameDialog(files, index + 1);
                        return super.remove();
                    }
                };
                nameDialog.button("OK", true);
                nameDialog.button("Cancel", false);
                final TextButton button = (TextButton) nameDialog.getButtonTable().getCells().first().getActor();
                
                textField.setTextFieldListener(new TextField.TextFieldListener() {
                    @Override
                    public void keyTyped(TextField textField, char c) {
                        if (c == '\n') {
                            if (!button.isDisabled()) {
                                String name = textField.getText();
                                if (addFont(name, fileHandle)) {
                                    nameDialog.hide();
                                }
                            }
                            Main.instance.getStage().setKeyboardFocus(textField);
                        }
                    }
                });
                
                textField.addListener(IbeamListener.get());
                
                nameDialog.getContentTable().defaults().padLeft(10.0f).padRight(10.0f);
                nameDialog.text("Please enter a name for the new font: ");
                nameDialog.getContentTable().row();
                nameDialog.getContentTable().add(textField).growX();
                nameDialog.getContentTable().row();
                nameDialog.text("Preview:");
                nameDialog.getContentTable().row();

                LabelStyle previewStyle = new LabelStyle();
                previewStyle.font = new BitmapFont(fileHandle);
                Table table = new Table(skin);
                table.setBackground("white");
                BitmapFontData bitmapFontData = new BitmapFontData(fileHandle, false);
                if (Utils.brightness(Utils.averageEdgeColor(new FileHandle(bitmapFontData.imagePaths[0]))) > .5f) {
                    table.setColor(Color.BLACK);
                } else {
                    table.setColor(Color.WHITE);
                }
                table.add(new Label("Lorem Ipsum", previewStyle)).pad(5.0f);

                nameDialog.getContentTable().add(table);
                nameDialog.key(Keys.ESCAPE, false);
                button.setDisabled(!FontData.validate(textField.getText()));
                textField.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        boolean disable = !FontData.validate(textField.getText());
                        if (!disable) {
                            for (FontData data : JsonData.getInstance().getFonts()) {
                                if (data.getName().equals(textField.getText())) {
                                    disable = true;
                                    break;
                                }
                            }
                        }
                        button.setDisabled(disable);
                    }
                });
                nameDialog.setColor(1.0f, 1.0f, 1.0f, 0.0f);
                nameDialog.show(getStage());
                getStage().setKeyboardFocus(textField);
                textField.selectAll();
            } catch (Exception e) {
                Gdx.app.error(getClass().getName(), "Error creating preview font from file", e);
                showAddFontErrorMessage();
            }
        }
    }
    
    private void showAddFontErrorMessage() {
        Dialog dialog = new Dialog("Error adding font...", skin, "dialog");
        dialog.text("Unable to add font. Check file paths.");
        dialog.button("Ok");
        dialog.show(getStage());
    }
}
