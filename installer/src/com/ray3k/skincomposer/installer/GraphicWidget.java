/*
 * The MIT License
 *
 * Copyright (c) 2024 Raymond Buckley.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.ray3k.skincomposer.installer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.ray3k.skincomposer.installer.SpineDrawable.SpineDrawableTemplate;

/**
 *
 * @author Raymond
 */
public class GraphicWidget extends Table {
    private float timeScale;
    private SpineDrawable spineDrawable;
    private Mode mode;
    
    public static enum Mode {
        START, INSTALLING, COMPLETE
    }

    public GraphicWidget(Skin skin) {
        super(skin);
        
        mode = Mode.START;
        
        setTouchable(Touchable.disabled);
        
        SkeletonJson skeletonJson = new SkeletonJson(new TextureAtlas(Gdx.files.internal("ui/skin-composer-installer-ui.atlas")));
        SkeletonRenderer skeletonRenderer = new SkeletonRenderer();
        skeletonRenderer.setPremultipliedAlpha(false);
        
        SpineDrawableTemplate template = new SpineDrawableTemplate();
        template.internalPath = Gdx.files.internal("ui/progress-bar.json");
        template.widthBones = new Array<>(new String[] {"resizer"});
        template.heightBones = new Array<>(new String[] {"resizer"});
        
        spineDrawable = new SpineDrawable(skeletonJson, skeletonRenderer, template);
        spineDrawable.getAnimationState().getData().setDefaultMix(0);
    }

    public float getTimeScale() {
        return timeScale;
    }

    public void setTimeScale(float timeScale) {
        this.timeScale = timeScale;
        
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (mode == Mode.START || mode == Mode.COMPLETE) {
            spineDrawable.getAnimationState().update(Gdx.graphics.getDeltaTime());
        } else if (mode == Mode.INSTALLING) {
//            spineDrawable.getAnimationState().update(Gdx.graphics.getDeltaTime());
            spineDrawable.getAnimationState().getCurrent(0).setTrackTime(timeScale);
        }
        spineDrawable.draw(batch, getX(), getY(), getWidth(), getHeight());
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        
        switch (mode) {
            case START:
                spineDrawable.getAnimationState().setAnimation(0, "start", false);
                break;
            case INSTALLING:
                spineDrawable.getAnimationState().setAnimation(0, "install", false);
                break;
            case COMPLETE:
                spineDrawable.getAnimationState().setAnimation(0, "complete", false);
                break;
        }
    }

    public SpineDrawable getSpineDrawable() {
        return spineDrawable;
    }
}
