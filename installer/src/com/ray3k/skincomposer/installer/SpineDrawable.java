/*
 * The MIT License
 *
 * Copyright 2024 Raymond Buckley.
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
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Bone;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;

public class SpineDrawable extends BaseDrawable {
    private Skeleton skeleton;
    private AnimationState animationState;
    private SkeletonRenderer skeletonRenderer;
    private Array<Bone> widthBones;
    private Array<Bone> heightBones;

    public SpineDrawable(SkeletonJson skeletonJson, SkeletonRenderer skeletonRenderer, SpineDrawableTemplate template) {
        this.skeletonRenderer = skeletonRenderer;
        
        SkeletonData skeletonData = skeletonJson.readSkeletonData(template.internalPath);
        skeleton = new Skeleton(skeletonData);
        AnimationStateData animationStateData = new AnimationStateData(skeletonData);
        animationState = new AnimationState(animationStateData);
        
        widthBones = new Array<Bone>();
        heightBones = new Array<Bone>();
        
        if (template.widthBones != null) for (String name : template.widthBones) {
            widthBones.add(skeleton.findBone(name));
        }
        
        if (template.heightBones != null) for (String name : template.heightBones) {
            heightBones.add(skeleton.findBone(name));
        }
        
        setTopHeight(template.topHeight);
        
        setBottomHeight(template.bottomHeight);
        
        setLeftWidth(template.leftWidth);
        
        setRightWidth(template.rightWidth);
        
        setMinWidth(template.minWidth);
        
        setMinHeight(template.minHeight);
    }

    public Skeleton getSkeleton() {
        return skeleton;
    }

    public AnimationState getAnimationState() {
        return animationState;
    }
    
    public void setWidthBones(String... boneNames) {
        for (String boneName : boneNames) {
            widthBones.add(skeleton.findBone(boneName));
        }
    }
    
    public void setHeightBones(String... boneNames) {
        for (String boneName : boneNames) {
            heightBones.add(skeleton.findBone(boneName));
        }
    }

    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        for (Bone bone : widthBones) {
            bone.setScaleX(width);
        }

        for (Bone bone : heightBones) {
            bone.setScaleY(height);
        }

        skeleton.setPosition(x, y);
        animationState.apply(skeleton);
        skeleton.updateWorldTransform();

        skeletonRenderer.draw(batch, skeleton);
        
        //reset to normal blending
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }
    
    public static class SpineDrawableTemplate {
        public Array<String> widthBones, heightBones;
        public float topHeight, bottomHeight, leftWidth, rightWidth, minWidth, minHeight;
        public FileHandle internalPath;
        
        public SpineDrawableTemplate() {
            
        }
        
        public SpineDrawableTemplate(SpineDrawableTemplate template) {
            if (template.widthBones != null) {
                widthBones = new Array<String>(template.widthBones);
            }
            
            if (template.heightBones != null) {
                heightBones = new Array<String>(template.heightBones);
            }
            
            topHeight = template.topHeight;
            
            bottomHeight = template.bottomHeight;
            
            leftWidth = template.leftWidth;
            
            rightWidth = template.rightWidth;
            
            minWidth = template.minWidth;
            
            minHeight = template.minHeight;
            
            internalPath = template.internalPath;
        }
    }
}
