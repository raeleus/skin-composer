/*
 * The MIT License
 *
 * Copyright 2018 Raymond Buckley.
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

package com.ray3k.skincomposer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;

public class AnimationDrawable extends BaseDrawable {
    private String regionName;
    private Skin skin;
    
    private Animation<TextureRegion> animation;
    private float stateTime;

    public AnimationDrawable(Skin skin, String regionName, float frameDuration) {
        super();
        stateTime = 0.0f;
        
        setRegion(skin, regionName, frameDuration);
    }

    public void update(float delta) {
        stateTime += delta;
    }
    
    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        batch.draw(animation.getKeyFrame(stateTime), x, y, width, height);
    }

    public String getRegionName() {
        return regionName;
    }

    public Animation<TextureRegion> getAnimation() {
        return animation;
    }

    public void setRegion(Skin skin, String regionName, float frameDuration) {
        this.skin = skin;
        this.regionName = regionName;
        animation = new Animation<>(frameDuration, skin.getRegions(regionName), PlayMode.LOOP);
        setMinWidth(animation.getKeyFrame(0.0f).getRegionWidth());
        setMinHeight(animation.getKeyFrame(0.0f).getRegionHeight());
    }

    public float getFrameDuration() {
        return animation.getFrameDuration();
    }

    public void setFrameDuration(float frameDuration) {
        animation.setFrameDuration(frameDuration);
    }
}
