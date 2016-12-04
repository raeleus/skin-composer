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

import com.ray3k.skincomposer.data.StyleProperty;
import com.ray3k.skincomposer.dialog.DialogDrawables;
import com.ray3k.skincomposer.dialog.DialogColors;
import com.ray3k.skincomposer.dialog.DialogAbout;
import com.ray3k.skincomposer.panel.PanelPreview;
import com.ray3k.skincomposer.panel.PanelStyleProperties;
import com.ray3k.skincomposer.panel.PanelStatusBar;
import com.ray3k.skincomposer.panel.PanelClassBar;
import com.ray3k.skincomposer.panel.PanelPreviewProperties;
import com.ray3k.skincomposer.panel.PanelMenuBar;
import com.ray3k.skincomposer.data.StyleData;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ray3k.skincomposer.data.AtlasData;
import com.ray3k.skincomposer.data.JsonData;
import com.ray3k.skincomposer.data.ProjectData;
import com.ray3k.skincomposer.dialog.DialogColorPicker;
import com.ray3k.skincomposer.dialog.DialogColorPicker.ColorListener;
import com.ray3k.skincomposer.dialog.DialogColors.DialogColorsListener;
import com.ray3k.skincomposer.dialog.DialogFonts;
import com.ray3k.skincomposer.dialog.DialogLoading;
import com.ray3k.skincomposer.dialog.DialogSettings;
import com.ray3k.skincomposer.utils.Utils;

public class Main extends ApplicationAdapter {
    public final static String VERSION = "6";
    public static Main instance;
    private Stage stage;
    private static Skin skin;
    private static Skin newSkin;
    private DesktopWorker desktopWorker;
    private Array<Undoable> undoables;
    private int undoIndex;
    private boolean listeningForKeys;
    private boolean showingCloseDialog;
    private AnimatedDrawable loadingAnimation;
    
    @Override
    public void create() {
        if (Utils.isMac()) System.setProperty("java.awt.headless", "true");
        
        showingCloseDialog = false;
        listeningForKeys = true;
        undoables = new Array<>();
        undoIndex = -1;
        desktopWorker.attachLogListener();
        desktopWorker.sizeWindowToFit(800, 800, 50, Gdx.graphics);
        desktopWorker.centerWindow(Gdx.graphics);
        desktopWorker.setCloseListener(() -> {
            showCloseDialog();
            return false;
        });
        
        instance = this;
        
        if (ProjectData.instance().getSelectedSkin() == 0) {
            skin = new Skin(Gdx.files.internal("orange-peel-ui/uiskin.json"));
        } else {
            skin = new Skin(Gdx.files.internal("dark-peel-ui/dark-peel-ui.json"));
        }
        
        newSkin = new Skin(Gdx.files.internal("skin-composer-ui/skin-composer-ui.json"));
        
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (listeningForKeys && (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Keys.CONTROL_RIGHT))) {
                    if (keycode == Keys.Z) {
                        undo();
                    } else if (keycode == Keys.Y) {
                        redo();
                    } else if (keycode == Keys.N) {
                        PanelMenuBar.instance().newDialog();
                    } else if (keycode == Keys.O) {
                        PanelMenuBar.instance().openDialog();
                    } else if (keycode == Keys.S) {
                        if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)) {
                            PanelMenuBar.instance().saveAsDialog(null);
                        } else {
                            PanelMenuBar.instance().save(null);
                        }
                    }
                }
                return false;
            }
        });
        
        loadingAnimation = new AnimatedDrawable(.05f);
        loadingAnimation.addDrawable(skin.getDrawable("loading_0"));
        loadingAnimation.addDrawable(skin.getDrawable("loading_1"));
        loadingAnimation.addDrawable(skin.getDrawable("loading_2"));
        loadingAnimation.addDrawable(skin.getDrawable("loading_3"));
        loadingAnimation.addDrawable(skin.getDrawable("loading_4"));
        loadingAnimation.addDrawable(skin.getDrawable("loading_5"));
        loadingAnimation.addDrawable(skin.getDrawable("loading_6"));
        loadingAnimation.addDrawable(skin.getDrawable("loading_7"));
        
        ProjectData.instance().randomizeId();
        ProjectData.instance().setMaxTextureDimensions(1024, 1024);
        ProjectData.instance().setMaxUndos(30);
        
        AtlasData.getInstance().clearTempData();
        
        Table rootTable = new Table(skin);
        rootTable.setFillParent(true);
        stage.addActor(rootTable);
        
        Table table = new Table(skin);
        new PanelMenuBar(table, skin, stage);
        rootTable.add(table).growX();        
        rootTable.row();
        table = new Table(skin);
        table.setBackground("dim-orange");
        rootTable.add(table).height(2.0f).growX();
        
        rootTable.row();
        table = new Table(skin);
        rootTable.add(table).growX();
        PanelClassBar panelClassBar = new PanelClassBar(table, skin, stage);
        rootTable.row();
        table = new Table(skin);
        table.setBackground("dim-orange");
        rootTable.add(table).height(2.0f).growX();
        
        rootTable.row();
        
        ClickListener scrollPaneHoverListener = new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                stage.setScrollFocus(event.getTarget());
            }
        };
        
        table = new Table(skin);
        PanelStyleProperties panelStyleProperties = new PanelStyleProperties(table, skin, stage);
        Table previewContentTable = new Table(skin);
        new PanelPreview(previewContentTable, skin, stage);
        ScrollPane scrollPaneTop = new ScrollPane(previewContentTable, skin, "no-bg");
        scrollPaneTop.setFadeScrollBars(false);
        scrollPaneTop.setFlickScroll(false);
        Table previewTable = new Table();
        Label label = new Label("Preview", skin, "title");
        label.setAlignment(Align.center);
        previewTable.add(label).growX();
        previewTable.row();
        previewTable.add(scrollPaneTop).grow();
        previewTable.addListener(scrollPaneHoverListener);
        Table previewPropertiesContentTable = new Table(skin);
        new PanelPreviewProperties(previewPropertiesContentTable, skin, stage);
        ScrollPane scrollPaneBottom = new ScrollPane(previewPropertiesContentTable, skin, "no-bg");
        scrollPaneBottom.setFadeScrollBars(false);
        scrollPaneBottom.setFlickScroll(false);
        Table previewPropertiesTable = new Table();
        label = new Label("Preview Properties", skin, "title");
        label.setAlignment(Align.center);
        previewPropertiesTable.add(label).growX();
        previewPropertiesTable.row();
        previewPropertiesTable.add(scrollPaneBottom).grow();
        previewPropertiesTable.addListener(scrollPaneHoverListener);
        ScrollPane scrollPaneLeft = new ScrollPane(table, skin, "no-bg");
        scrollPaneLeft.setFadeScrollBars(false);
        scrollPaneLeft.setFlickScroll(false);
        scrollPaneLeft.addListener(scrollPaneHoverListener);
        SplitPane splitPaneRight = new SplitPane(previewTable, previewPropertiesTable, true, skin);
        splitPaneRight.setSplitAmount(.75f);
        splitPaneRight.setMinSplitAmount(.1f);
        splitPaneRight.setMaxSplitAmount(.9f);
        splitPaneRight.addListener(new ClickListener() {
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (event.getListenerActor().equals(event.getTarget())) {
                    Gdx.graphics.setSystemCursor(SystemCursor.Arrow);
                }
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (event.getListenerActor().equals(event.getTarget())) {
                    Gdx.graphics.setSystemCursor(SystemCursor.VerticalResize);
                }
            }
            
        });
        SplitPane splitPane = new SplitPane(scrollPaneLeft, splitPaneRight, false, skin);
        splitPane.setSplitAmount(.35f);
        splitPane.setMinSplitAmount(.1f);
        splitPane.setMaxSplitAmount(.9f);
        splitPane.addListener(new ClickListener() {
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (event.getListenerActor().equals(event.getTarget())) {
                    Gdx.graphics.setSystemCursor(SystemCursor.Arrow);
                }
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (event.getListenerActor().equals(event.getTarget())) {
                    Gdx.graphics.setSystemCursor(SystemCursor.HorizontalResize);
                }
            }
        });
        rootTable.add(splitPane).grow();
        
        rootTable.row();
        table = new Table(skin);
        new PanelStatusBar(table, skin, stage);
        rootTable.add(table).growX();
        
        panelStyleProperties.populate(panelClassBar.getStyleSelectBox().getSelected());
        stage.setScrollFocus(scrollPaneLeft);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        loadingAnimation.update(Gdx.graphics.getDeltaTime());
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
    
    /**
     * Creates a new StyleData object if one with the same name currently does not exist. If it does exist
     * it is returned and the properties are wiped. ClassName and deletable flag is retained.
     * @param className
     * @param styleName
     * @return 
     */
    public StyleData newStyle(Class className, String styleName) {
        Array<StyleData> styles = JsonData.getInstance().getClassStyleMap().get(className);
        
        StyleData data = null;
        
        for (StyleData tempStyle : styles) {
            if (tempStyle.name.equals(styleName)) {
                data = tempStyle;
                data.resetProperties();
            }
        }
        
        if (data == null) {
            data = new StyleData(className, styleName);
            styles.add(data);
        }
        
        return data;
    }
    
    public StyleData copyStyle(StyleData original, String styleName) {
        Array<StyleData> styles = JsonData.getInstance().getClassStyleMap().get(original.clazz);
        StyleData data = new StyleData(original, styleName);
        styles.add(data);
        
        return data;
    }
    
    public void deleteStyle(StyleData styleData) {
        Array<StyleData> styles = JsonData.getInstance().getClassStyleMap().get(styleData.clazz);
        styles.removeValue(styleData, true);
        
        //reset any properties pointing to this style to the default style
        if (styleData.clazz.equals(Label.class)) {
            for (StyleData data : JsonData.getInstance().getClassStyleMap().get(TextTooltip.class)) {
                StyleProperty property = data.properties.get("label");
                if (property != null && property.value.equals(styleData.name)) {
                    property.value = "default";
                }
            }
        } else if (styleData.clazz.equals(List.class)) {
            for (StyleData data : JsonData.getInstance().getClassStyleMap().get(SelectBox.class)) {
                StyleProperty property = data.properties.get("listStyle");
                if (property != null && property.value.equals(styleData.name)) {
                    property.value = "default";
                }
            }
        } else if (styleData.clazz.equals(ScrollPane.class)) {
            for (StyleData data : JsonData.getInstance().getClassStyleMap().get(SelectBox.class)) {
                StyleProperty property = data.properties.get("scrollStyle");
                if (property != null && property.value.equals(styleData.name)) {
                    property.value = "default";
                }
            }
        }
    }
    
    public void showDialogDrawables(StyleProperty property, EventListener listener) {
        DialogDrawables dialog = new DialogDrawables(newSkin, "dialog", property, listener);
        dialog.setFillParent(true);
        dialog.show(stage);
    }
    
    public void showDialogDrawables(StyleProperty property) {
        showDialogDrawables(property, null);
    }
    
    public void showDialogDrawables() {
        showDialogDrawables(null);
    }
    
    public void showDialogColors(StyleProperty styleProperty, DialogColorsListener listener) {
        DialogColors dialog = new DialogColors(newSkin, "dialog", styleProperty, listener);
        dialog.setFillParent(true);
        dialog.show(stage);
        dialog.populate();
    }
    
    public void showDialogColors(StyleProperty styleProperty) {
        showDialogColors(styleProperty, null);
    }
    
    public void showDialogColors() {
        showDialogColors(null);
    }
    
    public void showDialogAbout() {
        DialogAbout dialog = new DialogAbout(newSkin, "dialog");
        dialog.show(stage);
    }
    
    public void showDialogFonts(StyleProperty styleProperty, EventListener listener) {
        DialogFonts dialog = new DialogFonts(newSkin, "dialog", styleProperty, listener);
        dialog.setFillParent(true);
        dialog.show(stage);
        dialog.populate();
    }
    
    public void showDialogFonts(StyleProperty styleProperty) {
        showDialogFonts(styleProperty, null);
    }
    
    public void showDialogFonts() {
        showDialogFonts(null);
    }
    
    public void showDialogColorPicker(ColorListener listener) {
        showDialogColorPicker(null, listener);
    }
    
    public void showDialogColorPicker(Color previousColor, ColorListener listener) {
        DialogColorPicker dialog = new DialogColorPicker(newSkin, "dialog", listener, previousColor);
        dialog.show(stage);
    }
    
    public void showDialogSettings() {
        DialogSettings dialog = new DialogSettings("", newSkin, "dialog");
        dialog.show(stage);
    }
    
    public void showCloseDialog() {
        if (ProjectData.instance().areChangesSaved() || ProjectData.instance().isNewProject()) {
            Gdx.app.exit();
        } else {
            if (!showingCloseDialog) {
                showingCloseDialog = true;
                Dialog dialog = new Dialog("Save Changes?", skin, "dialog") {
                    @Override
                    protected void result(Object object) {
                        if ((int) object == 0) {
                            PanelMenuBar.instance().save(() -> {
                                if (ProjectData.instance().areChangesSaved()) {
                                    Gdx.app.exit();
                                }
                            });
                        } else if ((int) object == 1) {
                            Gdx.app.exit();
                        }
                        
                        showingCloseDialog = false;
                    }
                };
                Label label = new Label("Do you want to save\nyour changes before you quit?", skin);
                label.setAlignment(Align.center);
                dialog.text(label);
                dialog.getContentTable().getCells().first().pad(10.0f);
                dialog.button("Yes", 0).button("No", 1).button("Cancel", 2);
                java.awt.Toolkit.getDefaultToolkit().beep();
                dialog.show(stage);
            }
        }
    }
    
    public void showDialogLoading(Runnable runnable) {
        DialogLoading dialog = new DialogLoading("", skin, runnable);
        dialog.show(stage);
    }
    
    public void showDialogError(String title, String message, Runnable runnable) {
        Dialog dialog = new Dialog(title, skin, "dialog") {
            @Override
            public boolean remove() {
                if (runnable != null) {
                    runnable.run();
                }
                return super.remove();
            }
            
        };
        
        dialog.text(message);
        dialog.button("OK");
        dialog.show(stage);
    }

    public DesktopWorker getDesktopWorker() {
        return desktopWorker;
    }

    public void setDesktopWorker(DesktopWorker textureWorker) {
        this.desktopWorker = textureWorker;
    }

    public Stage getStage() {
        return stage;
    }

    public void clearUndoables() {
        undoables.clear();
        undoIndex = -1;
        
        PanelMenuBar.instance().getUndoButton().setDisabled(true);
        PanelMenuBar.instance().getUndoButton().setText("Undo");
        
        PanelMenuBar.instance().getRedoButton().setDisabled(true);
        PanelMenuBar.instance().getRedoButton().setText("Redo");
    }
    
    public void undo() {
        if (undoIndex >= 0 && undoIndex < undoables.size) {
            ProjectData.instance().setChangesSaved(false);
            Undoable undoable = undoables.get(undoIndex);
            undoable.undo();
            undoIndex--;

            if (undoIndex < 0) {
                PanelMenuBar.instance().getUndoButton().setDisabled(true);
                PanelMenuBar.instance().getUndoButton().setText("Undo");
            } else {
                PanelMenuBar.instance().getUndoButton().setText("Undo " + undoables.get(undoIndex).getUndoText());
            }

            PanelMenuBar.instance().getRedoButton().setDisabled(false);
            PanelMenuBar.instance().getRedoButton().setText("Redo " + undoable.getUndoText());
        }
    }
    
    public void redo() {
        if (undoIndex >= -1 && undoIndex < undoables.size) {
            ProjectData.instance().setChangesSaved(false);
            if (undoIndex < undoables.size - 1) {
                undoIndex++;
                undoables.get(undoIndex).redo();
            }

            if (undoIndex >= undoables.size - 1) {
                PanelMenuBar.instance().getRedoButton().setDisabled(true);
                PanelMenuBar.instance().getRedoButton().setText("Redo");
            } else {
                PanelMenuBar.instance().getRedoButton().setText("Redo " + undoables.get(undoIndex + 1).getUndoText());
            }

            PanelMenuBar.instance().getUndoButton().setDisabled(false);
            PanelMenuBar.instance().getUndoButton().setText("Undo " + undoables.get(undoIndex).getUndoText());
        }
    }
    
    public void addUndoable(Undoable undoable, boolean redoImmediately) {
        ProjectData.instance().setChangesSaved(false);
        undoIndex++;
        if (undoIndex <= undoables.size - 1) {
            undoables.removeRange(undoIndex, undoables.size - 1);
        }
        undoables.add(undoable);
        
        if (redoImmediately) {
            undoable.redo();
        }
        
        PanelMenuBar.instance().getRedoButton().setDisabled(true);
        PanelMenuBar.instance().getRedoButton().setText("Redo");
        PanelMenuBar.instance().getUndoButton().setDisabled(false);
        PanelMenuBar.instance().getUndoButton().setText("Undo " + undoable.getUndoText());
        
        if (undoables.size > ProjectData.instance().getMaxUndos()) {
            int offset = undoables.size - ProjectData.instance().getMaxUndos();
            
            undoIndex -= offset;
            undoIndex = MathUtils.clamp(undoIndex, -1, undoables.size - 1);
            undoables.removeRange(0, offset - 1);
        }
    }
    
    public void addUndoable(Undoable undoable) {
        addUndoable(undoable, false);
    }

    public boolean isListeningForKeys() {
        return listeningForKeys;
    }

    public void setListeningForKeys(boolean listeningForKeys) {
        this.listeningForKeys = listeningForKeys;
    }

    public AnimatedDrawable getLoadingAnimation() {
        return loadingAnimation;
    }

    public Skin getSkin() {
        return skin;
    }
}
