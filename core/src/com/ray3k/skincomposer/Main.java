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
import com.ray3k.skincomposer.data.StyleData;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ray3k.skincomposer.data.AtlasData;
import com.ray3k.skincomposer.data.JsonData;
import com.ray3k.skincomposer.data.ProjectData;
import com.ray3k.skincomposer.utils.Utils;

public class Main extends ApplicationAdapter {
    public final static String VERSION = "6";
    private static Main instance;
    private Stage stage;
    private static Skin skin;
    private DialogFactory dialogFactory;
    private DesktopWorker desktopWorker;
    private AnimatedDrawable loadingAnimation;
    private UndoableManager undoableManager;
    
    public static Main instance() {
        return instance;
    }
    
    @Override
    public void create() {
        skin = new Skin(Gdx.files.internal("skin-composer-ui/skin-composer-ui.json"));
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        
        initDefaults();
        
        populate();
    }
    
    private void initDefaults() {
        if (Utils.isMac()) System.setProperty("java.awt.headless", "true");
        
        dialogFactory = new DialogFactory(skin, stage);
        undoableManager = new UndoableManager();
        
        desktopWorker.attachLogListener();
        desktopWorker.sizeWindowToFit(800, 800, 50, Gdx.graphics);
        desktopWorker.centerWindow(Gdx.graphics);
        desktopWorker.setCloseListener(() -> {
            dialogFactory.showCloseDialog();
            return false;
        });
        
        instance = this;
        
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
    }

    private void populate() {
        stage.clear();
        
        RootTable root = new RootTable(stage, skin);
        root.setFillParent(true);
        root.addListener(new MainListener(root, dialogFactory, desktopWorker));
        stage.addActor(root);
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

    public void setUndoableManager(UndoableManager undoableManager) {
        this.undoableManager = undoableManager;
    }
}
