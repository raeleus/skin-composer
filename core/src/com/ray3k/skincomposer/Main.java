/*******************************************************************************
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
 ******************************************************************************/
package com.ray3k.skincomposer;

import com.badlogic.gdx.*;
import com.ray3k.skincomposer.dialog.DialogFactory;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.Hinting;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane.SplitPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip.TextTooltipStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TooltipManager;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.TreeStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ray3k.skincomposer.data.AtlasData;
import com.ray3k.skincomposer.data.JsonData;
import com.ray3k.skincomposer.data.ProjectData;
import com.ray3k.skincomposer.utils.Utils;

public class Main extends ApplicationAdapter {
    public final static String VERSION = "23";
    public static String newVersion;
    public static final Class[] BASIC_CLASSES = {Button.class, CheckBox.class,
        ImageButton.class, ImageTextButton.class, Label.class, List.class,
        ProgressBar.class, ScrollPane.class, SelectBox.class, Slider.class,
        SplitPane.class, TextButton.class, TextField.class, TextTooltip.class,
        Touchpad.class, Tree.class, Window.class};
    public static final Class[] STYLE_CLASSES = {ButtonStyle.class,
        CheckBoxStyle.class, ImageButtonStyle.class, ImageTextButtonStyle.class,
        LabelStyle.class, ListStyle.class, ProgressBarStyle.class,
        ScrollPaneStyle.class, SelectBoxStyle.class, SliderStyle.class,
        SplitPaneStyle.class, TextButtonStyle.class, TextFieldStyle.class,
        TextTooltipStyle.class, TouchpadStyle.class, TreeStyle.class,
        WindowStyle.class};
    private Stage stage;
    private static Skin skin;
    private DialogFactory dialogFactory;
    private DesktopWorker desktopWorker;
    private AnimatedDrawable loadingAnimation;
    private UndoableManager undoableManager;
    private ProjectData projectData;
    private RootTable rootTable;
    private IbeamListener ibeamListener;
    private MainListener mainListener;
    private HandListener handListener;
    private ResizeArrowListener verticalResizeArrowListener;
    private ResizeArrowListener horizontalResizeArrowListener;
    private TooltipManager tooltipManager;
    public static FileHandle appFolder;
    private String[] args;
    
    public Main (String[] args) {
        this.args = args;
    }
    
    @Override
    public void create() {
        if (Utils.isWindows()) {
            desktopWorker.closeSplashScreen();
        }
        
        appFolder = Gdx.files.external(".skincomposer/");
        
        skin = new Skin(Gdx.files.internal("skin-composer-ui/skin-composer-ui.json")) {
            //Override json loader to process FreeType fonts from skin JSON
            @Override
            protected Json getJsonLoader(final FileHandle skinFile) {
                Json json = super.getJsonLoader(skinFile);
                final Skin skin = this;

                json.setSerializer(FreeTypeFontGenerator.class, new Json.ReadOnlySerializer<FreeTypeFontGenerator>() {
                    @Override
                    public FreeTypeFontGenerator read(Json json,
                            JsonValue jsonData, Class type) {
                        String path = json.readValue("font", String.class, jsonData);
                        jsonData.remove("font");

                        Hinting hinting = Hinting.valueOf(json.readValue("hinting",
                                String.class, "AutoMedium", jsonData));
                        jsonData.remove("hinting");

                        TextureFilter minFilter = TextureFilter.valueOf(
                                json.readValue("minFilter", String.class, "Nearest", jsonData));
                        jsonData.remove("minFilter");

                        TextureFilter magFilter = TextureFilter.valueOf(
                                json.readValue("magFilter", String.class, "Nearest", jsonData));
                        jsonData.remove("magFilter");

                        FreeTypeFontParameter parameter = json.readValue(FreeTypeFontParameter.class, jsonData);
                        parameter.hinting = hinting;
                        parameter.minFilter = minFilter;
                        parameter.magFilter = magFilter;
                        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(skinFile.parent().child(path));
                        BitmapFont font = generator.generateFont(parameter);
                        skin.add(jsonData.name, font);
                        if (parameter.incremental) {
                            generator.dispose();
                            return null;
                        } else {
                            return generator;
                        }
                    }
                });

                return json;
            }
        };
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        
        initDefaults();
        
        populate();
    }
    
    private void initDefaults() {
        if (Utils.isMac()) System.setProperty("java.awt.headless", "true");
        
        skin.getFont("font").getData().markupEnabled = true;
        
        //copy defaults.json to temp folder if it doesn't exist
        var fileHandle = appFolder.child("texturepacker/defaults.json");
        if (!fileHandle.exists()) {
            Gdx.files.internal("defaults.json").copyTo(fileHandle);
        }
        
        //copy preview fonts to preview fonts folder if they do not exist
        fileHandle = appFolder.child("preview fonts/IBMPlexSerif-Medium.ttf");
        if (!fileHandle.exists()) {
            Gdx.files.internal("preview fonts/IBMPlexSerif-Medium.ttf").copyTo(fileHandle);
        }
        
        fileHandle = appFolder.child("preview fonts/Pacifico-Regular.ttf");
        if (!fileHandle.exists()) {
            Gdx.files.internal("preview fonts/Pacifico-Regular.ttf").copyTo(fileHandle);
        }
        
        fileHandle = appFolder.child("preview fonts/PressStart2P-Regular.ttf");
        if (!fileHandle.exists()) {
            Gdx.files.internal("preview fonts/PressStart2P-Regular.ttf").copyTo(fileHandle);
        }
        
        fileHandle = appFolder.child("preview fonts/SourceSansPro-Regular.ttf");
        if (!fileHandle.exists()) {
            Gdx.files.internal("preview fonts/SourceSansPro-Regular.ttf").copyTo(fileHandle);
        }
        
        ibeamListener = new IbeamListener();
        
        projectData = new ProjectData();
        projectData.setMain(this);
        projectData.randomizeId();
        projectData.setMaxUndos(30);
        
        newVersion = VERSION;
        if (projectData.isCheckingForUpdates()) {
            checkForUpdates(this);
        }
        
        dialogFactory = new DialogFactory(this);
        undoableManager = new UndoableManager(this);
        
        desktopWorker.attachLogListener();
        desktopWorker.sizeWindowToFit(800, 800, 50, Gdx.graphics);
        desktopWorker.centerWindow(Gdx.graphics);
        desktopWorker.setCloseListener(() -> {
            dialogFactory.showCloseDialog();
            return false;
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
        
        projectData.getAtlasData().clearTempData();
        handListener = new HandListener();
        
        verticalResizeArrowListener = new ResizeArrowListener(true);
        horizontalResizeArrowListener = new ResizeArrowListener(false);
        
        tooltipManager = new TooltipManager();
        tooltipManager.animations = false;
        tooltipManager.initialTime = .4f;
        tooltipManager.resetTime = 0.0f;
        tooltipManager.subsequentTime = 0.0f;
        tooltipManager.hideAll();
        tooltipManager.instant();
    }

    private void populate() {
        stage.clear();
        
        rootTable = new RootTable(this);
        rootTable.setFillParent(true);
        mainListener = new MainListener(this);
        rootTable.addListener(mainListener);
        rootTable.populate();
        stage.addActor(rootTable);
        rootTable.setRecentFilesDisabled(projectData.getRecentFiles().size == 0);
        
        //pass arguments
        if (!mainListener.argumentsPassed(args)) {
            //show welcome screen if there are no valid arguments
            mainListener.createWelcomeListener();
        }
    }
    
    @Override
    public void render() {
        Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        loadingAnimation.update(Gdx.graphics.getDeltaTime());
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        rootTable.fire(new StageResizeEvent(width, height));
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
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

    public AnimatedDrawable getLoadingAnimation() {
        return loadingAnimation;
    }

    public Skin getSkin() {
        return skin;
    }

    public UndoableManager getUndoableManager() {
        return undoableManager;
    }

    public ProjectData getProjectData() {
        return projectData;
    }
    
    public JsonData getJsonData() {
        return projectData.getJsonData();
    }
    
    public AtlasData getAtlasData() {
        return projectData.getAtlasData();
    }

    public void setProjectData(ProjectData projectData) {
        this.projectData = projectData;
    }

    public RootTable getRootTable() {
        return rootTable;
    }

    public DialogFactory getDialogFactory() {
        return dialogFactory;
    }

    public IbeamListener getIbeamListener() {
        return ibeamListener;
    }

    public MainListener getMainListener() {
        return mainListener;
    }

    public HandListener getHandListener() {
        return handListener;
    }

    public ResizeArrowListener getVerticalResizeArrowListener() {
        return verticalResizeArrowListener;
    }

    public ResizeArrowListener getHorizontalResizeArrowListener() {
        return horizontalResizeArrowListener;
    }

    public TooltipManager getTooltipManager() {
        return tooltipManager;
    }

    public static Class basicToStyleClass(Class clazz) {
        int i = 0;
        for (Class basicClass : BASIC_CLASSES) {
            if (clazz.equals(basicClass)) {
                break;
            } else {
                i++;
            }
        }
        return STYLE_CLASSES[i];
    }

    public static void checkForUpdates(Main main) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
                HttpRequest httpRequest = requestBuilder.newRequest().method(HttpMethods.GET).url("https://raw.githubusercontent.com/raeleus/skin-composer/master/version").build();
                Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
                    @Override
                    public void handleHttpResponse(Net.HttpResponse httpResponse) {
                        newVersion = httpResponse.getResultAsString();
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                main.rootTable.fire(new RootTable.RootTableEvent(RootTable.RootTableEnum.CHECK_FOR_UPDATES_COMPLETE));
                            }
                        });
                    }

                    @Override
                    public void failed(Throwable t) {
                        newVersion = VERSION;
                    }

                    @Override
                    public void cancelled() {
                        newVersion = VERSION;
                    }
                });
            }
        });
        
        thread.start();
    }
}
